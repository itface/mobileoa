package koa.android.demo.model;

/**
 * 审核意见模型
 * @author chenM
 *
 */
public class WFOpinionLogModel {
	private String userName;//发起人
	private String wfPointName;//节点名称
	private String signDate;//签办时间
	private String message;//留言
	private String operation;//办理选项
	
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
