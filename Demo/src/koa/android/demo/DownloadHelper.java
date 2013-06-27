package koa.android.demo;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;

import koa.android.demo.model.User;
import koa.android.logic.ProgressDialogHolder;
import koa.android.tools.Base64Encoder;
import koa.android.tools.HttpResponseUtil;
import koa.android.tools.OpenDownloadFileUtil;
import koa.android.tools.SetBackGround;
import android.app.AlertDialog;
import android.app.DownloadManager;
import android.app.DownloadManager.Request;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Environment;
import android.text.TextUtils.TruncateAt;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

/**
 * 下载工具帮助类
 * @author chenM
 *
 */
public class DownloadHelper extends BroadcastReceiver{
	//存在SD卡中的文件夹
	private static final String DIR = "KOA";
	private static DownloadHelper instance;
	private static Object lock = new Object();
	private ProgressDialog pd;
	private Long lastDownload;
	private HashMap<Long,File> fileHash = new HashMap<Long,File>();
	private Context curContext;
	public static final int MSG_DWPACKSIZE=1; 
	public static final int MSG_DWSIZE=2; 
	
	public static DownloadHelper getInstance() {
		if (instance == null) {
			synchronized (lock) {
				if (instance == null) {
					instance = new DownloadHelper();
				}
			}
		}
		return instance;
	}
	
	/**
	 * 动态创建附件列表
	 * @param meId					下载附件的参数
	 * @param metaDataMapId			下载附件的参数
	 * @param mgr					附件下载类的实例
	 * @param fileName				下载文件名
	 * @param conn					引用实例
	 * @param wf_accessory_table	添加的位置
	 */
	public void createAccessoryTable(final String meId,final String metaDataMapId,final DownloadManager mgr,String fileName,final Context conn,LinearLayout wf_accessory_table) {
		curContext = conn;
		if (fileName != null && !"".equals(fileName)) {
			String[] sa = fileName.split("@@@@");
			for(int i=0;i<sa.length;i++){
				//创建一行
				LinearLayout row = new LinearLayout(conn);
				LinearLayout.LayoutParams rowparam = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT);
				row.setOrientation(LinearLayout.HORIZONTAL);
				row.setLayoutParams(rowparam);
				//创建图片
				ImageView img = new ImageView(conn);
				img.setImageResource(SetBackGround.getAccessoryBg(sa[i]));
				img.setScaleType(ScaleType.FIT_XY);
				LinearLayout.LayoutParams imgParam = new LinearLayout.LayoutParams(25,25);
				imgParam.gravity = Gravity.CENTER_VERTICAL;
				img.setLayoutParams(imgParam);
				row.addView(img);
				//创建文件名
				final TextView fileNameText = new TextView(conn);
				fileNameText.setText(sa[i]);
				fileNameText.setTextColor(Color.parseColor("#3F8AC5"));
				fileNameText.setTextSize(16);
				fileNameText.setPadding(10, 10, 0, 10);
				fileNameText.setSingleLine();
				fileNameText.setEllipsize(TruncateAt.END);
				LinearLayout.LayoutParams fileNameTextParam = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
				fileNameText.setLayoutParams(fileNameTextParam);
				fileNameText.setBackgroundResource(R.drawable.wf_detail_attach);
				row.addView(fileNameText); 
				fileNameText.setOnClickListener(new OnClickListener() {
					@Override
					public void onClick(View v) {
						promptDown(meId,metaDataMapId,fileNameText.getText().toString(),conn,mgr);
					}
				});
				wf_accessory_table.addView(row);
			}
		}
	}
	
	// 提示是否下载
	private void promptDown(final String meId,final String metaDataMapId,final String fileName,final Context con,final DownloadManager mgr) {
		//创建对话框
		LayoutInflater li = LayoutInflater.from(con);
		View downloadV = li.inflate(R.layout.addsigndialog, null);
		TextView confirmDownloadWord = (TextView) downloadV
				.findViewById(R.id.exit_confirm_word);
		confirmDownloadWord.setText("确认打开附件"+"\""+fileName+"\"");
		AlertDialog.Builder ab = new AlertDialog.Builder(con);
		ab.setView(downloadV);// 设定对话框显示的View对象
		ab.setPositiveButton("确认",
				new android.content.DialogInterface.OnClickListener() {
					public void onClick(DialogInterface arg0, int arg1) {
						downLoadFile(meId,metaDataMapId,fileName,con,mgr);
					}
				});
		ab.setNegativeButton("取消", null);
		// 显示对话框
		ab.show();
	}
	
	//下载文件的方法
	public void downLoadFile(String meId,String metaDataMapId,String fileName,Context con,DownloadManager mgr) {
		StringBuffer url = new StringBuffer();
		String fileNameEncoded = "";
		String sissionId = User.getInstance().getUserContext(con).getSessionId();
		try {
			fileNameEncoded = Base64Encoder.encode(fileName.getBytes("UTF-8"));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		url.append(HttpResponseUtil.getInstance().getDownloadUrl(con)).append("?").append("flag1=").append(meId)
				.append("&flag2=").append(metaDataMapId).append("&sid=")
				.append(sissionId).append("&filename=").append(fileNameEncoded)
				.append("&rootDir=FormFile").append("&type=mobile");
		startDownload(con, fileName, url.toString(), mgr);
	}
	
	//开始下载
	public void startDownload(Context con,String fileName,String strUrl,DownloadManager mgr){
		//如果存在附件则直接打开附件
		if(checkFileExists(con,fileName).equals("exist")){
    		File fPath = getFilePath(fileName);
    		if(fPath!=null){
    			Intent openFileIntent = OpenDownloadFileUtil.getOpenFileIntent(fPath.toString());
    			openFile(fPath,openFileIntent);
    		}else{
    			Toast.makeText(con, "下载失败,请稍后再试", Toast.LENGTH_LONG).show();
    		}
    	//没有检测到SD卡
    	}else if(checkFileExists(con,fileName).equals("non-sdCard")){
    		Toast.makeText(con, "请插入sd卡后再试", Toast.LENGTH_LONG).show();
    	//没有检测到附件则下载附件
    	}else{
    		Uri uri=Uri.parse(strUrl); 
    		Environment.getExternalStoragePublicDirectory(DIR).mkdirs(); 
    		Request dwreq=new DownloadManager.Request(uri); 
    		dwreq.setTitle(fileName);
    		dwreq.setDescription(fileName); 
    		dwreq.setDestinationInExternalPublicDir(DIR,fileName); 
    		dwreq.setShowRunningNotification(true);
    		dwreq.setVisibleInDownloadsUi(false);
    		lastDownload = mgr.enqueue(dwreq);
    		File lastPath = getFilePath(fileName);
    		DownloadHelper.getInstance().fileHash.put(lastDownload, lastPath);
    		pd = new ProgressDialog(con);
    		pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
    		pd.setCancelable(true);
    		pd.setMessage("正在打开附件...");
    		pd.show();
    		ProgressDialogHolder.getInstance().addActivity(pd);
    	}
	}
	
	//获得文件路径
	public File getFilePath(String fileName){
		String sdDir = getSDPath();
		if(fileName!=null){
			return new File(sdDir+"/"+DIR+"/"+fileName);
		}else{
			return null;
		}
	}
	
	//检查文件是否存在
	public String checkFileExists(Context con,String fileName){
    	String sdDir = getSDPath();
    	if(null!=sdDir){
    		File f = new File(sdDir+"/"+DIR+"/"+fileName);
    		if(f.exists()){
    			return "exist";
    		}else{
    			return "non-exist";
    		}
    	}else{
    		return "non-sdCard";
    	}
    }
    
	//得到SD卡的路径
	public String getSDPath(){
		File sdDir = null;
		boolean sdCardExist = Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED);// 判断sd卡是否存在
		if (sdCardExist) {
			sdDir = Environment.getExternalStorageDirectory();// 获取跟目录
			return sdDir.toString();
		}else{
			return null;
		}
	} 
	
	//下载完毕后的回调函数
	@Override
	public void onReceive(Context context, Intent intent) {
		ProgressDialogHolder.getInstance().exit();
		String action = intent.getAction();
        if (DownloadManager.ACTION_DOWNLOAD_COMPLETE.equals(action)) {
        	long downloadId = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, 0);
        	File path = DownloadHelper.getInstance().fileHash.get(downloadId);
        	if(path!=null){
        		if(path.exists()){
        			Intent openFileIntent = OpenDownloadFileUtil.getOpenFileIntent(path.toString());
        			openFile(path,openFileIntent);
        		}else{
        			Toast.makeText(DownloadHelper.getInstance().curContext, "下载失败,请稍后再试", Toast.LENGTH_LONG).show();
        		}
        	}else{
        		Toast.makeText(DownloadHelper.getInstance().curContext, "下载失败,请稍后再试", Toast.LENGTH_LONG).show();
        	}
        }
	}
	
	//打开文件的方法
	public void openFile(File filePath, Intent intent) {
		Context con = DownloadHelper.getInstance().curContext;
		if(intent!=null){
			try {
				con.startActivity(intent);
			}catch(Exception e) {
				Toast.makeText(con, "没有找到打开文件相应的程序", Toast.LENGTH_LONG).show();
			}	
		}else{
			Toast.makeText(con, "没有找到打开文件相应的程序", Toast.LENGTH_LONG).show();
		}
	}
	
	//删除文件的方法
	public boolean deleteFile(Context con,String fileName){
		String sdDir = getSDPath();
    	if(null!=sdDir){
    		File f = new File(sdDir+"/"+DIR+"/"+fileName);
    		if(f.exists()){
    			f.delete();
    			return true;
    		}else{
    			return true;
    		}
    	}else{
    		return false;
    	}
	}
	
	/**
	 * 升级应用程序
	 * 
	 * @param con
	 * @param fileName
	 * @param updateUrl
	 * @param mgr
	 */
	public void updateApp(Context con, String fileName, String updateUrl,
			DownloadManager mgr) {
		curContext = con;
		//如果存在安装文件则打开文件
		if (checkFileExists(con, fileName).equals("exist")) {
			File fPath = getFilePath(fileName);
			if (fPath != null) {
				Intent openFileIntent = OpenDownloadFileUtil
						.getOpenFileIntent(fPath.toString());
				openFile(fPath, openFileIntent);
			} else {
				Toast.makeText(con, "升级失败,请稍后再试", Toast.LENGTH_LONG).show();
			}
		//没有检测到SD卡
		} else if (checkFileExists(con, fileName).equals("non-sdCard")) {
			Toast.makeText(con, "请插入sd卡后再试", Toast.LENGTH_LONG).show();
			Intent intent = new Intent();
			intent.setClass(con, MainActivity.class);
			con.startActivity(intent);
		} else {
			Uri uri=Uri.parse(updateUrl); 
    		Environment.getExternalStoragePublicDirectory(DIR).mkdirs(); 
    		Request dwreq=new DownloadManager.Request(uri); 
    		dwreq.setTitle("KOA升级");
    		dwreq.setDescription(fileName); 
    		dwreq.setDestinationInExternalPublicDir(DIR,fileName); 
    		dwreq.setShowRunningNotification(true);
    		dwreq.setVisibleInDownloadsUi(true);
    		lastDownload = mgr.enqueue(dwreq);
    		File lastPath = getFilePath(fileName);
    		DownloadHelper.getInstance().fileHash.put(lastDownload, lastPath);
    		pd = new ProgressDialog(con);
    		pd.setProgressStyle(ProgressDialog.STYLE_SPINNER);
    		pd.setCancelable(true);
    		pd.setMessage("正在升级...");
    		pd.show();
    		ProgressDialogHolder.getInstance().addActivity(pd);
		}
	}
}
