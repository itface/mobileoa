package koa.android.tools;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.ConnectException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import koa.android.demo.LoginActivity;
import koa.android.demo.SettingServiceIPActivity;
import koa.android.demo.model.User;

import org.apache.http.conn.ConnectTimeoutException;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.preference.PreferenceManager;

public class HttpResponseUtil {
	public String baseurl = "";
	public String downloadurl = "";
	public static final String SERVICE_IP_LOG = "service_ip_log";
	public String curIp;
	public boolean isAccessIpFormat = true;// 是否允许输入IP地址

	// 设置请求超时10秒钟
	private static final int REQUEST_TIMEOUT = 20 * 1000;
	// 设置等待数据超时时间10秒钟
	private static final int SO_TIMEOUT = 20 * 1000;
	private static HttpResponseUtil instance;
	private static Object lock = new Object();

	// 私有构造方法
	private HttpResponseUtil() {
	}

	public static HttpResponseUtil getInstance() {
		if (instance == null) {
			synchronized (lock) {
				if (instance == null) {
					instance = new HttpResponseUtil();
				}
			}
		}
		return instance;
	}

	/**
	 * 服务器读取
	 * 
	 * @param socketCmd
	 * @param sessionId
	 * @param paramHash
	 * @return
	 */
	public String getGetHttpInfo(String socketCmd, String sessionId, HashMap<String, Object> paramHash, Context context) {
		String returnStr = "";
		StringBuffer path = new StringBuffer(HttpResponseUtil.getInstance().getBaseUrl(context));
		path.append("?sid=").append(sessionId).append("&cmd=").append(socketCmd);
		if (paramHash != null) {
			Iterator<Entry<String, Object>> iter = paramHash.entrySet().iterator();
			while (iter.hasNext()) {
				Map.Entry<String, Object> entry = (Map.Entry<String, Object>) iter.next();
				Object key = entry.getKey();
				Object val = entry.getValue();
				if (key != null && val != null) {
					String valTemp = URLEncoder.encode(val.toString());
					path.append("&").append(key).append("=").append(valTemp);
				}
			}
		}
		try {
			URL url = new URL(path.toString());
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("POST");
			conn.setConnectTimeout(REQUEST_TIMEOUT);
			conn.setReadTimeout(SO_TIMEOUT);
			InputStream inStream = conn.getInputStream();
			byte[] data = readInputStream(inStream);
			String result = new String(data, "GBK");
			// 如果包含JavaScript,去掉JavaScript脚本
			if (result.contains("<script>")) {
				if (result.indexOf("\n\n\n<script>") > 0) {
					int begin = result.indexOf("\n\n\n<script>");
					String subString = result.substring(0, begin);
					result = subString;
				}
			}
			// 还原JSON中的特殊字符
			result = parseXMLsymbol.getInstance().parseXML(result);
			if (result.contains("与AWS服务器的通讯连接失败")) {// AWS服务器没有启动
				returnStr = "TIMEOUT";
			} else if (result.contains("重新登录")) {// 会话超时,重新登录
				return "SESSION_TIMEOUT";
			} else {
				returnStr = result;
			}
		} catch (ConnectTimeoutException e) {
			returnStr = "TIMEOUT";
			return returnStr;
		} catch (ConnectException e) {
			returnStr = "NETWORK_ERROR";
			return returnStr;
		} catch (IOException e) {
			returnStr = "NETWORK_ERROR";
			return returnStr;
		} catch (Exception e) {
			e.printStackTrace();
			returnStr = "TIMEOUT";
			return returnStr;
		}
		return returnStr;
	}

	/**
	 * 从输入流中获取数据
	 * 
	 * @param inStream
	 * @return
	 * @throws Exception
	 */
	public static byte[] readInputStream(InputStream inStream) throws Exception {
		ByteArrayOutputStream outStream = new ByteArrayOutputStream();
		byte[] buffer = new byte[1024];
		int len = 0;
		while ((len = inStream.read(buffer)) != -1) {
			outStream.write(buffer, 0, len);
		}
		inStream.close();
		return outStream.toByteArray();
	}

	/**
	 * 获得BASEURL
	 * 
	 * @return
	 */
	public String getBaseUrl(Context context) {
		StringBuffer baseAddress = new StringBuffer("http://");
		String serviceIP = getServiceAddress(context);
		baseAddress = baseAddress.append(serviceIP).append("/workflow/login.wf");
		return baseAddress.toString();
	}

	/**
	 * 获得DOWNLOADURL
	 * 
	 * @return
	 */
	public String getDownloadUrl(Context context) {
		StringBuffer baseAddress = new StringBuffer("http://");
		String serviceIP = getServiceAddress(context);
		baseAddress = baseAddress.append(serviceIP).append("/workflow/downfile.wf");
		return baseAddress.toString();
	}

	/**
	 * 获得服务器地址
	 * 
	 * @return
	 */
	public String getServiceAddress(Context context) {
		if (curIp == null || curIp.equals("")) {
			String ip = "";
			String serviceIp = PreferenceManager.getDefaultSharedPreferences(context).getString(SettingServiceIPActivity.SETTING_SERVICE_IP, SettingServiceIPActivity.SETTING_SERVICE_IP_DEFAULT);
			// 包含点认为是IP地址
			if (serviceIp.indexOf(".") > 0) {
				// 是否接收IP格式
				if (isAccessIpFormat) {
					ip = serviceIp;
				} else {
					ip = "";
				}
				// 否则是访问码
			} else {
				ip = decode(serviceIp);
			}
			curIp = ip;
			return ip;
		} else {
			return curIp;
		}
	}

	/**
	 * 对服务器IP进行解码
	 * 
	 * @param encodeIp
	 * @return
	 */
	public String decode(String encodeIp) {
		if (encodeIp != null && !encodeIp.equals("")) {
			String unEncodeIp = "";
			long temp = Long.parseLong(encodeIp, 16);
			long temp1 = Long.rotateRight(temp, 2);
			unEncodeIp = String.valueOf(temp1);
			StringBuffer sb = new StringBuffer();
			sb.append(deleteZero(unEncodeIp.substring(0, 3).trim())).append(".").append(deleteZero(unEncodeIp.substring(3, 6).trim())).append(".").append(deleteZero(unEncodeIp.substring(6, 9).trim())).append(".").append(deleteZero(unEncodeIp.substring(9, 12).trim()));
			if (unEncodeIp.length() > 12) {
				sb.append(":").append(unEncodeIp.substring(12, unEncodeIp.length()).trim());
			}
			return sb.toString();
		} else {
			return "";
		}
	}

	/**
	 * 删除前面零
	 * 
	 * @param ip
	 * @return
	 */
	public String deleteZero(String ipItem) {
		if (ipItem.startsWith("00")) {
			ipItem = ipItem.substring(2, ipItem.length());
			return ipItem;
		} else if (ipItem.startsWith("0")) {
			ipItem = ipItem.substring(1, ipItem.length());
			return ipItem;
		} else {
			return ipItem;
		}
	}

	/**
	 * 增加服务器地址记录
	 * 
	 * @param context
	 * @param new_IP_Address
	 */
	public void addServiceIPLog(Context context, String new_IP_Address) {
		List<String> ip_list = getServiceIPLog(context);
		if (ip_list.size() == 20) {
			ip_list.remove(0);
		}
		String oldLog = PreferenceManager.getDefaultSharedPreferences(context).getString(SERVICE_IP_LOG, "");
		if (!oldLog.contains(new_IP_Address)) {
			ip_list.add(new_IP_Address);
		}
		StringBuffer sb = new StringBuffer();
		for (int i = 0; i < ip_list.size(); i++) {
			sb.append(ip_list.get(i)).append("_");
		}
		PreferenceManager.getDefaultSharedPreferences(context).edit().putString(SERVICE_IP_LOG, sb.toString()).commit();
	}

	/**
	 * 获得服务器地址记录
	 * 
	 * @param context
	 * @return
	 */
	public List<String> getServiceIPLog(Context context) {
		String serviceIPLog = PreferenceManager.getDefaultSharedPreferences(context).getString(SERVICE_IP_LOG, "");
		String[] old_IP_Log_arr = serviceIPLog.split("_");
		List<String> ipList = new ArrayList<String>();
		for (int i = 0; i < old_IP_Log_arr.length; i++) {
			if (!"".equals(old_IP_Log_arr[i])) {
				ipList.add(old_IP_Log_arr[i]);
			}
		}
		return ipList;
	}

	/**
	 * 重新登录
	 * 
	 * @param context
	 */
	public void reLogin(final Context context) {
		System.exit(0);
		AlertDialog.Builder ab = new AlertDialog.Builder(context);
		ab.setCancelable(false);
		ab.setTitle("会话超时");
		ab.setMessage("当前会话超时,请重新登录");
		ab.setPositiveButton("确定", new android.content.DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				User.getInstance().setUserContext(null, context);
				Intent intent = new Intent();
				intent.setClass(context, LoginActivity.class);
				intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
				context.startActivity(intent);
			}
		});
		ab.create().show();
	}
}
