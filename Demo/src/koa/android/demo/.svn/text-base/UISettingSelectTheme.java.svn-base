package koa.android.demo;

import koa.android.logic.ActivityHoder;
import koa.android.tools.LoginTimeOut;
import koa.android.tools.SetBackGround;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class UISettingSelectTheme extends Activity implements OnGestureListener, OnTouchListener {
	private Button title_bt_left;
	private TextView top_title;
	private ImageView ui_setting_select_theme_thumbnail_blue;
	private ImageView ui_setting_select_theme_thumbnail_orange;
	private ImageView ui_setting_select_theme_thumbnail_black;
	// ===============手势识别=========================
	private GestureDetector mGestureDetector;
	private static final int FLING_MIN_DISTANCE = 150;
	private static final int FLING_MIN_VELOCITY = 200;
	private LinearLayout ui_setting_theme_layout;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		LoginTimeOut LoginTimeOut = new LoginTimeOut();
		boolean flag = LoginTimeOut.timeOut(UISettingSelectTheme.this);
		if (!flag) {
			mGestureDetector = new GestureDetector(this);
			setContentView(R.layout.ui_setting_select_theme);
			// 将Activity放到Activity中
			ActivityHoder.getInstance().addActivity(this);
			// 初始化UI组件
			setUpView();
			// 增加监听器
			setUpLisners();
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
		ui_setting_select_theme_thumbnail_blue = (ImageView) findViewById(R.id.ui_setting_select_theme_thumbnail_blue);
		ui_setting_select_theme_thumbnail_orange = (ImageView) findViewById(R.id.ui_setting_select_theme_thumbnail_orange);
		ui_setting_select_theme_thumbnail_black = (ImageView) findViewById(R.id.ui_setting_select_theme_thumbnail_black);
		setBackground();
		ui_setting_theme_layout = (LinearLayout) findViewById(R.id.ui_setting_theme_layout);
		ui_setting_theme_layout.setOnTouchListener(this);
		ui_setting_theme_layout.setLongClickable(true);
	}

	private void setUpLisners() {
		// 后退按钮
		title_bt_left.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				finish();
			}
		});
		ui_setting_select_theme_thumbnail_blue.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = getIntent();
				intent.putExtra("theme", "blue");
				UISettingSelectTheme.this.setResult(RESULT_OK, intent);
				UISettingSelectTheme.this.finish();
			}
		});
		ui_setting_select_theme_thumbnail_orange.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = getIntent();
				intent.putExtra("theme", "orange");
				UISettingSelectTheme.this.setResult(RESULT_OK, intent);
				UISettingSelectTheme.this.finish();
			}
		});
		ui_setting_select_theme_thumbnail_black.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = getIntent();
				intent.putExtra("theme", "black");
				UISettingSelectTheme.this.setResult(RESULT_OK, intent);
				UISettingSelectTheme.this.finish();
			}
		});
	}

	private void init() {
		top_title.setText("更换主题");
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
