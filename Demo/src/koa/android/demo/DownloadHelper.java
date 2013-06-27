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
 * ���ع��߰�����
 * @author chenM
 *
 */
public class DownloadHelper extends BroadcastReceiver{
	//����SD���е��ļ���
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
	 * ��̬���������б�
	 * @param meId					���ظ����Ĳ���
	 * @param metaDataMapId			���ظ����Ĳ���
	 * @param mgr					�����������ʵ��
	 * @param fileName				�����ļ���
	 * @param conn					����ʵ��
	 * @param wf_accessory_table	��ӵ�λ��
	 */
	public void createAccessoryTable(final String meId,final String metaDataMapId,final DownloadManager mgr,String fileName,final Context conn,LinearLayout wf_accessory_table) {
		curContext = conn;
		if (fileName != null && !"".equals(fileName)) {
			String[] sa = fileName.split("@@@@");
			for(int i=0;i<sa.length;i++){
				//����һ��
				LinearLayout row = new LinearLayout(conn);
				LinearLayout.LayoutParams rowparam = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.FILL_PARENT,LinearLayout.LayoutParams.WRAP_CONTENT);
				row.setOrientation(LinearLayout.HORIZONTAL);
				row.setLayoutParams(rowparam);
				//����ͼƬ
				ImageView img = new ImageView(conn);
				img.setImageResource(SetBackGround.getAccessoryBg(sa[i]));
				img.setScaleType(ScaleType.FIT_XY);
				LinearLayout.LayoutParams imgParam = new LinearLayout.LayoutParams(25,25);
				imgParam.gravity = Gravity.CENTER_VERTICAL;
				img.setLayoutParams(imgParam);
				row.addView(img);
				//�����ļ���
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
	
	// ��ʾ�Ƿ�����
	private void promptDown(final String meId,final String metaDataMapId,final String fileName,final Context con,final DownloadManager mgr) {
		//�����Ի���
		LayoutInflater li = LayoutInflater.from(con);
		View downloadV = li.inflate(R.layout.addsigndialog, null);
		TextView confirmDownloadWord = (TextView) downloadV
				.findViewById(R.id.exit_confirm_word);
		confirmDownloadWord.setText("ȷ�ϴ򿪸���"+"\""+fileName+"\"");
		AlertDialog.Builder ab = new AlertDialog.Builder(con);
		ab.setView(downloadV);// �趨�Ի�����ʾ��View����
		ab.setPositiveButton("ȷ��",
				new android.content.DialogInterface.OnClickListener() {
					public void onClick(DialogInterface arg0, int arg1) {
						downLoadFile(meId,metaDataMapId,fileName,con,mgr);
					}
				});
		ab.setNegativeButton("ȡ��", null);
		// ��ʾ�Ի���
		ab.show();
	}
	
	//�����ļ��ķ���
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
	
	//��ʼ����
	public void startDownload(Context con,String fileName,String strUrl,DownloadManager mgr){
		//������ڸ�����ֱ�Ӵ򿪸���
		if(checkFileExists(con,fileName).equals("exist")){
    		File fPath = getFilePath(fileName);
    		if(fPath!=null){
    			Intent openFileIntent = OpenDownloadFileUtil.getOpenFileIntent(fPath.toString());
    			openFile(fPath,openFileIntent);
    		}else{
    			Toast.makeText(con, "����ʧ��,���Ժ�����", Toast.LENGTH_LONG).show();
    		}
    	//û�м�⵽SD��
    	}else if(checkFileExists(con,fileName).equals("non-sdCard")){
    		Toast.makeText(con, "�����sd��������", Toast.LENGTH_LONG).show();
    	//û�м�⵽���������ظ���
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
    		pd.setMessage("���ڴ򿪸���...");
    		pd.show();
    		ProgressDialogHolder.getInstance().addActivity(pd);
    	}
	}
	
	//����ļ�·��
	public File getFilePath(String fileName){
		String sdDir = getSDPath();
		if(fileName!=null){
			return new File(sdDir+"/"+DIR+"/"+fileName);
		}else{
			return null;
		}
	}
	
	//����ļ��Ƿ����
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
    
	//�õ�SD����·��
	public String getSDPath(){
		File sdDir = null;
		boolean sdCardExist = Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED);// �ж�sd���Ƿ����
		if (sdCardExist) {
			sdDir = Environment.getExternalStorageDirectory();// ��ȡ��Ŀ¼
			return sdDir.toString();
		}else{
			return null;
		}
	} 
	
	//������Ϻ�Ļص�����
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
        			Toast.makeText(DownloadHelper.getInstance().curContext, "����ʧ��,���Ժ�����", Toast.LENGTH_LONG).show();
        		}
        	}else{
        		Toast.makeText(DownloadHelper.getInstance().curContext, "����ʧ��,���Ժ�����", Toast.LENGTH_LONG).show();
        	}
        }
	}
	
	//���ļ��ķ���
	public void openFile(File filePath, Intent intent) {
		Context con = DownloadHelper.getInstance().curContext;
		if(intent!=null){
			try {
				con.startActivity(intent);
			}catch(Exception e) {
				Toast.makeText(con, "û���ҵ����ļ���Ӧ�ĳ���", Toast.LENGTH_LONG).show();
			}	
		}else{
			Toast.makeText(con, "û���ҵ����ļ���Ӧ�ĳ���", Toast.LENGTH_LONG).show();
		}
	}
	
	//ɾ���ļ��ķ���
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
	 * ����Ӧ�ó���
	 * 
	 * @param con
	 * @param fileName
	 * @param updateUrl
	 * @param mgr
	 */
	public void updateApp(Context con, String fileName, String updateUrl,
			DownloadManager mgr) {
		curContext = con;
		//������ڰ�װ�ļ�����ļ�
		if (checkFileExists(con, fileName).equals("exist")) {
			File fPath = getFilePath(fileName);
			if (fPath != null) {
				Intent openFileIntent = OpenDownloadFileUtil
						.getOpenFileIntent(fPath.toString());
				openFile(fPath, openFileIntent);
			} else {
				Toast.makeText(con, "����ʧ��,���Ժ�����", Toast.LENGTH_LONG).show();
			}
		//û�м�⵽SD��
		} else if (checkFileExists(con, fileName).equals("non-sdCard")) {
			Toast.makeText(con, "�����sd��������", Toast.LENGTH_LONG).show();
			Intent intent = new Intent();
			intent.setClass(con, MainActivity.class);
			con.startActivity(intent);
		} else {
			Uri uri=Uri.parse(updateUrl); 
    		Environment.getExternalStoragePublicDirectory(DIR).mkdirs(); 
    		Request dwreq=new DownloadManager.Request(uri); 
    		dwreq.setTitle("KOA����");
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
    		pd.setMessage("��������...");
    		pd.show();
    		ProgressDialogHolder.getInstance().addActivity(pd);
		}
	}
}
