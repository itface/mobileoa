package koa.android.demo;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;

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
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextPaint;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

/**
 * 流程办理
 * 
 * @author chenM
 * 
 */
public class WfExamActivity extends Activity implements KoaActivity, OnGestureListener, OnTouchListener {
	private Button title_bt_left;// 回退按钮
	private TextView top_title;// 顶部文本域
	private ProgressDialog pd;
	private Intent intent;
	private String opinionButtonJson;
	private String auditId;
	private RelativeLayout opinion_area;
	private Button transBtn = null; // 办理按钮按钮
	private Button addsignBtn = null;// 添加加签按钮
	private Button notivrBtn = null;// 添加传阅按钮
	private EditText opinion_editText;// 审核意见文本框
	private int jsonStatus;// 判断是哪个CMD返回的数据
	private static final int OPININON_SAVE = 0;
	private static final int TRANSACTION = 1;
	// ===============手势识别=========================
	private GestureDetector mGestureDetector;
	private static final int FLING_MIN_DISTANCE = 150;
	private static final int FLING_MIN_VELOCITY = 200;
	private RelativeLayout wf_exam_layout;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		LoginTimeOut LoginTimeOut = new LoginTimeOut();
		boolean flag = LoginTimeOut.timeOut(WfExamActivity.this);
		if (!flag) {
			setContentView(R.layout.wf_exam);
			mGestureDetector = new GestureDetector(this);
			intent = getIntent();
			setUpView();
			setUpListeners();
			// 将Activity放到Activity中
			ActivityHoder.getInstance().addActivity(this);
			init();
		}
	}

	private void setUpView() {
		opinion_area = (RelativeLayout) findViewById(R.id.opinion_area);
		title_bt_left = (Button) findViewById(R.id.title_bt_left);
		top_title = (TextView) findViewById(R.id.top_title);
		transBtn = (Button) findViewById(R.id.transBtn);
		addsignBtn = (Button) findViewById(R.id.addsignBtn);
		notivrBtn = (Button) findViewById(R.id.notivrBtn);
		opinion_editText = (EditText) findViewById(R.id.opinion_text);
		wf_exam_layout = (RelativeLayout) findViewById(R.id.wf_exam_layout);
		wf_exam_layout.setOnTouchListener(this);
		wf_exam_layout.setLongClickable(true);
		setBackground();
	}

	private void setUpListeners() {
		// 后退按钮
		title_bt_left.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				finish();
			}
		});
		TextPaint tp = transBtn.getPaint();
		tp.setFakeBoldText(true);
		transBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// 判断是否存在有Button的JSON
				if (!"".equals(opinionButtonJson) && !"[]".equals(opinionButtonJson) && null != opinionButtonJson) {
					if (auditId == null) {
						Toast.makeText(getApplicationContext(), "请添加审核意见", Toast.LENGTH_SHORT).show();
					} else {
						pd = new ProgressDialog(WfExamActivity.this);
						pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
						pd.setCancelable(true);
						pd.setMessage("正在办理...");
						pd.show();
						ProgressDialogHolder.getInstance().addActivity(pd);
						jsonStatus = OPININON_SAVE;
						// 保存审核意见
						opinion_Audit_Save();
					}
				} else {
					pd = new ProgressDialog(WfExamActivity.this);
					pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
					pd.setCancelable(true);
					pd.setMessage("正在办理...");
					pd.show();
					ProgressDialogHolder.getInstance().addActivity(pd);
					// 顺序办理
					jsonStatus = TRANSACTION;
					transaction();
				}
			}
		});
		// 发送加签
		TextPaint tp1 = addsignBtn.getPaint();
		tp1.setFakeBoldText(true);
		addsignBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				intent.setClass(WfExamActivity.this, AddsignActivity.class);
				startActivity(intent);
			}
		});
		// 发送传阅
		TextPaint tp2 = notivrBtn.getPaint();
		tp2.setFakeBoldText(true);
		notivrBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				intent.setClass(WfExamActivity.this, WfAddNoticeActivity.class);
				startActivity(intent);
			}
		});
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

	public void init() {
		String userName = User.getInstance().getUserContext(WfExamActivity.this).getUserName();
		top_title.setText(userName + " - 待办");
		opinionButtonJson = intent.getStringExtra("opinionButtonJson");
		createOpinion_area(opinionButtonJson);
	}

	/**
	 * 创建意见列表区域
	 * 
	 * @param opinionButtonJson
	 */
	private void createOpinion_area(String opinionButtonJson) {
		RadioGroup radiogroup = new RadioGroup(this);
		RelativeLayout.LayoutParams rgp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.FILL_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
		radiogroup.setLayoutParams(rgp);
		if (!"".equals(opinionButtonJson) && !"[]".equals(opinionButtonJson) && null != opinionButtonJson) {
			try {
				JSONArray jsonArray = new JSONArray(opinionButtonJson);
				for (int i = 0; i < jsonArray.length(); i++) {
					JSONObject jsonObj = ((JSONObject) jsonArray.opt(i));
					final RadioButton radio = new RadioButton(this);
					String opinionName = jsonObj.getString("opinionName");
					radio.setText(opinionName);
					radio.setId(jsonObj.getInt("opinionId"));
					radio.setTextAppearance(getApplicationContext(), R.style.wf_exam_button_style);
					radio.setSingleLine();
					radio.setButtonDrawable(R.drawable.wf_detail_operate_bar_button_trans);
					if (jsonArray.length() == 1) {
						radio.setBackgroundResource(R.drawable.wf_detail_subtable_1_1);
					} else {
						if (i == 0) {
							radio.setBackgroundResource(R.drawable.wf_detail_subtable_2_1);
						} else if (i == jsonArray.length() - 1) {
							radio.setBackgroundResource(R.drawable.wf_detail_subtable_2_3);
						} else {
							radio.setBackgroundResource(R.drawable.wf_detail_subtable_2_2);
						}
					}
					RadioGroup.LayoutParams rbp = new RadioGroup.LayoutParams(RadioGroup.LayoutParams.FILL_PARENT, RadioGroup.LayoutParams.WRAP_CONTENT);
					radio.setLayoutParams(rbp);
					radio.setPadding(10, 0, 60, 0);
					int ischeck = jsonObj.getInt("isCheck"); // 判断默认值
					if (ischeck == 1) {
						radio.setChecked(true);
						auditId = String.valueOf(jsonObj.getInt("opinionId"));
					}
					radiogroup.addView(radio);
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
			// 装载审核意见列表
			opinion_area.addView(radiogroup);
			// 装载监听器
			radiogroup.setOnCheckedChangeListener(new OnCheckedChangeListener() {
				public void onCheckedChanged(RadioGroup group, int checkedId) {
					auditId = String.valueOf(checkedId);
				}
			});
		}
	}

	// 顺序办理，将任务添加到Service中
	public void transaction() {
		String sissionId = User.getInstance().getUserContext(WfExamActivity.this).getSessionId();
		String instanceId = intent.getStringExtra("instanceId"); // 获得实例ID
		String taskId = intent.getStringExtra("taskId"); // 获得任务ID
		String meId = intent.getStringExtra("meId"); // 获得数据id
		String formId = intent.getStringExtra("formId");
		HashMap<String, Object> tranHash = new HashMap<String, Object>();
		tranHash.put("id", instanceId);// 获得实例ID
		tranHash.put("task_id", taskId);// 获得任务ID
		tranHash.put("meId", meId); // 获得数据id
		tranHash.put("formId", formId);
		tranHash.put("openstate", "1");
		Task task = new Task(Task.KOA_COMMON, "KOA_Mobile_WF_BindReport_NextStepInfo", sissionId, "WfExamActivity", tranHash);
		// 将任务发送给Service来处理
		MainService.newTask(task);
		// 把自己添加到Activity集合里面
		MainService.addActivity(WfExamActivity.this);
	}

	// 添加意见
	public void opinion_Audit_Save() {
		String sign = "[发自Android]";
		String model = android.os.Build.MODEL; // 手机型号
		if (model.indexOf("MI") == 0) {
			sign = "[发自小米]";
		}
		String sissionId = User.getInstance().getUserContext(WfExamActivity.this).getSessionId();
		String instanceId = intent.getStringExtra("instanceId"); // 获得实例ID
		String taskId = intent.getStringExtra("taskId"); // 获得任务ID
		String meId = intent.getStringExtra("meId"); // 获得数据id
		String opinion = opinion_editText.getText().toString().trim();
		if (opinion != null && !"".equals(opinion.trim())) {
			opinion = opinion + "\n" + sign;
		} else {
			opinion = sign;
		}
		HashMap<String, Object> hashparam = new HashMap<String, Object>();
		hashparam.put("id", instanceId);
		hashparam.put("task_id", taskId);
		hashparam.put("auditId", auditId);
		hashparam.put("meId", meId);
		try {
			hashparam.put("opinion", Base64Encoder.encode(opinion.getBytes("UTF-8")));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		Task task = new Task(Task.KOA_COMMON, "KOA_Mobile_Worklist_Opinion_Audit_Save", sissionId, "WfExamActivity", hashparam);
		// 将任务发送给Service来处理
		MainService.newTask(task);
		// 把自己添加到Activity集合里面
		MainService.addActivity(WfExamActivity.this);
	}

	// 此方法用来处理页面的参数提交后的回调
	public void refresh(Object... params) {
		if (jsonStatus == OPININON_SAVE) {
			String opinionJson = params[0].toString();
			if (opinionJson.equals("true")) {
				// 顺序办理
				jsonStatus = TRANSACTION;
				transaction();
			} else if (opinionJson.equals("TIMEOUT")) {
				Toast.makeText(WfExamActivity.this, "加载超时,请稍后再试", Toast.LENGTH_SHORT).show();
			} else if (opinionJson.equals("SESSION_TIMEOUT")) {
				HttpResponseUtil.getInstance().reLogin(WfExamActivity.this);
			} else if (opinionJson.equals("NETWORK_ERROR")) {
				Toast.makeText(WfExamActivity.this, "网络连接异常，请稍后再试", Toast.LENGTH_SHORT).show();
			} else {
				ProgressDialogHolder.getInstance().exit();
				Toast.makeText(WfExamActivity.this, "审核意见添加失败", Toast.LENGTH_SHORT).show();
			}
		} else if (jsonStatus == TRANSACTION) {
			ProgressDialogHolder.getInstance().exit();
			String json = params[0].toString();
			if (json == null || "".equals(json)) {
				Toast.makeText(WfExamActivity.this, "返回值异常，请联系管理员", Toast.LENGTH_SHORT).show();
			} else if (json.equals("TIMEOUT")) {
				Toast.makeText(WfExamActivity.this, "加载超时,请稍后再试", Toast.LENGTH_SHORT).show();
			} else if (json.equals("SESSION_TIMEOUT")) {
				HttpResponseUtil.getInstance().reLogin(WfExamActivity.this);
			} else if (json.equals("NETWORK_ERROR")) {
				Toast.makeText(WfExamActivity.this, "网络连接异常，请稍后再试", Toast.LENGTH_SHORT).show();
			} else if (json.contains("error")) {
				Toast.makeText(WfExamActivity.this, "找不到办理人，请联系管理员", Toast.LENGTH_SHORT).show();
			} else {
				WFRunModel model = JsonUtil.parseRunJson(json);
				System.out.println(json);
				// 判断流程办理动作
				if (model.getTaskStatus() == WFRunModel.TASK_TYPE_ERROR) {
					Toast.makeText(WfExamActivity.this, "找不到办理人，请联系管理员", Toast.LENGTH_SHORT).show();
				} else if (model.getTaskStatus() == WFRunModel.TASK_TYPE_ARCHIVES) {
					Toast.makeText(WfExamActivity.this, "流程流转完毕，已归档", Toast.LENGTH_SHORT).show();
					intent.setClass(WfExamActivity.this, TodoListAllActivity.class);
					startActivity(intent);
				} else if (model.getTaskStatus() == WFRunModel.TASK_TYPE_NOFIND_SENDER) {
					Toast.makeText(WfExamActivity.this, "系统未找到下一个办理人，请联系管理员", Toast.LENGTH_SHORT).show();
				} else if (model.getTaskStatus() == WFRunModel.TASK_TYPE_NOFIND_TASK_END) {
					Toast.makeText(WfExamActivity.this, "当前任务已办理完毕!", Toast.LENGTH_SHORT).show();
					intent.setClass(WfExamActivity.this, TodoListAllActivity.class);
					startActivity(intent);
				} else { // 顺序办理
					Intent wfIntent = new Intent(WfExamActivity.this, SendNextActivity.class);
					wfIntent.putExtra("WFRunModel", model);
					wfIntent.putExtra("instanceId", intent.getStringExtra("instanceId"));
					wfIntent.putExtra("taskId", intent.getStringExtra("taskId"));
					wfIntent.putExtra("flowTitle", intent.getStringExtra("flowTitle"));
					startActivity(wfIntent);
				}
			}
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
