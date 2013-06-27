package koa.android.demo;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;

import koa.android.demo.model.Task;
import koa.android.demo.model.User;
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
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

/**
 * 发送加签
 * 
 * @author chenM
 * 
 */
public class AddsignActivity extends Activity implements KoaActivity, OnGestureListener, OnTouchListener {
	private Button title_bt_left;// 回退按钮
	private TextView top_title;// 顶部文本域
	private Button transBtn = null;
	private Intent intent;
	private TextView tvStepName = null;
	private AutoCompleteTextView sender = null;
	private ProgressDialog pd;
	private String sessionId;
	private RelativeLayout wf_priority_area;
	private Drawable wfSenderClear; // 搜索文本框清除文本内容图标
	// ===============手势识别=========================
	private GestureDetector mGestureDetector;
	private static final int FLING_MIN_DISTANCE = 150;
	private static final int FLING_MIN_VELOCITY = 200;
	private LinearLayout sendnext_layout;
	private TextView wf_sendnext_title_text;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		LoginTimeOut LoginTimeOut = new LoginTimeOut();
		boolean flag = LoginTimeOut.timeOut(AddsignActivity.this);
		if (!flag) {
			setContentView(R.layout.sendnext);
			mGestureDetector = new GestureDetector(this);
			// 获得参数
			intent = getIntent();
			sessionId = User.getInstance().getUserContext(AddsignActivity.this).getSessionId();
			// 初始化UI组件
			setUpView();
			// 增加组件监听器
			setUpViewListener();
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
		transBtn = (Button) findViewById(R.id.sendActionBtn);
		wf_priority_area = (RelativeLayout) findViewById(R.id.wf_priority_area);
		final Resources res = getResources();
		wfSenderClear = res.getDrawable(R.drawable.wf_search_clear);
		sendnext_layout = (LinearLayout) findViewById(R.id.sendnext_layout);
		sendnext_layout.setOnTouchListener(this);
		sendnext_layout.setLongClickable(true);
		wf_sendnext_title_text = (TextView) findViewById(R.id.wf_sendnext_title_text);
		wf_sendnext_title_text.setText("加签给:");
		setBackground();
	}

	// 增加组件监听器
	public void setUpViewListener() {
		// 加签按钮
		transBtn.setText("发送加签");
		TextPaint tp = transBtn.getPaint();
		tp.setFakeBoldText(true);
		transBtn.setOnClickListener(new SenderBtnListener());
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
					String senderUserJson = HttpResponseUtil.getInstance().getGetHttpInfo("KOA_Mobile_Get_UserName_By_Word", sessionId, userHash, AddsignActivity.this);
					if (senderUserJson.equals("TIMEOUT")) {
						Toast.makeText(AddsignActivity.this, "加载超时，请稍后再试", Toast.LENGTH_SHORT).show();
					} else if (senderUserJson.equals("SESSION_TIMEOUT")) {
						HttpResponseUtil.getInstance().reLogin(AddsignActivity.this);
					} else if (senderUserJson.equals("NETWORK_ERROR")) {
						Toast.makeText(AddsignActivity.this, "网络连接异常，请稍后再试", Toast.LENGTH_SHORT).show();
					} else {
						try {
							JSONArray array = new JSONArray(senderUserJson);
							String[] temp = new String[array.length()];
							for (int i = 0; i < array.length(); i++) {
								temp[i] = array.getString(i);
								ArrayAdapter<String> adapter = new ArrayAdapter<String>(AddsignActivity.this, R.layout.wf_help_inputuserid, temp);
								sender.setAdapter(adapter);
							}
						} catch (JSONException e) {
							e.printStackTrace();
						}
					}
				}
			}
		});
		// 后退按钮
		title_bt_left.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				finish();
			}
		});
	}

	// 初始化
	@Override
	public void init() {
		// 设置键盘输入
		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_MASK_ADJUST);
		top_title.setText("加签");
		String flowTitle = intent.getStringExtra("flowTitle");
		tvStepName.setText(flowTitle);
		wf_priority_area.setVisibility(View.GONE);
	}

	/**
	 * 执行发送动作
	 * 
	 */
	class SenderBtnListener implements OnClickListener {
		public void onClick(View v) {
			pd = new ProgressDialog(AddsignActivity.this);
			pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			pd.setCancelable(true);
			pd.setMessage("正在发送...");
			pd.show();
			ProgressDialogHolder.getInstance().addActivity(pd);
			String senderUser = sender.getText().toString();
			String checkSenderUser = senderUser.replaceAll("\\s", "_");
			String sessionId = User.getInstance().getUserContext(AddsignActivity.this).getSessionId();
			;
			HashMap<String, Object> userHash = new HashMap<String, Object>();
			userHash.put("userNames", checkSenderUser);
			String senderUserJson = HttpResponseUtil.getInstance().getGetHttpInfo("KOA_Mobile_Check_UserName", sessionId, userHash, AddsignActivity.this);
			if (senderUserJson == null && "".equals(senderUserJson)) {
				Toast.makeText(getApplicationContext(), "加载超时,请稍后再试", Toast.LENGTH_SHORT).show();
			} else if (senderUserJson.equals("TIMEOUT")) {
				Toast.makeText(AddsignActivity.this, "加载超时,请稍后再试", Toast.LENGTH_SHORT).show();
			} else if (senderUserJson.equals("SESSION_TIMEOUT")) {
				HttpResponseUtil.getInstance().reLogin(AddsignActivity.this);
			} else if (senderUserJson.equals("NETWORK_ERROR")) {
				Toast.makeText(AddsignActivity.this, "网络连接异常，请稍后再试", Toast.LENGTH_SHORT).show();
			} else {
				senderUserJson = senderUserJson.substring(1, senderUserJson.length() - 1);
				String result = JsonUtil.parseJson(senderUserJson, "result");
				if (result.equals("true")) {
					HashMap<String, Object> hashparam = new HashMap<String, Object>();
					String instanceId = intent.getStringExtra("instanceId"); // 获得实例ID
					String taskId = intent.getStringExtra("taskId"); // 获得任务ID
					hashparam.put("id", instanceId);
					hashparam.put("taskId", taskId);
					String flowTitle = intent.getStringExtra("flowTitle");
					try {
						hashparam.put("mailTo", Base64Encoder.encode(senderUser.getBytes("UTF-8")));
						hashparam.put("title", Base64Encoder.encode(flowTitle.getBytes("UTF-8")));
					} catch (UnsupportedEncodingException e) {
						e.printStackTrace();
					}
					Task task = new Task(Task.KOA_COMMON, "KOA_Mobile_WorkFlow_Execute_Worklist_AddParticipants_Send", sessionId, "AddsignActivity", hashparam);
					// 将任务发送给Service来处理
					MainService.newTask(task);
					// 把自己添加到Activity集合里面
					MainService.addActivity(AddsignActivity.this);
				} else {
					ProgressDialogHolder.getInstance().exit();
					Toast.makeText(getApplicationContext(), "输入账户不合法", Toast.LENGTH_SHORT).show();
				}
			}
		}
	}

	@Override
	public void refresh(Object... params) {
		ProgressDialogHolder.getInstance().exit();
		String json = params[0].toString();
		if (json == null || "".equals(json)) {
			Toast.makeText(getApplicationContext(), "加载超时,请稍后再试", Toast.LENGTH_SHORT).show();
		} else if (json.equals("TIMEOUT")) {
			Toast.makeText(getApplicationContext(), "加载超时,请稍后再试", Toast.LENGTH_SHORT).show();
		} else if (json.equals("SESSION_TIMEOUT")) {
			HttpResponseUtil.getInstance().reLogin(AddsignActivity.this);
		} else if (json.equals("NETWORK_ERROR")) {
			Toast.makeText(AddsignActivity.this, "网络连接异常，请稍后再试", Toast.LENGTH_SHORT).show();
		} else if (json.contains("发送成功")) {
			Toast.makeText(getApplicationContext(), "发送成功", Toast.LENGTH_SHORT).show();
			intent.setClass(AddsignActivity.this, TodoListAllActivity.class);
			startActivity(intent);
			finish();
		} else if (json.contains("发送失败")) {
			Toast.makeText(getApplicationContext(), "发送失败", Toast.LENGTH_SHORT).show();
		}
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