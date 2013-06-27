package koa.android.tools;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * ��������Ƿ��Ѿ�����
 * @author chenM
 *
 */
public class NetUtil {
	public static boolean checkNet(Context context){
		try{
			ConnectivityManager connectivity = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
			if(connectivity!=null){
				//��ȡ�������ӹ���Ķ���
				NetworkInfo info = connectivity.getActiveNetworkInfo();
				if(info!=null&&info.isConnected()){
					//�жϵ�ǰ�����Ƿ��Ѿ�����
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
