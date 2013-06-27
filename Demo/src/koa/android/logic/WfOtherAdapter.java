package koa.android.logic;

import java.util.List;

import koa.android.demo.R;
import koa.android.demo.model.WFTaskModel;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * 流程适配器(无收回按钮,通知传阅和在办)
 * @author chenM
 */
public class WfOtherAdapter extends BaseAdapter{
	public List<WFTaskModel> alls;
	public Context context;
	    	
	public WfOtherAdapter(Context con,List<WFTaskModel> list){
		super();
		alls = list;
		context = con;
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
	public View getView(int position, View convertView, ViewGroup parent){
		View todolist_item_View = null;
		todolist_item_View = LayoutInflater.from(context).inflate(R.layout.todolist_item, null);
		TextView taskOwner = (TextView)todolist_item_View.findViewById(R.id.taskOwner);
		TextView taskDate = (TextView)todolist_item_View.findViewById(R.id.taskDate);
		TextView flowTitle = (TextView)todolist_item_View.findViewById(R.id.flowTitle);
		ImageView Msg_isRead = (ImageView)todolist_item_View.findViewById(R.id.Msg_isRead);
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
		return todolist_item_View;
	}
}
