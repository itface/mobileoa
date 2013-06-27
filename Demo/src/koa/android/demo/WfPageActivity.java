package koa.android.demo;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import koa.android.demo.model.Task;
import koa.android.demo.model.User;
import koa.android.demo.model.WFRunModel;
import koa.android.logic.ActivityHoder;
import koa.android.logic.MainService;
import koa.android.logic.ProgressDialogHolder;
import koa.android.tools.Base64Encoder;
import koa.android.tools.CreateFieldsHelper;
import koa.android.tools.CreateSubTableHelper;
import koa.android.tools.HttpResponseUtil;
import koa.android.tools.JsonUtil;
import koa.android.tools.LoginTimeOut;
import koa.android.tools.SetBackGround;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.app.DownloadManager;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.method.ScrollingMovementMethod;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * ����-���̰���
 * 
 * @author chenM
 * 
 */
public class WfPageActivity extends Activity implements KoaActivity, OnGestureListener, OnTouchListener {
	private Button title_bt_left;// ���˰�ť
	private TextView top_title;// �����ı���
	// ���������Button
	private Button transBtn;// ͬ�ⰴť
	private Button examBtn;// �������̰�ť
	private Button optionBtn;// ������ʷ��ť
	private Button formviewBtn;// �鿴ԭ����ť
	// ������Ϣ��ʾ����
	private TextView tvWfTitle = null;// ���������ı���
	private TextView tvWfCreateuser = null;// ���������ı���
	private TextView tvWfCreatedate = null;// ������ʱ���ı���
	private TextView tvWfMemo = null;// ����ժҪ�ı���
	private String baseUrl = "";
	private Handler handler;
	private ExecutorService executorService;
	private static int THREADPOOL_SIZE = 4;// �̳߳صĴ�С
	private Intent intent;
	private String formjson;// ���������ص�ҳ�����ݵ�JSON������ժҪ��Button��
	private int firstAuditId;// ��һ����������ť��ID
	private ProgressDialog pd;
	private String opinionButtonJson;// ���������ص�������Button��JSON
	private boolean dataReturnFlag = false;// �������Ƿ񷵻����ݱ�־λ
	private String instanceId;
	private String taskId;
	private String flowTitle;
	private int jsonStatus;// �ж����ĸ�CMD���ص�����
	private static final int OPININON_SAVE = 0;
	private static final int TRANSACTION = 1;
	private LinearLayout wf_detail_operate_bar;
	// ������
	private RelativeLayout wf_read_process_bar;
	// ================���ظ����õ��ı���=================
	private DownloadManager mgr = null;
	private LinearLayout wf_accessory_table;
	private LinearLayout wf_acessory_area;
	// ===============�ӱ�===========================
	private LinearLayout wf_detail_sublist_content;
	// ================ժҪ ===========================
	private LinearLayout wfdetail_area;
	// ===============������ʾ����=======================
	private LinearLayout wf_detail_fields;
	// ===============����ʶ��=========================
	private GestureDetector mGestureDetector;
	private static final int FLING_MIN_DISTANCE = 150;
	private static final int FLING_MIN_VELOCITY = 200;
	private ScrollView wf_all_ScrollView;
	private FrameLayout wf_main_FrameLayout;
	private boolean wetherCanDeal = false;
	private boolean timeOutFlag = false;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		LoginTimeOut LoginTimeOut = new LoginTimeOut();
		boolean flag = LoginTimeOut.timeOut(WfPageActivity.this);
		if (!flag) {
			setContentView(R.layout.wfpage);
			mGestureDetector = new GestureDetector(this);
			intent = getIntent();
			instanceId = intent.getStringExtra("instanceId");
			taskId = intent.getStringExtra("taskId");
			flowTitle = intent.getStringExtra("flowTitle");
			mgr = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
			setUpView();
			setUpListeners();
			// ��Activity�ŵ�Activity��
			ActivityHoder.getInstance().addActivity(this);
			init();
			handler = new GetWFPageHandler();
			executorService.submit(new GetWfPageThread());// ��ʱ����,����һ�����̻߳�ȡ����
			new Handler().postDelayed(new Runnable() {
				@Override
				public void run() {
					if (!dataReturnFlag) {
						wf_read_process_bar.setVisibility(View.VISIBLE);
					}
				}
			}, 1000);
		}
	}

	// ��ʼ�����е�UI���
	public void setUpView() {
		executorService = Executors.newFixedThreadPool(THREADPOOL_SIZE);
		title_bt_left = (Button) findViewById(R.id.title_bt_left);
		top_title = (TextView) findViewById(R.id.top_title);
		tvWfTitle = (TextView) findViewById(R.id.wfTaskTitle);
		tvWfCreateuser = (TextView) findViewById(R.id.wfsender);
		tvWfCreatedate = (TextView) findViewById(R.id.wfsenddate);
		tvWfMemo = (TextView) findViewById(R.id.wfmemo);
		transBtn = (Button) findViewById(R.id.transBtn);
		examBtn = (Button) findViewById(R.id.examBtn);
		optionBtn = (Button) findViewById(R.id.optionBtn);
		formviewBtn = (Button) findViewById(R.id.formviewBtn);
		wf_detail_operate_bar = (LinearLayout) findViewById(R.id.wf_detail_operate_bar);
		// ��������ʾ����
		wf_read_process_bar = (RelativeLayout) findViewById(R.id.wf_read_process_bar);
		// �ӱ���ʾ����
		wf_detail_sublist_content = (LinearLayout) findViewById(R.id.wf_detail_sublist_content);
		// ������ʾ����
		wf_accessory_table = (LinearLayout) findViewById(R.id.wf_acessory_area_right);
		wf_acessory_area = (LinearLayout) findViewById(R.id.wf_acessory_area);
		setBackground();
		// ����ժҪ
		wfdetail_area = (LinearLayout) findViewById(R.id.wfdetail_area);
		// ����
		wf_detail_fields = (LinearLayout) findViewById(R.id.wf_detail_fields);
		// ����ʶ��
		wf_all_ScrollView = (ScrollView) findViewById(R.id.wf_all_ScrollView);
		wf_all_ScrollView.setOnTouchListener(this);
		wf_all_ScrollView.setLongClickable(true);
		wf_main_FrameLayout = (FrameLayout) findViewById(R.id.wf_main_FrameLayout);
		wf_main_FrameLayout.setOnTouchListener(this);
		wf_main_FrameLayout.setLongClickable(true);
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
		SetBackGround.getInstance().setTheme(this, wf_detail_operate_bar, "wf_detail_operate_bar", R.drawable.wf_detail_operate_bar_bg_blue, null);
	}

	// �˷�������Ԥ����ҳ�����ݺͳ�ʼ��
	public void init() {
		baseUrl = HttpResponseUtil.getInstance().getBaseUrl(this);
		String userName = User.getInstance().getUserContext(WfPageActivity.this).getUserName();
		top_title.setText(userName + " - ����");
		// Ԥ����ҳ������
		String title = intent.getStringExtra("flowTitle");
		if (title != null)
			tvWfTitle.setText(title);
		String taskOwner = intent.getStringExtra("taskOwner");
		if (taskOwner != null)
			tvWfCreateuser.setText(taskOwner);
		String taskDate = intent.getStringExtra("taskDate");
		if (taskDate != null)
			tvWfCreatedate.setText(taskDate);
	}

	// �������������
	public void setUpListeners() {
		// ���˰�ť
		title_bt_left.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				finish();
			}
		});
		// �������ť
		transBtn.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				if (timeOutFlag) {
					Toast.makeText(WfPageActivity.this, "���س�ʱ,���Ժ�����", Toast.LENGTH_SHORT).show();
				} else {
					if (!dataReturnFlag) {
						Toast.makeText(WfPageActivity.this, "��ȴ����ݼ���", Toast.LENGTH_SHORT).show();
					} else {
						if (!wetherCanDeal) {
							Toast.makeText(WfPageActivity.this, "��ǰ���������صı�����,��ͨ�����԰���", Toast.LENGTH_LONG).show();
						} else {
							// ���û��ͬ��������
							if (firstAuditId == -1) {
								Toast.makeText(WfPageActivity.this, "��ǰ����û��ͬ�����,��ѡ�����������������", Toast.LENGTH_LONG).show();
							} else {
								// �ж��Ƿ������Button��JSON
								if (!"".equals(opinionButtonJson) && !"[]".equals(opinionButtonJson) && null != opinionButtonJson) {
									pd = new ProgressDialog(WfPageActivity.this);
									pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
									pd.setCancelable(true);
									pd.setMessage("���ڰ���...");
									pd.show();
									ProgressDialogHolder.getInstance().addActivity(pd);
									jsonStatus = OPININON_SAVE;
									// ����������
									opinion_Audit_Save();
								} else {
									pd = new ProgressDialog(WfPageActivity.this);
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
						}
					}
				}
			}
		});
		examBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (timeOutFlag) {
					Toast.makeText(WfPageActivity.this, "���س�ʱ,���Ժ�����", Toast.LENGTH_SHORT).show();
				} else {
					if (!dataReturnFlag) {
						Toast.makeText(WfPageActivity.this, "��ȴ����ݼ���", Toast.LENGTH_SHORT).show();
					} else {
						if (!wetherCanDeal) {
							Toast.makeText(WfPageActivity.this, "��ǰ���������صı�����,��ͨ�����԰���", Toast.LENGTH_LONG).show();
						} else {
							intent.putExtra("opinionButtonJson", opinionButtonJson);
							intent.setClass(WfPageActivity.this, WfExamActivity.class);
							startActivity(intent);
						}
					}
				}
			}
		});
		formviewBtn.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				StringBuffer url = new StringBuffer();
				String sissionId = User.getInstance().getUserContext(WfPageActivity.this).getSessionId();
				String instanceId = intent.getStringExtra("instanceId");// ��ȡinstanceId
				String taskId = intent.getStringExtra("taskId");// ��ȡtaskId
				url.append(baseUrl).append("?").append("sid=").append(sissionId).append("&cmd=WorkFlow_Execute_Worklist_BindReport_Open").append("&id=").append(instanceId).append("&task_id=").append(taskId).append("&openstate=1");
				Intent formViewIntent = new Intent(WfPageActivity.this, WfFormViewActivity.class);
				formViewIntent.putExtra("formURL", url.toString());
				startActivity(formViewIntent);
			}
		});
		// �����б�
		optionBtn.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				intent.setClass(WfPageActivity.this, OpinionHistoryActivity.class);
				startActivity(intent);
			}
		});
	}

	// �µ��߳�
	class GetWfPageThread implements Runnable {
		@Override
		public void run() {
			// ���������ˢ��ҳ��
			refreshDetail();
			Message msg = handler.obtainMessage();// ֪ͨ�߳�������Χ������
			handler.sendMessage(msg);
		}
	}

	// �����������ˢ��ҳ�����ݵķ���
	public void refreshDetail() {
		String sessionId = User.getInstance().getUserContext(WfPageActivity.this).getSessionId();// ��ȡsessionId
		if (sessionId != null || !"".equals(sessionId)) {
			HashMap<String, Object> hashparam = new HashMap<String, Object>();
			String instanceId = intent.getStringExtra("instanceId"); // ���ʵ��ID
			String taskId = intent.getStringExtra("taskId"); // �������ID
			hashparam.put("id", instanceId);
			hashparam.put("task_id", taskId);
			formjson = HttpResponseUtil.getInstance().getGetHttpInfo("KOA_Mobile_WF_BindReport_openFormParam", sessionId, hashparam, WfPageActivity.this);
		} else {
			formjson = "SESSION_ERROR";
		}
	}

	// ���շ���������,�������
	class GetWFPageHandler extends Handler {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			dataReturnFlag = true;
			wf_read_process_bar.setVisibility(View.GONE);
			System.out.println(formjson);

			if (formjson == null || formjson.equals("")) {
				Toast.makeText(WfPageActivity.this, "����Ϣ�����쳣", Toast.LENGTH_SHORT).show();
				timeOutFlag = true;
			} else if (formjson.equals("TIMEOUT")) {
				Toast.makeText(WfPageActivity.this, "���س�ʱ,���Ժ�����", Toast.LENGTH_SHORT).show();
				timeOutFlag = true;
			} else if (formjson.equals("SESSION_TIMEOUT")) {
				HttpResponseUtil.getInstance().reLogin(WfPageActivity.this);
			} else if (formjson.equals("SESSION_ERROR")) {
				Toast.makeText(WfPageActivity.this, "���س�ʱ,���Ժ�����", Toast.LENGTH_SHORT).show();
				timeOutFlag = true;
			} else if (formjson.equals("NETWORK_ERROR")) {
				Toast.makeText(WfPageActivity.this, "���������쳣�����Ժ�����", Toast.LENGTH_SHORT).show();
				timeOutFlag = true;
			} else {
				// �жϷ������˵�ժҪ��Ϣ�Ƿ�����д����fileName��content��fields��subTable��Ϊ��ʱ�����ܰ���
				boolean isAbstractNull = true;

				String formId = JsonUtil.parseJson(formjson, "formId");
				String meId = JsonUtil.parseJson(formjson, "meid");
				// ==================װ�ز���========================================
				intent.putExtra("formId", formId);
				intent.putExtra("meId", meId);
				// ==================��̬��Ӹ����б�===================================
				String fileName = JsonUtil.parseJson(formjson, "fileName");
				if (!fileName.equals("") && !"[]".equals(fileName)) {
					isAbstractNull = false;
					// �ش�ID���ڸ�������ʱ��Ĳ�������
					String metaDataMapId = JsonUtil.parseJson(formjson, "metaDataMapId");
					DownloadHelper.getInstance().createAccessoryTable(meId, metaDataMapId, mgr, fileName, WfPageActivity.this, wf_accessory_table);
					wf_acessory_area.setVisibility(View.VISIBLE);
				} else {
					wf_acessory_area.setVisibility(View.GONE);
				}
				// ===================װ������ժҪ=====================================
				String WfDetail = JsonUtil.parseJson(formjson, "content");
				if (WfDetail.equals("") || "[]".equals(WfDetail)) {
					wfdetail_area.setVisibility(View.GONE);
				} else {
					isAbstractNull = false;
					// װ������ժҪ
					tvWfMemo.setText(WfDetail);
					tvWfMemo.setMovementMethod(ScrollingMovementMethod.getInstance());
					wfdetail_area.setVisibility(View.VISIBLE);
				}
				// ===================�����������Ŀ=====================================
				String fields = JsonUtil.parseJson(formjson, "fields");
				if (!fields.equals("") && !"[]".equals(fields)) {
					isAbstractNull = false;
					CreateFieldsHelper.getInstance().createSubFields(WfPageActivity.this, formjson, wf_detail_fields);
					wf_detail_fields.setVisibility(View.VISIBLE);
				} else {
					wf_detail_fields.setVisibility(View.GONE);
				}
				// ==================��̬�����ӱ��б�====================================
				String subTable = JsonUtil.parseJson(formjson, "subTable");
				if (!subTable.equals("") && !"[]".equals(subTable)) {
					isAbstractNull = false;
					CreateSubTableHelper.getInstance().createSubTable("TodoListAllActivity", WfPageActivity.this, instanceId, taskId, subTable, wf_detail_sublist_content);
					wf_detail_sublist_content.setVisibility(View.VISIBLE);
				} else {
					wf_detail_sublist_content.setVisibility(View.GONE);
				}
				// ==============================��̬�������б�ť=====================
				// װ������б�Ĳ���
				opinionButtonJson = JsonUtil.parseJson(formjson, "button");
				if (!"".equals(opinionButtonJson) && !"[]".equals(opinionButtonJson)) {
					try {
						JSONArray jsonArray = new JSONArray(opinionButtonJson);
						for (int i = 0; i < jsonArray.length(); i++) {
							JSONObject jsonObj = ((JSONObject) jsonArray.opt(i));
							String opinionName = jsonObj.getString("opinionName");
							if (opinionName.equals("ͬ��")) {
								firstAuditId = jsonObj.getInt("opinionId");
								break;
							} else {
								firstAuditId = -1;
							}
						}
					} catch (JSONException e) {
						e.printStackTrace();
					}
				} else {
					firstAuditId = -1;
				}
				// ���´����ж��Ƿ���ժҪ����
				if (!isAbstractNull) {
					wetherCanDeal = true;
				} else {
					TextView tip = new TextView(WfPageActivity.this);
					tip.setText("����ʾ��\n��ǰ���������صı�����,��ͨ�����԰���");
					tip.setTextColor(Color.parseColor("#3F8AC5"));
					tip.setTextSize(16);
					tip.setGravity(Gravity.CENTER);
					wf_detail_sublist_content.addView(tip);
					wf_detail_sublist_content.setVisibility(View.VISIBLE);
				}
			}
		}
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
				Toast.makeText(WfPageActivity.this, "���س�ʱ,���Ժ�����", Toast.LENGTH_SHORT).show();
			} else if (opinionJson.equals("SESSION_TIMEOUT")) {
				HttpResponseUtil.getInstance().reLogin(WfPageActivity.this);
			} else if (opinionJson.equals("NETWORK_ERROR")) {
				Toast.makeText(WfPageActivity.this, "���������쳣�����Ժ�����", Toast.LENGTH_SHORT).show();
			} else {
				ProgressDialogHolder.getInstance().exit();
				Toast.makeText(WfPageActivity.this, "���������ʧ��", Toast.LENGTH_SHORT).show();
			}
		} else if (jsonStatus == TRANSACTION) {
			ProgressDialogHolder.getInstance().exit();
			String json = params[0].toString();
			if (json == null || "".equals(json)) {
				Toast.makeText(WfPageActivity.this, "����ֵ�쳣������ϵ����Ա", Toast.LENGTH_SHORT).show();
			} else if (json.equals("TIMEOUT")) {
				Toast.makeText(WfPageActivity.this, "���س�ʱ,���Ժ�����", Toast.LENGTH_SHORT).show();
			} else if (json.equals("SESSION_TIMEOUT")) {
				HttpResponseUtil.getInstance().reLogin(WfPageActivity.this);
			} else if (json.equals("NETWORK_ERROR")) {
				Toast.makeText(WfPageActivity.this, "���������쳣�����Ժ�����", Toast.LENGTH_SHORT).show();
			} else if (json.contains("error")) {
				Toast.makeText(WfPageActivity.this, "�Ҳ��������ˣ�����ϵ����Ա", Toast.LENGTH_SHORT).show();
			} else {
				WFRunModel model = JsonUtil.parseRunJson(json);
				// �ж����̰�����
				if (model.getTaskStatus() == WFRunModel.TASK_TYPE_ERROR) {
					Toast.makeText(WfPageActivity.this, "�Ҳ��������ˣ�����ϵ����Ա", Toast.LENGTH_SHORT).show();
				} else if (model.getTaskStatus() == WFRunModel.TASK_TYPE_ARCHIVES) {
					Toast.makeText(WfPageActivity.this, "������ת��ϣ��ѹ鵵", Toast.LENGTH_SHORT).show();
					intent.setClass(WfPageActivity.this, TodoListAllActivity.class);
					startActivity(intent);
				} else if (model.getTaskStatus() == WFRunModel.TASK_TYPE_NOFIND_SENDER) {
					Toast.makeText(WfPageActivity.this, "ϵͳδ�ҵ���һ�������ˣ�����ϵ����Ա", Toast.LENGTH_SHORT).show();
				} else if (model.getTaskStatus() == WFRunModel.TASK_TYPE_NOFIND_TASK_END) {
					Toast.makeText(WfPageActivity.this, "��ǰ�����Ѱ������!", Toast.LENGTH_SHORT).show();
					intent.setClass(WfPageActivity.this, TodoListAllActivity.class);
					startActivity(intent);
				} else { // ˳�����
					Intent wfIntent = new Intent(WfPageActivity.this, SendNextActivity.class);
					wfIntent.putExtra("WFRunModel", model);
					wfIntent.putExtra("instanceId", instanceId);
					wfIntent.putExtra("taskId", taskId);
					wfIntent.putExtra("flowTitle", flowTitle);
					startActivity(wfIntent);
				}
			}
		}
	}

	// ˳�������������ӵ�Service��
	public void transaction() {
		String sissionId = User.getInstance().getUserContext(WfPageActivity.this).getSessionId();
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
		Task task = new Task(Task.KOA_COMMON, "KOA_Mobile_WF_BindReport_NextStepInfo", sissionId, "WfPageActivity", tranHash);
		// �������͸�Service������
		MainService.newTask(task);
		// ���Լ���ӵ�Activity��������
		MainService.addActivity(WfPageActivity.this);
	}

	// ������
	public void opinion_Audit_Save() {
		String sissionId = User.getInstance().getUserContext(WfPageActivity.this).getSessionId();
		String instanceId = intent.getStringExtra("instanceId"); // ���ʵ��ID
		String taskId = intent.getStringExtra("taskId"); // �������ID
		String meId = intent.getStringExtra("meId"); // �������id
		String opinion = "[����Android]";
		String model = android.os.Build.MODEL; // �ֻ��ͺ�
		if (model.indexOf("MI") == 0) {
			opinion = "[����С��]";
		}
		HashMap<String, Object> hashparam = new HashMap<String, Object>();
		hashparam.put("id", instanceId);
		hashparam.put("task_id", taskId);
		hashparam.put("auditId", firstAuditId);
		hashparam.put("meId", meId);
		try {
			hashparam.put("opinion", Base64Encoder.encode(opinion.getBytes("UTF-8")));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		// hashparam.put("opinion", "");
		Task task = new Task(Task.KOA_COMMON, "KOA_Mobile_Worklist_Opinion_Audit_Save", sissionId, "WfPageActivity", hashparam);
		// �������͸�Service������
		MainService.newTask(task);
		// ���Լ���ӵ�Activity��������
		MainService.addActivity(WfPageActivity.this);
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