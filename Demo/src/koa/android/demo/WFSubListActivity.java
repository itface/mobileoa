package koa.android.demo;

import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import koa.android.demo.model.User;
import koa.android.logic.ActivityHoder;
import koa.android.logic.WfSubListAdapter;
import koa.android.tools.HttpResponseUtil;
import koa.android.tools.JsonUtil;
import koa.android.tools.LoginTimeOut;
import koa.android.tools.SetBackGround;

import org.json.JSONObject;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.GestureDetector;
import android.view.GestureDetector.OnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

/**
 * �ӱ�������ʾ��
 * 
 * @author chenM
 * 
 */
public class WFSubListActivity extends ListActivity implements OnGestureListener, OnTouchListener {
	private ListView listView;
	private TextView top_title;
	private Button title_bt_left;
	private LinearLayout wf_opinion_process_bar;
	private Handler handler;
	private WfSubListAdapter adapter;
	private String opinionListJson;
	private ExecutorService executorService;
	private static int THREADPOOL_SIZE = 4;// �̳߳صĴ�С
	private Intent intent;
	// ===============����ʶ��=========================
	private GestureDetector mGestureDetector;
	private static final int FLING_MIN_DISTANCE = 150;
	private static final int FLING_MIN_VELOCITY = 200;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		LoginTimeOut LoginTimeOut = new LoginTimeOut();
		boolean flag = LoginTimeOut.timeOut(WFSubListActivity.this);
		if (!flag) {
			mGestureDetector = new GestureDetector(this);
			intent = getIntent();
			setContentView(R.layout.wfopinionlog_list);
			setUpView();
			setUpListener();
			// ��Activity�ŵ�Activity��
			ActivityHoder.getInstance().addActivity(this);
			handler = new GetSubTableHandler();
			init();
			wf_opinion_process_bar.setVisibility(View.VISIBLE);
			executorService.submit(new GetSubTableThread());// ��ʱ����,����һ�����̻߳�ȡ����
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

	public void setUpListener() {
		// ���˰�ť
		title_bt_left.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				finish();
			}
		});
	}

	public void init() {
		executorService = Executors.newFixedThreadPool(THREADPOOL_SIZE);
		String subTableTitle = intent.getStringExtra("subTableTitle");
		if (subTableTitle != null) {
			if (subTableTitle.length() > 8) {
				top_title.setText(subTableTitle.substring(0, 8) + "...");
			} else {
				top_title.setText(subTableTitle);
			}
		}
	}

	public void setUpView() {
		top_title = (TextView) findViewById(R.id.top_title);
		listView = getListView();
		listView.setOnTouchListener(this);
		listView.setLongClickable(true);
		title_bt_left = (Button) findViewById(R.id.title_bt_left);
		setBackground();
		wf_opinion_process_bar = (LinearLayout) findViewById(R.id.wf_opinion_process_bar);
	}

	class GetSubTableHandler extends Handler {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			wf_opinion_process_bar.setVisibility(View.GONE);
			if (opinionListJson == null) {
				Toast.makeText(WFSubListActivity.this, "���ݼ����쳣", Toast.LENGTH_SHORT).show();
			} else if ("".equals(opinionListJson) || "[]".equals(opinionListJson)) {
				listView.setVisibility(View.GONE);
				Toast.makeText(WFSubListActivity.this, "�˱�����Ϊ��", Toast.LENGTH_SHORT).show();
				new Handler().postDelayed(new Runnable() {
					@Override
					public void run() {
						finish();
					}
				}, 3000);
			} else if (opinionListJson.equals("TIMEOUT")) {
				Toast.makeText(WFSubListActivity.this, "���س�ʱ,���Ժ�����", Toast.LENGTH_SHORT).show();
			} else if (opinionListJson.equals("SESSION_TIMEOUT")) {
				HttpResponseUtil.getInstance().reLogin(WFSubListActivity.this);
			} else if (opinionListJson.equals("SESSION_ERROR")) {
				Toast.makeText(WFSubListActivity.this, "���س�ʱ,���Ժ�����", Toast.LENGTH_SHORT).show();
			} else if (opinionListJson.equals("NETWORK_ERROR")) {
				Toast.makeText(WFSubListActivity.this, "���������쳣�����Ժ�����", Toast.LENGTH_SHORT).show();
			} else {
				List<List<JSONObject>> sublist = JsonUtil.parseWfSubListMulti(opinionListJson.substring(1, opinionListJson.length() - 1), "list");
				if (sublist.size() == 0) {
					listView.setVisibility(View.GONE);
					Toast.makeText(WFSubListActivity.this, "�˱�����Ϊ��", Toast.LENGTH_SHORT).show();
					new Handler().postDelayed(new Runnable() {
						@Override
						public void run() {
							finish();
						}
					}, 3000);
				}
				adapter = new WfSubListAdapter(WFSubListActivity.this, sublist);
				listView.setAdapter(adapter);
			}
		}

	}

	class GetSubTableThread implements Runnable {
		@Override
		public void run() {
			refreshList();
			Message msg = handler.obtainMessage();// ֪ͨ�߳�������Χ������
			handler.sendMessage(msg);
		}
	}

	// ˢ���б�ķ���
	public void refreshList() {
		String sissionId = User.getInstance().getUserContext(WFSubListActivity.this).getSessionId();// ��ȡsessionId
		if (sissionId != null || !"".equals(sissionId)) {
			String instanceId = intent.getStringExtra("instanceId");
			String taskId = intent.getStringExtra("taskId");
			String subTableName = intent.getStringExtra("subTableName");
			String fromActivity = intent.getStringExtra("fromActivity");
			HashMap<String, Object> hashparam = new HashMap<String, Object>();
			hashparam.put("id", instanceId);
			hashparam.put("taskId", taskId);
			hashparam.put("tableName", subTableName);
			if (fromActivity.equals("HistoryListActivity")) {
				hashparam.put("type", "taskLog");
			}
			String socketCmd = "KOA_Mobile_Son_Form_Show";
			opinionListJson = HttpResponseUtil.getInstance().getGetHttpInfo(socketCmd, sissionId, hashparam, WFSubListActivity.this);
		} else {
			opinionListJson = "SESSION_ERROR";
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
