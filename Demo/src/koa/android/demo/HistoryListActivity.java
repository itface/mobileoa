package koa.android.demo;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import koa.android.demo.model.User;
import koa.android.demo.model.WFTaskModel;
import koa.android.logic.ActivityHoder;
import koa.android.logic.MainService;
import koa.android.logic.ProgressDialogHolder;
import koa.android.logic.WfCancelAdapter;
import koa.android.tools.Base64Encoder;
import koa.android.tools.HttpResponseUtil;
import koa.android.tools.JsonUtil;
import koa.android.tools.LoginTimeOut;
import koa.android.tools.SetBackGround;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.InputType;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * 已办列表
 * 
 * @author chenM
 * 
 */
public class HistoryListActivity extends ListActivity {
	private List<WFTaskModel> taskList;
	private List<WFTaskModel> taskList_page;// 分页显示的LIST
	private ProgressDialog pd;
	private TextView top_title;
	private Button title_bt_left;
	private Button title_bt_right;
	private ListView listView;
	private Handler handler;
	private WfCancelAdapter adapter;
	private LinearLayout list_footer;
	private TextView foot_msg;
	private LinearLayout foot_loading;
	private ExecutorService executorService;
	private String todolist_json;
	private static int PAGE_SIZE = 20;// 每页显示的条数
	private int PAGE_NUM = 0;// 当前页面
	private static int THREADPOOL_SIZE = 4;// 线程池的大小
	// ============下面是流程搜索用到的变量===================================
	private int listStatus = LOAD_STATUS;
	private final static int LOAD_STATUS = 0;
	private final static int SEARCH_STATUS = 1;
	private EditText searchText;
	private Drawable wfSearchClear; // 搜索文本框清除文本内容图标
	private Drawable wfSearchGo; // 搜索文本框清除文本内容图标
	private int searchStatus = NOMAL_STATUS;
	public static final int NOMAL_STATUS = 0;
	public static final int GO_STATUS = 1;
	public static final int CLEAR_STATUS = 2;
	int cacheInputType = 0;// 备份输入法
	private List<WFTaskModel> cacheTaskList;// 备份流程列表
	private int catchPageNum;// 备份当前页面

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		LoginTimeOut LoginTimeOut = new LoginTimeOut();
		boolean flag = LoginTimeOut.timeOut(HistoryListActivity.this);
		if (!flag) {
			setContentView(R.layout.todolist);
			setUpView();
			setUpListeners();
			// 将Activity放到Activity中
			ActivityHoder.getInstance().addActivity(this);
			String userName = User.getInstance().getUserContext(HistoryListActivity.this).getUserName();
			top_title.setText(userName + " - 已办");
			pd = new ProgressDialog(HistoryListActivity.this);
			pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			pd.setCancelable(true);
			pd.setMessage("正在加载列表...");
			pd.show();
			ProgressDialogHolder.getInstance().addActivity(pd);
			handler = new GetTodoListAllHandler();
			executorService.submit(new GeTodoListAllThread());// 耗时操作,开启一个新线程获取数据
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
		SetBackGround.getInstance().setTheme(this, title_bt_right, "ui_login_outbtn", R.drawable.ui_title_loginout_btn_blue, null);
	}

	// 初始化所有的UI组件
	public void setUpView() {
		list_footer = (LinearLayout) LayoutInflater.from(HistoryListActivity.this).inflate(R.layout.list_footer, null);
		foot_msg = (TextView) list_footer.findViewById(R.id.foot_msg);
		foot_loading = (LinearLayout) list_footer.findViewById(R.id.foot_loading);
		top_title = (TextView) findViewById(R.id.top_title);
		title_bt_left = (Button) findViewById(R.id.title_bt_left);
		title_bt_right = (Button) findViewById(R.id.title_bt_right);
		listView = getListView();
		listView.addFooterView(list_footer);
		taskList = new ArrayList<WFTaskModel>();
		taskList_page = new ArrayList<WFTaskModel>();
		cacheTaskList = new ArrayList<WFTaskModel>();
		executorService = Executors.newFixedThreadPool(THREADPOOL_SIZE);
		final Resources res = getResources();
		searchText = (EditText) findViewById(R.id.wfSearchText);
		wfSearchClear = res.getDrawable(R.drawable.wf_search_clear);
		wfSearchGo = res.getDrawable(R.drawable.wf_search_go);
		setBackground();
	}

	// 定义组件监听器
	public void setUpListeners() {
		// 点击回退
		title_bt_left.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				Intent intent = getIntent();
				intent.setClass(HistoryListActivity.this, MainActivity.class);
				HistoryListActivity.this.startActivity(intent);
			}
		});
		// 退出按钮
		title_bt_right.setVisibility(View.VISIBLE);
		title_bt_right.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				MainService.promptExit(HistoryListActivity.this);
			}
		});
		// 点击更多
		foot_msg.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				if (listStatus == LOAD_STATUS) {
					executorService.submit(new GeTodoListAllThread());
				} else if (listStatus == SEARCH_STATUS) {
					executorService.submit(new GetSearchResultThread());
				}
				foot_msg.setVisibility(View.GONE);// 隐藏更多提示的TextView
				foot_loading.setVisibility(View.VISIBLE);// 显示最下方的进度条
			}
		});
		// 监听回车搜索事件
		searchText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
			@Override
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
				InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
				if (searchStatus == NOMAL_STATUS) {
					cacheInputType = searchText.getInputType();
				}
				if (actionId == EditorInfo.IME_ACTION_UNSPECIFIED || actionId == EditorInfo.IME_ACTION_NONE || actionId == EditorInfo.IME_ACTION_DONE || actionId == EditorInfo.IME_ACTION_NEXT || actionId == EditorInfo.IME_ACTION_GO || actionId == EditorInfo.IME_ACTION_SEARCH) {
					String serachWords = searchText.getText().toString();
					// 搜索关键词不为空不搜索
					if (null != serachWords && !"".equals(serachWords)) {
						clearListAndLoadSearch();
						searchText.setCompoundDrawablesWithIntrinsicBounds(null, null, wfSearchClear, null);
						// 关闭输入法
						imm.hideSoftInputFromWindow(searchText.getWindowToken(), 0);
						searchText.setInputType(InputType.TYPE_NULL);
						searchStatus = CLEAR_STATUS;
					} else {
						Toast.makeText(HistoryListActivity.this, "请输入搜索关键词", Toast.LENGTH_SHORT).show();
					}
				}
				return true;
			}
		});
		searchText.addTextChangedListener(new TextWatcher() {
			// 缓存上一次文本框内是否为空
			private boolean isnull = true;

			@Override
			public void afterTextChanged(Editable s) {
				if (TextUtils.isEmpty(s)) {
					if (!isnull) {
						searchText.setCompoundDrawablesWithIntrinsicBounds(null, null, null, null);
						isnull = true;
					}
				} else {
					if (isnull) {
						searchText.setCompoundDrawablesWithIntrinsicBounds(null, null, wfSearchGo, null);
						isnull = false;
						searchStatus = GO_STATUS;
					}
				}
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {
			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {

			}
		});
		searchText.setOnTouchListener(new OnTouchListener() {

			@Override
			public boolean onTouch(View v, MotionEvent event) {
				InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
				if (searchStatus == NOMAL_STATUS) {
					cacheInputType = searchText.getInputType();
				}
				switch (event.getAction()) {
				case MotionEvent.ACTION_UP:
					if (searchStatus == GO_STATUS) {
						int curX = (int) event.getX();
						if (curX > v.getWidth() - 50 && !TextUtils.isEmpty(searchText.getText())) {
							clearListAndLoadSearch();
							searchText.setCompoundDrawablesWithIntrinsicBounds(null, null, wfSearchClear, null);
							// 关闭输入法
							imm.hideSoftInputFromWindow(searchText.getWindowToken(), 0);
							searchText.setInputType(InputType.TYPE_NULL);
							searchStatus = CLEAR_STATUS;
						}
					} else if (searchStatus == CLEAR_STATUS) {
						searchText.onTouchEvent(event);
						// 还原输入法
						searchText.setInputType(cacheInputType);
						imm.hideSoftInputFromWindow(searchText.getWindowToken(), 0);
						int curX = (int) event.getX();
						if (curX > v.getWidth() - 50 && !TextUtils.isEmpty(searchText.getText())) {
							searchText.setText("");
							clearSearchAndConsumeList();
							searchStatus = NOMAL_STATUS;
							return true;// consume touch even
						}
					}
					break;
				}
				return false;
			}
		});
	}

	// 清空搜索还原列表
	private void clearSearchAndConsumeList() {
		listStatus = LOAD_STATUS;
		// 还原原有列表和分页
		taskList.clear();
		taskList.addAll(cacheTaskList);
		PAGE_NUM = catchPageNum;
		adapter = new WfCancelAdapter(HistoryListActivity.this, taskList, "HistoryListActivity");
		adapter.notifyDataSetChanged();
		setListAdapter(adapter);
	}

	// 清空源列表加载搜索列表
	private void clearListAndLoadSearch() {
		// 备份页数和原有列表
		catchPageNum = PAGE_NUM;
		cacheTaskList.addAll(taskList);
		// 变为搜索状态
		listStatus = SEARCH_STATUS;
		// 清空列表
		taskList.clear();
		PAGE_NUM = 0;
		adapter = new WfCancelAdapter(HistoryListActivity.this, taskList, "HistoryListActivity");
		adapter.notifyDataSetChanged();
		setListAdapter(adapter);
		pd = new ProgressDialog(HistoryListActivity.this);
		pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
		pd.setCancelable(true);
		pd.setMessage("正在加载列表...");
		pd.show();
		ProgressDialogHolder.getInstance().addActivity(pd);
		executorService.submit(new GetSearchResultThread());// 耗时操作,开启一个新线程获取数据
	}

	class GetSearchResultThread implements Runnable {
		@Override
		public void run() {
			PAGE_NUM++;
			refreshSearchResult(PAGE_NUM, PAGE_SIZE);
			Message msg = handler.obtainMessage();// 通知线程来处理范围的数据
			handler.sendMessage(msg);
		}
	}

	// 用来刷新列表的方法
	public void refreshSearchResult(int pageNum, int pageSize) {
		String keyword = searchText.getText().toString();
		String sissionId = User.getInstance().getUserContext(HistoryListActivity.this).getSessionId();// 获取sissionId
		if (sissionId != null || !"".equals(sissionId)) {
			HashMap<String, Object> param = new HashMap<String, Object>();
			try {
				param.put("keyword", Base64Encoder.encode(keyword.trim().getBytes("UTF-8")));
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			}
			param.put("type", "4");
			param.put("pageNum", pageNum);
			param.put("pageSize", pageSize);
			String socketCmd = "KOA_Mobile_WF_Select_TodoList";
			todolist_json = HttpResponseUtil.getInstance().getGetHttpInfo(socketCmd, sissionId, param, HistoryListActivity.this);
		} else {
			todolist_json = "SESSION_ERROR";
		}
	}

	class GetTodoListAllHandler extends Handler {
		@Override
		public void handleMessage(Message msg) {
			// 关闭进度条
			ProgressDialogHolder.getInstance().exit();
			// 处理反馈结果
			if (todolist_json == null || todolist_json.equals("")) {
				Toast.makeText(HistoryListActivity.this, "加载超时,请稍后再试", Toast.LENGTH_SHORT).show();
			} else if (todolist_json.equals("TIMEOUT")) {
				Toast.makeText(HistoryListActivity.this, "加载超时,请稍后再试", Toast.LENGTH_SHORT).show();
			} else if (todolist_json.equals("SESSION_TIMEOUT")) {
				HttpResponseUtil.getInstance().reLogin(HistoryListActivity.this);
			} else if (todolist_json.equals("NETWORK_ERROR")) {
				Toast.makeText(HistoryListActivity.this, "网络连接异常，请稍后再试", Toast.LENGTH_SHORT).show();
			} else if (todolist_json.equals("SESSION_ERROR")) {
				Toast.makeText(HistoryListActivity.this, "加载超时,请稍后再试", Toast.LENGTH_SHORT).show();
			} else {
				taskList_page = JsonUtil.parseWfJsonMulti(todolist_json.substring(1, todolist_json.length() - 1));
				if (PAGE_NUM > 1) {
					taskList.addAll(taskList_page);
				} else if (PAGE_NUM == 1) {
					taskList = taskList_page;
				}
				adapter = new WfCancelAdapter(HistoryListActivity.this, taskList, "HistoryListActivity");
				if (PAGE_NUM > 1) {
					if (taskList_page.size() > 0) {
						adapter.notifyDataSetChanged();
						setListAdapter(adapter);
						listView.setSelection((PAGE_NUM - 1) * PAGE_SIZE + 1);// 设置最新获取一页数据成功后显示数据的起始数据
					} else {
						Toast.makeText(HistoryListActivity.this, "没有更多数据", Toast.LENGTH_SHORT).show();
					}
				} else if (PAGE_NUM == 1) {
					listView.setAdapter(adapter);
				}
				foot_loading.setVisibility(View.GONE);// 隐藏下方的进度条
				foot_msg.setVisibility(View.VISIBLE);// 显示出更多提示TextView
			}
		}
	}

	class GeTodoListAllThread implements Runnable {
		@Override
		public void run() {
			PAGE_NUM++;
			refreshList(PAGE_NUM, PAGE_SIZE);
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
			pd = new ProgressDialog(HistoryListActivity.this);
			pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
			pd.setCancelable(true);
			pd.setMessage("正在加载列表...");
			pd.show();
			ProgressDialogHolder.getInstance().addActivity(pd);
			executorService.submit(new GeTodoListAllThread());
			break;
		case 2:// 退出
			MainService.exitApp(this);
			break;
		}
		return super.onMenuItemSelected(featureId, item);
	}

	// 用来刷新列表的方法
	public void refreshList(int pageNum, int pageSize) {
		String sissionId = User.getInstance().getUserContext(HistoryListActivity.this).getSessionId();// 获取sissionId
		if (sissionId != null || !"".equals(sissionId)) {
			String todolistType = "4";
			HashMap<String, Object> param = new HashMap<String, Object>();
			param.put("type", todolistType);
			param.put("pageNum", pageNum);
			param.put("pageSize", pageSize);
			String socketCmd = "KOA_Mobile_WFTodolistJson";
			todolist_json = HttpResponseUtil.getInstance().getGetHttpInfo(socketCmd, sissionId, param, HistoryListActivity.this);
		} else {
			todolist_json = "SESSION_ERROR";
		}
	}

	/**
	 * 点击Item事件
	 */
	protected void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		Intent intent = new Intent();
		if (taskList != null) {
			if (id >= 0 && id <= taskList.size()) {
				WFTaskModel wf = taskList.get(Integer.parseInt(id + ""));
				intent.putExtra("flowTitle", wf.getTaskTitle());
				intent.putExtra("taskOwner", wf.getOwner());
				intent.putExtra("targetUser", wf.getTagetUser());
				intent.putExtra("taskDate", wf.getTaskdate());
				if (wf.getInstanceId() != null)
					intent.putExtra("instanceId", wf.getInstanceId());
				intent.putExtra("taskId", wf.getTaskId());
				intent.putExtra("isCancel", wf.getIsCancel());
				intent.putExtra("fromActivity", "HistoryListActivity");// 放入来源的Activity
				intent.setClass(HistoryListActivity.this, WfReadActivity.class);
				startActivity(intent);
			}
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			Intent intent = new Intent();
			intent.setClass(HistoryListActivity.this, MainActivity.class);
			startActivity(intent);
			return true;
		}
		if (keyCode == KeyEvent.KEYCODE_SEARCH) {
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}

}