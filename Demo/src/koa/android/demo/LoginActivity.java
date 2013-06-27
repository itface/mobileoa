package koa.android.demo;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import koa.android.demo.model.LoginTimeOutModel;
import koa.android.demo.model.Task;
import koa.android.demo.model.User;
import koa.android.logic.ActivityHoder;
import koa.android.logic.CheckUser;
import koa.android.logic.MainService;
import koa.android.logic.ProgressDialogHolder;
import koa.android.logic.ToastDialogHolder;
import koa.android.tools.HttpResponseUtil;
import koa.android.tools.JsonUtil;
import koa.android.tools.NetUtil;
import koa.android.tools.SQLLiteUtil;
import koa.android.tools.SetBackGround;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Shader.TileMode;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.telephony.TelephonyManager;
import android.view.Display;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

/**
 * 登录页面
 * 
 * @author chenM
 * 
 */
public class LoginActivity extends Activity implements KoaActivity {
	public final static String APP_INSTALLER_NAME = "Koa.apk";
	private Button loginbtn = null;
	private EditText username = null;
	private EditText pwd = null;
	private ProgressDialog pd;
	private TelephonyManager tm;
	private boolean rememberPsd = true;
	private RelativeLayout ui_login;
	private ImageView ui_login_logo;
	private Button settingIP;
	private String sessionId;
	private boolean startServiceFlag = false;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login);
		// 将Activity放到Activity中
		ActivityHoder.getInstance().addActivity(this);
		setUpView();
		setUpListener();
		setBackground();
		init();
	}

	// 设置视图
	private void setUpView() {
		tm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
		loginbtn = (Button) findViewById(R.id.login);
		username = (EditText) findViewById(R.id.edtuser);
		pwd = (EditText) findViewById(R.id.edtpsd);
		ui_login = (RelativeLayout) findViewById(R.id.ui_login);
		ui_login_logo = (ImageView) findViewById(R.id.ui_login_logo);
		settingIP = (Button) findViewById(R.id.settingIP);
	}

	// 设置监听器
	private void setUpListener() {
		pwd.setOnEditorActionListener(new TextView.OnEditorActionListener() {
			@Override
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
				if (actionId == EditorInfo.IME_ACTION_UNSPECIFIED || actionId == EditorInfo.IME_ACTION_NONE || actionId == EditorInfo.IME_ACTION_DONE || actionId == EditorInfo.IME_ACTION_NEXT || actionId == EditorInfo.IME_ACTION_GO || actionId == EditorInfo.IME_ACTION_SEARCH) {
					beforeLoginEvent();
				}
				return false;
			}
		});
		loginbtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				beforeLoginEvent();
			}
		});
		settingIP.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.setClass(LoginActivity.this, SettingServiceIPActivity.class);
				LoginActivity.this.startActivity(intent);
			}
		});
	}

	// 设置背景图
	private void setBackground() {
		// 换肤设置背景图
		SetBackGround.getInstance().setTheme(LoginActivity.this, loginbtn, "loginbtn", R.drawable.ui_login_btn_blue, null);
		SetBackGround.getInstance().setTheme(LoginActivity.this, ui_login_logo, "ui_login_logo", R.drawable.ui_login_logo, null);
		SetBackGround.getInstance().setTheme(LoginActivity.this, settingIP, "ui_login_setting", R.drawable.ui_login_seeting, null);
		// 换肤设置背景图
		Uri BG_URI = UISettingActivity.getMainBG_URI(LoginActivity.this);
		String bg_uri = BG_URI.toString();
		if (bg_uri.contains("content://")) {
			// 自定义背景
			Display display = getWindowManager().getDefaultDisplay();
			int width = display.getWidth();
			int height = display.getHeight();
			String[] proj = { MediaStore.Images.Media.DATA };
			Cursor actualimagecursor = managedQuery(BG_URI, proj, null, null, null);
			int actual_image_column_index = actualimagecursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
			actualimagecursor.moveToFirst();
			String img_path = actualimagecursor.getString(actual_image_column_index);
			SetBackGround.getInstance().setBackgroundImg(LoginActivity.this, ui_login, "gridview", R.drawable.ui_login_bg_bule, TileMode.REPEAT, width, height, img_path);
		} else {
			// 原主题背景
			SetBackGround.getInstance().setGridviewBG(LoginActivity.this, ui_login, "gridview", R.drawable.ui_login_bg_bule, TileMode.REPEAT);
		}
	}

	@Override
	protected void onRestart() {
		super.onRestart();
		setBackground();
	}

	@Override
	protected void onResume() {
		super.onResume();
		if (pd != null) {
			pd.dismiss();
			pd = null;
		}
		// 删除安装文件
		DownloadHelper.getInstance().deleteFile(LoginActivity.this, APP_INSTALLER_NAME);
	}

	@Override
	protected void onStop() {
		super.onStop();
		ProgressDialogHolder.getInstance().exit();
	}

	// 初始化检查网络是否连接，数据库中是否有用户
	public void init() {
		// 检查数据库中是否存有用户
		SQLLiteUtil sq = new SQLLiteUtil(LoginActivity.this).open();
		Cursor cursor = sq.fetchAllUsers();
		while (cursor.moveToNext()) {
			String name = cursor.getString(cursor.getColumnIndex("name"));
			String password = cursor.getString(cursor.getColumnIndex("password"));
			username.setText(name);
			if (password.equals("")) {
				pwd.requestFocus();
			}
		}
		cursor.close();
		sq.close();
		// 检查网络是否连接
		if (NetUtil.checkNet(this)) {
			startServiceFlag = true;
			Intent it = new Intent("koa.android.logic.MainService");
			this.startService(it);
			String serviceIp = HttpResponseUtil.getInstance().getServiceAddress(LoginActivity.this);
			if (serviceIp.equals("")) {
				AlertDialog.Builder ab = new AlertDialog.Builder(LoginActivity.this);
				ab.setTitle("服务器连接超时");
				ab.setMessage("访问码为空,请设置访问码");
				ab.setNegativeButton("退出程序", new android.content.DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						MainService.exitApp(LoginActivity.this);
					}
				});
				ab.setPositiveButton("前往设置", new android.content.DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
						Intent intent = new Intent();
						intent.setClass(LoginActivity.this, SettingServiceIPActivity.class);
						LoginActivity.this.startActivity(intent);
					}
				});
				ab.create().show();
			}
		} else {
			// 弹出网络异常对话框
			MainService.AlertNetError(this);
		}
	}

	@Override
	public void refresh(Object... params) {
		String returnStr = params[0].toString();
		String userstr = params[1].toString();
		String pwdstr = params[2].toString();
		if (pd != null) {
			pd.dismiss();
			pd = null;
		}
		// 判断返回值是否为空
		if (returnStr == null || "".equals(returnStr)) {
			Toast.makeText(getApplicationContext(), "服务器返回值异常,请稍后再试", Toast.LENGTH_SHORT).show();
		} else if (returnStr.equals("TIMEOUT")) {
			Toast.makeText(getApplicationContext(), "加载超时,请稍后再试", Toast.LENGTH_SHORT).show();
		} else if (returnStr.equals("NETWORK_ERROR")) {
			Toast.makeText(getApplicationContext(), "网络连接异常，请稍后再试", Toast.LENGTH_SHORT).show();
		} else if (returnStr.equals("ERROR-0500")) {
			Toast.makeText(getApplicationContext(), "账户不存在", Toast.LENGTH_SHORT).show();
		} else if (returnStr.equals("ERROR-0501")) {
			Toast.makeText(getApplicationContext(), "账号或密码错误", Toast.LENGTH_SHORT).show();
		} else if (returnStr.equals("ERROR-0502")) {
			Toast.makeText(getApplicationContext(), "账号已冻结，10分钟后解冻", Toast.LENGTH_SHORT).show();
		} else if (returnStr.equals("ERROR-0504")) {
			Toast.makeText(getApplicationContext(), "数据库连接失败或表结构异常", Toast.LENGTH_SHORT).show();
		} else if (returnStr.equals("ERROR-0505")) {
			Toast.makeText(getApplicationContext(), "登录异常", Toast.LENGTH_SHORT).show();
		} else if (returnStr.equals("ERROR-0506")) {
			Toast.makeText(getApplicationContext(), "会话注册异常", Toast.LENGTH_SHORT).show();
		} else if (returnStr.equals("ERROR-0600")) {
			Toast.makeText(getApplicationContext(), "当前设备没有登录权限,请联系管理员", Toast.LENGTH_SHORT).show();
		} else if (returnStr.equals("ERROR-0601")) {
			Toast.makeText(getApplicationContext(), "当前设备禁止登录", Toast.LENGTH_SHORT).show();
		} else if (returnStr.equals("ERROR-0700")) {
			Toast.makeText(getApplicationContext(), "令牌验证错误", Toast.LENGTH_SHORT).show();
		} else if (returnStr.equals("ERROR-0599")) {
			Toast.makeText(getApplicationContext(), "您的账号已冻结", Toast.LENGTH_SHORT).show();
		} else {
			// 当验证成功且记住密码插入数据库
			SQLLiteUtil sq = new SQLLiteUtil(LoginActivity.this).open();
			Cursor cursor = sq.fetchAllUsers();
			if (rememberPsd) {
				if (cursor.getCount() > 0) {
					while (cursor.moveToNext()) {
						String name = cursor.getString(cursor.getColumnIndex("name"));
						sq.deleteUser(name);
					}
				}
				// 仅记住用户名，不记住密码
				sq.createUser(userstr, "");
			} else {
				if (cursor.getCount() > 0) {
					while (cursor.moveToNext()) {
						String name = cursor.getString(cursor.getColumnIndex("name"));
						if (name.equals(userstr)) {
							sq.deleteUser(name);
						}
					}
				}
			}
			// 当登录成功将服务器地址存入到服务器地址记录中
			String curIPAddress = PreferenceManager.getDefaultSharedPreferences(LoginActivity.this).getString(SettingServiceIPActivity.SETTING_SERVICE_IP, SettingServiceIPActivity.SETTING_SERVICE_IP_DEFAULT);
			HttpResponseUtil.getInstance().addServiceIPLog(LoginActivity.this, curIPAddress);
			cursor.close();
			sq.close();
			// 设置用户上下文
			User user = new User(userstr, pwdstr, returnStr, null, tm.getDeviceId());
			sessionId = returnStr;
			User.getInstance().setUserContext(user, LoginActivity.this);
			checkForUpDate();
		}
	}

	/**
	 * 登录前动作
	 */
	private void beforeLoginEvent() {
		// 关闭输入法
		InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
		imm.hideSoftInputFromWindow(pwd.getWindowToken(), 0);
		imm.hideSoftInputFromWindow(username.getWindowToken(), 0);
		// 关闭提示
		ToastDialogHolder.getInstance().exit();
		if (NetUtil.checkNet(LoginActivity.this)) {
			String serviceIp = HttpResponseUtil.getInstance().getServiceAddress(LoginActivity.this);
			if (serviceIp.equals("")) {
				Toast toast = Toast.makeText(LoginActivity.this, "访问码为空,请填写访问码", Toast.LENGTH_SHORT);
				toast.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL, 0, 40);
				toast.show();
				ToastDialogHolder.getInstance().addActivity(toast);
			} else {
				if (!startServiceFlag) {
					Intent it = new Intent("koa.android.logic.MainService");
					LoginActivity.this.startService(it);
				}
				loginEvent();
			}
		} else {
			Toast toast = Toast.makeText(LoginActivity.this, "网络连接异常,请稍后再试", Toast.LENGTH_SHORT);
			toast.setGravity(Gravity.CENTER_VERTICAL | Gravity.CENTER_HORIZONTAL, 0, 40);
			toast.show();
			ToastDialogHolder.getInstance().addActivity(toast);
		}
	}

	// 登录动作
	public void loginEvent() {
		String curIP = PreferenceManager.getDefaultSharedPreferences(LoginActivity.this).getString(SettingServiceIPActivity.SETTING_SERVICE_IP, SettingServiceIPActivity.SETTING_SERVICE_IP_DEFAULT);
		if (checkIPLegal(curIP)) {
			String userstr = username.getText().toString();
			String pwdstr = pwd.getText().toString();
			if (!"".equals(userstr) && !"".equals(pwdstr)) {
				if (pd == null) {
					pd = new ProgressDialog(LoginActivity.this);
					pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
					pd.setCancelable(true);
					pd.setMessage("正在登录,请稍等...");
					pd.show();
					ProgressDialogHolder.getInstance().addActivity(pd);
				}
				if (!userstr.equals("admin")) {
					userstr = userstr.toUpperCase();
				}
				Map<String, Object> userInfo = new HashMap<String, Object>();
				// 替换所有的空格
				userstr = userstr.replaceAll("\\s", "");
				pwdstr = pwdstr.replaceAll("\\s", "");
				String deviceId = tm.getDeviceId();
				userInfo.put("userid", userstr);
				userInfo.put("pwd", pwdstr);
				userInfo.put("deviceId", deviceId);
				Task task = new Task(Task.KOA_LOGIN, userInfo);
				// 将任务发送给Service来处理
				MainService.newTask(task);
				// 把自己添加到Activity集合里面
				MainService.addActivity(LoginActivity.this);
			} else {
				Toast toast = Toast.makeText(getApplicationContext(), "用户名或密码不允许为空", Toast.LENGTH_SHORT);
				toast.show();
			}
		} else {
			Toast toast = Toast.makeText(getApplicationContext(), "访问码不合法请重新设置", Toast.LENGTH_SHORT);
			toast.show();
		}
	}

	/**
	 * 检测整个 IP地址是否合法
	 * 
	 * @return
	 */
	private boolean checkIPLegal(String encodeIp) {
		if (encodeIp == null || encodeIp.equals("")) {
			return false;
			// 包含点认为是IP地址
		} else if (encodeIp.indexOf(".") > 0) {
			if (HttpResponseUtil.getInstance().isAccessIpFormat) {
				if (encodeIp.indexOf(":") > 0) {
					String[] ipArr = encodeIp.split(":");
					String ip = ipArr[0];
					String port = ipArr[1];
					if (checkIpFormat(ip)) {
						if (checkPortFormat(port)) {
							return true;
						} else {
							return false;
						}
					} else {
						return false;
					}
				} else {
					return checkIpFormat(encodeIp);
				}
			} else {
				return false;
			}
			// 否则是访问码
		} else {
			try {
				long temp = Long.parseLong(encodeIp, 16);
				if (String.valueOf(temp).length() < 12) {
					return false;
				} else {
					return true;
				}
			} catch (Exception e) {
				return false;
			}
		}
	}

	/**
	 * 验证IP地址合法性 //以验证127.400.600.2为例
	 * 
	 * @param ip
	 * @return
	 */
	private boolean checkIpFormat(String ip) {
		Pattern pattern = Pattern.compile("\\b((?!\\d\\d\\d)\\d+|1\\d\\d|2[0-4]\\d|25[0-5])\\.((?!\\d\\d\\d)\\d+|1\\d\\d|2[0-4]\\d|25[0-5])\\.((?!\\d\\d\\d)\\d+|1\\d\\d|2[0-4]\\d|25[0-5])\\.((?!\\d\\d\\d)\\d+|1\\d\\d|2[0-4]\\d|25[0-5])\\b");
		Matcher matcher = pattern.matcher(ip);
		return (matcher.matches());
	}

	/**
	 * 验证端口号合法性
	 * 
	 * @param port
	 * @return
	 */
	private boolean checkPortFormat(String port) {
		try {
			long temp = Long.parseLong(port);
			if (temp < 0 || temp > 65535) {
				return false;
			} else {
				return true;
			}
		} catch (Exception e) {
			return false;
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			MainService.exitApp(this);
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

	/**
	 * 检查是否有更新
	 */
	private void checkForUpDate() {
		String socketCmd = "KOA_Mobile_Version";
		String upDateJson = HttpResponseUtil.getInstance().getGetHttpInfo(socketCmd, sessionId, null, LoginActivity.this);
		handleUpDateJson(upDateJson);
	}

	/**
	 * 数据处理
	 * 
	 * @param upDateJson
	 */
	private void handleUpDateJson(String upDateJson) {
		String updateType;
		double serverVersion;
		double appVersion;
		String upDateURLTemp;
		final String upDateURL;
		if (upDateJson == null) {
			JumpMain();
		} else if (upDateJson.equals("") || upDateJson.equals("[]")) {
			JumpMain();
		} else if (upDateJson.equals("TIMEOUT")) {
			Toast.makeText(LoginActivity.this, "加载超时,请稍后再试", Toast.LENGTH_SHORT).show();
		} else if (upDateJson.equals("SESSION_TIMEOUT")) {
			Toast.makeText(LoginActivity.this, "加载超时,请稍后再试", Toast.LENGTH_SHORT).show();
		} else if (upDateJson.equals("NETWORK_ERROR")) {
			Toast.makeText(LoginActivity.this, "网络连接异常,请稍后再试", Toast.LENGTH_SHORT).show();
		} else {
			upDateJson = upDateJson.substring(1, upDateJson.length() - 1);
			upDateURLTemp = JsonUtil.parseJson(upDateJson, "FJUrl");
			if (upDateURLTemp.contains("\\")) {
				upDateURLTemp = upDateURLTemp.replace("\\", "/");
			}
			upDateURLTemp = "http://" + HttpResponseUtil.getInstance().getServiceAddress(LoginActivity.this) + upDateURLTemp;
			upDateURL = upDateURLTemp;
			updateType = JsonUtil.parseJson(upDateJson, "type");
			String serverVersionStr = JsonUtil.parseJson(upDateJson, "version");
			if (null != serverVersionStr && !"".equals(serverVersionStr)) {
				serverVersion = Double.parseDouble(serverVersionStr);
				CheckUser checkUser = new CheckUser();
				String version = checkUser.getAppVersion(LoginActivity.this);// 客户端版本号
				appVersion = Double.parseDouble(version);
				if (serverVersion > appVersion) {
					ProgressDialogHolder.getInstance().exit();
					if (updateType.equals("1")) {
						AlertDialog.Builder ab = new AlertDialog.Builder(LoginActivity.this);
						ab.setTitle("发现新版本");
						ab.setMessage("发现KOA新版本,请升级程序");
						ab.setNegativeButton("以后再说", new android.content.DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which) {
								JumpMain();
							}
						});
						ab.setPositiveButton("立即升级", new android.content.DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which) {
								DownloadManager mgr = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
								DownloadHelper.getInstance().updateApp(LoginActivity.this, APP_INSTALLER_NAME, upDateURL, mgr);
							}
						});
						ab.create().show();
					}
				} else {
					JumpMain();
				}
			} else {
				JumpMain();
			}
		}
	}

	// 跳转主页面
	private void JumpMain() {
		Intent intent = new Intent();
		intent.setClass(LoginActivity.this, MainActivity.class);
		startActivity(intent);
		finish();
		LoginTimeOutModel.setLastOperationTime(new Date().getTime());
	}
}