package koa.android.demo;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;

import koa.android.demo.model.Task;
import koa.android.demo.model.User;
import koa.android.logic.ActivityHoder;
import koa.android.logic.MainService;
import koa.android.logic.ProgressDialogHolder;
import koa.android.tools.Base64Encoder;
import koa.android.tools.HttpResponseUtil;
import koa.android.tools.LoginTimeOut;
import koa.android.tools.SetBackGround;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextPaint;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

/**
 * 流程收回
 * 
 * @author chenM
 * 
 */
public class WfAddsignOpinionActivity extends Activity implements KoaActivity, OnGestureListener, OnTouchListener {
	private Intent intent;
	private TextView top_title;
	private Button title_bt_left;// 回退按钮
	private Button addSignBtn = null;// 加签完毕按钮
	private ProgressDialog pd;
	private EditText opinion_text;
	private int jsonStatus;// 判断是哪个CMD返回的数据
	private static final int OPININON_SAVE = 0;
	private static final int TRANSACTION = 1;
	// ===============手势识别=========================
	private GestureDetector mGestureDetector;
	private static final int FLING_MIN_DISTANCE = 150;
	private static final int FLING_MIN_VELOCITY = 200;
	private RelativeLayout wf_cancel_layout;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		LoginTimeOut LoginTimeOut = new LoginTimeOut();
		boolean flag = LoginTimeOut.timeOut(WfAddsignOpinionActivity.this);
		if (!flag) {
			setContentView(R.layout.wf_cancel);
			mGestureDetector = new GestureDetector(this);
			intent = getIntent();
			setUpView();
			setUpListeners();
			// 将Activity放到Activity中
			ActivityHoder.getInstance().addActivity(this);
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

	// 初始化所有的UI组件
	public void setUpView() {
		opinion_text = (EditText) findViewById(R.id.opinion_text);
		opinion_text.setHint("意见留言...");
		top_title = (TextView) findViewById(R.id.top_title);
		title_bt_left = (Button) findViewById(R.id.title_bt_left);
		addSignBtn = (Button) findViewById(R.id.cancelActionBtn);
		addSignBtn.setText("加签完毕");
		wf_cancel_layout = (RelativeLayout) findViewById(R.id.wf_cancel_layout);
		wf_cancel_layout.setOnTouchListener(this);
		wf_cancel_layout.setLongClickable(true);
		setBackground();
	}

	public void init() {
		String userName = User.getInstance().getUserContext(WfAddsignOpinionActivity.this).getUserName();
		top_title.setText(userName + " - 待办");
		new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {
				InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
			}
		}, 100);
	}

	// 定义组件监听器
	public void setUpListeners() {
		// 后退按钮
		title_bt_left.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				finish();
			}
		});
		// 加签按钮
		TextPaint tp = addSignBtn.getPaint();
		tp.setFakeBoldText(true);
		addSignBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				promptAddSign(WfAddsignOpinionActivity.this);
			}
		});
	}

	// 确认加签按钮
	private void promptAddSign(Context con) {
		// 创建对话框
		LayoutInflater li = LayoutInflater.from(con);
		View exitV = li.inflate(R.layout.addsigndialog, null);
		AlertDialog.Builder ab = new AlertDialog.Builder(con);
		ab.setView(exitV);// 设定对话框显示的View对象
		ab.setPositiveButton(R.string.string_wf_addsign_confirm, new android.content.DialogInterface.OnClickListener() {
			public void onClick(DialogInterface arg0, int arg1) {
				opinion_Audit_Save();
			}
		});
		ab.setNegativeButton(R.string.cancel, null);
		// 显示对话框
		ab.show();
	}

	// 保存加签意见
	public void opinion_Audit_Save() {
		String opinion = opinion_text.getText().toString();
		if (null == opinion || "".equals(opinion)) {
			Toast.makeText(WfAddsignOpinionActivity.this, "请添加审核意见", Toast.LENGTH_LONG).show();
		} else {
			pd = new ProgressDialog(WfAddsignOpinionActivity.this);
			pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			pd.setCancelable(true);
			pd.setMessage("正在办理...");
			pd.show();
			ProgressDialogHolder.getInstance().addActivity(pd);
			String sissionId = User.getInstance().getUserContext(WfAddsignOpinionActivity.this).getSessionId();
			String instanceId = intent.getStringExtra("instanceId"); // 获得实例ID
			String taskId = intent.getStringExtra("taskId"); // 获得任务ID
			String meId = intent.getStringExtra("meId"); // 获得数据id
			HashMap<String, Object> hashparam = new HashMap<String, Object>();
			hashparam.put("id", instanceId);
			hashparam.put("task_id", taskId);
			hashparam.put("meId", meId);
			try {
				hashparam.put("opinion", Base64Encoder.encode(opinion.getBytes("UTF-8")));
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
			Task task = new Task(Task.KOA_COMMON, "KOA_Mobile_Worklist_Opinion_Audit_Save", sissionId, "WfAddsignOpinionActivity", hashparam);
			// 将任务发送给Service来处理
			MainService.newTask(task);
			// 把自己添加到Activity集合里面
			MainService.addActivity(WfAddsignOpinionActivity.this);
		}
	}

	@Override
	public void refresh(Object... params) {
		if (jsonStatus == OPININON_SAVE) {
			String opinionJson = params[0].toString();
			if (opinionJson.equals("true")) {
				// 顺序办理
				jsonStatus = TRANSACTION;
				signOK();
			} else if (opinionJson.equals("TIMEOUT")) {
				Toast.makeText(WfAddsignOpinionActivity.this, "加载超时,请稍后再试", Toast.LENGTH_SHORT).show();
			} else if (opinionJson.equals("SESSION_TIMEOUT")) {
				HttpResponseUtil.getInstance().reLogin(WfAddsignOpinionActivity.this);
			} else if (opinionJson.equals("NETWORK_ERROR")) {
				Toast.makeText(WfAddsignOpinionActivity.this, "网络连接异常，请稍后再试", Toast.LENGTH_SHORT).show();
			} else {
				ProgressDialogHolder.getInstance().exit();
				Toast.makeText(getApplicationContext(), "审核意见添加失败", Toast.LENGTH_SHORT).show();
			}
		} else if (jsonStatus == TRANSACTION) {
			String readResult = params[0].toString();
			ProgressDialogHolder.getInstance().exit();
			if (readResult == null || "".equals(readResult)) {
				Toast.makeText(getApplicationContext(), "返回值异常", Toast.LENGTH_SHORT).show();
			} else if (readResult.equals("TIMEOUT")) {
				Toast.makeText(WfAddsignOpinionActivity.this, "加载超时,请稍后再试", Toast.LENGTH_SHORT).show();
			} else if (readResult.equals("SESSION_TIMEOUT")) {
				HttpResponseUtil.getInstance().reLogin(WfAddsignOpinionActivity.this);
			} else if (readResult.equals("NETWORK_ERROR")) {
				Toast.makeText(WfAddsignOpinionActivity.this, "网络连接异常，请稍后再试", Toast.LENGTH_SHORT).show();
			} else {
				Toast.makeText(getApplicationContext(), readResult, Toast.LENGTH_SHORT).show();
				intent.setClass(WfAddsignOpinionActivity.this, TodoListAllActivity.class);
				startActivity(intent);
				finish();
			}
		}
	}

	// 加签完毕
	public void signOK() {
		jsonStatus = TRANSACTION;
		String sissionId = User.getInstance().getUserContext(WfAddsignOpinionActivity.this).getSessionId();
		String instanceId = intent.getStringExtra("instanceId"); // 获得实例ID
		String taskId = intent.getStringExtra("taskId"); // 获得任务ID
		HashMap<String, Object> hashParam = new HashMap<String, Object>();
		hashParam.put("id", instanceId);
		hashParam.put("taskId", taskId);// 获得任务ID
		Task task = new Task(Task.KOA_COMMON, "KOA_Mobile_WorkFlow_Execute_Worklist_AddParticipants_Ok", sissionId, "WfAddsignOpinionActivity", hashParam);
		// 将任务发送给Service来处理
		MainService.newTask(task);
		// 把自己添加到Activity集合里面
		MainService.addActivity(WfAddsignOpinionActivity.this);
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