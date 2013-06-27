package koa.android.logic;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;

import koa.android.demo.KoaActivity;
import koa.android.demo.R;
import koa.android.demo.TodoListAllActivity;
import koa.android.demo.UISettingActivity;
import koa.android.demo.WfAddsignActivity;
import koa.android.demo.WfNoticeActivity;
import koa.android.demo.WfPageActivity;
import koa.android.demo.model.Task;
import koa.android.demo.model.User;
import koa.android.demo.model.WFTaskModel;
import koa.android.tools.HttpResponseUtil;
import koa.android.tools.JsonUtil;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;

public class MainService extends Service implements Runnable{
	    //任务队列
		private static Queue<Task> tasks = new LinkedList<Task>();
		//定义装载所有的Activity
		private static ArrayList<Activity> appActivities = new ArrayList<Activity>();
		//是否运行线程
		private boolean isRun;
		//是否运行推送
		private static boolean isPushRun ;
		//现在拥有的待办个数
		private static String curWfTodoNum;
		//从服务器抓取新的待办的间隔时间,单位秒
		private int FETCH_INTERVAL = 5*60;
		//通知栏管理
		private NotificationManager KoaNotiManager;

		Handler handler = new Handler()
		{
			//接收任务处理结果通知UI更新
			public void handleMessage(android.os.Message msg) 
			{
				switch (msg.what)
				{
				 case Task.KOA_LOGIN:// 用户登录
					//更新UI
					KoaActivity LoginActivity = (KoaActivity)getActivityByName("LoginActivity");
					String returnstr = msg.getData().getString("returnstr");
					String userid = msg.getData().getString("userid");
					String pwd = msg.getData().getString("pwd");
					LoginActivity.refresh(returnstr,userid,pwd);
					break;
				 case Task.KOA_COMMON://统一获取数据
					 String activityName = msg.getData().getString("activityName");
					 KoaActivity commonActivity = (KoaActivity)getActivityByName(activityName);
					 String jsonStr = msg.getData().getString("jsonStr");
					 commonActivity.refresh(jsonStr);
				 default:
					break;
				}
			};
		};
		
		/**
		 * 添加一个Activity对象
		 * @param activity
		 */
		public static void addActivity(Activity activity)
		{
			appActivities.add(activity);
		}
		
		/**
		 * 添加任务到任务队列中
		 * @param t
		 */
		public static void newTask(Task t)
		{
			tasks.add(t);
		}
		
		/**
		 * 设置当前待办个数
		 * @param curWfTodoNum
		 */
		public static void setCurWfTodoNum(String curWfTodoNum) {
			MainService.curWfTodoNum = curWfTodoNum;
		}
		
		/**
		 * 获得当前待办个数
		 * @return
		 */
		public static String getCurWfTodoNum() {
			return curWfTodoNum;
		}
		
		/**
		 * 设置是否推送消息
		 * @param isPushRun
		 */
		public static void setPushRun(boolean isPushRun) {
			MainService.isPushRun = isPushRun;
		}

		/**
		 * 根据Activity的Name获取Activity对象
		 * @param name
		 * @return
		 */
		private Activity getActivityByName(String name)
		{
			List<Integer> indexs = new ArrayList<Integer>(); 
			if(!appActivities.isEmpty())
			{	
				for(int i=0;i<appActivities.size();i++){
					Activity activity = appActivities.get(i);
					if(activity.getClass().getName().indexOf(name) >0)
					{
						indexs.add(i);
					}
				}
				int index = indexs.get(indexs.size()-1);
				return appActivities.get(index);
			}
			return null;
		}
		
		
		
		@Override
		public void onCreate()
		{
			super.onCreate();
			isRun = true;
			isPushRun = UISettingActivity.getIsMsgPush(MainService.this);
			Thread thread = new Thread(this);
			thread.start();
		}
		
		public void run()
		{
			int timeFlag = 0;
			while(isRun)
			{
				Task task = null;
				synchronized (tasks){
					if(!tasks.isEmpty())
					{
						task = tasks.poll();// 执行完任务后把改任务从任务队列中移除
						if(null != task)
						{
							doTask(task);
						}
					}
				}
				//每1秒监听一次是否有任务
				try{Thread.sleep(1000);}catch (Exception e) {			}
				//是否开启推送服务
				if(isPushRun){
					//如果待办个数不为空,则开始监控抓取数据
					if(curWfTodoNum!=null){
						if(timeFlag>FETCH_INTERVAL){
							fetchNewWf();
							timeFlag = 1;
						}else{
							timeFlag ++;
						}
					}
				}
			}
		}
		
		// 处理任务
		private void doTask(Task t)
		{
			Message msg = handler.obtainMessage();
			msg.what =t.getTaskId();
			
			switch (t.getTaskId())
			{
				case Task.KOA_LOGIN:
					 Map<String,Object> userMap = t.getTaskParams();
					 String userid = (String) userMap.get("userid");
					 String pwd = (String) userMap.get("pwd");
					 String deviceId = (String) userMap.get("deviceId");
					 CheckUser ck = new CheckUser();
					 String returnstr = ck.check(userid,pwd,deviceId,MainService.this);
					 Bundle bundle = new Bundle();
					 bundle.putString("returnstr",returnstr); 
                     bundle.putString("userid",userid);  
                     bundle.putString("pwd",pwd);  
                     msg.setData(bundle);
                     break;
				case Task.KOA_COMMON:
					 String socketCmd = t.getSocketCmd();
					 String sissionId = t.getSessionId();
					 String activityName = t.getActivityName();
					 HashMap<String,Object> param = (HashMap<String, Object>) t.getTaskParams();
					 String jsonStr = HttpResponseUtil.getInstance().getGetHttpInfo(socketCmd, sissionId, param,MainService.this);
					 Bundle bundle_common = new Bundle();
					 bundle_common.putString("activityName", activityName);
					 bundle_common.putString("jsonStr",jsonStr); 
					 msg.setData(bundle_common);
					 break;
				case Task.KOA_NO_BACK:
					 String socketCmd1 = t.getSocketCmd();
					 String sissionId1 = t.getSessionId();
					 HashMap<String,Object> param1 = (HashMap<String, Object>) t.getTaskParams();
					 HttpResponseUtil.getInstance().getGetHttpInfo(socketCmd1, sissionId1, param1,MainService.this);
				default:
					 break;
			}
			//发送任务处理结果
			handler.sendMessage(msg);
		}
		
		@Override
		public IBinder onBind(Intent intent) {
			return null;
		}
		
		//提示网络异常
		public static void AlertNetError(final Context context){
	    	AlertDialog.Builder ab = new AlertDialog.Builder(context);
	    	ab.setTitle(R.string.string_login_alertTitle);
	    	ab.setMessage(R.string.string_login_alertMsg);
	    	ab.setNegativeButton(R.string.string_login_exit, new android.content.DialogInterface.OnClickListener(){
				@Override
				public void onClick(DialogInterface dialog, int which) {
					exitApp(context);
				}
	    	});
	    	ab.setPositiveButton(R.string.string_login_settingNet, new android.content.DialogInterface.OnClickListener(){
				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
					context.startActivity(new Intent(android.provider.Settings.ACTION_WIRELESS_SETTINGS));
				}
	    	});
	    	ab.create().show();
	    }
		//退出程序
		public static void exitApp(Context context){
			//关闭服务
			Intent it = new Intent("koa.android.logic.MainService");
			context.stopService(it);
			//关闭所有Activity
			ActivityHoder.getInstance().exit();
			//清空用户上下文信息
			User.getInstance().setUserContext(null, context);
		}
		
		//确认退出
		public static void promptExit(final Context con)
		{
			//创建对话框
			LayoutInflater li=LayoutInflater.from(con);
			View exitV=li.inflate(R.layout.exitdialog, null);
			AlertDialog.Builder ab=new AlertDialog.Builder(con);
			ab.setView(exitV);//设定对话框显示的View对象
			ab.setPositiveButton(R.string.exit, new OnClickListener(){
				public void onClick(DialogInterface arg0, int arg1) {
					exitApp(con);
				}
			});
			ab.setNegativeButton(R.string.cancel, null);
			//显示对话框
			ab.show();
		}
		

		//请求服务器抓取新的待办
		public void fetchNewWf(){
			int NewCount = 0;
			int oldCount = 0;
			String sissionId = User.getInstance().getUserContext(MainService.this).getSessionId();
			KoaNotiManager=(NotificationManager)getSystemService(NOTIFICATION_SERVICE);
		    String loginInfo = HttpResponseUtil.getInstance().getGetHttpInfo("KOA_Mobile_Login_After_Info", sissionId, null,MainService.this);
		    if("".equals(loginInfo)||"TIMEOUT".equals(loginInfo)){
		    	//如果超时不做任何操作
		    }else if(loginInfo.equals("SESSION_TIMEOUT")){
		    	//如果会话过期不做任何操作
		    }else if (loginInfo.equals("NETWORK_ERROR")) {
		    	//如果网络连接异常不做任何操作
			}else{
		    	try {
					loginInfo = loginInfo.substring(1,loginInfo.length()-1);
					JSONObject jsonObject = new JSONObject(loginInfo);
					NewCount =Integer.parseInt(jsonObject.getString("flowCount"));
				}catch (JSONException e){
					e.printStackTrace();
				}
			    oldCount= Integer.parseInt(MainService.getCurWfTodoNum());
			    if(NewCount>oldCount){
			    	setNotiType(R.drawable.app_icon,"您有"+(NewCount-oldCount)+"条新的待办任务",NewCount-oldCount);
			    	//提醒结束后设置新的待办个数
			    	MainService.setCurWfTodoNum(String.valueOf(NewCount));
			    }
		    }
		}
	   
	/**
	 * 发出Notification的方法
	 * @param iconId//状态栏提示显示的图标
	 * @param text//状态栏提示显示的文字
	 * @param wfTodoNum//待办的个数
	 */
	public void setNotiType(int iconId, String text, int wfTodoNum) {
		Intent notifyIntent = new Intent();
		if (wfTodoNum == 1) {// 如果只有一条待办,直接跳转到办理页面
			WFTaskModel wf = getJumpParam();
			if(wf!=null){
				notifyIntent.putExtra("flowTitle", wf.getTaskTitle());
				notifyIntent.putExtra("taskOwner", wf.getOwner());
				notifyIntent.putExtra("targetUser", wf.getTagetUser());
				notifyIntent.putExtra("taskDate", wf.getTaskdate());
				notifyIntent.putExtra("instanceId", wf.getInstanceId());
				notifyIntent.putExtra("taskId", wf.getTaskId());
				String taskStatus = wf.getStatus();
				if (taskStatus.equals("1")) {// 待办
					notifyIntent.setClass(MainService.this, WfPageActivity.class);
				} else if (taskStatus.equals("2")) {// 传阅
					notifyIntent.setClass(MainService.this, WfNoticeActivity.class);
				} else if (taskStatus.equals("9")) {// 通知
					notifyIntent.setClass(MainService.this, WfNoticeActivity.class);
				} else if (taskStatus.equals("11")) {// 加签
					notifyIntent.setClass(MainService.this, WfAddsignActivity.class);
				}
			}else{
				notifyIntent.setClass(MainService.this, TodoListAllActivity.class);
			}
		} else {// 否则跳转到待办列表页面
			notifyIntent.setClass(MainService.this, TodoListAllActivity.class);
		}
		notifyIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
		notifyIntent.addFlags(Intent.FILL_IN_DATA);
		PendingIntent appIntent = PendingIntent.getActivity(MainService.this,
				0, notifyIntent, PendingIntent.FLAG_UPDATE_CURRENT);
		Notification Noti = new Notification();
		Noti.icon = iconId;
		Noti.tickerText = text;
		Noti.flags |= Notification.FLAG_AUTO_CANCEL;
		Noti.defaults |= Notification.DEFAULT_VIBRATE;
		Noti.defaults |= Notification.DEFAULT_LIGHTS;
		Noti.defaults |= Notification.DEFAULT_SOUND;
		Noti.setLatestEventInfo(MainService.this, "金山KOA提醒", text, appIntent);
		KoaNotiManager.notify(R.string.string_wf_notifyNewWf, Noti);
	}

	/**
	 * 获得跳转参数
	 */
	public WFTaskModel getJumpParam() {
		HashMap<String, Object> param = new HashMap<String, Object>();
		param.put("type", "1");
		String socketCmd = "KOA_Mobile_WFTodolistJson";
		String sissionId = User.getInstance().getUserContext(MainService.this).getSessionId();
		String todolist_json = HttpResponseUtil.getInstance().getGetHttpInfo(socketCmd, sissionId, param,MainService.this);
		if("".equals(todolist_json)||"TIMEOUT".equals(todolist_json)){
	    	return null;
	    }else if(todolist_json.equals("SESSION_TIMEOUT")){
	    	return null;
	    }else if (todolist_json.equals("NETWORK_ERROR")) {
	    	return null;
		}else{
	    	List<WFTaskModel> taskList = JsonUtil.parseWfJsonMulti(todolist_json.substring(1,todolist_json.length()-1));
	    	return taskList.get(0);
	    }	
	}
}
