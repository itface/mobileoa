package koa.android.demo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import koa.android.demo.model.User;
import koa.android.logic.ActivityHoder;
import koa.android.logic.MainService;
import koa.android.logic.ProgressDialogHolder;
import koa.android.logic.WfMainAdapter;
import koa.android.tools.HttpResponseUtil;
import koa.android.tools.LoginTimeOut;
import koa.android.tools.SetBackGround;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Shader.TileMode;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.view.Display;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

/**
 * 主页
 * 
 * @author chenM
 * 
 */
public class MainActivity extends Activity {
	private LinearLayout mainView;
	private ImageView ui_main_imageView;
	private GridView gridview;
	private Button title_bt_left;
	private TextView top_title;
	private Handler handler;
	private String count;// 待办流程个数
	private String sysmsg_count;// 系统消息个数
	private String userName;// 用户姓名
	private String loginInfo;// 登录信息
	private ExecutorService executorService;
	private static int THREADPOOL_SIZE = 4;// 线程池的大小
	private boolean dataReturnFlag = false;// 服务器是否返回数据标志位
	private ProgressDialog pd;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		LoginTimeOut LoginTimeOut = new LoginTimeOut();
		boolean flag = LoginTimeOut.timeOut(MainActivity.this);
		if (!flag) {
			setContentView(R.layout.main);
			// 将Activity放到Activity中
			ActivityHoder.getInstance().addActivity(this);
			setUpView();
			setUpLisners();
			init();
			handler = new GetMainHandler();
			executorService.submit(new GeMainThread());// 耗时操作,开启一个新线程获取数据
		}
	}

	private void init() {
		String userName = User.getInstance().getUserContext(MainActivity.this).getUserName();
		if (null != userName && !"".equals(userName)) {
			top_title.setText(userName);
		}
		// 预装载功能列表
		loadGridView("", "");
	}

	// 设置背景图
	private void setBackground() {
		Uri BG_URI = UISettingActivity.getMainBG_URI(MainActivity.this);
		String bg_uri = BG_URI.toString();// 图片uri
		if (bg_uri.contains("content://")) {
			// 自定义背景
			gridview.setBackgroundDrawable(null);
			top_title.setBackgroundDrawable(null);
			Display display = getWindowManager().getDefaultDisplay();
			int width = display.getWidth();// 屏幕宽度
			int height = display.getHeight();// 屏幕高度
			String[] proj = { MediaStore.Images.Media.DATA };
			Cursor actualimagecursor = managedQuery(BG_URI, proj, null, null, null);
			int actual_image_column_index = actualimagecursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
			actualimagecursor.moveToFirst();
			String img_path = actualimagecursor.getString(actual_image_column_index);
			SetBackGround.getInstance().setBackgroundImg(MainActivity.this, mainView, "gridview", R.drawable.ui_login_bg_bule, TileMode.REPEAT, width, height, img_path);
		} else {
			// 原主题背景
			ui_main_imageView.setVisibility(View.GONE);// 隐藏imageView
			SetBackGround.getInstance().setGridviewBG(MainActivity.this, gridview, "gridview", R.drawable.ui_login_bg_bule, TileMode.REPEAT);
			SetBackGround.getInstance().setTheme(MainActivity.this, top_title, "top_title", R.drawable.ui_title_bg_blue, TileMode.REPEAT);
		}
	}

	@Override
	protected void onRestart() {
		super.onRestart();
		setBackground();
	}

	// 初始化UI组件
	public void setUpView() {
		mainView = (LinearLayout) findViewById(R.id.mainView);
		ui_main_imageView = (ImageView) findViewById(R.id.ui_main_imageView);
		title_bt_left = (Button) findViewById(R.id.title_bt_left);
		top_title = (TextView) findViewById(R.id.top_title);
		title_bt_left.setVisibility(View.GONE);
		gridview = (GridView) findViewById(R.id.ui_main_gridView);
		executorService = Executors.newFixedThreadPool(THREADPOOL_SIZE);
		setBackground();
	}

	// 定义组件的监听器
	public void setUpLisners() {
		gridview.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				if (!dataReturnFlag) {
					Toast.makeText(MainActivity.this, "请等待数据加载", Toast.LENGTH_SHORT).show();
				} else {
					Intent intent = new Intent();
					switch (position) {
					case 0:
						intent.setClass(MainActivity.this, TodoListAllActivity.class);
						startActivity(intent);
						finish();
						break;
					case 1:
						intent.setClass(MainActivity.this, TodoListActivity.class);
						startActivity(intent);
						finish();
						break;
					case 2:
						intent.setClass(MainActivity.this, HistoryListActivity.class);
						startActivity(intent);
						finish();
						break;
					case 3:
						intent.setClass(MainActivity.this, noticeListActivity.class);
						startActivity(intent);
						finish();
						break;
					case 4:
						intent.setClass(MainActivity.this, UISettingFrameActivity.class);
						startActivity(intent);
						finish();
						break;
					default:
						break;
					}
				}
			}
		});
	}

	/**
	 * 装载功能列表
	 * 
	 * @param count
	 *            流程个数
	 * @param sysmsg_count
	 *            系统消息个数
	 */
	public void loadGridView(String count, String sysmsg_count) {
		ArrayList<HashMap<String, Object>> menuList = new ArrayList<HashMap<String, Object>>();
		HashMap<String, Object> map1 = new HashMap<String, Object>();
		HashMap<String, Object> map2 = new HashMap<String, Object>();
		HashMap<String, Object> map3 = new HashMap<String, Object>();
		HashMap<String, Object> map4 = new HashMap<String, Object>();
		HashMap<String, Object> map5 = new HashMap<String, Object>();
		map1.put("ItemImage", R.drawable.ui_main_icon_all);
		map1.put("ItemText", "待办流程" + count);
		menuList.add(map1);
		map2.put("ItemImage", R.drawable.ui_main_icon_todo);
		map2.put("ItemText", "在办未结");
		menuList.add(map2);
		map3.put("ItemImage", R.drawable.ui_main_icon_history);
		map3.put("ItemText", "已办流程");
		menuList.add(map3);
		map4.put("ItemImage", R.drawable.ui_main_icon_notice);
		map4.put("ItemText", "通知传阅");
		menuList.add(map4);
		map5.put("ItemImage", R.drawable.ui_main_icon_setting);
		map5.put("ItemText", "设置");
		menuList.add(map5);
		WfMainAdapter saMenuItem = new WfMainAdapter(MainActivity.this, menuList);
		gridview.setAdapter(saMenuItem);
	}

	class GetMainHandler extends Handler {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			ProgressDialogHolder.getInstance().exit();
			dataReturnFlag = true;
			if (loginInfo == null || loginInfo.equals("")) {
				Toast.makeText(MainActivity.this, "加载超时,请稍后再试", Toast.LENGTH_SHORT).show();
			} else if (loginInfo.equals("TIMEOUT")) {
				Toast.makeText(MainActivity.this, "加载超时,请稍后再试", Toast.LENGTH_SHORT).show();
			} else if (loginInfo.equals("SESSION_TIMEOUT")) {
				HttpResponseUtil.getInstance().reLogin(MainActivity.this);
			} else if (loginInfo.equals("NETWORK_ERROR")) {
				Toast.makeText(MainActivity.this, "网络连接异常，请稍后再试", Toast.LENGTH_SHORT).show();
			} else {
				parseJson(loginInfo);
				// 装载功能列表
				loadGridView(count, sysmsg_count);
				top_title.setText(userName);
			}
		}
	}

	public void parseJson(String loginInfo) {
		try {
			loginInfo = loginInfo.substring(1, loginInfo.length() - 1);
			JSONObject jsonObject = new JSONObject(loginInfo);
			count = "(" + jsonObject.getString("flowCount") + ")";

			// 将待办个数设置到Service中
			MainService.setCurWfTodoNum(jsonObject.getString("flowCount"));

			sysmsg_count = "(" + jsonObject.getString("newsCount") + ")";
			userName = jsonObject.getString("loginUserName");
			User user = User.getInstance().getUserContext(MainActivity.this);
			user.setUserName(userName);
			User.getInstance().setUserContext(user, MainActivity.this);
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	class GeMainThread implements Runnable {
		@Override
		public void run() {
			refreshGridView();
			Message msg = handler.obtainMessage();// 通知UI线程来处理范围的数据
			handler.sendMessage(msg);
		}
	}

	// 获得刷新列表的参数
	public void refreshGridView() {
		String sissionId = User.getInstance().getUserContext(MainActivity.this).getSessionId();
		loginInfo = HttpResponseUtil.getInstance().getGetHttpInfo("KOA_Mobile_Login_After_Info", sissionId, null, MainActivity.this);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			MainService.promptExit(this);
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(1, 1, 0, "刷新").setIcon(R.drawable.ui_menu_refresh);
		menu.add(1, 2, 1, "退出").setIcon(R.drawable.ui_menu_loginout);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		switch (item.getItemId()) {
		case 1:// 刷新
			pd = new ProgressDialog(MainActivity.this);
			pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			pd.setCancelable(true);
			pd.setMessage("正在加载列表...");
			pd.show();
			ProgressDialogHolder.getInstance().addActivity(pd);
			executorService.submit(new GeMainThread());
			break;
		case 2:// 退出
			MainService.promptExit(this);
			break;
		}
		return super.onMenuItemSelected(featureId, item);
	}
}