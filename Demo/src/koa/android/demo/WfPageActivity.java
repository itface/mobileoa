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
 * 待办-流程办理
 * 
 * @author chenM
 * 
 */
public class WfPageActivity extends Activity implements KoaActivity, OnGestureListener, OnTouchListener {
	private Button title_bt_left;// 回退按钮
	private TextView top_title;// 顶部文本域
	// 操作区域的Button
	private Button transBtn;// 同意按钮
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
	private int firstAuditId;// 第一个审核意见按钮的ID
	private ProgressDialog pd;
	private String opinionButtonJson;// 服务器返回的审核意见Button的JSON
	private boolean dataReturnFlag = false;// 服务器是否返回数据标志位
	private String instanceId;
	private String taskId;
	private String flowTitle;
	private int jsonStatus;// 判断是哪个CMD返回的数据
	private static final int OPININON_SAVE = 0;
	private static final int TRANSACTION = 1;
	private LinearLayout wf_detail_operate_bar;
	// 进度条
	private RelativeLayout wf_read_process_bar;
	// ================下载附件用到的变量=================
	private DownloadManager mgr = null;
	private LinearLayout wf_accessory_table;
	private LinearLayout wf_acessory_area;
	// ===============子表===========================
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
			// 将Activity放到Activity中
			ActivityHoder.getInstance().addActivity(this);
			init();
			handler = new GetWFPageHandler();
			executorService.submit(new GetWfPageThread());// 耗时操作,开启一个新线程获取数据
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

	// 初始化所有的UI组件
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
		// 进度条显示区域
		wf_read_process_bar = (RelativeLayout) findViewById(R.id.wf_read_process_bar);
		// 子表显示区域
		wf_detail_sublist_content = (LinearLayout) findViewById(R.id.wf_detail_sublist_content);
		// 附件显示区域
		wf_accessory_table = (LinearLayout) findViewById(R.id.wf_acessory_area_right);
		wf_acessory_area = (LinearLayout) findViewById(R.id.wf_acessory_area);
		setBackground();
		// 流程摘要
		wfdetail_area = (LinearLayout) findViewById(R.id.wfdetail_area);
		// 其他
		wf_detail_fields = (LinearLayout) findViewById(R.id.wf_detail_fields);
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
		baseUrl = HttpResponseUtil.getInstance().getBaseUrl(this);
		String userName = User.getInstance().getUserContext(WfPageActivity.this).getUserName();
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
		// 点击办理按钮
		transBtn.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				if (timeOutFlag) {
					Toast.makeText(WfPageActivity.this, "加载超时,请稍后再试", Toast.LENGTH_SHORT).show();
				} else {
					if (!dataReturnFlag) {
						Toast.makeText(WfPageActivity.this, "请等待数据加载", Toast.LENGTH_SHORT).show();
					} else {
						if (!wetherCanDeal) {
							Toast.makeText(WfPageActivity.this, "当前任务有隐藏的必填项,请通过电脑办理", Toast.LENGTH_LONG).show();
						} else {
							// 如果没有同意这个意见
							if (firstAuditId == -1) {
								Toast.makeText(WfPageActivity.this, "当前任务没有同意意见,请选择其他意见继续办理", Toast.LENGTH_LONG).show();
							} else {
								// 判断是否存在有Button的JSON
								if (!"".equals(opinionButtonJson) && !"[]".equals(opinionButtonJson) && null != opinionButtonJson) {
									pd = new ProgressDialog(WfPageActivity.this);
									pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
									pd.setCancelable(true);
									pd.setMessage("正在办理...");
									pd.show();
									ProgressDialogHolder.getInstance().addActivity(pd);
									jsonStatus = OPININON_SAVE;
									// 保存审核意见
									opinion_Audit_Save();
								} else {
									pd = new ProgressDialog(WfPageActivity.this);
									pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
									pd.setCancelable(true);
									pd.setMessage("正在办理...");
									pd.show();
									ProgressDialogHolder.getInstance().addActivity(pd);
									// 顺序办理
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
					Toast.makeText(WfPageActivity.this, "加载超时,请稍后再试", Toast.LENGTH_SHORT).show();
				} else {
					if (!dataReturnFlag) {
						Toast.makeText(WfPageActivity.this, "请等待数据加载", Toast.LENGTH_SHORT).show();
					} else {
						if (!wetherCanDeal) {
							Toast.makeText(WfPageActivity.this, "当前任务有隐藏的必填项,请通过电脑办理", Toast.LENGTH_LONG).show();
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
				String instanceId = intent.getStringExtra("instanceId");// 获取instanceId
				String taskId = intent.getStringExtra("taskId");// 获取taskId
				url.append(baseUrl).append("?").append("sid=").append(sissionId).append("&cmd=WorkFlow_Execute_Worklist_BindReport_Open").append("&id=").append(instanceId).append("&task_id=").append(taskId).append("&openstate=1");
				Intent formViewIntent = new Intent(WfPageActivity.this, WfFormViewActivity.class);
				formViewIntent.putExtra("formURL", url.toString());
				startActivity(formViewIntent);
			}
		});
		// 审批列表
		optionBtn.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				intent.setClass(WfPageActivity.this, OpinionHistoryActivity.class);
				startActivity(intent);
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
		String sessionId = User.getInstance().getUserContext(WfPageActivity.this).getSessionId();// 获取sessionId
		if (sessionId != null || !"".equals(sessionId)) {
			HashMap<String, Object> hashparam = new HashMap<String, Object>();
			String instanceId = intent.getStringExtra("instanceId"); // 获得实例ID
			String taskId = intent.getStringExtra("taskId"); // 获得任务ID
			hashparam.put("id", instanceId);
			hashparam.put("task_id", taskId);
			formjson = HttpResponseUtil.getInstance().getGetHttpInfo("KOA_Mobile_WF_BindReport_openFormParam", sessionId, hashparam, WfPageActivity.this);
		} else {
			formjson = "SESSION_ERROR";
		}
	}

	// 接收服务器参数,构建表格
	class GetWFPageHandler extends Handler {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			dataReturnFlag = true;
			wf_read_process_bar.setVisibility(View.GONE);
			System.out.println(formjson);

			if (formjson == null || formjson.equals("")) {
				Toast.makeText(WfPageActivity.this, "表单信息加载异常", Toast.LENGTH_SHORT).show();
				timeOutFlag = true;
			} else if (formjson.equals("TIMEOUT")) {
				Toast.makeText(WfPageActivity.this, "加载超时,请稍后再试", Toast.LENGTH_SHORT).show();
				timeOutFlag = true;
			} else if (formjson.equals("SESSION_TIMEOUT")) {
				HttpResponseUtil.getInstance().reLogin(WfPageActivity.this);
			} else if (formjson.equals("SESSION_ERROR")) {
				Toast.makeText(WfPageActivity.this, "加载超时,请稍后再试", Toast.LENGTH_SHORT).show();
				timeOutFlag = true;
			} else if (formjson.equals("NETWORK_ERROR")) {
				Toast.makeText(WfPageActivity.this, "网络连接异常，请稍后再试", Toast.LENGTH_SHORT).show();
				timeOutFlag = true;
			} else {
				// 判断服务器端的摘要信息是否有填写，当fileName，content，fields，subTable均为空时，不能办理
				boolean isAbstractNull = true;

				String formId = JsonUtil.parseJson(formjson, "formId");
				String meId = JsonUtil.parseJson(formjson, "meid");
				// ==================装载参数========================================
				intent.putExtra("formId", formId);
				intent.putExtra("meId", meId);
				// ==================动态添加附件列表===================================
				String fileName = JsonUtil.parseJson(formjson, "fileName");
				if (!fileName.equals("") && !"[]".equals(fileName)) {
					isAbstractNull = false;
					// 回传ID用于附件下载时候的参数传递
					String metaDataMapId = JsonUtil.parseJson(formjson, "metaDataMapId");
					DownloadHelper.getInstance().createAccessoryTable(meId, metaDataMapId, mgr, fileName, WfPageActivity.this, wf_accessory_table);
					wf_acessory_area.setVisibility(View.VISIBLE);
				} else {
					wf_acessory_area.setVisibility(View.GONE);
				}
				// ===================装载流程摘要=====================================
				String WfDetail = JsonUtil.parseJson(formjson, "content");
				if (WfDetail.equals("") || "[]".equals(WfDetail)) {
					wfdetail_area.setVisibility(View.GONE);
				} else {
					isAbstractNull = false;
					// 装载流程摘要
					tvWfMemo.setText(WfDetail);
					tvWfMemo.setMovementMethod(ScrollingMovementMethod.getInstance());
					wfdetail_area.setVisibility(View.VISIBLE);
				}
				// ===================附加区域的项目=====================================
				String fields = JsonUtil.parseJson(formjson, "fields");
				if (!fields.equals("") && !"[]".equals(fields)) {
					isAbstractNull = false;
					CreateFieldsHelper.getInstance().createSubFields(WfPageActivity.this, formjson, wf_detail_fields);
					wf_detail_fields.setVisibility(View.VISIBLE);
				} else {
					wf_detail_fields.setVisibility(View.GONE);
				}
				// ==================动态创建子表列表====================================
				String subTable = JsonUtil.parseJson(formjson, "subTable");
				if (!subTable.equals("") && !"[]".equals(subTable)) {
					isAbstractNull = false;
					CreateSubTableHelper.getInstance().createSubTable("TodoListAllActivity", WfPageActivity.this, instanceId, taskId, subTable, wf_detail_sublist_content);
					wf_detail_sublist_content.setVisibility(View.VISIBLE);
				} else {
					wf_detail_sublist_content.setVisibility(View.GONE);
				}
				// ==============================动态添加意见列表按钮=====================
				// 装载意见列表的参数
				opinionButtonJson = JsonUtil.parseJson(formjson, "button");
				if (!"".equals(opinionButtonJson) && !"[]".equals(opinionButtonJson)) {
					try {
						JSONArray jsonArray = new JSONArray(opinionButtonJson);
						for (int i = 0; i < jsonArray.length(); i++) {
							JSONObject jsonObj = ((JSONObject) jsonArray.opt(i));
							String opinionName = jsonObj.getString("opinionName");
							if (opinionName.equals("同意")) {
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
				// 以下代码判断是否有摘要内容
				if (!isAbstractNull) {
					wetherCanDeal = true;
				} else {
					TextView tip = new TextView(WfPageActivity.this);
					tip.setText("【提示】\n当前任务有隐藏的必填项,请通过电脑办理");
					tip.setTextColor(Color.parseColor("#3F8AC5"));
					tip.setTextSize(16);
					tip.setGravity(Gravity.CENTER);
					wf_detail_sublist_content.addView(tip);
					wf_detail_sublist_content.setVisibility(View.VISIBLE);
				}
			}
		}
	}

	// 此方法用来处理页面的参数提交后的回调
	public void refresh(Object... params) {
		if (jsonStatus == OPININON_SAVE) {
			String opinionJson = params[0].toString();
			if (opinionJson.equals("true")) {
				// 顺序办理
				jsonStatus = TRANSACTION;
				transaction();
			} else if (opinionJson.equals("TIMEOUT")) {
				Toast.makeText(WfPageActivity.this, "加载超时,请稍后再试", Toast.LENGTH_SHORT).show();
			} else if (opinionJson.equals("SESSION_TIMEOUT")) {
				HttpResponseUtil.getInstance().reLogin(WfPageActivity.this);
			} else if (opinionJson.equals("NETWORK_ERROR")) {
				Toast.makeText(WfPageActivity.this, "网络连接异常，请稍后再试", Toast.LENGTH_SHORT).show();
			} else {
				ProgressDialogHolder.getInstance().exit();
				Toast.makeText(WfPageActivity.this, "审核意见添加失败", Toast.LENGTH_SHORT).show();
			}
		} else if (jsonStatus == TRANSACTION) {
			ProgressDialogHolder.getInstance().exit();
			String json = params[0].toString();
			if (json == null || "".equals(json)) {
				Toast.makeText(WfPageActivity.this, "返回值异常，请联系管理员", Toast.LENGTH_SHORT).show();
			} else if (json.equals("TIMEOUT")) {
				Toast.makeText(WfPageActivity.this, "加载超时,请稍后再试", Toast.LENGTH_SHORT).show();
			} else if (json.equals("SESSION_TIMEOUT")) {
				HttpResponseUtil.getInstance().reLogin(WfPageActivity.this);
			} else if (json.equals("NETWORK_ERROR")) {
				Toast.makeText(WfPageActivity.this, "网络连接异常，请稍后再试", Toast.LENGTH_SHORT).show();
			} else if (json.contains("error")) {
				Toast.makeText(WfPageActivity.this, "找不到办理人，请联系管理员", Toast.LENGTH_SHORT).show();
			} else {
				WFRunModel model = JsonUtil.parseRunJson(json);
				// 判断流程办理动作
				if (model.getTaskStatus() == WFRunModel.TASK_TYPE_ERROR) {
					Toast.makeText(WfPageActivity.this, "找不到办理人，请联系管理员", Toast.LENGTH_SHORT).show();
				} else if (model.getTaskStatus() == WFRunModel.TASK_TYPE_ARCHIVES) {
					Toast.makeText(WfPageActivity.this, "流程流转完毕，已归档", Toast.LENGTH_SHORT).show();
					intent.setClass(WfPageActivity.this, TodoListAllActivity.class);
					startActivity(intent);
				} else if (model.getTaskStatus() == WFRunModel.TASK_TYPE_NOFIND_SENDER) {
					Toast.makeText(WfPageActivity.this, "系统未找到下一个办理人，请联系管理员", Toast.LENGTH_SHORT).show();
				} else if (model.getTaskStatus() == WFRunModel.TASK_TYPE_NOFIND_TASK_END) {
					Toast.makeText(WfPageActivity.this, "当前任务已办理完毕!", Toast.LENGTH_SHORT).show();
					intent.setClass(WfPageActivity.this, TodoListAllActivity.class);
					startActivity(intent);
				} else { // 顺序办理
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

	// 顺序办理，将任务添加到Service中
	public void transaction() {
		String sissionId = User.getInstance().getUserContext(WfPageActivity.this).getSessionId();
		String instanceId = intent.getStringExtra("instanceId"); // 获得实例ID
		String taskId = intent.getStringExtra("taskId"); // 获得任务ID
		String meId = intent.getStringExtra("meId"); // 获得数据id
		String formId = intent.getStringExtra("formId");
		HashMap<String, Object> tranHash = new HashMap<String, Object>();
		tranHash.put("id", instanceId);// 获得实例ID
		tranHash.put("task_id", taskId);// 获得任务ID
		tranHash.put("meId", meId); // 获得数据id
		tranHash.put("formId", formId);
		tranHash.put("openstate", "1");
		Task task = new Task(Task.KOA_COMMON, "KOA_Mobile_WF_BindReport_NextStepInfo", sissionId, "WfPageActivity", tranHash);
		// 将任务发送给Service来处理
		MainService.newTask(task);
		// 把自己添加到Activity集合里面
		MainService.addActivity(WfPageActivity.this);
	}

	// 添加意见
	public void opinion_Audit_Save() {
		String sissionId = User.getInstance().getUserContext(WfPageActivity.this).getSessionId();
		String instanceId = intent.getStringExtra("instanceId"); // 获得实例ID
		String taskId = intent.getStringExtra("taskId"); // 获得任务ID
		String meId = intent.getStringExtra("meId"); // 获得数据id
		String opinion = "[发自Android]";
		String model = android.os.Build.MODEL; // 手机型号
		if (model.indexOf("MI") == 0) {
			opinion = "[发自小米]";
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
		// 将任务发送给Service来处理
		MainService.newTask(task);
		// 把自己添加到Activity集合里面
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