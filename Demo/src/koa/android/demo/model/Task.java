package koa.android.demo.model;

import java.util.Map;
/**
 * 任务类用来统一定义任务
 * @author chenM
 *
 */
public class Task
{

	//任务ID
	private int taskId;
	
	//服务器socketCmd
	private String socketCmd;
	
	//用户
	private String sessionId;
	
	//
	private String activityName;
	
	//参数
	private Map<String, Object> taskParams;
	
	//统一请求数据
	public static final int KOA_COMMON=1;
	
	//登录
	public static final int KOA_LOGIN=2;
	
	//无需数据返回的请求
	public static final int KOA_NO_BACK = 3;
	
	
	public Task(int taskId, Map<String, Object> taskParams)
	{
		this.taskId = taskId;
		this.taskParams = taskParams;
	}

	public Task(int taskId, String socketCmd, String sessionId,String activityName, Map<String, Object> taskParams) {
		this.taskId = taskId;
		this.socketCmd = socketCmd;
		this.sessionId = sessionId;
		this.activityName = activityName;
		this.taskParams = taskParams;
	}

	public int getTaskId()
	{
		return taskId;
	}

	public void setTaskId(int taskId)
	{
		this.taskId = taskId;
	}



	public Map<String, Object> getTaskParams()
	{
		return taskParams;
	}



	public void setTaskParams(Map<String, Object> taskParams)
	{
		this.taskParams = taskParams;
	}



	public String getSocketCmd() {
		return socketCmd;
	}



	public void setSocketCmd(String socketCmd) {
		this.socketCmd = socketCmd;
	}



	public String getSessionId() {
		return sessionId;
	}



	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}

	public String getActivityName() {
		return activityName;
	}

	public void setActivityName(String activityName) {
		this.activityName = activityName;
	}
	
}
