package koa.android.demo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import koa.android.demo.model.Task;
import koa.android.demo.model.User;
import koa.android.demo.model.WFOpinionLogModel;
import koa.android.logic.ActivityHoder;
import koa.android.logic.MainService;
import koa.android.logic.WfOpinionAdapter;
import koa.android.tools.CreateFieldsHelper;
import koa.android.tools.CreateSubTableHelper;
import koa.android.tools.HttpResponseUtil;
import koa.android.tools.JsonUtil;
import koa.android.tools.LoginTimeOut;
import koa.android.tools.SetBackGround;
import android.app.DownloadManager;
import android.app.ListActivity;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.method.ScrollingMovementMethod;
import android.util.TypedValue;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * ����ֻ��
 * 
 * @author chenM
 * 
 */
public class WfReadActivity extends ListActivity implements KoaActivity, OnGestureListener, OnTouchListener {
	private Button title_bt_left;// ���˰�ť
	private Button formviewBtn = null;// �鿴����ť
	private TextView tvWfTitle = null;// ���������ı���
	private TextView tvWfCreateuser = null;// ���������ı���
	private TextView tvWfCreatedate = null;// ������ʱ���ı���
	private TextView tvWfMemo = null;// ����ժҪ�ı���
	private TextView top_title;// �����ı���
	private String baseUrl = "";
	private Handler handler;
	private ExecutorService executorService;
	private static int THREADPOOL_SIZE = 4;// �̳߳صĴ�С
	private Intent intent;
	private String formjson;// ���������ص�ҳ�����ݵ�JSON������ժҪ��Button��
	private List<WFOpinionLogModel> opinionList_temp;
	private List<WFOpinionLogModel> opinionList;
	private ListView listView;
	private WfOpinionAdapter adapter;
	private LinearLayout list_Header;
	private LinearLayout wf_read_process_bar;
	private String openstate;
	// ================���ظ����õ��ı���==================
	private DownloadManager mgr = null;
	private LinearLayout wf_accessory_table;
	private LinearLayout wf_acessory_area;
	// ===============�ӱ�============================
	private LinearLayout wf_detail_sublist_content;
	// ================ժҪ ===========================
	private LinearLayout wfdetail_area;
	// ===============������ʾ����=======================
	private LinearLayout wf_detail_fields;
	// ===============����ʶ��=========================
	private GestureDetector mGestureDetector;
	private static final int FLING_MIN_DISTANCE = 150;
	private static final int FLING_MIN_VELOCITY = 200;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		LoginTimeOut LoginTimeOut = new LoginTimeOut();
		boolean flag = LoginTimeOut.timeOut(WfReadActivity.this);
		if (!flag) {
			setContentView(R.layout.wfpage_read);
			mGestureDetector = new GestureDetector(this);
			intent = getIntent();
			mgr = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
			setUpView();
			setUpListeners();
			// ��Activity�ŵ�Activity��
			ActivityHoder.getInstance().addActivity(this);
			init();
			handler = new GetWfReadHandler();
			executorService.submit(new GetWfPageThread());// ��ʱ����,����һ�����̻߳�ȡ����
			opinionList_temp = new ArrayList<WFOpinionLogModel>();
			opinionList = new ArrayList<WFOpinionLogModel>();
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
		SetBackGround.getInstance().setTheme(this, formviewBtn, "ui_title_formview_btn", R.drawable.ui_title_formview_btn_blue, null);
	}

	// ��ʼ�����е�UI���
	public void setUpView() {
		list_Header = (LinearLayout) LayoutInflater.from(WfReadActivity.this).inflate(R.layout.wf_detail, null);
		listView = getListView();
		listView.addHeaderView(list_Header);
		listView.setOnTouchListener(this);
		listView.setLongClickable(true);
		executorService = Executors.newFixedThreadPool(THREADPOOL_SIZE);
		title_bt_left = (Button) findViewById(R.id.title_bt_left);
		top_title = (TextView) findViewById(R.id.top_title);
		tvWfTitle = (TextView) findViewById(R.id.wfTaskTitle);
		tvWfCreateuser = (TextView) findViewById(R.id.wfsender);
		tvWfCreatedate = (TextView) findViewById(R.id.wfsenddate);
		tvWfMemo = (TextView) findViewById(R.id.wfmemo);
		// �鿴����ť
		formviewBtn = (Button) findViewById(R.id.title_bt_right);
		// ������
		wf_read_process_bar = (LinearLayout) findViewById(R.id.wf_read_process_bar);
		// �ӱ���ʾ����
		wf_detail_sublist_content = (LinearLayout) findViewById(R.id.wf_detail_sublist_content);
		// ������ʾ����
		wf_accessory_table = (LinearLayout) findViewById(R.id.wf_acessory_area_right);
		wf_acessory_area = (LinearLayout) findViewById(R.id.wf_acessory_area);
		// ����ժҪ
		wfdetail_area = (LinearLayout) findViewById(R.id.wfdetail_area);
		// ����
		wf_detail_fields = (LinearLayout) findViewById(R.id.wf_detail_fields);
	}

	// �˷�������Ԥ����ҳ�����ݺͳ�ʼ��
	public void init() {
		baseUrl = HttpResponseUtil.getInstance().getBaseUrl(this);
		String fromActivity = intent.getStringExtra("fromActivity");
		String userName = User.getInstance().getUserContext(WfReadActivity.this).getUserName();
		if (fromActivity.equals("noticeListActivity")) {
			top_title.setText(userName + " - ֪ͨ����");
			openstate = "2";
		} else if (fromActivity.equals("TodoListActivity")) {
			top_title.setText(userName + " - �ڰ�");
			openstate = "6";
		} else if (fromActivity.equals("HistoryListActivity")) {
			top_title.setText(userName + " - �Ѱ�");
			openstate = "6";
		} else {
			openstate = "2";
		}
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
		// Դ�����
		formviewBtn.setVisibility(View.VISIBLE);
		Resources resources = formviewBtn.getResources();
		float wPx = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 52, resources.getDisplayMetrics());
		float hPx = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 34, resources.getDisplayMetrics());
		FrameLayout.LayoutParams fp = new FrameLayout.LayoutParams(Math.round(wPx), Math.round(hPx));
		fp.gravity = Gravity.RIGHT;
		fp.setMargins(0, 8, 8, 0);
		formviewBtn.setLayoutParams(fp);
		setBackground();
		formviewBtn.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				StringBuffer url = new StringBuffer();
				String sissionId = User.getInstance().getUserContext(WfReadActivity.this).getSessionId();
				String instanceId = intent.getStringExtra("instanceId");// ��ȡinstanceId
				String taskId = intent.getStringExtra("taskId");// ��ȡtaskId
				url.append(baseUrl).append("?").append("sid=").append(sissionId).append("&cmd=WorkFlow_Execute_Worklist_BindReport_Open").append("&id=").append(instanceId).append("&task_id=").append(taskId).append("&openstate=").append(openstate);
				Intent formViewIntent = new Intent(WfReadActivity.this, WfFormViewActivity.class);
				formViewIntent.putExtra("formURL", url.toString());
				startActivity(formViewIntent);
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
		String sessionId = User.getInstance().getUserContext(WfReadActivity.this).getSessionId();// ��ȡsessionId
		if (sessionId != null || !"".equals(sessionId)) {
			HashMap<String, Object> hashparam = new HashMap<String, Object>();
			String instanceId = intent.getStringExtra("instanceId"); // ���ʵ��ID
			String taskId = intent.getStringExtra("taskId"); // �������ID
			String fromActivity = intent.getStringExtra("fromActivity");
			if (fromActivity.equals("HistoryListActivity")) {
				hashparam.put("type", "taskLog");
			}
			hashparam.put("id", instanceId);
			hashparam.put("task_id", taskId);
			formjson = HttpResponseUtil.getInstance().getGetHttpInfo("KOA_Mobile_WF_BindReport_openFormParam", sessionId, hashparam, WfReadActivity.this);
		} else {
			formjson = "SESSION_ERROR";
		}
	}

	class GetWfReadHandler extends Handler {
		@Override
		public void handleMessage(Message msg) {
			if (formjson == null || formjson.equals("")) {
				Toast.makeText(WfReadActivity.this, "�����ݼ����쳣", Toast.LENGTH_SHORT).show();
				getOpinionList();
			} else if (formjson.equals("TIMEOUT")) {
				Toast.makeText(WfReadActivity.this, "���س�ʱ,���Ժ�����", Toast.LENGTH_SHORT).show();
			} else if (formjson.equals("SESSION_TIMEOUT")) {
				HttpResponseUtil.getInstance().reLogin(WfReadActivity.this);
			} else if (formjson.equals("SESSION_ERROR")) {
				Toast.makeText(WfReadActivity.this, "���س�ʱ,���Ժ�����", Toast.LENGTH_SHORT).show();
			} else if (formjson.equals("NETWORK_ERROR")) {
				Toast.makeText(WfReadActivity.this, "���������쳣�����Ժ�����", Toast.LENGTH_SHORT).show();
			} else {
				String formId = JsonUtil.parseJson(formjson, "formId");
				String meId = JsonUtil.parseJson(formjson, "meid");
				// ==================װ�ز���========================================
				intent.putExtra("formId", formId);
				intent.putExtra("meId", meId);
				// ==================��̬��Ӹ����б�===================================
				String fileName = JsonUtil.parseJson(formjson, "fileName");
				if (!fileName.equals("") && !"[]".equals(fileName)) {
					String metaDataMapId = JsonUtil.parseJson(formjson, "metaDataMapId");
					DownloadHelper.getInstance().createAccessoryTable(meId, metaDataMapId, mgr, fileName, WfReadActivity.this, wf_accessory_table);
					wf_acessory_area.setVisibility(View.VISIBLE);
				} else {
					wf_acessory_area.setVisibility(View.GONE);
				}
				// �ش�ID���ڸ�������ʱ��Ĳ�������
				// ===================װ������ժҪ=====================================
				String WfDetail = JsonUtil.parseJson(formjson, "content");
				if (WfDetail.equals("")) {
					wfdetail_area.setVisibility(View.GONE);
				} else {
					// װ������ժҪ
					tvWfMemo.setText(WfDetail);
					tvWfMemo.setMovementMethod(ScrollingMovementMethod.getInstance());
					wfdetail_area.setVisibility(View.VISIBLE);
				}
				// ===================�����������Ŀ=====================================
				String fields = JsonUtil.parseJson(formjson, "fields");
				if (!fields.equals("")) {
					CreateFieldsHelper.getInstance().createSubFields(WfReadActivity.this, formjson, wf_detail_fields);
					wf_detail_fields.setVisibility(View.VISIBLE);
				} else {
					wf_detail_fields.setVisibility(View.GONE);
				}
				// ==================��̬�����ӱ��б�====================================
				String subTable = JsonUtil.parseJson(formjson, "subTable");
				if (!subTable.equals("") && !"[]".equals(subTable)) {
					String instanceId = intent.getStringExtra("instanceId");
					String taskId = intent.getStringExtra("taskId");
					CreateSubTableHelper.getInstance().createSubTable(intent.getStringExtra("fromActivity"), WfReadActivity.this, instanceId, taskId, subTable, wf_detail_sublist_content);
					wf_detail_sublist_content.setVisibility(View.VISIBLE);
				} else {
					wf_detail_sublist_content.setVisibility(View.GONE);
				}
				getOpinionList();
			}
		}
	}

	/**
	 * ��������б�ķ���
	 */
	public void getOpinionList() {
		String sessionId = User.getInstance().getUserContext(WfReadActivity.this).getSessionId();// ��ȡsessionId
		String instanceId = intent.getStringExtra("instanceId"); // ���ʵ��ID
		HashMap<String, Object> hashparam = new HashMap<String, Object>();
		hashparam.put("id", instanceId);
		Task task = new Task(Task.KOA_COMMON, "KOA_Mobile_WF_Send_Log", sessionId, "WfReadActivity", hashparam);
		// �������͸�Service������
		MainService.newTask(task);
		// ���Լ���ӵ�Activity��������
		MainService.addActivity(WfReadActivity.this);
	}

	@Override
	public void refresh(Object... params) {
		String opinionListJson = params[0].toString();
		wf_read_process_bar.setVisibility(View.GONE);
		// ���������
		if (opinionListJson == null) {
			Toast.makeText(WfReadActivity.this, "�����б�����쳣", Toast.LENGTH_SHORT).show();
		} else if (opinionListJson.equals("TIMEOUT")) {
			Toast.makeText(WfReadActivity.this, "���س�ʱ,���Ժ�����", Toast.LENGTH_SHORT).show();
		} else if (opinionListJson.equals("SESSION_TIMEOUT")) {
			HttpResponseUtil.getInstance().reLogin(WfReadActivity.this);
		} else if (opinionListJson.equals("SESSION_ERROR")) {
			Toast.makeText(WfReadActivity.this, "���س�ʱ,���Ժ�����", Toast.LENGTH_SHORT).show();
		} else if (opinionListJson.equals("NETWORK_ERROR")) {
			Toast.makeText(WfReadActivity.this, "���������쳣�����Ժ�����", Toast.LENGTH_SHORT).show();
		} else {
			if (opinionListJson.equals("[]")) {
				Toast.makeText(WfReadActivity.this, "��ʱû���������", Toast.LENGTH_SHORT).show();
			}
			opinionList_temp = JsonUtil.parseWFOpinionLog(opinionListJson);
			boolean changeFlg = false;
			if (opinionList == null) {
				opinionList.clear();
				opinionList.addAll(opinionList_temp);
				changeFlg = true;
			} else {
				opinionList = opinionList_temp;
			}
			adapter = new WfOpinionAdapter(WfReadActivity.this, opinionList);
			if (changeFlg) {
				adapter.notifyDataSetChanged();
			} else {
				listView.setAdapter(adapter);
			}
			listView.setVisibility(View.VISIBLE);
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