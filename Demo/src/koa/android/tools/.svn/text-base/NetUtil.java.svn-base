package koa.android.tools;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * 检测网络是否已经连接
 * @author chenM
 *
 */
public class NetUtil {
	public static boolean checkNet(Context context){
		try{
			ConnectivityManager connectivity = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
			if(connectivity!=null){
				//获取网络连接管理的对象
				NetworkInfo info = connectivity.getActiveNetworkInfo();
				if(info!=null&&info.isConnected()){
					//判断当前网络是否已经连接
					if(info.getState()==NetworkInfo.State.CONNECTED){
						return true;
					}
				}
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		return false;
	}
}
