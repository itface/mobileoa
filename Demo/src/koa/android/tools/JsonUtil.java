package koa.android.tools;

import java.util.ArrayList;
import java.util.List;

import koa.android.demo.model.WFOpinionLogModel;
import koa.android.demo.model.WFRunModel;
import koa.android.demo.model.WFTaskModel;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


public class JsonUtil {
	//解析JSON的方法
	public static String parseJson(String strResult,String fieldName) { 
    	String str = "";
    	try {
			JSONObject jsonObj = new JSONObject(strResult);
			boolean isfieldNameNull = jsonObj.isNull(fieldName);
			if(!isfieldNameNull){
				str = jsonObj.getString(fieldName);
			}
		} catch (JSONException e) { 
			e.printStackTrace();
		}
        return str;
    }
	/**
	 * 解析流程任务列表JSON
	 * @param strResult
	 * @return
	 */
	public static List<WFTaskModel> parseWfJsonMulti(String strResult) {
		List<WFTaskModel> taskList = new ArrayList<WFTaskModel>();
		try {
			JSONObject jsonObject = new JSONObject(strResult);
			JSONArray arrayJson = jsonObject.getJSONArray("ToDoItem");
			for (int i = 0; i < arrayJson.length(); i++) {
				JSONObject jsonObj = ((JSONObject) arrayJson.opt(i));
				WFTaskModel model = new WFTaskModel();
				boolean isFlowTitleNull = jsonObj.isNull("flowTitle");
				if (!isFlowTitleNull) {
					String taskTitle = jsonObj.getString("flowTitle");
					model.setTaskTitle(taskTitle);
				}
				boolean isTargetUserNull = jsonObj.isNull("targetUser");
				if (!isTargetUserNull) {
					String targetUser = jsonObj.getString("targetUser");
					model.setTagetUser(targetUser);
				}
				boolean isTaskDateNull = jsonObj.isNull("taskDate");
				if (!isTaskDateNull) {
					String taskDate = jsonObj.getString("taskDate");
					model.setTaskdate(taskDate);
				}
				boolean isTaskOwnerNull = jsonObj.isNull("userCNName");
				if (!isTaskOwnerNull) {
					String taskOwner = jsonObj.getString("taskOwner");
					model.setOwner(taskOwner);
				}
				boolean isUserCNNameNull = jsonObj.isNull("userCNName");
				if (!isUserCNNameNull) {
					String taskOwner_CN = jsonObj.getString("userCNName");
					model.setOwner_CN(taskOwner_CN);
				}
				boolean isBindIdNull = jsonObj.isNull("bindId");
				if (!isBindIdNull) {
					String instanceId = jsonObj.getString("bindId");
					model.setInstanceId(instanceId);
				}
				boolean isTaskIdNull = jsonObj.isNull("taskId");
				if (!isTaskIdNull) {
					String taskId = jsonObj.getString("taskId");
					model.setTaskId(taskId);
				}
				boolean isTaskStatusNull = jsonObj.isNull("taskStatus");
				if (!isTaskStatusNull) {
					String taskStatus = jsonObj.getString("taskStatus");
					model.setStatus(taskStatus);
				}
				boolean isReadNull = jsonObj.isNull("isRead");
				if (!isReadNull) {
					String isRead = jsonObj.getString("isRead");
					model.setIsRead(isRead);
				}
				boolean IsCancelNull = jsonObj.isNull("isCancel");
				if (!IsCancelNull) {
					String isCancel = jsonObj.getString("isCancel");
					model.setIsCancel(isCancel);
				}
				taskList.add(model);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return taskList;
	}

	/**
	 * 获得办理动作参数JSON值
	 * @param jsonStr
	 * @param jsonName
	 * @return
	 */
	public static WFRunModel parseRunJson(String jsonStr) {
		WFRunModel model = new WFRunModel();
		try {
			JSONObject jsonObj = new JSONObject(jsonStr.substring(1,jsonStr.length() - 1));
			if (jsonObj != null) {
				int taskId = jsonObj.getInt("taskId");
				model.setTaskId(taskId);
				int workflowid = jsonObj.getInt("workflowid");
				model.setWorkflowId(workflowid);
				int meId = jsonObj.getInt("meId");
				model.setMeId(meId);
				int routePointType = jsonObj.getInt("routePointType");
				model.setRoutePointType(routePointType);
				int tranType = jsonObj.getInt("tranType");
				model.setTranType(tranType);
				int taskStatus = jsonObj.getInt("taskStatus");
				model.setTaskStatus(taskStatus);
				int workflowStepid = jsonObj.getInt("workflowStepid");
				model.setWorkflowStepId(workflowStepid);
				int priority = jsonObj.getInt("priority");
				model.setPriority(priority);
				int instanceId = jsonObj.getInt("instanceId");
				model.setInstanceId(instanceId);
				int stepNo = jsonObj.getInt("stepNo");
				model.setWfStepNo(stepNo);
				boolean isStepNameNull = jsonObj.isNull("stepName");
				if(!isStepNameNull){
					String stepName = jsonObj.getString("stepName");
					model.setWfStepNAME(stepName);
				}
				boolean isUserListNull = jsonObj.isNull("senderUserList");
				if(!isUserListNull){
					String userList = jsonObj.getString("senderUserList");
					model.setUserlist(userList);
				}
				int localDepartmentId = jsonObj.getInt("localDepartmentId");
				model.setLocalDepartmentId(localDepartmentId);
				int num = jsonObj.getInt("num");
				model.setNum(num);
				int showType = jsonObj.getInt("showType");
				model.setShowType(showType);
			}

		} catch (JSONException e) {
			e.printStackTrace();
		}
		return model;
	}
	
	/**
	 * 解析审批意见列表JSON的方法
	 * @return
	 */
	public static List<WFOpinionLogModel> parseWFOpinionLog(String strResult){
		List<WFOpinionLogModel> taskList = new ArrayList<WFOpinionLogModel>();
        try {
        	JSONArray arrayJson = new JSONArray(strResult);
            for(int i = 0; i < arrayJson.length() ; i++){ 
                JSONObject jsonObj = ((JSONObject)arrayJson.opt(i));
                WFOpinionLogModel model = new WFOpinionLogModel();
                
                boolean isUserNameNull = jsonObj.isNull("userName");
				if(!isUserNameNull){
					String userName = jsonObj.getString("userName");
					model.setUserName(userName);
				}
				boolean iswfPointNameNull = jsonObj.isNull("wfPointName");
				if(!iswfPointNameNull){
					String wfPointName = jsonObj.getString("wfPointName"); 
					model.setWfPointName(wfPointName);
				}
				boolean issignDateNull = jsonObj.isNull("signDate");
				if(!issignDateNull){
					String signDate = jsonObj.getString("signDate"); 
					model.setSignDate(signDate);
				}
				boolean ismessageNull = jsonObj.isNull("message");
				if(!ismessageNull){
					String message = jsonObj.getString("message"); 
					model.setMessage(message);
				}
				boolean isoperationNull = jsonObj.isNull("operation");
				if(!isoperationNull){
					String operation = jsonObj.getString("operation");
					model.setOperation(operation);
				}
                taskList.add(model);
            }
        } catch (JSONException e) { 
			e.printStackTrace();
        } 
        return taskList;
	}
	
	/**
	 * 解析流程任务列表JSON
	 * @param strResult
	 * @return
	 */
	public static List<List<JSONObject>> parseWfSubListMulti(String strResult,String fieldName) {
		List<List<JSONObject>> taskList = new ArrayList<List<JSONObject>>();
		try {
			String list = parseJson(strResult, fieldName);
			JSONArray arrayJson = new JSONArray(list);
			for (int i = 0; i < arrayJson.length(); i++) {
				JSONArray inArrayJson = ((JSONArray) arrayJson.opt(i));
				List<JSONObject> inList = new ArrayList<JSONObject>();
				for (int j = 0; j < inArrayJson.length(); j++) {
					JSONObject jsonObj =  (JSONObject)inArrayJson.opt(j);
					inList.add(jsonObj);
				}
				taskList.add(inList);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return taskList;
	}
}
