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
public class WfCancelActivity extends Activity implements KoaActivity, OnGestureListener, OnTouchListener {
	private Intent intent;
	private TextView top_title;
	private Button title_bt_left;// 回退按钮
	private Button cancelBtn = null;// 收回按钮
	private String fromActivity;// 来源页面
	private ProgressDialog pd;
	private EditText opinion_text;
	// ===============手势识别=========================
	private GestureDetector mGestureDetector;
	private static final int FLING_MIN_DISTANCE = 150;
	private static final int FLING_MIN_VELOCITY = 200;
	private RelativeLayout wf_cancel_layout;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		LoginTimeOut LoginTimeOut = new LoginTimeOut();
		boolean flag = LoginTimeOut.timeOut(WfCancelActivity.this);
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
		cancelBtn = (Button) findViewById(R.id.cancelActionBtn);
		top_title = (TextView) findViewById(R.id.top_title);
		title_bt_left = (Button) findViewById(R.id.title_bt_left);
		wf_cancel_layout = (RelativeLayout) findViewById(R.id.wf_cancel_layout);
		wf_cancel_layout.setOnTouchListener(this);
		wf_cancel_layout.setLongClickable(true);
		setBackground();
	}

	public void init() {
		fromActivity = intent.getStringExtra("fromActivity");
		String userName = User.getInstance().getUserContext(WfCancelActivity.this).getUserName();
		if (fromActivity.equals("TodoListActivity")) {
			top_title.setText(userName + " - 在办");
		} else if (fromActivity.equals("HistoryListActivity")) {
			top_title.setText(userName + " - 已办");
		}
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
		// 收回按钮
		TextPaint tp = cancelBtn.getPaint();
		tp.setFakeBoldText(true);
		cancelBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				promptCancel(WfCancelActivity.this);
			}
		});
	}

	// 确认办理
	public void promptCancel(Context con) {
		// 创建对话框
		LayoutInflater li = LayoutInflater.from(con);
		View cancelV = li.inflate(R.layout.addsigndialog, null);
		TextView confirmDownloadWord = (TextView) cancelV.findViewById(R.id.exit_confirm_word);
		confirmDownloadWord.setText("确认收回");
		AlertDialog.Builder ab = new AlertDialog.Builder(con);
		ab.setView(cancelV);// 设定对话框显示的View对象
		ab.setPositiveButton("确认", new android.content.DialogInterface.OnClickListener() {
			public void onClick(DialogInterface arg0, int arg1) {
				cancelWf();
			}
		});
		ab.setNegativeButton("取消", null);
		// 显示对话框
		ab.show();
	}

	// 流程收回
	private void cancelWf() {
		String cancel_opi = opinion_text.getText().toString();
		if (cancel_opi.equals("")) {
			Toast.makeText(WfCancelActivity.this, "请输入收回原因", Toast.LENGTH_SHORT).show();
		} else {
			pd = new ProgressDialog(WfCancelActivity.this);
			pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			pd.setCancelable(true);
			pd.setMessage("正在发送...");
			pd.show();
			ProgressDialogHolder.getInstance().addActivity(pd);
			String sissionId = User.getInstance().getUserContext(WfCancelActivity.this).getSessionId();
			String instanceId = intent.getStringExtra("instanceId"); // 获得实例ID
			String taskId = intent.getStringExtra("taskId"); // 获得任务ID
			HashMap<String, Object> paramHash = new HashMap<String, Object>();
			paramHash.put("id", instanceId);// 获得实例ID
			paramHash.put("taskId", taskId);// 获得任务ID
			try {
				paramHash.put("opinion", Base64Encoder.encode(cancel_opi.getBytes("UTF-8")));
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
			Task task = new Task(Task.KOA_COMMON, "KOA_Mobile_WF_Undo_MyFinishTask", sissionId, "WfCancelActivity", paramHash);
			// 将任务发送给Service来处理
			MainService.newTask(task);
			// 把自己添加到Activity集合里面
			MainService.addActivity(WfCancelActivity.this);
		}
	}

	// 此方法用来处理页面的参数提交后的回调
	public void refresh(Object... params) {
		ProgressDialogHolder.getInstance().exit();
		String readResult = params[0].toString();
		if (readResult == null || "".equals(readResult)) {
			Toast.makeText(WfCancelActivity.this, "返回值异常", Toast.LENGTH_SHORT).show();
		} else if (readResult.equals("TIMEOUT")) {
			Toast.makeText(WfCancelActivity.this, "加载超时,请稍后再试", Toast.LENGTH_SHORT).show();
		} else if (readResult.equals("SESSION_TIMEOUT")) {
			HttpResponseUtil.getInstance().reLogin(WfCancelActivity.this);
		} else if (readResult.equals("NETWORK_ERROR")) {
			Toast.makeText(WfCancelActivity.this, "网络连接异常，请稍后再试", Toast.LENGTH_SHORT).show();
		} else if (readResult.startsWith("1")) {
			Toast.makeText(WfCancelActivity.this, "收回成功", Toast.LENGTH_SHORT).show();
			Intent jumpIntent = new Intent();
			if (fromActivity.equals("TodoListActivity")) {
				jumpIntent.setClass(WfCancelActivity.this, TodoListActivity.class);
			} else if (fromActivity.equals("HistoryListActivity")) {
				jumpIntent.setClass(WfCancelActivity.this, HistoryListActivity.class);
			}
			startActivity(jumpIntent);
			finish();
		} else if (readResult.startsWith("-999")) {
			Toast.makeText(WfCancelActivity.this, "数据库出错", Toast.LENGTH_SHORT).show();
		} else {
			Toast.makeText(WfCancelActivity.this, "收回失败", Toast.LENGTH_SHORT).show();
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