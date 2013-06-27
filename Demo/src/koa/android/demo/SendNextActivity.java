package koa.android.demo;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import koa.android.demo.model.Task;
import koa.android.demo.model.User;
import koa.android.demo.model.WFRunModel;
import koa.android.logic.ActivityHoder;
import koa.android.logic.MainService;
import koa.android.logic.ProgressDialogHolder;
import koa.android.tools.Base64Encoder;
import koa.android.tools.HttpResponseUtil;
import koa.android.tools.JsonUtil;
import koa.android.tools.LoginTimeOut;
import koa.android.tools.SetBackGround;

import org.json.JSONArray;
import org.json.JSONException;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

/**
 * ���Ͱ����ҳ��
 * 
 * @author chenM
 * 
 */
public class SendNextActivity extends Activity implements KoaActivity, OnGestureListener, OnTouchListener {
	private Button title_bt_left;// ���˰�ť
	private TextView top_title;// �����ı���
	private Button transBtn = null;
	private Intent intent;
	private TextView tvStepName = null;
	private AutoCompleteTextView sender = null;
	private WFRunModel model;
	private ProgressDialog pd;
	private String sessionId;
	private RadioGroup wf_priority_Group;
	private RadioButton p1;
	private RadioButton p2;
	private RadioButton p3;
	private RadioButton p4;
	private TextView wf_priority_text1;
	private TextView wf_priority_text2;
	private TextView wf_priority_text3;
	private TextView wf_priority_text4;
	// ���ȼ�����
	private int PRIORITY_NONE = 1;// ��
	private int PRIORITY_LOW = 0;// ��
	private int PRIORITY_MEDIUM = 2;// ��
	private int PRIORITY_HEIGH = 3;// ��
	private String priority;
	// ��һ�ڵ��������ʾ��ʽ
	private int SENDER_SHOWTYPE_1 = 1;// ��ʾ�ı������û�¼��
	private int SENDER_SHOWTYPE_2 = 2;// ��ѡ�����û�ѡ��
	private int SENDER_SHOWTYPE_3 = 3;// ��ѡ��ȫѡ,�������
	private int SENDER_SHOWTYPE_4 = 4;// ��ʾ�ı����ı�������༭
	private LinearLayout wf_sender_area;
	private int TotalCheckedNum = 0;
	private Map<String, String> senderMap;// ����ȫ��Hash,�����ڷ���ģʽ2��ʱ�� ��ʱ��ŷ�����
	private Drawable wfSenderClear; // �����ı�������ı�����ͼ��
	// ===============����ʶ��=========================
	private GestureDetector mGestureDetector;
	private static final int FLING_MIN_DISTANCE = 150;
	private static final int FLING_MIN_VELOCITY = 200;
	private LinearLayout sendnext_layout;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		LoginTimeOut LoginTimeOut = new LoginTimeOut();
		boolean flag = LoginTimeOut.timeOut(SendNextActivity.this);
		if (!flag) {
			setContentView(R.layout.sendnext);
			mGestureDetector = new GestureDetector(this);
			// ��ò���
			intent = getIntent();
			model = (WFRunModel) intent.getSerializableExtra("WFRunModel");
			sessionId = User.getInstance().getUserContext(SendNextActivity.this).getSessionId();
			senderMap = new HashMap<String, String>();
			// ��Activity�ŵ�Activity��
			ActivityHoder.getInstance().addActivity(this);
			// ��ʼ��UI���
			setUpView();
			// �������������
			setUpListener();
			init();
		}
	}

	@Override
	protected void onRestart() {
		super.onRestart();
		setBackground();
	}

	// ���ñ���ͼ
	private void setBackground() {
		// ���ñ���ͼ
		SetBackGround.getInstance().setTheme(this, top_title, "top_title", R.drawable.ui_title_bg_blue, null);
		SetBackGround.getInstance().setTheme(this, title_bt_left, "ui_backbtn", R.drawable.ui_title_back_btn_blue, null);
	}

	// ��ʼ��UI���
	public void setUpView() {
		title_bt_left = (Button) findViewById(R.id.title_bt_left);
		top_title = (TextView) findViewById(R.id.top_title);
		tvStepName = (TextView) findViewById(R.id.senderStepName);
		sender = (AutoCompleteTextView) findViewById(R.id.tasksender);
		sender.setThreshold(3);
		sender.setOverScrollMode(View.OVER_SCROLL_IF_CONTENT_SCROLLS);
		transBtn = (Button) findViewById(R.id.sendActionBtn);
		wf_priority_Group = (RadioGroup) findViewById(R.id.wf_priority_Group);
		p1 = (RadioButton) findViewById(R.id.wf_priority_Button1);
		p1.setId(PRIORITY_NONE);
		p2 = (RadioButton) findViewById(R.id.wf_priority_Button2);
		p2.setId(PRIORITY_LOW);
		p3 = (RadioButton) findViewById(R.id.wf_priority_Button3);
		p3.setId(PRIORITY_MEDIUM);
		p4 = (RadioButton) findViewById(R.id.wf_priority_Button4);
		p4.setId(PRIORITY_HEIGH);
		wf_sender_area = (LinearLayout) findViewById(R.id.wf_sender_area);
		wf_priority_text1 = (TextView) findViewById(R.id.wf_priority_text1);
		wf_priority_text2 = (TextView) findViewById(R.id.wf_priority_text2);
		wf_priority_text3 = (TextView) findViewById(R.id.wf_priority_text3);
		wf_priority_text4 = (TextView) findViewById(R.id.wf_priority_text4);
		final Resources res = getResources();
		wfSenderClear = res.getDrawable(R.drawable.wf_search_clear);
		sendnext_layout = (LinearLayout) findViewById(R.id.sendnext_layout);
		sendnext_layout.setOnTouchListener(this);
		sendnext_layout.setLongClickable(true);
		setBackground();
	}

	// �������������
	public void setUpListener() {
		wf_priority_text1.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				p1.setChecked(true);
				p2.setChecked(false);
				p3.setChecked(false);
				p4.setChecked(false);
			}
		});
		wf_priority_text2.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				p2.setChecked(true);
				p1.setChecked(false);
				p3.setChecked(false);
				p4.setChecked(false);
			}
		});
		wf_priority_text3.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				p3.setChecked(true);
				p2.setChecked(false);
				p1.setChecked(false);
				p4.setChecked(false);
			}
		});
		wf_priority_text4.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				p4.setChecked(true);
				p2.setChecked(false);
				p3.setChecked(false);
				p1.setChecked(false);
			}
		});
		// ˳�����ť
		TextPaint tp = transBtn.getPaint();
		tp.setFakeBoldText(true);
		transBtn.setOnClickListener(new SenderBtnListener());
		// ���˰�ť
		title_bt_left.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				finish();
			}
		});
		wf_priority_Group.setOnCheckedChangeListener(new android.widget.RadioGroup.OnCheckedChangeListener() {
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				priority = String.valueOf(checkedId);
			}
		});
		sender.addTextChangedListener(new TextWatcher() {
			// ������һ���ı������Ƿ�Ϊ��
			private boolean isnull = true;

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {

			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {

			}

			@Override
			public void afterTextChanged(Editable s) {
				if (TextUtils.isEmpty(s)) {
					if (!isnull) {
						sender.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
						isnull = true;
					}
				} else {
					if (isnull) {
						sender.setCompoundDrawablesWithIntrinsicBounds(null, null, wfSenderClear, null);
						isnull = false;
					}
				}
			}
		});
		sender.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				switch (event.getAction()) {
				case MotionEvent.ACTION_UP:
					int curX = (int) event.getX();
					if (curX > v.getWidth() - 43 && !TextUtils.isEmpty(sender.getText())) {
						sender.onTouchEvent(event);
						sender.setText("");
						return true;
					}
				}
				return false;
			}
		});
		// AutoCompleteTextView������
		sender.addTextChangedListener(new TextWatcher() {
			@Override
			public void afterTextChanged(Editable s) {
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				String str = s.toString();
				if (str.length() > 3) {
					HashMap<String, Object> userHash = new HashMap<String, Object>();
					userHash.put("keyword", str);
					String senderUserJson = HttpResponseUtil.getInstance().getGetHttpInfo("KOA_Mobile_Get_UserName_By_Word", sessionId, userHash, SendNextActivity.this);
					if (senderUserJson.equals("TIMEOUT")) {
						Toast.makeText(SendNextActivity.this, "���س�ʱ�����Ժ�����", Toast.LENGTH_SHORT).show();
					} else if (senderUserJson.equals("SESSION_TIMEOUT")) {
						HttpResponseUtil.getInstance().reLogin(SendNextActivity.this);
					} else if (senderUserJson.equals("NETWORK_ERROR")) {
						Toast.makeText(SendNextActivity.this, "���������쳣�����Ժ�����", Toast.LENGTH_SHORT).show();
					} else {
						try {
							JSONArray array = new JSONArray(senderUserJson);
							String[] temp = new String[array.length()];
							for (int i = 0; i < array.length(); i++) {
								temp[i] = array.getString(i);
								ArrayAdapter<String> adapter = new ArrayAdapter<String>(SendNextActivity.this, R.layout.wf_help_inputuserid, temp);
								sender.setAdapter(adapter);
							}
						} catch (JSONException e) {
							e.printStackTrace();
						}
					}
				}
			}
		});
	}

	// ��ʼ��
	@Override
	public void init() {
		// ���ü�������
		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_MASK_ADJUST);
		top_title.setText("����");
		// �ж���һ�ڵ��Ƿ���ʾ,���������,���������;������ڼ�����ʼ��ҳ��
		if (model.getWfStepNAME() == null || "".equals(model.getWfStepNAME())) {
			Toast.makeText(SendNextActivity.this, "����Ťת�쳣������ϵ����Ա", Toast.LENGTH_LONG).show();
			transBtn.setVisibility(View.GONE);
		} else {
			// ���÷��ͽڵ�����
			String flowTitle = intent.getStringExtra("flowTitle");
			if (flowTitle.indexOf(")") > 0) {
				String flow = flowTitle.substring(flowTitle.indexOf(")") + 1, flowTitle.length() - 1);
				tvStepName.setText("(" + model.getWfStepNAME() + ")" + flow);
			} else {
				tvStepName.setText(model.getWfStepNAME() + intent.getStringExtra("flowTitle"));
			}
			// ����Ĭ�Ϸ������ȼ�
			int priority = model.getPriority();
			if (priority == PRIORITY_NONE) {
				p1.setChecked(true);
			} else if (priority == PRIORITY_LOW) {
				p2.setChecked(true);
			} else if (priority == PRIORITY_MEDIUM) {
				p3.setChecked(true);
			} else if (priority == PRIORITY_HEIGH) {
				p4.setChecked(true);
			} else {
				p1.setChecked(true);
			}
			// ��������������ʾ��ʽ
			if (model.getShowType() == SENDER_SHOWTYPE_1) {// ��ʾ�ı������û�¼��
				if (model.getUserlist() != null || !"".equals(model.getUserlist())) {
					sender.setText(model.getUserlist());
				}
			} else if (model.getShowType() == SENDER_SHOWTYPE_2) {// ��ѡ�����û�ѡ��
				if (model.getUserlist() != null || !"".equals(model.getUserlist())) {
					// ���ط����������
					sender.setVisibility(View.GONE);
					String[] UserArr = model.getUserlist().split(" ");
					int num = model.getNum();
					if (num == 0) {
						num = 999;
					}// ���������Ϊ0,�����Ʒ�������
					createMulti(UserArr, num, true);
				}
			} else if (model.getShowType() == SENDER_SHOWTYPE_3) {// ��ѡ��ȫѡ,�������
				if (model.getUserlist() != null || !"".equals(model.getUserlist())) {
					// ���ط����������
					sender.setVisibility(View.GONE);
					String[] UserArr = model.getUserlist().split(" ");
					int num = UserArr.length;
					if (num == 0) {
						num = 999;
					}// ���������Ϊ0,�����Ʒ�������
					createMulti(UserArr, num, false);
				}
			} else if (model.getShowType() == SENDER_SHOWTYPE_4) {// ��ʾ�ı����ı�������༭
				if (model.getUserlist() != null || !"".equals(model.getUserlist())) {
					sender.setText(model.getUserlist());
					sender.setFocusable(false);
					sender.setEnabled(false);
					sender.setClickable(false);
				}
			} else {// ��û����ʾ��ʽ,��Ĭ�Ϸ�ʽ��ʾ
				if (model.getUserlist() != null || !"".equals(model.getUserlist())) {
					sender.setText(model.getUserlist());
				}
			}
		}
	}

	/**
	 * ������Աѡ��ѡ��
	 * 
	 * @param UserArr
	 *            ��ѡ������
	 * @param num
	 *            ѡ�����
	 */
	public void createMulti(String[] UserArr, final int num, boolean changeable) {
		for (int i = 0; i < UserArr.length; i++) {
			// ��̬����������
			CheckBox c = new CheckBox(SendNextActivity.this);
			c.setText(UserArr[i]);
			c.setTextAppearance(getApplicationContext(), R.style.wf_sendnext_checkbox_style);
			c.setButtonDrawable(R.drawable.wf_detail_operate_bar_button_trans);
			if (UserArr.length == 1) {
				c.setBackgroundResource(R.drawable.wf_detail_subtable_1_1);
			} else {
				if (i == 0) {
					c.setBackgroundResource(R.drawable.wf_detail_subtable_2_1);
				} else if (i == UserArr.length - 1) {
					c.setBackgroundResource(R.drawable.wf_detail_subtable_2_3);
				} else {
					c.setBackgroundResource(R.drawable.wf_detail_subtable_2_2);
				}
			}
			LinearLayout.LayoutParams cp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
			c.setLayoutParams(cp);
			c.setPadding(20, 0, 60, 0);
			wf_sender_area.addView(c);
			// �����ѡ����Ϊ1,��Ĭ�Ϲ�ѡ
			if (UserArr.length == 1) {
				c.setChecked(true);
				TotalCheckedNum++;
				senderMap.put(UserArr[i], UserArr[i]);
			}
			// ���ɸı�
			if (!changeable) {
				c.setChecked(true);
				c.setOnCheckedChangeListener(new android.widget.CompoundButton.OnCheckedChangeListener() {
					@Override
					public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
						Toast.makeText(SendNextActivity.this, "��ǰ�����˲��ɸı�", Toast.LENGTH_SHORT).show();
						if (!isChecked) {
							buttonView.setChecked(true);
						}
					}
				});
			} else {// ���Ըı�
				c.setOnCheckedChangeListener(new android.widget.CompoundButton.OnCheckedChangeListener() {

					@Override
					public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
						if (isChecked) {
							TotalCheckedNum++;
							if (TotalCheckedNum > num) {
								Toast.makeText(SendNextActivity.this, "��ǰ���������Ϊ" + num + "��", Toast.LENGTH_SHORT).show();
								buttonView.setChecked(false);
								TotalCheckedNum--;
							} else {
								senderMap.put(buttonView.getText().toString(), buttonView.getText().toString());
							}
						} else if (!isChecked) {
							TotalCheckedNum--;
							if (senderMap.containsKey(buttonView.getText().toString())) {
								senderMap.remove(buttonView.getText().toString());
							}
						}
					}
				});
			}

		}
	}

	/**
	 * ִ�з��Ͷ���
	 * 
	 */
	class SenderBtnListener implements OnClickListener {
		public void onClick(View v) {
			String senderUser = "";
			if (model.getShowType() == SENDER_SHOWTYPE_1) {
				senderUser = sender.getText().toString().trim();
			} else if (model.getShowType() == SENDER_SHOWTYPE_2) {
				Iterator<Entry<String, String>> i = senderMap.entrySet().iterator();
				while (i.hasNext()) {
					Map.Entry<String, String> me = (Map.Entry<String, String>) i.next();
					String key = (String) me.getKey();
					senderUser = senderUser + key + " ";
				}
				senderUser = senderUser.trim();
			} else if (model.getShowType() == SENDER_SHOWTYPE_3 || model.getShowType() == SENDER_SHOWTYPE_4) {
				senderUser = model.getUserlist().trim();
			}
			if (null == senderUser || "".equals(senderUser)) {
				Toast.makeText(SendNextActivity.this, "�������˲���Ϊ��", Toast.LENGTH_SHORT).show();
			} else {
				transBtn.setEnabled(false);
				pd = new ProgressDialog(SendNextActivity.this);
				pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
				pd.setCancelable(true);
				pd.setMessage("���ڷ���...");
				pd.show();
				ProgressDialogHolder.getInstance().addActivity(pd);
				String checkSenderUser = senderUser.replaceAll("\\s", "_");
				String sessionId = User.getInstance().getUserContext(SendNextActivity.this).getSessionId();
				HashMap<String, Object> userHash = new HashMap<String, Object>();
				userHash.put("userNames", checkSenderUser);
				String senderUserJson = HttpResponseUtil.getInstance().getGetHttpInfo("KOA_Mobile_Check_UserName", sessionId, userHash, SendNextActivity.this);
				if (senderUserJson == null || "".equals(senderUserJson)) {
					ProgressDialogHolder.getInstance().exit();
					Toast.makeText(getApplicationContext(), "�˻���鷵��ֵ�쳣", Toast.LENGTH_SHORT).show();
					transBtn.setEnabled(true);
				} else {
					senderUserJson = senderUserJson.substring(1, senderUserJson.length() - 1);
					String result = JsonUtil.parseJson(senderUserJson, "result");
					if (result.equals("true")) {
						HashMap<String, Object> hashparam = new HashMap<String, Object>();
						String instanceId = intent.getStringExtra("instanceId"); // ���ʵ��ID
						String taskId = intent.getStringExtra("taskId"); // �������ID
						hashparam.put("id", instanceId);
						hashparam.put("taskId", taskId);
						try {
							hashparam.put("MAIL_TO", Base64Encoder.encode(senderUser.trim().getBytes("UTF-8")));
							if (intent.getStringExtra("flowTitle") != null) {
								hashparam.put("TITLE", Base64Encoder.encode(("(" + model.getWfStepNAME() + ")" + intent.getStringExtra("flowTitle")).getBytes("UTF-8")));
							}
						} catch (UnsupportedEncodingException e) {
							e.printStackTrace();
						}
						hashparam.put("stepno", model.getWfStepNo());
						hashparam.put("runStyle", model.getRoutePointType());
						hashparam.put("localDepartmentId", model.getLocalDepartmentId());
						hashparam.put("priority", priority);
						Task task = new Task(Task.KOA_COMMON, "KOA_Mobile_WF_Transaction_Send", sessionId, "SendNextActivity", hashparam);
						// �������͸�Service������
						MainService.newTask(task);
						// ���Լ���ӵ�Activity��������
						MainService.addActivity(SendNextActivity.this);
					} else {
						transBtn.setEnabled(true);
						ProgressDialogHolder.getInstance().exit();
						Toast.makeText(getApplicationContext(), "�����˻����Ϸ�", Toast.LENGTH_SHORT).show();
					}
				}
			}
		}
	}

	@Override
	public void refresh(Object... params) {
		ProgressDialogHolder.getInstance().exit();
		String json = params[0].toString();
		if (json == null || "".equals(json)) {
			Toast.makeText(getApplicationContext(), "����ֵ�쳣", Toast.LENGTH_SHORT).show();
		} else if (json.equals("TIMEOUT")) {
			Toast.makeText(getApplicationContext(), "���س�ʱ,���Ժ�����", Toast.LENGTH_SHORT).show();
		} else if (json.equals("SESSION_TIMEOUT")) {
			HttpResponseUtil.getInstance().reLogin(SendNextActivity.this);
		} else if (json.equals("NETWORK_ERROR")) {
			Toast.makeText(SendNextActivity.this, "���������쳣�����Ժ�����", Toast.LENGTH_SHORT).show();
		} else if (json.equals("SUCCESS")) {
			Toast.makeText(getApplicationContext(), "�����ͳɹ�", Toast.LENGTH_LONG).show();
			intent.setClass(SendNextActivity.this, TodoListAllActivity.class);
			startActivity(intent);
			finish();
		} else if (json.equals("TASK_END")) {
			Toast.makeText(getApplicationContext(), "�����������", Toast.LENGTH_LONG).show();
			intent.setClass(SendNextActivity.this, TodoListAllActivity.class);
			startActivity(intent);
			finish();
		} else if (json.equals("TASK_ERROR-001")) {
			Toast.makeText(getApplicationContext(), "���̰����쳣", Toast.LENGTH_LONG).show();
		} else if (json.equals("TASK_ERROR-002")) {
			Toast.makeText(getApplicationContext(), "�����ܱ�����,Ŀ���û����������Ʒ�Χ�ڣ�", Toast.LENGTH_LONG).show();
		} else if (json.equals("task break")) {
			Toast.makeText(getApplicationContext(), "�����ܽ���, ����û�а������", Toast.LENGTH_LONG).show();
		} else {
			Toast.makeText(getApplicationContext(), "����ֵ�쳣", Toast.LENGTH_SHORT).show();
		}
		transBtn.setEnabled(true);
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		return mGestureDetector.onTouchEvent(event);
	}

	@Override
	public boolean onDown(MotionEvent e) {
		return false;
	}

	@Override
	public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
		if (e1 != null && e2 != null) {
			if (e2.getX() - e1.getX() > FLING_MIN_DISTANCE && Math.abs(velocityX) > FLING_MIN_VELOCITY && e2.getX() - e1.getX() > e1.getY() - e2.getY()) {
				finish();
			}
		}
		return true;
	}

	@Override
	public void onLongPress(MotionEvent e) {
	}

	@Override
	public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
		return false;
	}

	@Override
	public void onShowPress(MotionEvent e) {

	}

	@Override
	public boolean onSingleTapUp(MotionEvent e) {
		return false;
	}

}