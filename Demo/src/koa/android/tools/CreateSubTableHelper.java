package koa.android.tools;

import koa.android.demo.R;
import koa.android.demo.WFSubListActivity;

import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;


/**
 * �����ӱ������
 * @author chenM
 *
 */
public class CreateSubTableHelper{
	//����SD���е��ļ���
	private static CreateSubTableHelper instance;
	private static Object lock = new Object();
	
	public static CreateSubTableHelper getInstance() {
		if (instance == null) {
			synchronized (lock) {
				if (instance == null) {
					instance = new CreateSubTableHelper();
				}
			}
		}
		return instance;
	}
	
	/**
	 * ��̬�����ӱ�
	 * @param fromActivity		             ��Դ��Activity
	 * @param con					���ö���
	 * @param instanceId			ʵ��ID
	 * @param taskId				����ID
	 * @param subTableJson			�ӱ�JSON
	 * @param wf_detail_sublist_content	�����ӱ��λ��
	 */
	public void createSubTable(String fromActivity,final Context con,final String instanceId,final String taskId,String subTableJson ,LinearLayout wf_detail_sublist_content){
		try{
			JSONArray jsonArray = new JSONArray(subTableJson);
			int rowNum = jsonArray.length();
			if(rowNum==1){
				JSONObject jsonObj = ((JSONObject) jsonArray.opt(0));
				String tableTitle = jsonObj.getString("tableTitle");
				String tableName = jsonObj.getString("tableName");
				RelativeLayout firstRow = createSubTableRow(fromActivity,con,instanceId,taskId,tableTitle, R.drawable.wf_detail_subtable_1_1,tableName);
				wf_detail_sublist_content.addView(firstRow);
			}else{
				JSONObject firstjsonObj = ((JSONObject) jsonArray.opt(0));
				String firstTableTitle = firstjsonObj.getString("tableTitle");
				String firstTableName = firstjsonObj.getString("tableName");
				RelativeLayout firstRow = createSubTableRow(fromActivity,con,instanceId,taskId,firstTableTitle, R.drawable.wf_detail_subtable_2_1,firstTableName);
				wf_detail_sublist_content.addView(firstRow);
				if(rowNum>2){
					for(int i=1;i<rowNum-1;i++){
						JSONObject inJsonObj = ((JSONObject) jsonArray.opt(i));
						String inTableTitle = inJsonObj.getString("tableTitle");
						String inTableName = inJsonObj.getString("tableName");
						RelativeLayout inRow = createSubTableRow(fromActivity,con,instanceId,taskId,inTableTitle, R.drawable.wf_detail_subtable_2_2,inTableName);
						wf_detail_sublist_content.addView(inRow);
					}
				}
				JSONObject lastjsonObj = ((JSONObject) jsonArray.opt(rowNum-1));
				String lastTableTitle = lastjsonObj.getString("tableTitle");
				String lastTableName = lastjsonObj.getString("tableName");
				RelativeLayout lastRow = createSubTableRow(fromActivity,con,instanceId,taskId,lastTableTitle, R.drawable.wf_detail_subtable_2_3,lastTableName);
				wf_detail_sublist_content.addView(lastRow);
			}
		}catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * �����ӱ��һ��
	 * @param tableName
	 * @param resBg
	 */
	public RelativeLayout createSubTableRow(final String fromActivity,final Context con,final String instanceId,final String taskId,final String tableTitle,int resBg,final String tableName){
		//����һ��
		RelativeLayout row = new RelativeLayout(con);
		LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT);
		row.setLayoutParams(lp);
		row.setBackgroundResource(resBg);
		//�����ӱ�ͼ��
		ImageView il = new ImageView(con);
		il.setImageResource(R.drawable.wf_detail_subtable_icon);
		il.setPadding(10, 3, 5, 3);
		RelativeLayout.LayoutParams ilp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,RelativeLayout.LayoutParams.FILL_PARENT);
		ilp.addRule(RelativeLayout.CENTER_VERTICAL);
		il.setLayoutParams(ilp);
		il.setId(1);
		row.addView(il,ilp);
		//��������
		TextView rowTitle = new TextView(con);
		rowTitle.setText(tableTitle);
		rowTitle.setTextSize(16);
		rowTitle.setTextColor(Color.parseColor("#3F8AC5"));
		rowTitle.setPadding(5, 3, 3, 3);
		rowTitle.setGravity(Gravity.CENTER_VERTICAL);
		RelativeLayout.LayoutParams rp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,RelativeLayout.LayoutParams.FILL_PARENT);
		rp.addRule(RelativeLayout.RIGHT_OF,1);
		rp.addRule(RelativeLayout.CENTER_VERTICAL);
		row.addView(rowTitle,rp);
		//������ͷ
		ImageView iv = new ImageView(con);
		iv.setImageResource(R.drawable.wf_detail_opinnion_history_bar_icon_right);
		iv.setPadding(3, 3, 10, 3);
		RelativeLayout.LayoutParams ip = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,RelativeLayout.LayoutParams.FILL_PARENT);
		ip.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
		ip.addRule(RelativeLayout.CENTER_VERTICAL);
		row.addView(iv,ip);
		//���Ӽ�����
		row.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Intent intent = new Intent();
				intent.putExtra("subTableTitle", tableTitle);
				intent.putExtra("subTableName", tableName);
				intent.putExtra("instanceId", instanceId);
				intent.putExtra("taskId", taskId);
				intent.putExtra("fromActivity", fromActivity);
				intent.setClass(con, WFSubListActivity.class);
				con.startActivity(intent);
			}
		});
		return row;
	}
	
}
