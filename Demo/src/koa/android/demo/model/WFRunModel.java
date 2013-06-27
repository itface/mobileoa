package koa.android.demo.model;

import java.io.Serializable;


public class WFRunModel implements Serializable{
	private static final long serialVersionUID = 1L;
	/**
	 * ����״̬��������ת�쳣 
	 */
	public static final int TASK_TYPE_ERROR = -1;
	/**
	 * ����״̬������˳�����
	 */
	public static final int TASK_TYPE_TRANSACTION = 0;
	/**
	 *����״̬�� ���̹鵵
	 */
	public static final int TASK_TYPE_ARCHIVES = 1;
	/**
	 *����״̬�� û�ҵ���һ������
	 */
	public static final int TASK_TYPE_NOFIND_SENDER = 2;
	/**
	 * ������˲�ǩʱ�����������������
	 */
	public static final int TASK_TYPE_NOFIND_TASK_END = 3;


	
	/**
	 * ����״̬��
	 * (��̬����)
	 */
	public static final int TRANSITION_TYPE_USERINPUT = 0;
	/**
	 * ����״̬��
	 * (��̬����)
	 */
	public static final int TRANSITION_TYPE_SELECT = 1;
	
	/**
	 * ����״̬��
	 * (��̬����)
	 */
	public static final int TRANSITION_TYPE_SENDALL = 2;
	
	
	private int stepLimit;
	
	/**
	 * ·������                    0����ǩ 1����ǩ
	 */
	private int routePointType;
	
	/**
	 * �������
	 */
	private String taskTitle ;
	
	/**
	 * ��������״̬
	 */
	private int taskStatus;

	/**
	 * ���ȼ�
	 */
	private int priority;
	
	
	

	/**
	 * �����û��б�
	 */
	private String userlist;
	
	
	/**
	 * ��������
	 * 0���û�¼���ַ
	 * 1��ѡ����
	 * 2��ֱ�ӷ���
	 */
	private int tranType;
	
	
	private int taskId;
	private int meId;
	private int workflowId;
	private int workflowStepId;
	private int instanceId;
	private int localDepartmentId;
	
	/**
	 * �ڵ��������
	 */
	private int num;
	/**
	 * ��������
	 */
	private int showType;
	
	
	/**
	 * �����һ�ڵ�����û�
	 * @return
	 */
	public String getUserlist() {
		return userlist;
	}
	/**
	 * ������һ�ڵ�����û�
	 * @param userlist
	 */
	public void setUserlist(String userlist) {
		this.userlist = userlist;
	}

	/**
	 * �����ת����
	 * @return
	 */
	public int getTranType() {
		return tranType;
	}

	/**
	 * ������ת����
	 * @param tranType
	 */
	public void setTranType(int tranType) {
		this.tranType = tranType;
	}
	
	/**
	 * ���̽ڵ���
	 */
	private int wfStepNo;
	
	/**
	 * ���̽ڵ�����
	 */
	private String wfStepNAME;
	
	/**
	 * �����һ�ڵ���ת���
	 * @return
	 */
	public int getWfStepNo() {
		return wfStepNo;
	}

	/**
	 * ������һ�ڵ���ת���
	 * @param wfStepNo
	 */
	public void setWfStepNo(int wfStepNo) {
		this.wfStepNo = wfStepNo;
	}
	
	/**
	 * ��ȡ�ڵ�����
	 * @return
	 */
	public String getWfStepNAME() {
		return wfStepNAME;
	}
	/**
	 * ���ýڵ�����
	 * @param wfStepNAME
	 */
	public void setWfStepNAME(String wfStepNAME) {
		this.wfStepNAME = wfStepNAME;
	}


	
	/**
	 * ��ȡ�����������
	 * @return
	 */
	public String getTaskTitle() {
		return taskTitle;
	}
	/**
	 * ���������������
	 * @return
	 */
	public void setTaskTitle(String taskTitle) {
		this.taskTitle = taskTitle;
	}


	/**
	 * ������ȼ�
	 * @return
	 */
	public int getPriority() {
		return priority;
	}

	/**
	 * �������ȼ�
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
