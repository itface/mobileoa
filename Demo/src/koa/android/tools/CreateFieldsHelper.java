package koa.android.tools;

import koa.android.demo.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.util.TypedValue;
import android.widget.LinearLayout;
import android.widget.TextView;


/**
 * 创建子表帮助类
 * @author chenM
 *
 */
public class CreateFieldsHelper{
	//存在SD卡中的文件夹
	private static CreateFieldsHelper instance;
	private static Object lock = new Object();
	
	public static CreateFieldsHelper getInstance() {
		if (instance == null) {
			synchronized (lock) {
				if (instance == null) {
					instance = new CreateFieldsHelper();
				}
			}
		}
		return instance;
	}
	
	/**
	 * 动态构建显示区域
	 * @param con
	 * @param subTableJson
	 * @param wf_detail_fields
	 */
	public void createSubFields(Context con,String subTableJson ,LinearLayout wf_detail_fields){
		String tableTitle = "";
		String tableName = "";
		try{
			JSONObject jsonObject = new JSONObject(subTableJson);
			JSONArray arrayJson = jsonObject.getJSONArray("fields");
			for (int i = 0; i < arrayJson.length(); i++) {
				JSONObject jsonObj = ((JSONObject) arrayJson.opt(i));
				boolean istableTitleNull = jsonObj.isNull("tableTitle");
				if (!istableTitleNull) {
					tableTitle = jsonObj.getString("tableTitle");
				}
				boolean istableNameNull = jsonObj.isNull("tableName");
				if (!istableNameNull) {
					tableName = jsonObj.getString("tableName");
				}
				LinearLayout row = createRow(con,tableTitle,tableName);
				wf_detail_fields.addView(row);
			}
		}catch (JSONException e) { 
			e.printStackTrace();
        } 
	}
	
	private LinearLayout createRow(Context context,String tableTitle,String tableName){
		LinearLayout row = new LinearLayout(context);
		LinearLayout.LayoutParams rp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT);
		row.setOrientation(LinearLayout.HORIZONTAL);
		row.setLayoutParams(rp);
		TextView keyText = new TextView(context);
		keyText.setBackgroundResource(R.drawable.wf_detail_table_left);
		keyText.setText(tableTitle);
		keyText.setTextColor(Color.BLACK);
		keyText.setTextSize(16);
		Resources resources = keyText.getResources();  
		float pPx = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 8, resources.getDisplayMetrics());
		keyText.setPadding(5, Math.round(pPx), 5, Math.round(pPx));
		float fPx = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 80, resources.getDisplayMetrics());
		LinearLayout.LayoutParams kp = new LinearLayout.LayoutParams(Math.round(fPx),LinearLayout.LayoutParams.FILL_PARENT);
		row.addView(keyText,kp);
		TextView valueText = new TextView(context);
		valueText.setBackgroundResource(R.drawable.wf_detail_table_right);
		valueText.setPadding(5, Math.round(pPx), 5, Math.round(pPx));
		valueText.setSingleLine(false);
		valueText.setText(tableName);
		valueText.setTextColor(Color.parseColor("#404040"));
		valueText.setTextSize(16);
		LinearLayout.LayoutParams vp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT,LinearLayout.LayoutParams.FILL_PARENT);
		row.addView(valueText,vp);
		return row;
	}
}
