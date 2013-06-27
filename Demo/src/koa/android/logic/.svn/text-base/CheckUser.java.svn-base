package koa.android.logic;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.ConnectException;
import java.net.URLEncoder;

import koa.android.tools.HttpResponseUtil;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.util.Log;

/**
 * 用户名和密码的验证
 * 
 * @author chenM
 * 
 */
public class CheckUser {
	private HttpResponse httpResponse = null;
	private HttpEntity httpEntity = null;
	// 设置请求超时10秒钟
	private static final int REQUEST_TIMEOUT = 20 * 1000;
	// 设置等待数据超时时间10秒钟
	private static final int SO_TIMEOUT = 20 * 1000;

	String returnStr = "";

	public String check(String userstr, String pwdstr, String deviceId, Context context) {
		pwdstr = URLEncoder.encode(pwdstr);
		userstr = URLEncoder.encode(userstr);
		String  version  = getAppVersion(context);
		StringBuffer url = new StringBuffer(HttpResponseUtil.getInstance().getBaseUrl(context) + "?cmd=Login");
		url.append("&userid=").append(userstr).append("&pwd=").append(pwdstr).append("&UserSignedData=").append("androidMobile").append("&oSystemLogin=").append(deviceId).append("&versionLogin=").append(version);
		HttpGet httpGet = new HttpGet(url.toString());
		HttpClient httpClient = getHttpClient();
		InputStream inputStream = null;
		try {
			httpResponse = httpClient.execute(httpGet);
			httpEntity = httpResponse.getEntity();
			inputStream = httpEntity.getContent();
			BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
			StringBuffer result = new StringBuffer();
			String line = "";
			while ((line = reader.readLine()) != null) {
				result.append(line);
			}
			// 获得返回值
			returnStr = result.toString();
			if (returnStr.contains("<br><br><br><br><div align=center>")) {
				returnStr = "TIMEOUT";
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
			returnStr = "TIMEOUT";
			return returnStr;
		}
		return returnStr;
	}

	/**
	 * 添加请求超时时间和等待时间
	 * 
	 * @return
	 */
	public HttpClient getHttpClient() {
		BasicHttpParams httpParams = new BasicHttpParams();
		HttpConnectionParams.setConnectionTimeout(httpParams, REQUEST_TIMEOUT);
		HttpConnectionParams.setSoTimeout(httpParams, SO_TIMEOUT);
		HttpClient client = new DefaultHttpClient(httpParams);
		return client;
	}

	/**
	 * 获得程序版本号
	 * 
	 * @param context
	 * @return
	 */
	public String getAppVersion(Context context) {
		String version = "";
		try {
			PackageManager pm = context.getPackageManager();
			PackageInfo pi = pm.getPackageInfo(context.getPackageName(), 0);
			version = pi.versionName;
			if (version == null || version.length() <= 0) {
				return "";
			}
		} catch (Exception e) {
			Log.e("VersionInfo", "Exception", e);
		}
		return version;
	}
}
