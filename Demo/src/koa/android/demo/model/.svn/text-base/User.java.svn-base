package koa.android.demo.model;

import android.content.Context;
import android.preference.PreferenceManager;



/**
 * 用户上下文
 * @author chenM
 *
 */
public class User implements java.io.Serializable{
	private static final long serialVersionUID = 1L;
	private String userId;//用户名
	private String password;//密码
	private String sessionId;//sessionId
	private String userName;//用户中文姓名
	private String deviceId;
	private static User instance;
	private static Object lock = new Object();
	
	//私有构造方法
	private User(){
		
	}
	
	public static User getInstance() {
		if (instance == null) {
			synchronized (lock) {
				if (instance == null) {
					instance = new User();
				}
			}
		}
		return instance;
	}
	
	//获得用户
	public User getUserContext(Context context) {
		//内存中的读取速度比XML高
		if(null==instance||null==instance.getSessionId()||null==instance.getUserName()||null==instance.getDeviceId()||null==instance.getUserId()){
			String userName = PreferenceManager.getDefaultSharedPreferences(context).getString("userName","");
			String sessionId = PreferenceManager.getDefaultSharedPreferences(context).getString("sessionId","");
			String userId = PreferenceManager.getDefaultSharedPreferences(context).getString("userId","");
			String password = "";
			String deviceId = PreferenceManager.getDefaultSharedPreferences(context).getString("deviceId","");
			User user = new User(userId, password, sessionId,userName,deviceId);
			instance = user;
			return user;
		}else{
			return instance;
		}
	}
	
	//设置用户
	public void setUserContext(User user,Context context) {
		if(null!=user){
			String userName = user.getUserName();
			if(userName==null){
				userName = "";
			}
			String sessionId = user.getSessionId();
			if(sessionId==null){
				sessionId = "";
			}
			String userId = user.getUserId();
			if(userId==null){
				userId = "";
			}
			String password = user.getPassword();
			if(password==null){
				password = "";
			}
			String deviceId = user.getDeviceId();
			if(deviceId==null){
				deviceId = "";
			}
			//设置内存中的User
			User user_noNull = new User(userId, password, sessionId, userName, deviceId);
			instance = user_noNull;
			//设置XML中的User
			PreferenceManager.getDefaultSharedPreferences(context).edit().putString("userName",userName).commit();
			PreferenceManager.getDefaultSharedPreferences(context).edit().putString("sessionId",sessionId).commit();
			PreferenceManager.getDefaultSharedPreferences(context).edit().putString("userId",userId).commit();
			//不保存用户密码
			PreferenceManager.getDefaultSharedPreferences(context).edit().putString("password","").commit();
			PreferenceManager.getDefaultSharedPreferences(context).edit().putString("deviceId",deviceId).commit();
		}else{
			PreferenceManager.getDefaultSharedPreferences(context).edit().putString("userName","").commit();
			PreferenceManager.getDefaultSharedPreferences(context).edit().putString("sessionId","").commit();
			PreferenceManager.getDefaultSharedPreferences(context).edit().putString("userId","").commit();
			PreferenceManager.getDefaultSharedPreferences(context).edit().putString("password","").commit();
			PreferenceManager.getDefaultSharedPreferences(context).edit().putString("deviceId","").commit();
		}
	}
	
	public User(String userId, String password, String sessionId,
			String userName, String deviceId) {
		super();
		this.userId = userId;
		this.password = password;
		this.sessionId = sessionId;
		this.userName = userName;
		this.deviceId = deviceId;
	}

	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getSessionId() {
		return sessionId;
	}
	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getDeviceId() {
		return deviceId;
	}
	public void setDeviceId(String deviceId) {
		this.deviceId = deviceId;
	}
	
}
