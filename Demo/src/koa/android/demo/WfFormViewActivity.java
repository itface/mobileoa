package koa.android.demo;

import koa.android.logic.ActivityHoder;
import koa.android.tools.LoginTimeOut;
import koa.android.tools.SetBackGround;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.TextView;

public class WfFormViewActivity extends Activity {
	private Button title_bt_left;// ���˰�ť
	private TextView top_title;// �����ı���
	private Intent intent;
	private String formURL;
	private WebView formWebView;
	private ProgressDialog pd;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		LoginTimeOut LoginTimeOut = new LoginTimeOut();
		boolean flag = LoginTimeOut.timeOut(WfFormViewActivity.this);
		if (!flag) {
			setContentView(R.layout.wf_formview);
			intent = getIntent();
			setUpView();
			setUpListeners();
			init();
			// ��Activity�ŵ�Activity��
			ActivityHoder.getInstance().addActivity(this);
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

	private void init() {
		top_title.setText("ԭ��");
		formURL = intent.getStringExtra("formURL");
		WfFormViewActivity.this.linkPage(formURL);
	}

	private void setUpView() {
		title_bt_left = (Button) findViewById(R.id.title_bt_left);
		top_title = (TextView) findViewById(R.id.top_title);
		formWebView = (WebView) findViewById(R.id.wf_formview);
		formWebView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
		formWebView.getSettings().setSupportZoom(true);
		formWebView.getSettings().setBuiltInZoomControls(true);
		setBackground();
	}

	private void setUpListeners() {
		// ���˰�ť
		title_bt_left.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				finish();
			}
		});
	}

	// ����ҳ��ķ���
	private void linkPage(String uri) {
		WebSettings ws = formWebView.getSettings();
		ws.setJavaScriptEnabled(true);
		formWebView.requestFocus();
		/* WebView������ҳ */
		formWebView.loadUrl(uri);

		formWebView.setWebViewClient(new WebViewClient() {
			@Override
			public void onPageStarted(WebView view, String url, Bitmap favicon) {
				/* ��ʼ������ҳʱ����ProgressDialog */
				pd = new ProgressDialog(WfFormViewActivity.this);
				pd.setMessage("���ڼ���");
				pd.setIndeterminate(true);
				pd.setCancelable(true);
				pd.show();
				super.onPageStarted(view, url, favicon);
			}

			public void onPageFinished(WebView view, String url) {
				try {
					/* ��ҳ�������ʱ�ر�ProgressDialog */
					pd.dismiss();
				} catch (Exception e) {
					e.printStackTrace();
					try {
						/* ����Exceptionʱ�ر�ProgressDialog */
						pd.dismiss();
					} catch (Exception e2) {
						e2.printStackTrace();
					}
				}
			}
		});
	}

}