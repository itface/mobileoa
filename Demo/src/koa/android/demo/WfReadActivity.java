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
 * 流程只读
 * 
 * @author chenM
 * 
 */
public class WfReadActivity extends ListActivity implements KoaActivity, OnGestureListener, OnTouchListener {
	private Button title_bt_left;// 回退按钮
	private Button formviewBtn = null;// 查看表单按钮
	private TextView tvWfTitle = null;// 任务名称文本域
	private TextView tvWfCreateuser = null;// 任务发送人文本域
	private TextView tvWfCreatedate = null;// 任务发送时间文本域
	private TextView tvWfMemo = null;// 任务摘要文本域
	private TextView top_title;// 顶部文本域
	private String baseUrl = "";
	private Handler handler;
	private ExecutorService executorService;
	private static int THREADPOOL_SIZE = 4;// 线程池的大小
	private Intent intent;
	private String formjson;// 服务器返回的页面内容的JSON（包含摘要和Button）
	private List<WFOpinionLogModel> opinionList_temp;
	private List<WFOpinionLogModel> opinionList;
	private ListView listView;
	private WfOpinionAdapter adapter;
	private LinearLayout list_Header;
	private LinearLayout wf_read_process_bar;
	private String openstate;
	// ================下载附件用到的变量==================
	private DownloadManager mgr = null;
	private LinearLayout wf_accessory_table;
	private LinearLayout wf_acessory_area;
	// ===============子表============================
	private LinearLayout wf_detail_sublist_content;
	// ================摘要 ===========================
	private LinearLayout wfdetail_area;
	// ===============其他显示区域=======================
	private LinearLayout wf_detail_fields;
	// ===============手势识别=========================
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
			// 将Activity放到Activity中
			ActivityHoder.getInstance().addActivity(this);
			init();
			handler = new GetWfReadHandler();
			executorService.submit(new GetWfPageThread());// 耗时操作,开启一个新线程获取数据
			opinionList_temp = new ArrayList<WFOpinionLogModel>();
			opinionList = new ArrayList<WFOpinionLogModel>();
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
		SetBackGround.getInstance().setTheme(this, formviewBtn, "ui_title_formview_btn", R.drawable.ui_title_formview_btn_blue, null);
	}

	// 初始化所有的UI组件
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
		// 查看表单按钮
		formviewBtn = (Button) findViewById(R.id.title_bt_right);
		// 进度条
		wf_read_process_bar = (LinearLayout) findViewById(R.id.wf_read_process_bar);
		// 子表显示区域
		wf_detail_sublist_content = (LinearLayout) findViewById(R.id.wf_detail_sublist_content);
		// 附件显示区域
		wf_accessory_table = (LinearLayout) findViewById(R.id.wf_acessory_area_right);
		wf_acessory_area = (LinearLayout) findViewById(R.id.wf_acessory_area);
		// 流程摘要
		wfdetail_area = (LinearLayout) findViewById(R.id.wfdetail_area);
		// 其他
		wf_detail_fields = (LinearLayout) findViewById(R.id.wf_detail_fields);
	}

	// 此方法用来预加载页面内容和初始化
	public void init() {
		baseUrl = HttpResponseUtil.getInstance().getBaseUrl(this);
		String fromActivity = intent.getStringExtra("fromActivity");
		String userName = User.getInstance().getUserContext(WfReadActivity.this).getUserName();
		if (fromActivity.equals("noticeListActivity")) {
			top_title.setText(userName + " - 通知传阅");
			openstate = "2";
		} else if (fromActivity.equals("TodoListActivity")) {
			top_title.setText(userName + " - 在办");
			openstate = "6";
		} else if (fromActivity.equals("HistoryListActivity")) {
			top_title.setText(userName + " - 已办");
			openstate = "6";
		} else {
			openstate = "2";
		}
		// 预加载页面内容
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

	// 定义组件监听器
	public void setUpListeners() {
		// 后退按钮
		title_bt_left.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				finish();
			}
		});
		// 源表单浏览
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
				String instanceId = intent.getStringExtra("instanceId");// 获取instanceId
				String taskId = intent.getStringExtra("taskId");// 获取taskId
				url.append(baseUrl).append("?").append("sid=").append(sissionId).append("&cmd=WorkFlow_Execute_Worklist_BindReport_Open").append("&id=").append(instanceId).append("&task_id=").append(taskId).append("&openstate=").append(openstate);
				Intent formViewIntent = new Intent(WfReadActivity.this, WfFormViewActivity.class);
				formViewIntent.putExtra("formURL", url.toString());
				startActivity(formViewIntent);
			}
		});
	}

	// 新的线程
	class GetWfPageThread implements Runnable {
		@Override
		public void run() {
			// 请求服务器刷新页面
			refreshDetail();
			Message msg = handler.obtainMessage();// 通知线程来处理范围的数据
			handler.sendMessage(msg);
		}
	}

	// 请求服务器，刷新页面内容的方法
	public void refreshDetail() {
		String sessionId = User.getInstance().getUserContext(WfReadActivity.this).getSessionId();// 获取sessionId
		if (sessionId != null || !"".equals(sessionId)) {
			HashMap<String, Object> hashparam = new HashMap<String, Object>();
			String instanceId = intent.getStringExtra("instanceId"); // 获得实例ID
			String taskId = intent.getStringExtra("taskId"); // 获得任务ID
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
				Toast.makeText(WfReadActivity.this, "表单数据加载异常", Toast.LENGTH_SHORT).show();
				getOpinionList();
			} else if (formjson.equals("TIMEOUT")) {
				Toast.makeText(WfReadActivity.this, "加载超时,请稍后再试", Toast.LENGTH_SHORT).show();
			} else if (formjson.equals("SESSION_TIMEOUT")) {
				HttpResponseUtil.getInstance().reLogin(WfReadActivity.this);
			} else if (formjson.equals("SESSION_ERROR")) {
				Toast.makeText(WfReadActivity.this, "加载超时,请稍后再试", Toast.LENGTH_SHORT).show();
			} else if (formjson.equals("NETWORK_ERROR")) {
				Toast.makeText(WfReadActivity.this, "网络连接异常，请稍后再试", Toast.LENGTH_SHORT).show();
			} else {
				String formId = JsonUtil.parseJson(formjson, "formId");
				String meId = JsonUtil.parseJson(formjson, "meid");
				// ==================装载参数========================================
				intent.putExtra("formId", formId);
				intent.putExtra("meId", meId);
				// ==================动态添加附件列表===================================
				String fileName = JsonUtil.parseJson(formjson, "fileName");
				if (!fileName.equals("") && !"[]".equals(fileName)) {
					String metaDataMapId = JsonUtil.parseJson(formjson, "metaDataMapId");
					DownloadHelper.getInstance().createAccessoryTable(meId, metaDataMapId, mgr, fileName, WfReadActivity.this, wf_accessory_table);
					wf_acessory_area.setVisibility(View.VISIBLE);
				} else {
					wf_acessory_area.setVisibility(View.GONE);
				}
				// 回传ID用于附件下载时候的参数传递
				// ===================装载流程摘要=====================================
				String WfDetail = JsonUtil.parseJson(formjson, "content");
				if (WfDetail.equals("")) {
					wfdetail_area.setVisibility(View.GONE);
				} else {
					// 装载流程摘要
					tvWfMemo.setText(WfDetail);
					tvWfMemo.setMovementMethod(ScrollingMovementMethod.getInstance());
					wfdetail_area.setVisibility(View.VISIBLE);
				}
				// ===================附加区域的项目=====================================
				String fields = JsonUtil.parseJson(formjson, "fields");
				if (!fields.equals("")) {
					CreateFieldsHelper.getInstance().createSubFields(WfReadActivity.this, formjson, wf_detail_fields);
					wf_detail_fields.setVisibility(View.VISIBLE);
				} else {
					wf_detail_fields.setVisibility(View.GONE);
				}
				// ==================动态创建子表列表====================================
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
	 * 获得审批列表的方法
	 */
	public void getOpinionList() {
		String sessionId = User.getInstance().getUserContext(WfReadActivity.this).getSessionId();// 获取sessionId
		String instanceId = intent.getStringExtra("instanceId"); // 获得实例ID
		HashMap<String, Object> hashparam = new HashMap<String, Object>();
		hashparam.put("id", instanceId);
		Task task = new Task(Task.KOA_COMMON, "KOA_Mobile_WF_Send_Log", sessionId, "WfReadActivity", hashparam);
		// 将任务发送给Service来处理
		MainService.newTask(task);
		// 把自己添加到Activity集合里面
		MainService.addActivity(WfReadActivity.this);
	}

	@Override
	public void refresh(Object... params) {
		String opinionListJson = params[0].toString();
		wf_read_process_bar.setVisibility(View.GONE);
		// 处理反馈结果
		if (opinionListJson == null) {
			Toast.makeText(WfReadActivity.this, "审批列表加载异常", Toast.LENGTH_SHORT).show();
		} else if (opinionListJson.equals("TIMEOUT")) {
			Toast.makeText(WfReadActivity.this, "加载超时,请稍后再试", Toast.LENGTH_SHORT).show();
		} else if (opinionListJson.equals("SESSION_TIMEOUT")) {
			HttpResponseUtil.getInstance().reLogin(WfReadActivity.this);
		} else if (opinionListJson.equals("SESSION_ERROR")) {
			Toast.makeText(WfReadActivity.this, "加载超时,请稍后再试", Toast.LENGTH_SHORT).show();
		} else if (opinionListJson.equals("NETWORK_ERROR")) {
			Toast.makeText(WfReadActivity.this, "网络连接异常，请稍后再试", Toast.LENGTH_SHORT).show();
		} else {
			if (opinionListJson.equals("[]")) {
				Toast.makeText(WfReadActivity.this, "暂时没有审批意见", Toast.LENGTH_SHORT).show();
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