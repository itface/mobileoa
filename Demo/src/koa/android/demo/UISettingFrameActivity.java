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
 * ����Activity������
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
			// ��Activity�ŵ�Activity��
			ActivityHoder.getInstance().addActivity(this);
			// ��ʼ��UI���
			setUpView();
			// ���Ӽ�����
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

	// ���ñ���ͼ
	private void setBackground() {
		// ���ñ���ͼ
		SetBackGround.getInstance().setTheme(this, top_title, "top_title", R.drawable.ui_title_bg_blue, null);
		SetBackGround.getInstance().setTheme(this, title_bt_left, "ui_backbtn", R.drawable.ui_title_back_btn_blue, null);
	}

	private void setUpView() {
		title_bt_left = (Button) findViewById(R.id.title_bt_left);
		top_title = (TextView) findViewById(R.id.top_title);
		setBackground();
	}

	private void setUpLisners() {
		// ���˰�ť
		title_bt_left.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.setClass(UISettingFrameActivity.this, MainActivity.class);
				UISettingFrameActivity.this.startActivity(intent);
			}
		});
	}

	private void init() {
		top_title.setText("����");
	}

	/**
	 * ����ѡ��ͼƬ��Դ��Ļص�
	 */
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// ��ȡ��ǰ���Activityʵ��
		Activity subActivity = mLocalActivityManager.getCurrentActivity();
		// �ж��Ƿ�ʵ�ַ���ֵ�ӿ�
		if (subActivity instanceof OnFrameActivityResultListener) {
			// ��ȡ����ֵ�ӿ�ʵ��
			OnFrameActivityResultListener listener = (OnFrameActivityResultListener) subActivity;
			// ת��������Activity
			listener.onFrameActivityResult(requestCode, resultCode, data);
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	/**
	 * ����ӿڸ���Activity���ص�
	 * 
	 * @author chenM
	 * 
	 */
	public interface OnFrameActivityResultListener {
		public void onFrameActivityResult(int requestCode, int resultCode, Intent data);
	}

}
