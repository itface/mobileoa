package koa.android.demo;

import koa.android.logic.ActivityHoder;
import koa.android.tools.LoginTimeOut;
import koa.android.tools.SetBackGround;
import android.app.Activity;
import android.app.LocalActivityManager;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.TextView;

/**
 * 设置Activity的外框架
 * 
 * @author chenM
 * 
 */
public class UISettingFrameActivity extends Activity {
	protected LocalActivityManager mLocalActivityManager;
	private FrameLayout mBoday;
	private Button title_bt_left;
	private TextView top_title;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		LoginTimeOut LoginTimeOut = new LoginTimeOut();
		boolean flag = LoginTimeOut.timeOut(UISettingFrameActivity.this);
		if (!flag) {
			setContentView(R.layout.ui_setting_frame);
			// 将Activity放到Activity中
			ActivityHoder.getInstance().addActivity(this);
			// 初始化UI组件
			setUpView();
			// 增加监听器
			setUpLisners();
			mBoday = (FrameLayout) findViewById(R.id.frame);
			mLocalActivityManager = new LocalActivityManager(this, true);
			mLocalActivityManager.dispatchCreate(null);
			Intent intent = new Intent();
			intent.setClass(UISettingFrameActivity.this, UISettingActivity.class);
			View v = mLocalActivityManager.startActivity("one", intent).getDecorView();
			mBoday.removeAllViews();
			mBoday.addView(v);
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

	private void setUpView() {
		title_bt_left = (Button) findViewById(R.id.title_bt_left);
		top_title = (TextView) findViewById(R.id.top_title);
		setBackground();
	}

	private void setUpLisners() {
		// 后退按钮
		title_bt_left.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.setClass(UISettingFrameActivity.this, MainActivity.class);
				UISettingFrameActivity.this.startActivity(intent);
			}
		});
	}

	private void init() {
		top_title.setText("设置");
	}

	/**
	 * 接受选择图片资源后的回调
	 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// 获取当前活动的Activity实例
		Activity subActivity = mLocalActivityManager.getCurrentActivity();
		// 判断是否实现返回值接口
		if (subActivity instanceof OnFrameActivityResultListener) {
			// 获取返回值接口实例
			OnFrameActivityResultListener listener = (OnFrameActivityResultListener) subActivity;
			// 转发请求到子Activity
			listener.onFrameActivityResult(requestCode, resultCode, data);
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	/**
	 * 定义接口给子Activity来回调
	 * 
	 * @author chenM
	 * 
	 */
	public interface OnFrameActivityResultListener {
		public void onFrameActivityResult(int requestCode, int resultCode, Intent data);
	}

}
