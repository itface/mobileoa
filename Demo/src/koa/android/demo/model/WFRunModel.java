package koa.android.demo.model;

import java.io.Serializable;


public class WFRunModel implements Serializable{
	private static final long serialVersionUID = 1L;
	/**
	 * 任务状态：流程流转异常 
	 */
	public static final int TASK_TYPE_ERROR = -1;
	/**
	 * 任务状态：流程顺序办理
	 */
	public static final int TASK_TYPE_TRANSACTION = 0;
	/**
	 *任务状态： 流程归档
	 */
	public static final int TASK_TYPE_ARCHIVES = 1;
	/**
	 *任务状态： 没找到下一办理人
	 */
	public static final int TASK_TYPE_NOFIND_SENDER = 2;
	/**
	 * 任务多人并签时，任务正常办理结束
	 */
	public static final int TASK_TYPE_NOFIND_TASK_END = 3;


	
	/**
	 * 办理状态：
	 * (静态常量)
	 */
	public static final int TRANSITION_TYPE_USERINPUT = 0;
	/**
	 * 办理状态：
	 * (静态常量)
	 */
	public static final int TRANSITION_TYPE_SELECT = 1;
	
	/**
	 * 办理状态：
	 * (静态常量)
	 */
	public static final int TRANSITION_TYPE_SENDALL = 2;
	
	
	private int stepLimit;
	
	/**
	 * 路由类型                    0：会签 1：并签
	 */
	private int routePointType;
	
	/**
	 * 任务标题
	 */
	private String taskTitle ;
	
	/**
	 * 流程任务状态
	 */
	private int taskStatus;

	/**
	 * 优先级
	 */
	private int priority;
	
	
	

	/**
	 * 办理用户列表
	 */
	private String userlist;
	
	
	/**
	 * 办理类型
	 * 0：用户录入地址
	 * 1：选择发送
	 * 2：直接发送
	 */
	private int tranType;
	
	
	private int taskId;
	private int meId;
	private int workflowId;
	private int workflowStepId;
	private int instanceId;
	private int localDepartmentId;
	
	/**
	 * 节点办理人数
	 */
	private int num;
	/**
	 * 办理类型
	 */
	private int showType;
	
	
	/**
	 * 获得下一节点办理用户
	 * @return
	 */
	public String getUserlist() {
		return userlist;
	}
	/**
	 * 设置下一节点办理用户
	 * @param userlist
	 */
	public void setUserlist(String userlist) {
		this.userlist = userlist;
	}

	/**
	 * 获得流转类型
	 * @return
	 */
	public int getTranType() {
		return tranType;
	}

	/**
	 * 设置流转类型
	 * @param tranType
	 */
	public void setTranType(int tranType) {
		this.tranType = tranType;
	}
	
	/**
	 * 流程节点编号
	 */
	private int wfStepNo;
	
	/**
	 * 流程节点名称
	 */
	private String wfStepNAME;
	
	/**
	 * 获得下一节点跳转编号
	 * @return
	 */
	public int getWfStepNo() {
		return wfStepNo;
	}

	/**
	 * 设置下一节点跳转编号
	 * @param wfStepNo
	 */
	public void setWfStepNo(int wfStepNo) {
		this.wfStepNo = wfStepNo;
	}
	
	/**
	 * 获取节点名称
	 * @return
	 */
	public String getWfStepNAME() {
		return wfStepNAME;
	}
	/**
	 * 设置节点名称
	 * @param wfStepNAME
	 */
	public void setWfStepNAME(String wfStepNAME) {
		this.wfStepNAME = wfStepNAME;
	}


	
	/**
	 * 获取流程任务标题
	 * @return
	 */
	public String getTaskTitle() {
		return taskTitle;
	}
	/**
	 * 设置流程任务标题
	 * @return
	 */
	public void setTaskTitle(String taskTitle) {
		this.taskTitle = taskTitle;
	}


	/**
	 * 获得优先级
	 * @return
	 */
	public int getPriority() {
		return priority;
	}

	/**
	 * 设置优先级
	 * @param priority
	 */
	public void setPriority(int priority) {
		this.priority = priority;
	}

	public int getTaskStatus() {
		return taskStatus;
	}

	public void setTaskStatus(int taskStatus) {
		this.taskStatus = taskStatus;
	}
	
	public int getRoutePointType() {
		return routePointType;
	}
	public void setRoutePointType(int routePointType) {
		this.routePointType = routePointType;
	}
	public int getStepLimit() {
		return stepLimit;
	}
	public void setStepLimit(int stepLimit) {
		this.stepLimit = stepLimit;
	}
	public int getTaskId() {
		return taskId;
	}
	public void setTaskId(int taskId) {
		this.taskId = taskId;
	}
	public int getMeId() {
		return meId;
	}
	public void setMeId(int meId) {
		this.meId = meId;
	}
	public int getWorkflowId() {
		return workflowId;
	}
	public void setWorkflowId(int workflowId) {
		this.workflowId = workflowId;
	}
	public int getWorkflowStepId() {
		return workflowStepId;
	}
	public void setWorkflowStepId(int workflowStepId) {
		this.workflowStepId = workflowStepId;
	}
	public int getInstanceId() {
		return instanceId;
	}
	public void setInstanceId(int instanceId) {
		this.instanceId = instanceId;
	}
	public int getLocalDepartmentId() {
		return localDepartmentId;
	}
	public void setLocalDepartmentId(int localDepartmentId) {
		this.localDepartmentId = localDepartmentId;
	}
	public int getNum() {
		return num;
	}
	public void setNum(int num) {
		this.num = num;
	}
	public int getShowType() {
		return showType;
	}
	public void setShowType(int showType) {
		this.showType = showType;
	}
	
	
}
