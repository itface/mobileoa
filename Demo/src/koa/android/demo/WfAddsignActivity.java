package koa.android.demo;

import java.util.HashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import koa.android.demo.model.User;
import koa.android.logic.ActivityHoder;
import koa.android.tools.CreateFieldsHelper;
import koa.android.tools.CreateSubTableHelper;
import koa.android.tools.HttpResponseUtil;
import koa.android.tools.JsonUtil;
import koa.android.tools.LoginTimeOut;
import koa.android.tools.SetBackGround;
import android.app.Activity;
import android.app.DownloadManager;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.method.ScrollingMovementMethod;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
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
 * 待办-加签完毕
 * 
 * @author chenM
 * 
 */
public class WfAddsignActivity extends Activity implements KoaActivity, OnGestureListener, OnTouchListener {
	private Button title_bt_left;// 回退按钮
	private TextView top_title;// 顶部文本域
	// 操作区域的Button
	private Button transBtn;// 加签按钮
	private Button examBtn;// 审批流程按钮
	private Button optionBtn;// 审批历史按钮
	private Button formviewBtn;// 查看原表单按钮
	// 流程信息显示区域
	private TextView tvWfTitle = null;// 任务名称文本域
	private TextView tvWfCreateuser = null;// 任务发送人文本域
	private TextView tvWfCreatedate = null;// 任务发送时间文本域
	private TextView tvWfMemo = null;// 任务摘要文本域
	private String baseUrl = "";
	private Handler handler;
	private ExecutorService executorService;
	private static int THREADPOOL_SIZE = 4;// 线程池的大小
	private Intent intent;
	private String formjson;// 服务器返回的页面内容的JSON（包含摘要和Button）
	private boolean dataReturnFlag = false;// 服务器是否返回数据标志位
	private LinearLayout wf_detail_operate_bar;
	// 进度条显示
	private RelativeLayout wf_read_process_bar;
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
	private ScrollView wf_all_ScrollView;
	private FrameLayout wf_main_FrameLayout;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		LoginTimeOut LoginTimeOut = new LoginTimeOut();
		boolean flag = LoginTimeOut.timeOut(WfAddsignActivity.this);
		if (!flag) {
			setContentView(R.layout.wfpage);
			mGestureDetector = new GestureDetector(this);
			intent = getIntent();
			mgr = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
			setUpView();
			setUpListeners();
			// 将Activity放到Activity中
			ActivityHoder.getInstance().addActivity(this);
			init();
			handler = new GetWFAddsignHandler();
			executorService.submit(new GetWfPageThread());// 耗时操作,开启一个新线程获取数据
		}
	}

	// 初始化所有的UI组件
	public void setUpView() {
		executorService = Executors.newFixedThreadPool(THREADPOOL_SIZE);
		title_bt_left = (Button) findViewById(R.id.title_bt_left);
		top_title = (TextView) findViewById(R.id.top_title);
		tvWfTitle = (TextView) findViewById(R.id.wfTaskTitle);
		tvWfCreateuser = (TextView) findViewById(R.id.wfsender);
		tvWfCreatedate = (TextView) findViewById(R.id.wfsenddate);
		tvWfMemo = (TextView) findViewById(R.id.wfmemo);
		optionBtn = (Button) findViewById(R.id.optionBtn);
		formviewBtn = (Button) findViewById(R.id.formviewBtn);
		transBtn = (Button) findViewById(R.id.transBtn);
		wf_detail_operate_bar = (LinearLayout) findViewById(R.id.wf_detail_operate_bar);
		// 设置加签按钮
		transBtn.setText("加签此流程");
		Drawable drawableTop = getResources().getDrawable(R.drawable.wf_detail_addsign);
		transBtn.setCompoundDrawablesWithIntrinsicBounds(null, drawableTop, null, null);
		examBtn = (Button) findViewById(R.id.examBtn);
		// 隐藏审批流程按钮
		examBtn.setVisibility(View.GONE);
		// 进度条显示区域
		wf_read_process_bar = (RelativeLayout) findViewById(R.id.wf_read_process_bar);
		// 子表显示区域
		wf_detail_sublist_content = (LinearLayout) findViewById(R.id.wf_detail_sublist_content);
		// 附件显示区域
		wf_accessory_table = (LinearLayout) findViewById(R.id.wf_acessory_area_right);
		wf_acessory_area = (LinearLayout) findViewById(R.id.wf_acessory_area);
		// 流程摘要
		wfdetail_area = (LinearLayout) findViewById(R.id.wfdetail_area);
		// 其他
		wf_detail_fields = (LinearLayout) findViewById(R.id.wf_detail_fields);
		setBackground();
		// 手势识别
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

	// 设置背景图
	private void setBackground() {
		// 设置背景图
		SetBackGround.getInstance().setTheme(this, top_title, "top_title", R.drawable.ui_title_bg_blue, null);
		SetBackGround.getInstance().setTheme(this, title_bt_left, "ui_backbtn", R.drawable.ui_title_back_btn_blue, null);
		SetBackGround.getInstance().setTheme(this, wf_detail_operate_bar, "wf_detail_operate_bar", R.drawable.wf_detail_operate_bar_bg_blue, null);
	}

	// 此方法用来预加载页面内容和初始化
	public void init() {
		baseUrl = HttpResponseUtil.getInstance().getBaseUrl(WfAddsignActivity.this);
		String userName = User.getInstance().getUserContext(WfAddsignActivity.this).getUserName();
		top_title.setText(userName + " - 待办");
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
		// 点击加签按钮
		transBtn.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				if (!dataReturnFlag) {
					Toast.makeText(WfAddsignActivity.this, "请等待数据加载", Toast.LENGTH_SHORT).show();
				} else {
					intent.setClass(WfAddsignActivity.this, WfAddsignOpinionActivity.class);
					startActivity(intent);
				}
			}
		});
		// 浏览原表单
		formviewBtn.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				StringBuffer url = new StringBuffer();
				String sissionId = User.getInstance().getUserContext(WfAddsignActivity.this).getSessionId();
				String instanceId = intent.getStringExtra("instanceId");// 获取instanceId
				String taskId = intent.getStringExtra("taskId");// 获取taskId
				url.append(baseUrl).append("?").append("sid=").append(sissionId).append("&cmd=WorkFlow_Execute_Worklist_BindReport_Open").append("&id=").append(instanceId).append("&task_id=").append(taskId).append("&openstate=1");
				Intent formViewIntent = new Intent(WfAddsignActivity.this, WfFormViewActivity.class);
				formViewIntent.putExtra("formURL", url.toString());
				startActivity(formViewIntent);
			}
		});
		// 审批列表
		optionBtn.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				intent.setClass(WfAddsignActivity.this, OpinionHistoryActivity.class);
				startActivity(intent);
			}
		});
	}

	// 新的线程
	class GetWfPageThread implements Runnable {
		@Override
		public void run() {
			wf_read_process_bar.setVisibility(View.VISIBLE);
			// 请求服务器刷新页面
			refreshDetail();
			Message msg = handler.obtainMessage();// 通知线程来处理范围的数据
			handler.sendMessage(msg);
		}
	}

	// 请求服务器，刷新页面内容的方法
	public void refreshDetail() {
		String sessionId = User.getInstance().getUserContext(WfAddsignActivity.this).getSessionId();// 获取sessionId
		if (sessionId != null || !"".equals(sessionId)) {
			HashMap<String, Object> hashparam = new HashMap<String, Object>();
			String instanceId = intent.getStringExtra("instanceId"); // 获得实例ID
			String taskId = intent.getStringExtra("taskId"); // 获得任务ID
			hashparam.put("id", instanceId);
			hashparam.put("task_id", taskId);
			formjson = HttpResponseUtil.getInstance().getGetHttpInfo("KOA_Mobile_WF_BindReport_openFormParam", sessionId, hashparam, WfAddsignActivity.this);
		} else {
			formjson = "SESSION_ERROR";
		}
	}

	// 接收服务器参数,构建表格
	class GetWFAddsignHandler extends Handler {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			wf_read_process_bar.setVisibility(View.GONE);
			dataReturnFlag = true;
			if (formjson == null || formjson.equals("")) {
				Toast.makeText(WfAddsignActivity.this, "表单数据加载异常", Toast.LENGTH_SHORT).show();
			} else if (formjson.equals("TIMEOUT")) {
				Toast.makeText(WfAddsignActivity.this, "加载超时,请稍后再试", Toast.LENGTH_SHORT).show();
			} else if (formjson.equals("NETWORK_ERROR")) {
				Toast.makeText(WfAddsignActivity.this, "网络连接异常，请稍后再试", Toast.LENGTH_SHORT).show();
			} else if (formjson.equals("SESSION_TIMEOUT")) {
				HttpResponseUtil.getInstance().reLogin(WfAddsignActivity.this);
			} else if (formjson.equals("SESSION_ERROR")) {
				Toast.makeText(WfAddsignActivity.this, "加载超时,请稍后再试", Toast.LENGTH_SHORT).show();
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
					DownloadHelper.getInstance().createAccessoryTable(meId, metaDataMapId, mgr, fileName, WfAddsignActivity.this, wf_accessory_table);
					wf_acessory_area.setVisibility(View.VISIBLE);
				} else {
					wf_acessory_area.setVisibility(View.GONE);
				}
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
					CreateFieldsHelper.getInstance().createSubFields(WfAddsignActivity.this, formjson, wf_detail_fields);
					wf_detail_fields.setVisibility(View.VISIBLE);
				} else {
					wf_detail_fields.setVisibility(View.GONE);
				}
				// ==================动态创建子表列表====================================
				String subTable = JsonUtil.parseJson(formjson, "subTable");
				if (!subTable.equals("") && !"[]".equals(subTable)) {
					String instanceId = intent.getStringExtra("instanceId");
					String taskId = intent.getStringExtra("taskId");
					CreateSubTableHelper.getInstance().createSubTable("TodoListAllActivity", WfAddsignActivity.this, instanceId, taskId, subTable, wf_detail_sublist_content);
					wf_detail_sublist_content.setVisibility(View.VISIBLE);
				} else {
					wf_detail_sublist_content.setVisibility(View.GONE);
				}
			}
		}
	}

	// 此方法用来处理页面的参数提交后的回调
	public void refresh(Object... params) {

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