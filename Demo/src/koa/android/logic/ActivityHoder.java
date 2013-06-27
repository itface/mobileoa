package koa.android.logic;

import java.util.LinkedList;
import java.util.List;

import android.app.Activity;
import android.app.Application;
/**
 * ����ÿһActivity
 */
public class ActivityHoder extends Application{
	private List<Activity> activityList = new LinkedList<Activity>();
	private static ActivityHoder instance;
	
	private ActivityHoder(){}
	
	//����ģʽ�л�ȡΨһ��ActivityHoderʵ��
	public static ActivityHoder getInstance(){
		if(null==instance){
			instance = new ActivityHoder();
		}
		return instance;
	}
	//���Activity��������  
	public void addActivity(Activity activity){
		activityList.add(activity);
	}
	
	//��������Activity��finish
	public void exit(){
		for(Activity activity:activityList){
			activity.finish();
		}
		System.exit(0);
	}
}
