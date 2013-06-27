package koa.android.logic;

import java.util.List;

import koa.android.demo.R;
import koa.android.demo.WfCancelActivity;
import koa.android.demo.model.WFTaskModel;
import koa.android.tools.SetBackGround;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * 流程适配器(有收回按钮,已办)
 * @author chenM
 */
public class WfCancelAdapter extends BaseAdapter{
	public List<WFTaskModel> alls;
	public Context context;
	public String fromActivity;
	    	
	public WfCancelAdapter(Context con,List<WFTaskModel> list,String fromActivity){
		super();
		alls = list;
		context = con;
		this.fromActivity = fromActivity;
	}
	
	@Override
	public int getCount() {
		return alls.size();
	}

	@Override
	public WFTaskModel getItem(int position) {
		return alls.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}
	
	@Override
	public View getView(final int position, View convertView, ViewGroup parent){
		View todolist_item_View = null;
		todolist_item_View = LayoutInflater.from(context).inflate(R.layout.todolist_item, null);
		TextView taskOwner = (TextView)todolist_item_View.findViewById(R.id.taskOwner);
		TextView taskDate = (TextView)todolist_item_View.findViewById(R.id.taskDate);
		TextView flowTitle = (TextView)todolist_item_View.findViewById(R.id.flowTitle);
		ImageView Msg_isRead = (ImageView)todolist_item_View.findViewById(R.id.Msg_isRead);
		Button wf_list_item_cancel_button = (Button)todolist_item_View.findViewById(R.id.wf_list_item_cancel_button);
		if(alls.get(position).getOwner_CN()!=null){
			taskOwner.setText(alls.get(position).getOwner_CN());
		}
		if(alls.get(position).getTaskdate()!=null){
			taskDate.setText(alls.get(position).getTaskdate());
		}
		if(alls.get(position).getTaskTitle()!=null){
			flowTitle.setText(alls.get(position).getTaskTitle());
		}
		Msg_isRead.setImageResource(R.drawable.wf_list_item_other_icon);
		if(alls.get(position).getIsCancel()!=null&&alls.get(position).getIsCancel().equals("true")){
			wf_list_item_cancel_button.setVisibility(View.VISIBLE);
			SetBackGround.getInstance().setTheme(context,wf_list_item_cancel_button,"list_cancle",R.drawable.wf_list_cancle_btn_blue,null);
			wf_list_item_cancel_button.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v){
					Intent intent = new Intent();
					intent.putExtra("flowTitle", alls.get(position).getTaskTitle());
		    		intent.putExtra("taskOwner", alls.get(position).getOwner());
		    		intent.putExtra("targetUser", alls.get(position).getTagetUser());
		    		intent.putExtra("taskDate", alls.get(position).getTaskdate());
		    		intent.putExtra("instanceId", alls.get(position).getInstanceId());
		    		intent.putExtra("taskId", alls.get(position).getTaskId());
		    		intent.putExtra("isCancel", alls.get(position).getIsCancel());
		    		if("TodoListActivity".equals(fromActivity)){
		    			intent.putExtra("fromActivity", "TodoListActivity");//放入来源的Activity
		    		}else if("HistoryListActivity".equals(fromActivity)){
		    			intent.putExtra("fromActivity", "HistoryListActivity");//放入来源的Activity
		    		}
					intent.setClass(context, WfCancelActivity.class);
					context.startActivity(intent);
				}
			});
		}else{
			wf_list_item_cancel_button.setVisibility(View.GONE);
		}
		return todolist_item_View;
	}
}
