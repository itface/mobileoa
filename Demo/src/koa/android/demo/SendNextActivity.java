package koa.android.demo;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import koa.android.demo.model.Task;
import koa.android.demo.model.User;
import koa.android.demo.model.WFRunModel;
import koa.android.logic.ActivityHoder;
import koa.android.logic.MainService;
import koa.android.logic.ProgressDialogHolder;
import koa.android.tools.Base64Encoder;
import koa.android.tools.HttpResponseUtil;
import koa.android.tools.JsonUtil;
import koa.android.tools.LoginTimeOut;
import koa.android.tools.SetBackGround;

import org.json.JSONArray;
import org.json.JSONException;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

/**
 * 发送办理的页面
 * 
 * @author chenM
 * 
 */
public class SendNextActivity extends Activity implements KoaActivity, OnGestureListener, OnTouchListener {
	private Button title_bt_left;// 回退按钮
	private TextView top_title;// 顶部文本域
	private Button transBtn = null;
	private Intent intent;
	private TextView tvStepName = null;
	private AutoCompleteTextView sender = null;
	private WFRunModel model;
	private ProgressDialog pd;
	private String sessionId;
	private RadioGroup wf_priority_Group;
	private RadioButton p1;
	private RadioButton p2;
	private RadioButton p3;
	private RadioButton p4;
	private TextView wf_priority_text1;
	private TextView wf_priority_text2;
	private TextView wf_priority_text3;
	private TextView wf_priority_text4;
	// 优先级定义
	private int PRIORITY_NONE = 1;// 无
	private int PRIORITY_LOW = 0;// 低
	private int PRIORITY_MEDIUM = 2;// 中
	private int PRIORITY_HEIGH = 3;// 高
	private String priority;
	// 下一节点办理人显示方式
	private int SENDER_SHOWTYPE_1 = 1;// 显示文本框，让用户录入
	private int SENDER_SHOWTYPE_2 = 2;// 复选框，让用户选择
	private int SENDER_SHOWTYPE_3 = 3;// 复选框，全选,不许更改
	private int SENDER_SHOWTYPE_4 = 4;// 显示文本框，文本框不允许编辑
	private LinearLayout wf_sender_area;
	private int TotalCheckedNum = 0;
	private Map<String, String> senderMap;// 定义全局Hash,用于在发送模式2的时候， 临时存放发送人
	private Drawable wfSenderClear; // 搜索文本框清除文本内容图标
	// ===============手势识别=========================
	private GestureDetector mGestureDetector;
	private static final int FLING_MIN_DISTANCE = 150;
	private static final int FLING_MIN_VELOCITY = 200;
	private LinearLayout sendnext_layout;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		LoginTimeOut LoginTimeOut = new LoginTimeOut();
		boolean flag = LoginTimeOut.timeOut(SendNextActivity.this);
		if (!flag) {
			setContentView(R.layout.sendnext);
			mGestureDetector = new GestureDetector(this);
			// 获得参数
			intent = getIntent();
			model = (WFRunModel) intent.getSerializableExtra("WFRunModel");
			sessionId = User.getInstance().getUserContext(SendNextActivity.this).getSessionId();
			senderMap = new HashMap<String, String>();
			// 将Activity放到Activity中
			ActivityHoder.getInstance().addActivity(this);
			// 初始化UI组件
			setUpView();
			// 增加组件监听器
			setUpListener();
			init();
		}
	}

	@Override
	protected void onRestart() {
		super.onRestart();
		setBackground();
	}

	// 设置背景图
	private void setBackground() {
		// 设置背景图
		SetBackGround.getInstance().setTheme(this, top_title, "top_title", R.drawable.ui_title_bg_blue, null);
		SetBackGround.getInstance().setTheme(this, title_bt_left, "ui_backbtn", R.drawable.ui_title_back_btn_blue, null);
	}

	// 初始化UI组件
	public void setUpView() {
		title_bt_left = (Button) findViewById(R.id.title_bt_left);
		top_title = (TextView) findViewById(R.id.top_title);
		tvStepName = (TextView) findViewById(R.id.senderStepName);
		sender = (AutoCompleteTextView) findViewById(R.id.tasksender);
		sender.setThreshold(3);
		sender.setOverScrollMode(View.OVER_SCROLL_IF_CONTENT_SCROLLS);
		transBtn = (Button) findViewById(R.id.sendActionBtn);
		wf_priority_Group = (RadioGroup) findViewById(R.id.wf_priority_Group);
		p1 = (RadioButton) findViewById(R.id.wf_priority_Button1);
		p1.setId(PRIORITY_NONE);
		p2 = (RadioButton) findViewById(R.id.wf_priority_Button2);
		p2.setId(PRIORITY_LOW);
		p3 = (RadioButton) findViewById(R.id.wf_priority_Button3);
		p3.setId(PRIORITY_MEDIUM);
		p4 = (RadioButton) findViewById(R.id.wf_priority_Button4);
		p4.setId(PRIORITY_HEIGH);
		wf_sender_area = (LinearLayout) findViewById(R.id.wf_sender_area);
		wf_priority_text1 = (TextView) findViewById(R.id.wf_priority_text1);
		wf_priority_text2 = (TextView) findViewById(R.id.wf_priority_text2);
		wf_priority_text3 = (TextView) findViewById(R.id.wf_priority_text3);
		wf_priority_text4 = (TextView) findViewById(R.id.wf_priority_text4);
		final Resources res = getResources();
		wfSenderClear = res.getDrawable(R.drawable.wf_search_clear);
		sendnext_layout = (LinearLayout) findViewById(R.id.sendnext_layout);
		sendnext_layout.setOnTouchListener(this);
		sendnext_layout.setLongClickable(true);
		setBackground();
	}

	// 增加组件监听器
	public void setUpListener() {
		wf_priority_text1.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				p1.setChecked(true);
				p2.setChecked(false);
				p3.setChecked(false);
				p4.setChecked(false);
			}
		});
		wf_priority_text2.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				p2.setChecked(true);
				p1.setChecked(false);
				p3.setChecked(false);
				p4.setChecked(false);
			}
		});
		wf_priority_text3.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				p3.setChecked(true);
				p2.setChecked(false);
				p1.setChecked(false);
				p4.setChecked(false);
			}
		});
		wf_priority_text4.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				p4.setChecked(true);
				p2.setChecked(false);
				p3.setChecked(false);
				p1.setChecked(false);
			}
		});
		// 顺序办理按钮
		TextPaint tp = transBtn.getPaint();
		tp.setFakeBoldText(true);
		transBtn.setOnClickListener(new SenderBtnListener());
		// 后退按钮
		title_bt_left.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				finish();
			}
		});
		wf_priority_Group.setOnCheckedChangeListener(new android.widget.RadioGroup.OnCheckedChangeListener() {
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				priority = String.valueOf(checkedId);
			}
		});
		sender.addTextChangedListener(new TextWatcher() {
			// 缓存上一次文本框内是否为空
			private boolean isnull = true;

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {

			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {

			}

			@Override
			public void afterTextChanged(Editable s) {
				if (TextUtils.isEmpty(s)) {
					if (!isnull) {
						sender.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
						isnull = true;
					}
				} else {
					if (isnull) {
						sender.setCompoundDrawablesWithIntrinsicBounds(null, null, wfSenderClear, null);
						isnull = false;
					}
				}
			}
		});
		sender.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				switch (event.getAction()) {
				case MotionEvent.ACTION_UP:
					int curX = (int) event.getX();
					if (curX > v.getWidth() - 43 && !TextUtils.isEmpty(sender.getText())) {
						sender.onTouchEvent(event);
						sender.setText("");
						return true;
					}
				}
				return false;
			}
		});
		// AutoCompleteTextView监听器
		sender.addTextChangedListener(new TextWatcher() {
			@Override
			public void afterTextChanged(Editable s) {
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				String str = s.toString();
				if (str.length() > 3) {
					HashMap<String, Object> userHash = new HashMap<String, Object>();
					userHash.put("keyword", str);
					String senderUserJson = HttpResponseUtil.getInstance().getGetHttpInfo("KOA_Mobile_Get_UserName_By_Word", sessionId, userHash, SendNextActivity.this);
					if (senderUserJson.equals("TIMEOUT")) {
						Toast.makeText(SendNextActivity.this, "加载超时，请稍后再试", Toast.LENGTH_SHORT).show();
					} else if (senderUserJson.equals("SESSION_TIMEOUT")) {
						HttpResponseUtil.getInstance().reLogin(SendNextActivity.this);
					} else if (senderUserJson.equals("NETWORK_ERROR")) {
						Toast.makeText(SendNextActivity.this, "网络连接异常，请稍后再试", Toast.LENGTH_SHORT).show();
					} else {
						try {
							JSONArray array = new JSONArray(senderUserJson);
							String[] temp = new String[array.length()];
							for (int i = 0; i < array.length(); i++) {
								temp[i] = array.getString(i);
								ArrayAdapter<String> adapter = new ArrayAdapter<String>(SendNextActivity.this, R.layout.wf_help_inputuserid, temp);
								sender.setAdapter(adapter);
							}
						} catch (JSONException e) {
							e.printStackTrace();
						}
					}
				}
			}
		});
	}

	// 初始化
	@Override
	public void init() {
		// 设置键盘输入
		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_MASK_ADJUST);
		top_title.setText("办理");
		// 判断下一节点是否显示,如果不存在,则不允许办理;如果存在继续初始化页面
		if (model.getWfStepNAME() == null || "".equals(model.getWfStepNAME())) {
			Toast.makeText(SendNextActivity.this, "流程扭转异常，请联系管理员", Toast.LENGTH_LONG).show();
			transBtn.setVisibility(View.GONE);
		} else {
			// 设置发送节点名称
			String flowTitle = intent.getStringExtra("flowTitle");
			if (flowTitle.indexOf(")") > 0) {
				String flow = flowTitle.substring(flowTitle.indexOf(")") + 1, flowTitle.length() - 1);
				tvStepName.setText("(" + model.getWfStepNAME() + ")" + flow);
			} else {
				tvStepName.setText(model.getWfStepNAME() + intent.getStringExtra("flowTitle"));
			}
			// 设置默认发送优先级
			int priority = model.getPriority();
			if (priority == PRIORITY_NONE) {
				p1.setChecked(true);
			} else if (priority == PRIORITY_LOW) {
				p2.setChecked(true);
			} else if (priority == PRIORITY_MEDIUM) {
				p3.setChecked(true);
			} else if (priority == PRIORITY_HEIGH) {
				p4.setChecked(true);
			} else {
				p1.setChecked(true);
			}
			// 设置任务发送人显示方式
			if (model.getShowType() == SENDER_SHOWTYPE_1) {// 显示文本框，让用户录入
				if (model.getUserlist() != null || !"".equals(model.getUserlist())) {
					sender.setText(model.getUserlist());
				}
			} else if (model.getShowType() == SENDER_SHOWTYPE_2) {// 复选框，让用户选择
				if (model.getUserlist() != null || !"".equals(model.getUserlist())) {
					// 隐藏发送人输入框
					sender.setVisibility(View.GONE);
					String[] UserArr = model.getUserlist().split(" ");
					int num = model.getNum();
					if (num == 0) {
						num = 999;
					}// 如果发送人为0,则不限制发送人数
					createMulti(UserArr, num, true);
				}
			} else if (model.getShowType() == SENDER_SHOWTYPE_3) {// 复选框，全选,不许更改
				if (model.getUserlist() != null || !"".equals(model.getUserlist())) {
					// 隐藏发送人输入框
					sender.setVisibility(View.GONE);
					String[] UserArr = model.getUserlist().split(" ");
					int num = UserArr.length;
					if (num == 0) {
						num = 999;
					}// 如果发送人为0,则不限制发送人数
					createMulti(UserArr, num, false);
				}
			} else if (model.getShowType() == SENDER_SHOWTYPE_4) {// 显示文本框，文本框不允许编辑
				if (model.getUserlist() != null || !"".equals(model.getUserlist())) {
					sender.setText(model.getUserlist());
					sender.setFocusable(false);
					sender.setEnabled(false);
					sender.setClickable(false);
				}
			} else {// 若没有显示方式,按默认方式显示
				if (model.getUserlist() != null || !"".equals(model.getUserlist())) {
					sender.setText(model.getUserlist());
				}
			}
		}
	}

	/**
	 * 创建人员选择复选框
	 * 
	 * @param UserArr
	 *            候选发送人
	 * @param num
	 *            选择个数
	 */
	public void createMulti(String[] UserArr, final int num, boolean changeable) {
		for (int i = 0; i < UserArr.length; i++) {
			// 动态创建发送人
			CheckBox c = new CheckBox(SendNextActivity.this);
			c.setText(UserArr[i]);
			c.setTextAppearance(getApplicationContext(), R.style.wf_sendnext_checkbox_style);
			c.setButtonDrawable(R.drawable.wf_detail_operate_bar_button_trans);
			if (UserArr.length == 1) {
				c.setBackgroundResource(R.drawable.wf_detail_subtable_1_1);
			} else {
				if (i == 0) {
					c.setBackgroundResource(R.drawable.wf_detail_subtable_2_1);
				} else if (i == UserArr.length - 1) {
					c.setBackgroundResource(R.drawable.wf_detail_subtable_2_3);
				} else {
					c.setBackgroundResource(R.drawable.wf_detail_subtable_2_2);
				}
			}
			LinearLayout.LayoutParams cp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
			c.setLayoutParams(cp);
			c.setPadding(20, 0, 60, 0);
			wf_sender_area.addView(c);
			// 如果候选人数为1,则默认勾选
			if (UserArr.length == 1) {
				c.setChecked(true);
				TotalCheckedNum++;
				senderMap.put(UserArr[i], UserArr[i]);
			}
			// 不可改变
			if (!changeable) {
				c.setChecked(true);
				c.setOnCheckedChangeListener(new android.widget.CompoundButton.OnCheckedChangeListener() {
					@Override
					public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
						Toast.makeText(SendNextActivity.this, "当前发送人不可改变", Toast.LENGTH_SHORT).show();
						if (!isChecked) {
							buttonView.setChecked(true);
						}
					}
				});
			} else {// 可以改变
				c.setOnCheckedChangeListener(new android.widget.CompoundButton.OnCheckedChangeListener() {

					@Override
					public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
						if (isChecked) {
							TotalCheckedNum++;
							if (TotalCheckedNum > num) {
								Toast.makeText(SendNextActivity.this, "当前发送人最多为" + num + "人", Toast.LENGTH_SHORT).show();
								buttonView.setChecked(false);
								TotalCheckedNum--;
							} else {
								senderMap.put(buttonView.getText().toString(), buttonView.getText().toString());
							}
						} else if (!isChecked) {
							TotalCheckedNum--;
							if (senderMap.containsKey(buttonView.getText().toString())) {
								senderMap.remove(buttonView.getText().toString());
							}
						}
					}
				});
			}

		}
	}

	/**
	 * 执行发送动作
	 * 
	 */
	class SenderBtnListener implements OnClickListener {
		public void onClick(View v) {
			String senderUser = "";
			if (model.getShowType() == SENDER_SHOWTYPE_1) {
				senderUser = sender.getText().toString().trim();
			} else if (model.getShowType() == SENDER_SHOWTYPE_2) {
				Iterator<Entry<String, String>> i = senderMap.entrySet().iterator();
				while (i.hasNext()) {
					Map.Entry<String, String> me = (Map.Entry<String, String>) i.next();
					String key = (String) me.getKey();
					senderUser = senderUser + key + " ";
				}
				senderUser = senderUser.trim();
			} else if (model.getShowType() == SENDER_SHOWTYPE_3 || model.getShowType() == SENDER_SHOWTYPE_4) {
				senderUser = model.getUserlist().trim();
			}
			if (null == senderUser || "".equals(senderUser)) {
				Toast.makeText(SendNextActivity.this, "任务发送人不可为空", Toast.LENGTH_SHORT).show();
			} else {
				transBtn.setEnabled(false);
				pd = new ProgressDialog(SendNextActivity.this);
				pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
				pd.setCancelable(true);
				pd.setMessage("正在发送...");
				pd.show();
				ProgressDialogHolder.getInstance().addActivity(pd);
				String checkSenderUser = senderUser.replaceAll("\\s", "_");
				String sessionId = User.getInstance().getUserContext(SendNextActivity.this).getSessionId();
				HashMap<String, Object> userHash = new HashMap<String, Object>();
				userHash.put("userNames", checkSenderUser);
				String senderUserJson = HttpResponseUtil.getInstance().getGetHttpInfo("KOA_Mobile_Check_UserName", sessionId, userHash, SendNextActivity.this);
				if (senderUserJson == null || "".equals(senderUserJson)) {
					ProgressDialogHolder.getInstance().exit();
					Toast.makeText(getApplicationContext(), "账户检查返回值异常", Toast.LENGTH_SHORT).show();
					transBtn.setEnabled(true);
				} else {
					senderUserJson = senderUserJson.substring(1, senderUserJson.length() - 1);
					String result = JsonUtil.parseJson(senderUserJson, "result");
					if (result.equals("true")) {
						HashMap<String, Object> hashparam = new HashMap<String, Object>();
						String instanceId = intent.getStringExtra("instanceId"); // 获得实例ID
						String taskId = intent.getStringExtra("taskId"); // 获得任务ID
						hashparam.put("id", instanceId);
						hashparam.put("taskId", taskId);
						try {
							hashparam.put("MAIL_TO", Base64Encoder.encode(senderUser.trim().getBytes("UTF-8")));
							if (intent.getStringExtra("flowTitle") != null) {
								hashparam.put("TITLE", Base64Encoder.encode(("(" + model.getWfStepNAME() + ")" + intent.getStringExtra("flowTitle")).getBytes("UTF-8")));
							}
						} catch (UnsupportedEncodingException e) {
							e.printStackTrace();
						}
						hashparam.put("stepno", model.getWfStepNo());
						hashparam.put("runStyle", model.getRoutePointType());
						hashparam.put("localDepartmentId", model.getLocalDepartmentId());
						hashparam.put("priority", priority);
						Task task = new Task(Task.KOA_COMMON, "KOA_Mobile_WF_Transaction_Send", sessionId, "SendNextActivity", hashparam);
						// 将任务发送给Service来处理
						MainService.newTask(task);
						// 把自己添加到Activity集合里面
						MainService.addActivity(SendNextActivity.this);
					} else {
						transBtn.setEnabled(true);
						ProgressDialogHolder.getInstance().exit();
						Toast.makeText(getApplicationContext(), "输入账户不合法", Toast.LENGTH_SHORT).show();
					}
				}
			}
		}
	}

	@Override
	public void refresh(Object... params) {
		ProgressDialogHolder.getInstance().exit();
		String json = params[0].toString();
		if (json == null || "".equals(json)) {
			Toast.makeText(getApplicationContext(), "返回值异常", Toast.LENGTH_SHORT).show();
		} else if (json.equals("TIMEOUT")) {
			Toast.makeText(getApplicationContext(), "加载超时,请稍后再试", Toast.LENGTH_SHORT).show();
		} else if (json.equals("SESSION_TIMEOUT")) {
			HttpResponseUtil.getInstance().reLogin(SendNextActivity.this);
		} else if (json.equals("NETWORK_ERROR")) {
			Toast.makeText(SendNextActivity.this, "网络连接异常，请稍后再试", Toast.LENGTH_SHORT).show();
		} else if (json.equals("SUCCESS")) {
			Toast.makeText(getApplicationContext(), "任务发送成功", Toast.LENGTH_LONG).show();
			intent.setClass(SendNextActivity.this, TodoListAllActivity.class);
			startActivity(intent);
			finish();
		} else if (json.equals("TASK_END")) {
			Toast.makeText(getApplicationContext(), "流程任务结束", Toast.LENGTH_LONG).show();
			intent.setClass(SendNextActivity.this, TodoListAllActivity.class);
			startActivity(intent);
			finish();
		} else if (json.equals("TASK_ERROR-001")) {
			Toast.makeText(getApplicationContext(), "流程办理异常", Toast.LENGTH_LONG).show();
		} else if (json.equals("TASK_ERROR-002")) {
			Toast.makeText(getApplicationContext(), "任务不能被发送,目标用户不属于限制范围内！", Toast.LENGTH_LONG).show();
		} else if (json.equals("task break")) {
			Toast.makeText(getApplicationContext(), "任务不能结束, 数据没有办理完毕", Toast.LENGTH_LONG).show();
		} else {
			Toast.makeText(getApplicationContext(), "返回值异常", Toast.LENGTH_SHORT).show();
		}
		transBtn.setEnabled(true);
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		return mGestureDetector.onTouchEvent(event);
	}

	@Override
	public boolean onDown(MotionEvent e) {
		return false;
	}

	@Override
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
		if (e1 != null && e2 != null) {
			if (e2.getX() - e1.getX() > FLING_MIN_DISTANCE && Math.abs(velocityX) > FLING_MIN_VELOCITY && e2.getX() - e1.getX() > e1.getY() - e2.getY()) {
				finish();
			}
		}
		return true;
	}

	@Override
	public void onLongPress(MotionEvent e) {
	}

	@Override
	public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
		return false;
	}

	@Override
	public void onShowPress(MotionEvent e) {

	}

	@Override
	public boolean onSingleTapUp(MotionEvent e) {
		return false;
	}

}