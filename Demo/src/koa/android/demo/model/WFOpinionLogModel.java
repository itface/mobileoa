package koa.android.demo.model;

/**
 * ������ģ��
 * @author chenM
 *
 */
public class WFOpinionLogModel {
	private String userName;//������
	private String wfPointName;//�ڵ�����
	private String signDate;//ǩ��ʱ��
	private String message;//����
	private String operation;//����ѡ��
	
	public WFOpinionLogModel() {
		super();
	}
	
	public WFOpinionLogModel(String userName, String wfPointName,
			String signDate, String message, String operation) {
		super();
		this.userName = userName;
		this.wfPointName = wfPointName;
		this.signDate = signDate;
		this.message = message;
		this.operation = operation;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getWfPointName() {
		return wfPointName;
	}
	public void setWfPointName(String wfPointName) {
		this.wfPointName = wfPointName;
	}
	public String getSignDate() {
		return signDate;
	}
	public void setSignDate(String signDate) {
		this.signDate = signDate;
	}
	public String getMessage() {
		return message;
	}
	public void setMessage(String message) {
		this.message = message;
	}
	public String getOperation() {
		return operation;
	}
	public void setOperation(String operation) {
		this.operation = operation;
	}
	
}
