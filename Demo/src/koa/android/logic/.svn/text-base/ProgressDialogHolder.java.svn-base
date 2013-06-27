package koa.android.logic;

import java.util.LinkedList;
import java.util.List;

import android.app.Application;
import android.app.ProgressDialog;

public class ProgressDialogHolder extends Application{
	private List<ProgressDialog> progressDialogList = new LinkedList<ProgressDialog>();
	private static ProgressDialogHolder instance;
	
	private ProgressDialogHolder(){}
	
	//单例模式中获取唯一的ProgressDialogHolder实例
	public static ProgressDialogHolder getInstance(){
		if(null==instance){
			instance = new ProgressDialogHolder();
		}
		return instance;
	}
	//添加ProgressDialog到容器中  
	public void addActivity(ProgressDialog progressDialog){
		progressDialogList.add(progressDialog);
	}
	
	//遍历所有ProgressDialog
	public void exit(){
		for(ProgressDialog progressDialog:progressDialogList){
			progressDialog.dismiss();
		}
		progressDialogList.clear();
	}

	public List<ProgressDialog> getProgressDialogList() {
		return progressDialogList;
	}

	public void setProgressDialogList(List<ProgressDialog> progressDialogList) {
		this.progressDialogList = progressDialogList;
	}
}
