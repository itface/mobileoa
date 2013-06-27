package koa.android.logic;

import java.util.LinkedList;
import java.util.List;

import android.app.Application;
import android.widget.Toast;

public class ToastDialogHolder extends Application{
	private List<Toast> ToastList = new LinkedList<Toast>();
	private static ToastDialogHolder instance;
	
	private ToastDialogHolder(){}
	
	//单例模式中获取唯一的ToastHolder实例
	public static ToastDialogHolder getInstance(){
		if(null==instance){
			instance = new ToastDialogHolder();
		}
		return instance;
	}
	//添加Toast到容器中  
	public void addActivity(Toast Toast){
		ToastList.add(Toast);
	}
	
	//遍历所有Toast
	public void exit(){
		for(Toast toast:ToastList){
			if(toast!=null){
				toast.cancel();
			}
		}
		ToastList.clear();
	}

	public List<Toast> getToastList() {
		return ToastList;
	}

	public void setToastList(List<Toast> ToastList) {
		this.ToastList = ToastList;
	}
}
