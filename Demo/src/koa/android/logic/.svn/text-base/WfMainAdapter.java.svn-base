package koa.android.logic;

import java.util.HashMap;
import java.util.List;

import koa.android.demo.R;
import koa.android.tools.SetBackGround;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * ¡˜≥Ã  ≈‰∆˜
 * @author chenM
 */
public class WfMainAdapter extends BaseAdapter{
	public List<HashMap<String, Object>> alls;
	public Context context;
	    	
	public WfMainAdapter(Context con,List<HashMap<String, Object>> list){
		super();
		alls = list;
		context = con;
	}
	
	@Override
	public int getCount() {
		return alls.size();
	}

	@Override
	public HashMap<String, Object> getItem(int position) {
		return alls.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent){
		View main_item_View = null;
		main_item_View = LayoutInflater.from(context).inflate(R.layout.menuitem, null);
		ImageView ItemImage = (ImageView) main_item_View.findViewById(R.id.ItemImage);
		TextView ItemText = (TextView)main_item_View.findViewById(R.id.ItemText);
		TextView toDoNum = (TextView)main_item_View.findViewById(R.id.toDoNum);
		String text = (String)alls.get(position).get("ItemText");
		int temp = text.indexOf("(");
		if(temp>0){
			String str = text.substring(0,temp);
			String num = text.substring(temp+1,text.length()-1);
			ItemText.setText(str);
			toDoNum.setVisibility(View.VISIBLE);
			toDoNum.setText(num);
		}else{
			ItemText.setText(text);
		}
		int imgRes = (Integer)alls.get(position).get("ItemImage");
		ItemImage.setImageResource(imgRes);
		String theme = SetBackGround.getInstance().getTheme(context);
		if(theme.equals("black")){
			ItemText.setTextColor(Color.BLACK);
		}else{
			ItemText.setTextColor(Color.WHITE);
		}
		return main_item_View;
	}
}
