package koa.android.demo.model;

import java.util.Map;
/**
 * ����������ͳһ��������
 * @author chenM
 *
 */
public class Task
{

	//����ID
	private int taskId;
	
	//������socketCmd
	private String socketCmd;
	
	//�û�
	private String sessionId;
	
	//
	private String activityName;
	
	//����
	private Map<String, Object> taskParams;
	
	//ͳһ��������
	public static final int KOA_COMMON=1;
	
	//��¼
	public static final int KOA_LOGIN=2;
	
	//�������ݷ��ص�����
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
