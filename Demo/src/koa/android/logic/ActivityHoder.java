package koa.android.logic;

import java.util.LinkedList;
import java.util.List;

import android.app.Activity;
import android.app.Application;
/**
 * 储存每一Activity
 */
public class ActivityHoder extends Application{
	private List<Activity> activityList = new LinkedList<Activity>();
	private static ActivityHoder instance;
	
	private ActivityHoder(){}
	
	//单例模式中获取唯一的ActivityHoder实例
	public static ActivityHoder getInstance(){
		if(null==instance){
			instance = new ActivityHoder();
		}
		return instance;
	}
	//添加Activity到容器中  
	public void addActivity(Activity activity){
		activityList.add(activity);
	}
	
	//遍历所有Activity并finish
	public void exit(){
		for(Activity activity:activityList){
			activity.finish();
		}
		System.exit(0);
	}
}
