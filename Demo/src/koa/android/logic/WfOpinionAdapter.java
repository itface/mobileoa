package koa.android.logic;

import java.util.List;

import koa.android.demo.R;
import koa.android.demo.model.WFOpinionLogModel;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

 /**
 * 定义审批意见列表的适配器
 * @author chenM
 *
 */
public class WfOpinionAdapter extends BaseAdapter{
	public List<WFOpinionLogModel> alls;
	public Context context;
	
	public WfOpinionAdapter(Context con,List<WFOpinionLogModel> list){
		super();
		alls = list;
		context = con;
	}

	@Override
	public int getCount() {
		return alls.size();
	}

	@Override
	public WFOpinionLogModel getItem(int position) {
		return alls.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View opinion_item_View = null;
		opinion_item_View = LayoutInflater.from(context).inflate(R.layout.wfopinionlog_item,null);
		TextView wf_opinion_userName = (TextView)opinion_item_View.findViewById(R.id.wf_opinion_userName);
		TextView wf_opinion_wfPointName = (TextView)opinion_item_View.findViewById(R.id.wf_opinion_wfPointName);
		TextView wf_opinion_operation = (TextView)opinion_item_View.findViewById(R.id.wf_opinion_operation);
		TextView wf_opinion_message = (TextView)opinion_item_View.findViewById(R.id.wf_opinion_message);
		TextView wf_opinion_signDate = (TextView)opinion_item_View.findViewById(R.id.wf_opinion_signDate);
		TextView wf_opinion_other = (TextView)opinion_item_View.findViewById(R.id.wf_opinion_other);
		String opinion_msg = alls.get(position).getMessage();
		if(opinion_msg!=null&&!"".equals(opinion_msg)){
			if(alls.get(position).getUserName()!=null){
				wf_opinion_userName.setText(alls.get(position).getUserName());
			}
			if(alls.get(position).getWfPointName()!=null){
				wf_opinion_wfPointName.setText(alls.get(position).getWfPointName());
			}
			if(alls.get(position).getOperation()!=null){
				wf_opinion_operation.setText(alls.get(position).getOperation());
			}
			if(opinion_msg!=null){
				wf_opinion_message.setText(opinion_msg);
			}
			String signDate = alls.get(position).getSignDate();
			if(signDate!=null){
				if(signDate.length()==19&&signDate.contains(":")){
					signDate = signDate.substring(5,16);
					wf_opinion_signDate.setText(signDate);
				}else{
					wf_opinion_signDate.setText(signDate);
				}
				wf_opinion_signDate.setTextSize(14);
			}
			wf_opinion_other.setText("意见留言");
			wf_opinion_other.setTextColor(Color.BLACK);
		}else{
			wf_opinion_userName.setVisibility(View.GONE);
			wf_opinion_operation.setVisibility(View.GONE);
			if(alls.get(position).getWfPointName()!=null){
				wf_opinion_wfPointName.setText(alls.get(position).getWfPointName());
			}
			String signDate = alls.get(position).getSignDate();
			if(signDate!=null){
				if(signDate.length()==19&&signDate.contains(":")){
					signDate = signDate.substring(5,16);
					wf_opinion_signDate.setText(signDate);
				}else{
					wf_opinion_signDate.setText(signDate);
				}
				wf_opinion_signDate.setTextSize(14);
			}
			if(alls.get(position).getUserName()!=null){
				wf_opinion_other.setText(alls.get(position).getUserName());
			}
			if(alls.get(position).getOperation()!=null){
				wf_opinion_message.setText(alls.get(position).getOperation());
			}
		}
		return opinion_item_View;
	}
}