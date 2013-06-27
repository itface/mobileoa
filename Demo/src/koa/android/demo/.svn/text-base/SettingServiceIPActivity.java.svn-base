package koa.android.demo;

import java.util.List;

import koa.android.logic.ActivityHoder;
import koa.android.tools.HttpResponseUtil;
import koa.android.tools.LoginTimeOut;
import koa.android.tools.SetBackGround;
import android.app.Activity;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

/**
 * 设置服务器地址的页面
 * 
 * @author chenM
 * 
 */
public class SettingServiceIPActivity extends Activity {
	private Button title_bt_left;// 回退按钮
	private Button title_bt_right;// 保存按钮
	private TextView top_title;// 顶部文本域
	private EditText Service_ip_edit;// 服务器IP文本框
	public static final String SETTING_SERVICE_IP = "service_ip";
	public static final String SETTING_SERVICE_IP_DEFAULT = "";
	private RelativeLayout service_ip_log_area;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		LoginTimeOut LoginTimeOut = new LoginTimeOut();
		boolean flag = LoginTimeOut.timeOut(SettingServiceIPActivity.this);
		if (!flag) {
			setContentView(R.layout.setting_service_ip);
			setUpView();
			setUpListeners();
			init();
			// 将Activity放到Activity中
			ActivityHoder.getInstance().addActivity(this);
		}
	}

	private void init() {
		String curIP = PreferenceManager.getDefaultSharedPreferences(SettingServiceIPActivity.this).getString(SettingServiceIPActivity.SETTING_SERVICE_IP, SettingServiceIPActivity.SETTING_SERVICE_IP_DEFAULT);
		Service_ip_edit.setText(curIP);
		List<String> ipList = HttpResponseUtil.getInstance().getServiceIPLog(SettingServiceIPActivity.this);
		if (ipList != null && ipList.size() > 0) {
			createIpLog_area(ipList);
		}
	}

	@Override
	protected void onRestart() {
		super.onRestart();
		setBackground();
	}

	// 设置背景图
	private void setBackground() {
		// 设置背景图
		SetBackGround.getInstance().setTheme(this, top_title, "top_title", R.drawable.ui_title_bg_blue, null);
		SetBackGround.getInstance().setTheme(this, title_bt_left, "ui_backbtn", R.drawable.ui_title_back_btn_blue, null);
		SetBackGround.getInstance().setTheme(this, title_bt_right, "ui_title_save_btn", R.drawable.ui_title_save_btn_blue, null);
	}

	private void setUpView() {
		title_bt_left = (Button) findViewById(R.id.title_bt_left);
		top_title = (TextView) findViewById(R.id.top_title);
		title_bt_right = (Button) findViewById(R.id.title_bt_right);
		Service_ip_edit = (EditText) findViewById(R.id.Service_ip_edit);
		service_ip_log_area = (RelativeLayout) findViewById(R.id.service_ip_log_area);
	}

	private void setUpListeners() {
		top_title.setText("访问设置");
		title_bt_left.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		title_bt_right.setVisibility(View.VISIBLE);
		setBackground();
		title_bt_right.setOnClickListener(new OnClickListener() {
			// 保存服务器地址
			@Override
			public void onClick(View v) {
				String serviceIP = Service_ip_edit.getText().toString();
				if (serviceIP.equals("")) {
					Toast.makeText(SettingServiceIPActivity.this, "请输入访问码", Toast.LENGTH_LONG).show();
				} else {
					if (serviceIP.indexOf("：") > 0) {
						serviceIP = serviceIP.replaceAll("：", ":");
					}
					PreferenceManager.getDefaultSharedPreferences(SettingServiceIPActivity.this).edit().putString(SETTING_SERVICE_IP, serviceIP).commit();
					Toast.makeText(SettingServiceIPActivity.this, "保存成功", Toast.LENGTH_LONG).show();
					// 将目前的IP置为空，重新解析
					HttpResponseUtil.getInstance().curIp = "";
					finish();
				}
			}
		});
	}

	/**
	 * 创建IP地址选择区域
	 * 
	 * @param ipList
	 */
	private void createIpLog_area(List<String> ipList) {
		RadioGroup radiogroup = new RadioGroup(this);
		RelativeLayout.LayoutParams rgp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.FILL_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
		radiogroup.setLayoutParams(rgp);
		for (int i = 0; i < ipList.size(); i++) {
			final RadioButton radio = new RadioButton(this);
			String item = ipList.get(i);
			radio.setText(item);
			radio.setId(i);
			radio.setTextAppearance(getApplicationContext(), R.style.wf_exam_button_style);
			radio.setSingleLine();
			radio.setButtonDrawable(R.drawable.wf_detail_operate_bar_button_trans);
			if (ipList.size() == 1) {
				radio.setBackgroundResource(R.drawable.wf_detail_subtable_1_1);
			} else {
				if (i == 0) {
					radio.setBackgroundResource(R.drawable.wf_detail_subtable_2_1);
				} else if (i == ipList.size() - 1) {
					radio.setBackgroundResource(R.drawable.wf_detail_subtable_2_3);
				} else {
					radio.setBackgroundResource(R.drawable.wf_detail_subtable_2_2);
				}
			}
			RadioGroup.LayoutParams rbp = new RadioGroup.LayoutParams(RadioGroup.LayoutParams.FILL_PARENT, RadioGroup.LayoutParams.WRAP_CONTENT);
			radio.setLayoutParams(rbp);
			radio.setPadding(10, 0, 60, 0);
			String curIP = PreferenceManager.getDefaultSharedPreferences(SettingServiceIPActivity.this).getString(SettingServiceIPActivity.SETTING_SERVICE_IP, SettingServiceIPActivity.SETTING_SERVICE_IP_DEFAULT);
			if (curIP.equals(item)) {
				radio.setChecked(true);
			}
			radio.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					String selectIp = (String) radio.getText();
					Service_ip_edit.setText(selectIp);
				}
			});
			radiogroup.addView(radio);
		}
		radiogroup.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				RadioButton r = (RadioButton) group.getChildAt(checkedId);
				String selectIp = (String) r.getText();
				Service_ip_edit.setText(selectIp);
			}
		});
		service_ip_log_area.addView(radiogroup);
	}
}
