package koa.android.demo;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import koa.android.demo.UISettingFrameActivity.OnFrameActivityResultListener;
import koa.android.demo.model.User;
import koa.android.logic.CheckUser;
import koa.android.logic.MainService;
import koa.android.logic.ProgressDialogHolder;
import koa.android.tools.HttpResponseUtil;
import koa.android.tools.JsonUtil;
import koa.android.tools.LoginTimeOut;
import android.app.AlertDialog;
import android.app.DownloadManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.view.KeyEvent;
import android.widget.Toast;

/**
 * 设置页面
 * 
 * @author chenM
 * 
 */
public class UISettingActivity extends PreferenceActivity implements OnFrameActivityResultListener {
	public static final String PUSH_MSG_KEY = "settings_pushMsg";
	public static final boolean PUSH_MSG_DEFAULT = true;
	public static final String SELECT_MAIN_BG_KEY = "settings_select_main_bg";
	public static final String SELECT_MAIN_BG_DEFAULT = String.valueOf(R.drawable.ui_login_bg_bule);
	public static final String SELECT_MAIN_THEME_KEY = "settings_select_theme";
	public static final String SELECT_MAIN_THEME_DEFAULT = "blue";
	public static final String CHECK_FOR_NEW_VERSION = "settings_check_for_new_version";
	public static final int REQUEST_CODE_BG = 1;
	public static final int REQUEST_CODE_THEME = 2;
	private CheckBoxPreference pushMsg;
	private Preference settings_select_main_bg;
	private Preference settings_select_theme;
	private Preference settings_check_for_new_version;
	private ExecutorService executorService;
	private UpdateHandler updateHandler;
	private static int THREADPOOL_SIZE = 4;// 线程池的大小
	private String upDateJson;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		LoginTimeOut LoginTimeOut = new LoginTimeOut();
		boolean flag = LoginTimeOut.timeOut(UISettingActivity.this);
		if (!flag) {
			addPreferencesFromResource(R.xml.ui_setting);
			setUpView();
			setUpListener();
		}
	}

	private void setUpView() {
		pushMsg = (CheckBoxPreference) findPreference(PUSH_MSG_KEY);
		settings_select_main_bg = (Preference) findPreference(SELECT_MAIN_BG_KEY);
		settings_select_theme = (Preference) findPreference(SELECT_MAIN_THEME_KEY);
		settings_check_for_new_version = (Preference) findPreference(CHECK_FOR_NEW_VERSION);
		executorService = Executors.newFixedThreadPool(THREADPOOL_SIZE);
	}

	private void setUpListener() {
		pushMsg.setOnPreferenceChangeListener(new OnPreferenceChangeListener() {
			@Override
			public boolean onPreferenceChange(Preference preference, Object newValue) {
				// 设置推送
				MainService.setPushRun(Boolean.parseBoolean(newValue.toString()));
				return true;
			}
		});
		settings_select_main_bg.setOnPreferenceClickListener(new OnPreferenceClickListener() {
			@Override
			public boolean onPreferenceClick(Preference preference) {
				Intent intent = new Intent();
				intent.setType("image/*");
				intent.setAction(Intent.ACTION_GET_CONTENT);
				// 由于子Activity无法响应onActivityResult方法,只能调用父Activity的选择资源方法
				getParent().startActivityForResult(intent, REQUEST_CODE_BG);
				return true;
			}
		});
		settings_select_theme.setOnPreferenceClickListener(new OnPreferenceClickListener() {

			@Override
			public boolean onPreferenceClick(Preference preference) {
				Intent intent = new Intent();
				intent.setClass(UISettingActivity.this, UISettingSelectTheme.class);
				// 由于子Activity无法响应onActivityResult方法,只能调用父Activity的选择资源方法
				getParent().startActivityForResult(intent, REQUEST_CODE_THEME);
				return true;
			}
		});
		CheckUser checkUser = new CheckUser();
		String version = checkUser.getAppVersion(UISettingActivity.this);// 客户端版本号
		settings_check_for_new_version.setTitle("检查新版本");
		settings_check_for_new_version.setSummary("当前版本:KOA-AM,v" + version);
		settings_check_for_new_version.setOnPreferenceClickListener(new OnPreferenceClickListener() {
			@Override
			public boolean onPreferenceClick(Preference preference) {
				Toast.makeText(UISettingActivity.this, "检测新版本,请稍后", Toast.LENGTH_SHORT).show();
				checkForUpDate();
				return true;
			}
		});
	}

	/**
	 * 响应父Activity的回调
	 */
	@Override
	public void onFrameActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == REQUEST_CODE_BG && resultCode == RESULT_OK) {
			Uri uri = data.getData();
			setMainBG_URI(UISettingActivity.this, uri.toString());
			Toast.makeText(UISettingActivity.this, "更换背景图成功", Toast.LENGTH_SHORT).show();
			Intent intent = new Intent();
			intent.setClass(UISettingActivity.this, MainActivity.class);
			startActivity(intent);
		} else if (requestCode == REQUEST_CODE_THEME && resultCode == RESULT_OK) {
			String theme = data.getStringExtra("theme");
			setMainBG_URI(UISettingActivity.this, theme);
			setTheme(UISettingActivity.this, theme);
		}
	}

	/**
	 * 设置主题
	 * 
	 * @param context
	 * @param theme
	 */
	public static void setTheme(Context context, String theme) {
		PreferenceManager.getDefaultSharedPreferences(context).edit().putString(SELECT_MAIN_THEME_KEY, theme).commit();
	}

	/**
	 * 获得主题
	 * 
	 * @param context
	 */
	public static String getTheme(Context context) {
		return PreferenceManager.getDefaultSharedPreferences(context).getString(SELECT_MAIN_THEME_KEY, SELECT_MAIN_THEME_DEFAULT);
	}

	/**
	 * 设置背景图地址
	 * 
	 * @param context
	 * @param URI
	 */
	public static void setMainBG_URI(Context context, String URI) {
		PreferenceManager.getDefaultSharedPreferences(context).edit().putString(SELECT_MAIN_BG_KEY, URI).commit();
	}

	/**
	 * 获得背景图地址
	 * 
	 * @param context
	 * @return
	 */
	public static Uri getMainBG_URI(Context context) {
		String bg_Uri = PreferenceManager.getDefaultSharedPreferences(context).getString(SELECT_MAIN_BG_KEY, SELECT_MAIN_BG_DEFAULT);
		return Uri.parse(bg_Uri);
	}

	/**
	 * 对外提供接口判断是否推送消息
	 * 
	 * @param class1
	 * @return
	 */
	public static boolean getIsMsgPush(Context context) {
		return PreferenceManager.getDefaultSharedPreferences(context).getBoolean(PUSH_MSG_KEY, PUSH_MSG_DEFAULT);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			Intent intent = new Intent();
			intent.setClass(this, MainActivity.class);
			startActivity(intent);
			finish();
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	/**
	 * 检查是否有更新
	 */
	private void checkForUpDate() {
		updateHandler = new UpdateHandler();
		executorService.submit(new UpdateThread());// 耗时操作,开启一个新线程获取数据
	}

	private class UpdateThread implements Runnable {
		@Override
		public void run() {
			String socketCmd = "KOA_Mobile_Version";
			String sessionId = User.getInstance().getUserContext(UISettingActivity.this).getSessionId();
			upDateJson = HttpResponseUtil.getInstance().getGetHttpInfo(socketCmd, sessionId, null, UISettingActivity.this);
			Message msg = updateHandler.obtainMessage();// 通知线程来处理范围的数据
			updateHandler.sendMessage(msg);
		}
	}

	// 数据处理类
	class UpdateHandler extends Handler {
		private String updateType;
		private double serverVersion;
		private double appVersion;
		private String upDateURL;

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			ProgressDialogHolder.getInstance().exit();
			if (upDateJson == null) {
				Toast.makeText(UISettingActivity.this, "加载超时,请稍后再试", Toast.LENGTH_SHORT).show();
			} else if (upDateJson.equals("") || upDateJson.equals("[]")) {
				Toast.makeText(UISettingActivity.this, "加载超时,请稍后再试", Toast.LENGTH_SHORT).show();
			} else if (upDateJson.equals("TIMEOUT")) {
				Toast.makeText(UISettingActivity.this, "加载超时,请稍后再试", Toast.LENGTH_SHORT).show();
			} else if (upDateJson.equals("SESSION_TIMEOUT")) {
				Toast.makeText(UISettingActivity.this, "加载超时,请稍后再试", Toast.LENGTH_SHORT).show();
			} else if (upDateJson.equals("NETWORK_ERROR")) {
				Toast.makeText(UISettingActivity.this, "网络连接异常，请稍后再试", Toast.LENGTH_SHORT).show();
			} else {
				upDateJson = upDateJson.substring(1, upDateJson.length() - 1);
				upDateURL = "http://" + HttpResponseUtil.getInstance().getServiceAddress(UISettingActivity.this) + JsonUtil.parseJson(upDateJson, "FJUrl");
				updateType = JsonUtil.parseJson(upDateJson, "type");
				String serverVersionStr = JsonUtil.parseJson(upDateJson, "version");
				if (null != serverVersionStr && !"".equals(serverVersionStr)) {
					serverVersion = Double.parseDouble(serverVersionStr);
					CheckUser checkUser = new CheckUser();
					String version = checkUser.getAppVersion(UISettingActivity.this);
					appVersion = Double.parseDouble(version);
					if (serverVersion > appVersion) {
						if (updateType.equals("1")) {
							AlertDialog.Builder ab = new AlertDialog.Builder(UISettingActivity.this);
							ab.setTitle("发现新版本");
							ab.setMessage("发现KOA新版本,请升级程序");
							ab.setNegativeButton("以后再说", new android.content.DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog, int which) {
									dialog.dismiss();
								}
							});
							ab.setPositiveButton("立即升级", new android.content.DialogInterface.OnClickListener() {
								@Override
								public void onClick(DialogInterface dialog, int which) {
									DownloadManager mgr = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
									DownloadHelper.getInstance().updateApp(UISettingActivity.this, LoginActivity.APP_INSTALLER_NAME, upDateURL, mgr);
								}
							});
							ab.create().show();
						}
					} else {
						Toast.makeText(UISettingActivity.this, "没有检测到新版本", Toast.LENGTH_SHORT).show();
					}
				} else {
					Toast.makeText(UISettingActivity.this, "没有检测到新版本", Toast.LENGTH_SHORT).show();
				}
			}
		}
	}
}