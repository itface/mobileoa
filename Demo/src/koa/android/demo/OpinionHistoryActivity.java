package koa.android.demo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import koa.android.demo.model.User;
import koa.android.demo.model.WFOpinionLogModel;
import koa.android.logic.ActivityHoder;
import koa.android.logic.WfOpinionAdapter;
import koa.android.tools.HttpResponseUtil;
import koa.android.tools.JsonUtil;
import koa.android.tools.LoginTimeOut;
import koa.android.tools.SetBackGround;
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
 * ����������¼�б���ʾ��
 * 
 * @author chenM
 * 
 */
public class OpinionHistoryActivity extends ListActivity implements OnGestureListener, OnTouchListener {
	private ListView listView;
	private TextView top_title;
	private Button title_bt_left;
	private Handler handler;
	private Intent intent;
	private WfOpinionAdapter adapter;
	private String opinionListJson;
	private ExecutorService executorService;
	private static int THREADPOOL_SIZE = 4;// �̳߳صĴ�С
	private Boolean jsonParseFlag = true;// JSONת���Ƿ�ɹ���־λ
	private List<WFOpinionLogModel> opinionList_temp;
	private List<WFOpinionLogModel> opinionList;
	private LinearLayout wf_opinion_process_bar;
	// ===============����ʶ��=========================
	private GestureDetector mGestureDetector;
	private static final int FLING_MIN_DISTANCE = 150;
	private static final int FLING_MIN_VELOCITY = 200;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		LoginTimeOut LoginTimeOut = new LoginTimeOut();
		boolean flag = LoginTimeOut.timeOut(OpinionHistoryActivity.this);
		if (!flag) {
			setContentView(R.layout.wfopinionlog_list);
			mGestureDetector = new GestureDetector(this);
			setUpView();
			setUpListener();
			// ��Activity�ŵ�Activity��
			ActivityHoder.getInstance().addActivity(this);
			intent = getIntent();
			handler = new GetOpinionHistoryHandler();
			init();
			wf_opinion_process_bar.setVisibility(View.VISIBLE);
			executorService.submit(new GetOpinionHistoryThread());// ��ʱ����,����һ�����̻߳�ȡ����
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
		top_title.setText("����������¼");
		executorService = Executors.newFixedThreadPool(THREADPOOL_SIZE);
	}

	public void setUpView() {
		top_title = (TextView) findViewById(R.id.top_title);
		listView = getListView();
		listView.setOnTouchListener(this);
		listView.setLongClickable(true);
		title_bt_left = (Button) findViewById(R.id.title_bt_left);
		wf_opinion_process_bar = (LinearLayout) findViewById(R.id.wf_opinion_process_bar);
		setBackground();
	}

	class GetOpinionHistoryHandler extends Handler {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			wf_opinion_process_bar.setVisibility(View.GONE);
			// ���������
			if (opinionListJson == null) {
				Toast.makeText(OpinionHistoryActivity.this, "���س�ʱ,���Ժ�����", Toast.LENGTH_SHORT).show();
			} else if (opinionListJson.equals("[]")) {
				listView.setVisibility(View.GONE);
				Toast.makeText(OpinionHistoryActivity.this, "δ��������������¼", Toast.LENGTH_SHORT).show();
				new Handler().postDelayed(new Runnable() {
					@Override
					public void run() {
						finish();
					}
				}, 3000);
			} else if (opinionListJson.equals("TIMEOUT")) {
				Toast.makeText(OpinionHistoryActivity.this, "���س�ʱ,���Ժ�����", Toast.LENGTH_SHORT).show();
			} else if (opinionListJson.equals("SESSION_ERROR")) {
				Toast.makeText(OpinionHistoryActivity.this, "���س�ʱ,���Ժ�����", Toast.LENGTH_SHORT).show();
			} else if (opinionListJson.equals("SESSION_TIMEOUT")) {
				HttpResponseUtil.getInstance().reLogin(OpinionHistoryActivity.this);
			} else if (opinionListJson.equals("NETWORK_ERROR")) {
				Toast.makeText(OpinionHistoryActivity.this, "���������쳣�����Ժ�����", Toast.LENGTH_SHORT).show();
			} else {
				opinionList_temp = JsonUtil.parseWFOpinionLog(opinionListJson);
				if (jsonParseFlag == false) {
					Toast.makeText(OpinionHistoryActivity.this, "���ݽ����쳣", Toast.LENGTH_SHORT).show();
				} else {
					if (opinionList_temp.size() == 0) {
						listView.setVisibility(View.GONE);
						Toast.makeText(OpinionHistoryActivity.this, "δ��������������¼", Toast.LENGTH_SHORT).show();
						new Handler().postDelayed(new Runnable() {
							@Override
							public void run() {
								finish();
							}
						}, 3000);
					} else {
						boolean changeFlg = false;
						if (opinionList == null) {
							opinionList.clear();
							opinionList.addAll(opinionList_temp);
							changeFlg = true;
						} else {
							opinionList = opinionList_temp;
						}
						adapter = new WfOpinionAdapter(OpinionHistoryActivity.this, opinionList);
						if (changeFlg) {
							adapter.notifyDataSetChanged();
						} else {
							listView.setAdapter(adapter);
						}
					}
				}
			}
		}

	}

	class GetOpinionHistoryThread implements Runnable {
		@Override
		public void run() {
			refreshList();
			Message msg = handler.obtainMessage();// ֪ͨ�߳�������Χ������
			handler.sendMessage(msg);
		}
	}

	// ˢ���б�ķ���
	public void refreshList() {
		String sissionId = User.getInstance().getUserContext(OpinionHistoryActivity.this).getSessionId();// ��ȡsessionId
		if (sissionId != null || !"".equals(sissionId)) {
			HashMap<String, Object> param = new HashMap<String, Object>();
			String instanceId = intent.getStringExtra("instanceId"); // ���ʵ��ID
			param.put("id", instanceId);
			String socketCmd = "KOA_Mobile_WF_Send_Log";
			opinionListJson = HttpResponseUtil.getInstance().getGetHttpInfo(socketCmd, sissionId, param, OpinionHistoryActivity.this);
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
