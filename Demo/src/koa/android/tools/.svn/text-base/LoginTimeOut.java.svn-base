package koa.android.tools;

import java.util.Date;

import koa.android.demo.model.LoginTimeOutModel;
import android.content.Context;

/**
 * 客户端判断用户登录是否超时，操作时长最大间隔--timeOutLimit
 * 
 * @author SuQi
 * 
 */
public class LoginTimeOut {

	public boolean timeOut(Context context) {
		boolean flag = false;
		int timeOutLimit = 1000 * 60 * 60 * 24;// 超时时间
		long loginTime = LoginTimeOutModel.getLastOperationTime();
		long timeNow = new Date().getTime();
		if (loginTime != 0) {
			if ((timeNow - loginTime) / timeOutLimit > 1) {
				// 超时登出，返回登录页面
				HttpResponseUtil.getInstance().reLogin(context);
				LoginTimeOutModel.setLastOperationTime(0);
				flag = true;
			} else {
				// 继续运行，更新最后操作时间
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
