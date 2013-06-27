package koa.android.demo;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import koa.android.demo.model.User;
import koa.android.demo.model.WFTaskModel;
import koa.android.logic.ActivityHoder;
import koa.android.logic.MainService;
import koa.android.logic.ProgressDialogHolder;
import koa.android.logic.PullToRefreshListView;
import koa.android.logic.PullToRefreshListView.IXListViewListener;
import koa.android.logic.WfAdapter;
import koa.android.tools.HttpResponseUtil;
import koa.android.tools.JsonUtil;
import koa.android.tools.LoginTimeOut;
import koa.android.tools.SetBackGround;
import android.app.Activity;
import android.app.NotificationManager;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

/**
 * 待办页面
 * 
 * @author chenM
 * 
 */
public class TodoListAllActivity extends Activity {
	private List<WFTaskModel> taskList;
	private List<WFTaskModel> taskList_temp;// 重新请求过来的LIST
	private ProgressDialog pd;
	private TextView top_title;
	private Button title_bt_left;
	private Button title_bt_right;
	private PullToRefreshListView listView;
	private Handler handler;
	private WfAdapter adapter;
	private ExecutorService executorService;
	private String todolist_json;
	private static int THREADPOOL_SIZE = 4;// 线程池的大小
	private NotificationManager KoaNotiManager;// 状态栏提醒管理
	private boolean isListViewFresh = true;
	private int cacheListViewPosition = 0;
	private LinearLayout wf_list_search_area;
	private LinearLayout todolistall_main_linearLayout;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		LoginTimeOut LoginTimeOut = new LoginTimeOut();
		boolean flag = LoginTimeOut.timeOut(TodoListAllActivity.this);
		if (!flag) {
			setContentView(R.layout.todolistall);
			setUpView();
			setUpListeners();
			// 将Activity放到Activity中
			ActivityHoder.getInstance().addActivity(this);
			KoaNotiManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
			String userName = User.getInstance().getUserContext(TodoListAllActivity.this).getUserName();
			top_title.setText(userName + " - 待办");
			pd = new ProgressDialog(TodoListAllActivity.this);
			pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			pd.setCancelable(true);
			pd.setMessage("正在加载列表...");
			pd.show();
			ProgressDialogHolder.getInstance().addActivity(pd);
			handler = new GetTodoListAllHandler();
			executorService.submit(new GeTodoListAllThread());// 耗时操作,开启一个新线程获取数据
		}
	}

	// 初始化所有的UI组件
	public void setUpView() {
		top_title = (TextView) findViewById(R.id.top_title);
		listView = (PullToRefreshListView) findViewById(R.id.pullToRefreshListView);
		taskList = new ArrayList<WFTaskModel>();
		executorService = Executors.newFixedThreadPool(THREADPOOL_SIZE);
		title_bt_left = (Button) findViewById(R.id.title_bt_left);
		title_bt_right = (Button) findViewById(R.id.title_bt_right);
		wf_list_search_area = (LinearLayout) findViewById(R.id.wf_list_search_area);
		todolistall_main_linearLayout = (LinearLayout) findViewById(R.id.todolistall_main_linearLayout);
		wf_list_search_area.setVisibility(View.GONE);
		setBackground();
	}

	// 设置背景图
	private void setBackground() {
		// 设置背景图
		SetBackGround.getInstance().setTheme(this, top_title, "top_title", R.drawable.ui_title_bg_blue, null);
		SetBackGround.getInstance().setTheme(this, title_bt_left, "ui_backbtn", R.drawable.ui_title_back_btn_blue, null);
		SetBackGround.getInstance().setTheme(this, title_bt_right, "ui_login_outbtn", R.drawable.ui_title_loginout_btn_blue, null);
	}

	// 恢复页面后刷新列表
	protected void onRestart() {
		super.onRestart();
		if (isListViewFresh) {
			pd = new ProgressDialog(TodoListAllActivity.this);
			pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			pd.setCancelable(true);
			pd.setMessage("正在加载列表...");
			pd.show();
			ProgressDialogHolder.getInstance().addActivity(pd);
			executorService.submit(new GeTodoListAllThread());
		}
		setBackground();
	}

	// 定义组件监听器
	public void setUpListeners() {
		listView.setOnScrollListener(new OnScrollListener() {
			@Override
			public void onScrollStateChanged(AbsListView view, int scrollState) {
				if (scrollState == OnScrollListener.SCROLL_STATE_IDLE) {
					cacheListViewPosition = listView.getFirstVisiblePosition();
					SimpleDateFormat sdf = new SimpleDateFormat(" yyyy/MM/dd HH:mm");
					listView.setRefreshTime(sdf.format(new Date()));
				}
			}

			@Override
			public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
			}
		});

		listView.setXListViewListener(new IXListViewListener() {
			public void onRefresh() {
				listView.stopRefresh();
				listView.stopLoadMore();
				SimpleDateFormat sdf = new SimpleDateFormat(" yyyy/MM/dd HH:mm");
				listView.setRefreshTime(sdf.format(new Date()));
				executorService.submit(new GeTodoListAllThread());
			}

			public void onLoadMore() {
				executorService.submit(new GeTodoListAllThread());
			}
		});

		listView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long id) {
				Intent intent = new Intent();
				if (taskList != null) {
					if (id >= 0 && id <= taskList.size()) {
						WFTaskModel wf = taskList.get(Integer.parseInt(id + ""));
						if (wf.getIsRead().equals("true")) {
							isListViewFresh = false;
						} else {
							isListViewFresh = true;
						}
						intent.putExtra("flowTitle", wf.getTaskTitle());
						intent.putExtra("taskOwner", wf.getOwner());
						intent.putExtra("targetUser", wf.getTagetUser());
						intent.putExtra("taskDate", wf.getTaskdate());
						if (wf.getInstanceId() != null)
							intent.putExtra("instanceId", wf.getInstanceId());
						intent.putExtra("taskId", wf.getTaskId());
						String taskStatus = wf.getStatus();
						intent.putExtra("fromActivity", "TodoListAllActivity");
						if (taskStatus.equals("1")) {// 待办
							intent.setClass(TodoListAllActivity.this, WfPageActivity.class);
						} else if (taskStatus.equals("-1")) {// 退回
							intent.setClass(TodoListAllActivity.this, WfPageActivity.class);
						} else if (taskStatus.equals("2")) {// 传阅
							intent.setClass(TodoListAllActivity.this, WfNoticeActivity.class);
						} else if (taskStatus.equals("9")) {// 通知
							intent.setClass(TodoListAllActivity.this, WfNoticeActivity.class);
						} else if (taskStatus.equals("11")) {// 加签
							intent.setClass(TodoListAllActivity.this, WfAddsignActivity.class);
						}
						startActivity(intent);
					}
				}
			}
		});
		// 后退按钮
		title_bt_left.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				Intent intent = getIntent();
				intent.setClass(TodoListAllActivity.this, MainActivity.class);
				startActivity(intent);
			}
		});
		// 退出按钮
		title_bt_right.setVisibility(View.VISIBLE);
		title_bt_right.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				MainService.promptExit(TodoListAllActivity.this);
			}
		});
	}

	// 数据处理类
	class GetTodoListAllHandler extends Handler {
		@Override
		public void handleMessage(Message msg) {
			// 关闭进度条
			ProgressDialogHolder.getInstance().exit();
			// 处理反馈结果
			if (todolist_json == null) {
				Toast.makeText(TodoListAllActivity.this, "加载超时,请稍后再试", Toast.LENGTH_SHORT).show();
			} else if (todolist_json.equals("")) {
				Toast.makeText(TodoListAllActivity.this, "没有更多数据", Toast.LENGTH_SHORT).show();
			} else if (todolist_json.equals("TIMEOUT")) {
				Toast.makeText(TodoListAllActivity.this, "加载超时,请稍后再试", Toast.LENGTH_SHORT).show();
			} else if (todolist_json.equals("SESSION_TIMEOUT")) {
				HttpResponseUtil.getInstance().reLogin(TodoListAllActivity.this);
			} else if (todolist_json.equals("SESSION_ERROR")) {
				Toast.makeText(TodoListAllActivity.this, "加载超时,请稍后再试", Toast.LENGTH_SHORT).show();
			} else if (todolist_json.equals("NETWORK_ERROR")) {
				Toast.makeText(TodoListAllActivity.this, "网络连接异常，请稍后再试", Toast.LENGTH_SHORT).show();
			} else {
				taskList_temp = JsonUtil.parseWfJsonMulti(todolist_json.substring(1, todolist_json.length() - 1));
				boolean changeFlg = false;
				if (taskList.size() > 0) {
					taskList.clear();
					taskList.addAll(taskList_temp);
					changeFlg = true;
				} else {
					taskList = taskList_temp;
				}
				adapter = new WfAdapter(TodoListAllActivity.this, taskList);
				if (changeFlg) {
					adapter.notifyDataSetChanged();
					listView.setAdapter(adapter);
				} else {
					listView.setAdapter(adapter);
				}
				// 没有待办任务时候给出提示
				if (taskList.size() == 0) {
					listView.setVisibility(View.GONE);
					TextView tip = new TextView(TodoListAllActivity.this);
					tip.setText("您当前没有待办流程");
					tip.setTextColor(Color.parseColor("#3F8AC5"));
					tip.setTextSize(20);
					tip.setPadding(0, 100, 0, 0);
					tip.setGravity(Gravity.CENTER);
					todolistall_main_linearLayout.addView(tip);
				}
				// 加载完毕后设定当前的待办个数
				MainService.setCurWfTodoNum(String.valueOf(taskList.size()));
				KoaNotiManager.cancel(R.string.string_wf_notifyNewWf);
				if (isListViewFresh) {
					listView.setSelection(cacheListViewPosition + 1);
				}
				Toast.makeText(TodoListAllActivity.this, "加载完毕", Toast.LENGTH_SHORT).show();
				// 刷新完毕
				try {
					Thread.sleep(500);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				listView.stopRefresh();
				listView.stopLoadMore();
			}
		}
	}

	class GeTodoListAllThread implements Runnable {
		@Override
		public void run() {
			refreshList(1, 1000);
			Message msg = handler.obtainMessage();// 通知线程来处理范围的数据
			handler.sendMessage(msg);
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		menu.add(1, 1, 0, "刷新").setIcon(R.drawable.ui_menu_refresh);
		menu.add(1, 2, 1, "退出").setIcon(R.drawable.ui_menu_loginout);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		switch (item.getItemId()) {
		case 1:// 设置
			pd = new ProgressDialog(TodoListAllActivity.this);
			pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			pd.setCancelable(true);
			pd.setMessage("正在加载列表...");
			pd.show();
			ProgressDialogHolder.getInstance().addActivity(pd);
			executorService.submit(new GeTodoListAllThread());
			break;
		case 2:// 退出
			MainService.promptExit(this);
			break;
		}
		return super.onMenuItemSelected(featureId, item);
	}

	// 用来刷新列表的方法
	public void refreshList(int pageNum, int pageSize) {
		String sissionId = User.getInstance().getUserContext(TodoListAllActivity.this).getSessionId();// 获取sissionId
		if (sissionId != null || !"".equals(sissionId)) {
			String todolistType = "1";
			HashMap<String, Object> param = new HashMap<String, Object>();
			param.put("type", todolistType);
			param.put("pageNum", pageNum);
			param.put("pageSize", pageSize);
			String socketCmd = "KOA_Mobile_WFTodolistJson";
			todolist_json = HttpResponseUtil.getInstance().getGetHttpInfo(socketCmd, sissionId, param, TodoListAllActivity.this);
		} else {
			todolist_json = "SESSION_ERROR";
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			Intent intent = new Intent();
			intent.setClass(TodoListAllActivity.this, MainActivity.class);
			startActivity(intent);
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}
}