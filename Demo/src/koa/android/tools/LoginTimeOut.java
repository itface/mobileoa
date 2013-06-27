package koa.android.tools;

import java.util.Date;

import koa.android.demo.model.LoginTimeOutModel;
import android.content.Context;

/**
 * �ͻ����ж��û���¼�Ƿ�ʱ������ʱ�������--timeOutLimit
 * 
 * @author SuQi
 * 
 */
public class LoginTimeOut {

	public boolean timeOut(Context context) {
		boolean flag = false;
		int timeOutLimit = 1000 * 60 * 60 * 24;// ��ʱʱ��
		long loginTime = LoginTimeOutModel.getLastOperationTime();
		long timeNow = new Date().getTime();
		if (loginTime != 0) {
			if ((timeNow - loginTime) / timeOutLimit > 1) {
				// ��ʱ�ǳ������ص�¼ҳ��
				HttpResponseUtil.getInstance().reLogin(context);
				LoginTimeOutModel.setLastOperationTime(0);
				flag = true;
			} else {
				// �������У�����������ʱ��
				LoginTimeOutModel.setLastOperationTime(timeNow);
			}
		}
		return flag;
	}

	public static int dip2px(Context context, float dipValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (dipValue * scale + 0.5f);
	}

	public static int px2dip(Context context, float pxValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (pxValue / scale + 0.5f);
	}

}
