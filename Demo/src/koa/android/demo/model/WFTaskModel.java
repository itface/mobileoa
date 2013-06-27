package koa.android.demo.model;

/**
 * 流程任务模型
 * 描述流程任务列表项
 * @author YangDayong
 *
 */
public class WFTaskModel {
	private String taskTitle;
	private String owner;
	private String tagetUser;
	private String taskdate;
	private String instanceId;
	private String taskId;
	private String status;
	private String isRead;
	private String workflowId;
	private String owner_CN;//owner的中文姓名
	private String isCancel;//是否允许收回
	
	
	public String getWorkflowId() {
		return workflowId;
	}
	public void setWorkflowId(String workflowId) {
		this.workflowId = workflowId;
	}
	public String getIsCancel() {
		return isCancel;
	}
	public void setIsCancel(String isCancel) {
		this.isCancel = isCancel;
	}
	public String getIsRead() {
		return isRead;
	}
	public void setIsRead(String isRead) {
		this.isRead = isRead;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getTaskId() {
		return taskId;
	}
	public void setTaskId(String taskId) {
		this.taskId = taskId;
	}
	public String getOwner() {
		return owner;
	}
	public void setOwner(String owner) {
		this.owner = owner;
	}
	public String getTagetUser() {
		return tagetUser;
	}
	public void setTagetUser(String tagetUser) {
		this.tagetUser = tagetUser;
	}
	public String getTaskTitle() {
		return taskTitle;
	}
	public void setTaskTitle(String taskTitle) {
		this.taskTitle = taskTitle;
	}
	
	public String getTaskdate() {
		return taskdate;
	}
	public void setTaskdate(String taskdate) {
		this.taskdate = taskdate;
	}
	public String getInstanceId() {
		return instanceId;
	}
	public void setInstanceId(String instanceId) {
		this.instanceId = instanceId;
	}
	public String getOwner_CN() {
		return owner_CN;
	}
	public void setOwner_CN(String owner_CN) {
		this.owner_CN = owner_CN;
	}
}
