package koa.android.tools;

import koa.android.demo.R;
import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.PopupWindow;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;



/**
 * 弹出载入进度条帮助类
 * @author chenM
 *
 */
public class popLoadWindowHelper{
	private static popLoadWindowHelper instance;
	private static Object lock = new Object();
	
	public static popLoadWindowHelper getInstance() {
		if (instance == null) {
			synchronized (lock) {
				if (instance == null) {
					instance = new popLoadWindowHelper();
				}
			}
		}
		return instance;
	}
	//弹出进度条
	public PopupWindow popWindow(String text,PopupWindow pop,View parent,Context context){
		pop = createPopupWindow(text,context);
		pop.setFocusable(false);
		pop.setOutsideTouchable(true);
		pop.showAtLocation(parent, Gravity.CENTER, 0, 0);
		return pop;
	}
	//关闭进度条
	public PopupWindow closeWindow(PopupWindow pop){
		if(null != pop && pop.isShowing()) {
			pop.dismiss();
		}
		return null;
	}

	// 创建弹出框
	private PopupWindow createPopupWindow(String text,Context context) {
		LayoutInflater factory = LayoutInflater.from(context); // 加载popWindow的layout
		final View textEntryView = factory.inflate(R.layout.load_progress,null);
		TextView ios_progress_msg = (TextView) textEntryView.findViewById(R.id.progress_msg);
		ios_progress_msg.setText(text);
		return new PopupWindow(textEntryView, LayoutParams.WRAP_CONTENT,
				LayoutParams.WRAP_CONTENT);
	}
}
