package koa.android.logic;

import java.util.List;

import koa.android.demo.R;

import org.json.JSONObject;

import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * 子表适配器
 * @author chenM
 */
public class WfSubListAdapter extends BaseAdapter{
	public List<List<JSONObject>> alls;
	public Context context;
	    	
	public WfSubListAdapter(Context con,List<List<JSONObject>> list){
		super();
		alls = list;
		context = con;
	}
	
	@Override
	public int getCount() {
		return alls.size();
	}

	@Override
	public List<JSONObject> getItem(int position) {
		return alls.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent){
		View todolist_item_View = null;
		todolist_item_View = LayoutInflater.from(context).inflate(R.layout.wf_sublist_item, null);
		LinearLayout wf_sublist_L = (LinearLayout) todolist_item_View.findViewById(R.id.wf_sublist_L);
		List<JSONObject> itemList = alls.get(position);
		try{
			if(itemList.size()==1){
				String key = itemList.get(0).getString("tableTitle");
				String value = itemList.get(0).getString("tableName");
				LinearLayout row = createRow(key,R.drawable.wf_sublist_item_1_1,value,R.drawable.wf_sublist_item_1_2);
				wf_sublist_L.addView(row);
			}else if(itemList.size()==2){
				String firstkey = itemList.get(0).getString("tableTitle");
				String firstvalue = itemList.get(0).getString("tableName");
				LinearLayout firstrow = createRow(firstkey,R.drawable.wf_sublist_item_2_1,firstvalue,R.drawable.wf_sublist_item_2_2);
				wf_sublist_L.addView(firstrow);
				String lastkey = itemList.get(1).getString("tableTitle");
				String lastvalue = itemList.get(1).getString("tableName");
				LinearLayout lastrow = createRow(lastkey,R.drawable.wf_sublist_item_2_5,lastvalue,R.drawable.wf_sublist_item_2_6);
				wf_sublist_L.addView(lastrow);
			}else{
				String firstkey = itemList.get(0).getString("tableTitle");
				String firstvalue = itemList.get(0).getString("tableName");
				LinearLayout firstrow = createRow(firstkey,R.drawable.wf_sublist_item_2_1,firstvalue,R.drawable.wf_sublist_item_2_2);
				wf_sublist_L.addView(firstrow);
				for(int i=1;i<itemList.size()-1;i++){
					String key = itemList.get(i).getString("tableTitle");
					String value = itemList.get(i).getString("tableName");
					LinearLayout row = createRow(key,R.drawable.wf_sublist_item_2_3,value,R.drawable.wf_sublist_item_2_4);
					wf_sublist_L.addView(row);
				}
				String lastkey = itemList.get(itemList.size()-1).getString("tableTitle");
				String lastvalue = itemList.get(itemList.size()-1).getString("tableName");
				LinearLayout lastrow = createRow(lastkey,R.drawable.wf_sublist_item_2_5,lastvalue,R.drawable.wf_sublist_item_2_6);
				wf_sublist_L.addView(lastrow);
			}
		}catch (Exception e) {
			e.printStackTrace();
		}
		return todolist_item_View;
	}
	
	/**
	 * 创建一行数据
	 * @param key		//键
	 * @param keyBg		//键的背景
	 * @param value		//值
	 * @param valueBg	//值的背景
	 * @return
	 */
	public LinearLayout createRow(String key,int keyBg,String value,int valueBg){
		LinearLayout row = new LinearLayout(context);
		LinearLayout.LayoutParams rp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT,LinearLayout.LayoutParams.FILL_PARENT);
		row.setLayoutParams(rp);
		row.setOrientation(LinearLayout.HORIZONTAL);
		TextView keyText = new TextView(context);
		TextView valueText = new TextView(context);
		keyText.setBackgroundResource(keyBg);
		keyText.setText(key);
		keyText.setTextSize(16);
		keyText.setTextColor(Color.BLACK);
		keyText.setPadding(10, 0, 0, 0);
		keyText.setGravity(Gravity.CENTER_VERTICAL);
		//从父child中设置
		LinearLayout.LayoutParams kp = new LinearLayout.LayoutParams(180,LinearLayout.LayoutParams.FILL_PARENT);
		keyText.setLayoutParams(kp);
		valueText.setBackgroundResource(valueBg);
		valueText.setText(value);
		valueText.setTextSize(16);
		valueText.setTextColor(Color.parseColor("#3F8AC5"));
		valueText.setPadding(10, 0, 0, 0);
		valueText.setGravity(Gravity.CENTER_VERTICAL);
		//从父child中设置
		LinearLayout.LayoutParams vp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT,LinearLayout.LayoutParams.FILL_PARENT);
		valueText.setLayoutParams(vp);
		row.addView(keyText); 
		row.addView(valueText);
		return row;
	}
}
