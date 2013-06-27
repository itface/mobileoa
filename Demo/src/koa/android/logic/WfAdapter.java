package koa.android.logic;

import java.util.List;

import koa.android.demo.R;
import koa.android.demo.model.WFTaskModel;
import koa.android.tools.SetBackGround;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Á÷³ÌÊÊÅäÆ÷(´ý°ì)
 * @author chenM
 */
public class WfAdapter extends BaseAdapter{
	public List<WFTaskModel> alls;
	public Context context;
	    	
	public WfAdapter(Context con,List<WFTaskModel> list){
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
		if(alls.get(position).getStatus().equals("-1")){
			SetBackGround.getInstance().setTheme(context,Msg_isRead,"list_recycle",R.drawable.wf_list_item_recycle_icon_blue,null);
		}else if(alls.get(position).getStatus().equals("2")||alls.get(position).getStatus().equals("9")){
			SetBackGround.getInstance().setTheme(context,Msg_isRead,"list_notice",R.drawable.wf_list_item_notice_icon_blue,null);
		}else{
			if(alls.get(position).getIsRead().equals("true")){
				Msg_isRead.setImageDrawable(null);
				Msg_isRead.setVisibility(View.GONE);
			}else if(alls.get(position).getIsRead().equals("false")){
				SetBackGround.getInstance().setTheme(context,Msg_isRead,"list_unread",R.drawable.wf_list_item_unread_icon_blue,null);
			}
		}
		return todolist_item_View;
	}
}
