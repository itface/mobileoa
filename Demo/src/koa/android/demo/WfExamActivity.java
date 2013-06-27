package koa.android.demo;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;

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
import org.json.JSONObject;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextPaint;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

/**
 * ���̰���
 * 
 * @author chenM
 * 
 */
public class WfExamActivity extends Activity implements KoaActivity, OnGestureListener, OnTouchListener {
	private Button title_bt_left;// ���˰�ť
	private TextView top_title;// �����ı���
	private ProgressDialog pd;
	private Intent intent;
	private String opinionButtonJson;
	private String auditId;
	private RelativeLayout opinion_area;
	private Button transBtn = null; // ����ť��ť
	private Button addsignBtn = null;// ��Ӽ�ǩ��ť
	private Button notivrBtn = null;// ��Ӵ��İ�ť
	private EditText opinion_editText;// �������ı���
	private int jsonStatus;// �ж����ĸ�CMD���ص�����
	private static final int OPININON_SAVE = 0;
	private static final int TRANSACTION = 1;
	// ===============����ʶ��=========================
	private GestureDetector mGestureDetector;
	private static final int FLING_MIN_DISTANCE = 150;
	private static final int FLING_MIN_VELOCITY = 200;
	private RelativeLayout wf_exam_layout;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		LoginTimeOut LoginTimeOut = new LoginTimeOut();
		boolean flag = LoginTimeOut.timeOut(WfExamActivity.this);
		if (!flag) {
			setContentView(R.layout.wf_exam);
			mGestureDetector = new GestureDetector(this);
			intent = getIntent();
			setUpView();
			setUpListeners();
			// ��Activity�ŵ�Activity��
			ActivityHoder.getInstance().addActivity(this);
			init();
		}
	}

	private void setUpView() {
		opinion_area = (RelativeLayout) findViewById(R.id.opinion_area);
		title_bt_left = (Button) findViewById(R.id.title_bt_left);
		top_title = (TextView) findViewById(R.id.top_title);
		transBtn = (Button) findViewById(R.id.transBtn);
		addsignBtn = (Button) findViewById(R.id.addsignBtn);
		notivrBtn = (Button) findViewById(R.id.notivrBtn);
		opinion_editText = (EditText) findViewById(R.id.opinion_text);
		wf_exam_layout = (RelativeLayout) findViewById(R.id.wf_exam_layout);
		wf_exam_layout.setOnTouchListener(this);
		wf_exam_layout.setLongClickable(true);
		setBackground();
	}

	private void setUpListeners() {
		// ���˰�ť
		title_bt_left.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				finish();
			}
		});
		TextPaint tp = transBtn.getPaint();
		tp.setFakeBoldText(true);
		transBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// �ж��Ƿ������Button��JSON
				if (!"".equals(opinionButtonJson) && !"[]".equals(opinionButtonJson) && null != opinionButtonJson) {
					if (auditId == null) {
						Toast.makeText(getApplicationContext(), "�����������", Toast.LENGTH_SHORT).show();
					} else {
						pd = new ProgressDialog(WfExamActivity.this);
						pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
						pd.setCancelable(true);
						pd.setMessage("���ڰ���...");
						pd.show();
						ProgressDialogHolder.getInstance().addActivity(pd);
						jsonStatus = OPININON_SAVE;
						// ����������
						opinion_Audit_Save();
					}
				} else {
					pd = new ProgressDialog(WfExamActivity.this);
					pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
					pd.setCancelable(true);
					pd.setMessage("���ڰ���...");
					pd.show();
					ProgressDialogHolder.getInstance().addActivity(pd);
					// ˳�����
					jsonStatus = TRANSACTION;
					transaction();
				}
			}
		});
		// ���ͼ�ǩ
		TextPaint tp1 = addsignBtn.getPaint();
		tp1.setFakeBoldText(true);
		addsignBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				intent.setClass(WfExamActivity.this, AddsignActivity.class);
				startActivity(intent);
			}
		});
		// ���ʹ���
		TextPaint tp2 = notivrBtn.getPaint();
		tp2.setFakeBoldText(true);
		notivrBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				intent.setClass(WfExamActivity.this, WfAddNoticeActivity.class);
				startActivity(intent);
			}
		});
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

	public void init() {
		String userName = User.getInstance().getUserContext(WfExamActivity.this).getUserName();
		top_title.setText(userName + " - ����");
		opinionButtonJson = intent.getStringExtra("opinionButtonJson");
		createOpinion_area(opinionButtonJson);
	}

	/**
	 * ��������б�����
	 * 
	 * @param opinionButtonJson
	 */
	private void createOpinion_area(String opinionButtonJson) {
		RadioGroup radiogroup = new RadioGroup(this);
		RelativeLayout.LayoutParams rgp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.FILL_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
		radiogroup.setLayoutParams(rgp);
		if (!"".equals(opinionButtonJson) && !"[]".equals(opinionButtonJson) && null != opinionButtonJson) {
			try {
				JSONArray jsonArray = new JSONArray(opinionButtonJson);
				for (int i = 0; i < jsonArray.length(); i++) {
					JSONObject jsonObj = ((JSONObject) jsonArray.opt(i));
					final RadioButton radio = new RadioButton(this);
					String opinionName = jsonObj.getString("opinionName");
					radio.setText(opinionName);
					radio.setId(jsonObj.getInt("opinionId"));
					radio.setTextAppearance(getApplicationContext(), R.style.wf_exam_button_style);
					radio.setSingleLine();
					radio.setButtonDrawable(R.drawable.wf_detail_operate_bar_button_trans);
					if (jsonArray.length() == 1) {
						radio.setBackgroundResource(R.drawable.wf_detail_subtable_1_1);
					} else {
						if (i == 0) {
							radio.setBackgroundResource(R.drawable.wf_detail_subtable_2_1);
						} else if (i == jsonArray.length() - 1) {
							radio.setBackgroundResource(R.drawable.wf_detail_subtable_2_3);
						} else {
							radio.setBackgroundResource(R.drawable.wf_detail_subtable_2_2);
						}
					}
					RadioGroup.LayoutParams rbp = new RadioGroup.LayoutParams(RadioGroup.LayoutParams.FILL_PARENT, RadioGroup.LayoutParams.WRAP_CONTENT);
					radio.setLayoutParams(rbp);
					radio.setPadding(10, 0, 60, 0);
					int ischeck = jsonObj.getInt("isCheck"); // �ж�Ĭ��ֵ
					if (ischeck == 1) {
						radio.setChecked(true);
						auditId = String.valueOf(jsonObj.getInt("opinionId"));
					}
					radiogroup.addView(radio);
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
			// װ���������б�
			opinion_area.addView(radiogroup);
			// װ�ؼ�����
			radiogroup.setOnCheckedChangeListener(new OnCheckedChangeListener() {
				public void onCheckedChanged(RadioGroup group, int checkedId) {
					auditId = String.valueOf(checkedId);
				}
			});
		}
	}

	// ˳�������������ӵ�Service��
	public void transaction() {
		String sissionId = User.getInstance().getUserContext(WfExamActivity.this).getSessionId();
		String instanceId = intent.getStringExtra("instanceId"); // ���ʵ��ID
		String taskId = intent.getStringExtra("taskId"); // �������ID
		String meId = intent.getStringExtra("meId"); // �������id
		String formId = intent.getStringExtra("formId");
		HashMap<String, Object> tranHash = new HashMap<String, Object>();
		tranHash.put("id", instanceId);// ���ʵ��ID
		tranHash.put("task_id", taskId);// �������ID
		tranHash.put("meId", meId); // �������id
		tranHash.put("formId", formId);
		tranHash.put("openstate", "1");
		Task task = new Task(Task.KOA_COMMON, "KOA_Mobile_WF_BindReport_NextStepInfo", sissionId, "WfExamActivity", tranHash);
		// �������͸�Service������
		MainService.newTask(task);
		// ���Լ���ӵ�Activity��������
		MainService.addActivity(WfExamActivity.this);
	}

	// ������
	public void opinion_Audit_Save() {
		String sign = "[����Android]";
		String model = android.os.Build.MODEL; // �ֻ��ͺ�
		if (model.indexOf("MI") == 0) {
			sign = "[����С��]";
		}
		String sissionId = User.getInstance().getUserContext(WfExamActivity.this).getSessionId();
		String instanceId = intent.getStringExtra("instanceId"); // ���ʵ��ID
		String taskId = intent.getStringExtra("taskId"); // �������ID
		String meId = intent.getStringExtra("meId"); // �������id
		String opinion = opinion_editText.getText().toString().trim();
		if (opinion != null && !"".equals(opinion.trim())) {
			opinion = opinion + "\n" + sign;
		} else {
			opinion = sign;
		}
		HashMap<String, Object> hashparam = new HashMap<String, Object>();
		hashparam.put("id", instanceId);
		hashparam.put("task_id", taskId);
		hashparam.put("auditId", auditId);
		hashparam.put("meId", meId);
		try {
			hashparam.put("opinion", Base64Encoder.encode(opinion.getBytes("UTF-8")));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		Task task = new Task(Task.KOA_COMMON, "KOA_Mobile_Worklist_Opinion_Audit_Save", sissionId, "WfExamActivity", hashparam);
		// �������͸�Service������
		MainService.newTask(task);
		// ���Լ���ӵ�Activity��������
		MainService.addActivity(WfExamActivity.this);
	}

	// �˷�����������ҳ��Ĳ����ύ��Ļص�
	public void refresh(Object... params) {
		if (jsonStatus == OPININON_SAVE) {
			String opinionJson = params[0].toString();
			if (opinionJson.equals("true")) {
				// ˳�����
				jsonStatus = TRANSACTION;
				transaction();
			} else if (opinionJson.equals("TIMEOUT")) {
				Toast.makeText(WfExamActivity.this, "���س�ʱ,���Ժ�����", Toast.LENGTH_SHORT).show();
			} else if (opinionJson.equals("SESSION_TIMEOUT")) {
				HttpResponseUtil.getInstance().reLogin(WfExamActivity.this);
			} else if (opinionJson.equals("NETWORK_ERROR")) {
				Toast.makeText(WfExamActivity.this, "���������쳣�����Ժ�����", Toast.LENGTH_SHORT).show();
			} else {
				ProgressDialogHolder.getInstance().exit();
				Toast.makeText(WfExamActivity.this, "���������ʧ��", Toast.LENGTH_SHORT).show();
			}
		} else if (jsonStatus == TRANSACTION) {
			ProgressDialogHolder.getInstance().exit();
			String json = params[0].toString();
			if (json == null || "".equals(json)) {
				Toast.makeText(WfExamActivity.this, "����ֵ�쳣������ϵ����Ա", Toast.LENGTH_SHORT).show();
			} else if (json.equals("TIMEOUT")) {
				Toast.makeText(WfExamActivity.this, "���س�ʱ,���Ժ�����", Toast.LENGTH_SHORT).show();
			} else if (json.equals("SESSION_TIMEOUT")) {
				HttpResponseUtil.getInstance().reLogin(WfExamActivity.this);
			} else if (json.equals("NETWORK_ERROR")) {
				Toast.makeText(WfExamActivity.this, "���������쳣�����Ժ�����", Toast.LENGTH_SHORT).show();
			} else if (json.contains("error")) {
				Toast.makeText(WfExamActivity.this, "�Ҳ��������ˣ�����ϵ����Ա", Toast.LENGTH_SHORT).show();
			} else {
				WFRunModel model = JsonUtil.parseRunJson(json);
				System.out.println(json);
				// �ж����̰�����
				if (model.getTaskStatus() == WFRunModel.TASK_TYPE_ERROR) {
					Toast.makeText(WfExamActivity.this, "�Ҳ��������ˣ�����ϵ����Ա", Toast.LENGTH_SHORT).show();
				} else if (model.getTaskStatus() == WFRunModel.TASK_TYPE_ARCHIVES) {
					Toast.makeText(WfExamActivity.this, "������ת��ϣ��ѹ鵵", Toast.LENGTH_SHORT).show();
					intent.setClass(WfExamActivity.this, TodoListAllActivity.class);
					startActivity(intent);
				} else if (model.getTaskStatus() == WFRunModel.TASK_TYPE_NOFIND_SENDER) {
					Toast.makeText(WfExamActivity.this, "ϵͳδ�ҵ���һ�������ˣ�����ϵ����Ա", Toast.LENGTH_SHORT).show();
				} else if (model.getTaskStatus() == WFRunModel.TASK_TYPE_NOFIND_TASK_END) {
					Toast.makeText(WfExamActivity.this, "��ǰ�����Ѱ������!", Toast.LENGTH_SHORT).show();
					intent.setClass(WfExamActivity.this, TodoListAllActivity.class);
					startActivity(intent);
				} else { // ˳�����
					Intent wfIntent = new Intent(WfExamActivity.this, SendNextActivity.class);
					wfIntent.putExtra("WFRunModel", model);
					wfIntent.putExtra("instanceId", intent.getStringExtra("instanceId"));
					wfIntent.putExtra("taskId", intent.getStringExtra("taskId"));
					wfIntent.putExtra("flowTitle", intent.getStringExtra("flowTitle"));
					startActivity(wfIntent);
				}
			}
		}
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
