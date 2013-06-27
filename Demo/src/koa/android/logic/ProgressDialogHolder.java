package koa.android.logic;

import java.util.LinkedList;
import java.util.List;

import android.app.Application;
import android.app.ProgressDialog;

public class ProgressDialogHolder extends Application{
	private List<ProgressDialog> progressDialogList = new LinkedList<ProgressDialog>();
	private static ProgressDialogHolder instance;
	
	private ProgressDialogHolder(){}
	
	//����ģʽ�л�ȡΨһ��ProgressDialogHolderʵ��
	public static ProgressDialogHolder getInstance(){
		if(null==instance){
			instance = new ProgressDialogHolder();
		}
		return instance;
	}
	//���ProgressDialog��������  
	public void addActivity(ProgressDialog progressDialog){
		progressDialogList.add(progressDialog);
	}
	
	//��������ProgressDialog
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
