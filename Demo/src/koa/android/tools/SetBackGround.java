package koa.android.tools;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;

import koa.android.demo.R;
import koa.android.demo.UISettingActivity;
import android.content.ContentResolver;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Shader.TileMode;
import android.graphics.drawable.BitmapDrawable;
import android.media.ExifInterface;
import android.net.Uri;
import android.view.View;

/**
 * 设置背景
 * 
 * @author chenM
 * 
 */
public class SetBackGround {
	private static SetBackGround instance;
	private static Object lock = new Object();
	private HashMap<Uri, BitmapDrawable> picMap = new HashMap<Uri, BitmapDrawable>();
	// 蓝色主题默认的背景图
	private final int BLUE_THEME_UI_LOGIN_BTN = R.drawable.ui_login_btn_blue;
	private final int BLUE_THEME_UI_LOGIN_LOGO = R.drawable.ui_login_logo;
	private final int BLUE_THEME_UI_LOGIN_SETTING = R.drawable.ui_login_seeting;
	private final int BLUE_THEME_GRIDVIEW = R.drawable.ui_login_bg_bule;
	private final int BLUE_THEME_TITLE = R.drawable.ui_title_bg_blue;
	private final int BLUE_THEME_TITLE_SAVE = R.drawable.ui_title_save_btn_blue;
	private final int BLUE_THEME_TITLE_LEFT = R.drawable.ui_title_back_btn_blue;
	private final int BLUE_THEME_TITLE_RIGHT = R.drawable.ui_title_loginout_btn_blue;
	private final int BLUE_THEME_FORMVIEW = R.drawable.ui_title_formview_btn_blue;
	private final int BLUE_THEME_OPERATE_BAR = R.drawable.wf_detail_operate_bar_bg_blue;
	private final int BLUE_THEME_LIST_UNREAD = R.drawable.wf_list_item_unread_icon_blue;
	private final int BLUE_THEME_LIST_RECYLE = R.drawable.wf_list_item_recycle_icon_blue;
	private final int BLUE_THEME_LIST_NOTICE = R.drawable.wf_list_item_notice_icon_blue;
	private final int BLUE_THEME_LIST_CANCLE = R.drawable.wf_list_cancle_btn_blue;
	// 黄色主题默认的背景图
	private final int ORANGE_THEME_UI_LOGIN_BTN = R.drawable.ui_login_btn_red;
	private final int ORANGE_THEME_UI_LOGIN_LOGO = R.drawable.ui_login_logo;
	private final int ORANGE_THEME_UI_LOGIN_SETTING = R.drawable.ui_login_seeting;
	private final int ORANGE_THEME_GRIDVIEW = R.drawable.ui_login_bg_red;
	private final int ORANGE_THEME_TITLE = R.drawable.ui_title_bg_red;
	private final int ORANGE_THEME_TITLE_SAVE = R.drawable.ui_title_save_btn_red;
	private final int ORANGE_THEME_TITLE_LEFT = R.drawable.ui_title_back_btn_red;
	private final int ORANGE_THEME_TITLE_RIGHT = R.drawable.ui_title_loginout_btn_red;
	private final int ORANGE_THEME_FORMVIEW = R.drawable.ui_title_formview_btn_red;
	private final int ORANGE_THEME_OPERATE_BAR = R.drawable.wf_detail_operate_bar_bg_red;
	private final int ORANGE_THEME_LIST_UNREAD = R.drawable.wf_list_item_unread_icon_red;
	private final int ORANGE_THEME_LIST_RECYLE = R.drawable.wf_list_item_recycle_icon_red;
	private final int ORANGE_THEME_LIST_NOTICE = R.drawable.wf_list_item_notice_icon_red;
	private final int ORANGE_THEME_LIST_CANCLE = R.drawable.wf_list_cancle_btn_red;
	// 黑色主题默认的背景图
	private final int BLACK_THEME_UI_LOGIN_BTN = R.drawable.ui_login_btn_black;
	private final int BLACK_THEME_UI_LOGIN_LOGO = R.drawable.ui_login_logo_black;
	private final int BLACK_THEME_UI_LOGIN_SETTING = R.drawable.ui_login_seeting_black;
	private final int BLACK_THEME_GRIDVIEW = R.drawable.ui_login_bg_black;
	private final int BLACK_THEME_TITLE = R.drawable.ui_title_bg_black;
	private final int BLACK_THEME_TITLE_SAVE = R.drawable.ui_title_save_btn_black;
	private final int BLACK_THEME_TITLE_LEFT = R.drawable.ui_title_back_btn_black;
	private final int BLACK_THEME_TITLE_RIGHT = R.drawable.ui_title_loginout_btn_black;
	private final int BLACK_THEME_FORMVIEW = R.drawable.ui_title_formview_btn_black;
	private final int BLACK_THEME_OPERATE_BAR = R.drawable.wf_detail_operate_bar_bg_black;
	private final int BLACK_THEME_LIST_UNREAD = R.drawable.wf_list_item_unread_icon_blue;
	private final int BLACK_THEME_LIST_RECYLE = R.drawable.wf_list_item_recycle_icon_blue;
	private final int BLACK_THEME_LIST_NOTICE = R.drawable.wf_list_item_notice_icon_blue;
	private final int BLACK_THEME_LIST_CANCLE = R.drawable.wf_list_cancle_btn_black;

	// 私有构造方法
	private SetBackGround() {

	}

	public static SetBackGround getInstance() {
		if (instance == null) {
			synchronized (lock) {
				if (instance == null) {
					instance = new SetBackGround();
				}
			}
		}
		return instance;
	}

	/**
	 * 返回主题
	 * 
	 * @return
	 */
	public String getTheme(Context conn) {
		String bg_uri = UISettingActivity.getTheme(conn);
		return bg_uri;
	}

	/**
	 * 设置主题
	 * 
	 * @param conn
	 * @param v
	 * @param viewName
	 */
	public void setTheme(Context conn, View v, String viewName, int defaultRes, TileMode tileMode) {
		String bg_uri = UISettingActivity.getTheme(conn);
		if (bg_uri == null || "".equals(bg_uri)) {
			v.setBackgroundResource(defaultRes);
		} else if (bg_uri.equals("blue")) {
			if (viewName.equals("gridview")) {
				v.setBackgroundResource(BLUE_THEME_GRIDVIEW);
			} else if (viewName.equals("top_title")) {
				v.setBackgroundResource(BLUE_THEME_TITLE);
			} else if (viewName.equals("loginbtn")) {
				v.setBackgroundResource(BLUE_THEME_UI_LOGIN_BTN);
			} else if (viewName.equals("ui_login_logo")) {
				v.setBackgroundResource(BLUE_THEME_UI_LOGIN_LOGO);
			} else if (viewName.equals("ui_backbtn")) {
				v.setBackgroundResource(BLUE_THEME_TITLE_LEFT);
			} else if (viewName.equals("ui_login_outbtn")) {
				v.setBackgroundResource(BLUE_THEME_TITLE_RIGHT);
			} else if (viewName.equals("ui_title_formview_btn")) {
				v.setBackgroundResource(BLUE_THEME_FORMVIEW);
			} else if (viewName.equals("wf_detail_operate_bar")) {
				v.setBackgroundResource(BLUE_THEME_OPERATE_BAR);
			} else if (viewName.equals("list_unread")) {
				v.setBackgroundResource(BLUE_THEME_LIST_UNREAD);
			} else if (viewName.equals("list_recycle")) {
				v.setBackgroundResource(BLUE_THEME_LIST_RECYLE);
			} else if (viewName.equals("list_notice")) {
				v.setBackgroundResource(BLUE_THEME_LIST_NOTICE);
			} else if (viewName.equals("list_cancle")) {
				v.setBackgroundResource(BLUE_THEME_LIST_CANCLE);
			} else if (viewName.equals("ui_login_setting")) {
				v.setBackgroundResource(BLUE_THEME_UI_LOGIN_SETTING);
			} else if (viewName.equals("ui_title_save_btn")) {
				v.setBackgroundResource(BLUE_THEME_TITLE_SAVE);
			} else {
				v.setBackgroundResource(defaultRes);
			}
		} else if (bg_uri.equals("orange")) {
			if (viewName.equals("gridview")) {
				v.setBackgroundResource(ORANGE_THEME_GRIDVIEW);
			} else if (viewName.equals("top_title")) {
				v.setBackgroundResource(ORANGE_THEME_TITLE);
			} else if (viewName.equals("loginbtn")) {
				v.setBackgroundResource(ORANGE_THEME_UI_LOGIN_BTN);
			} else if (viewName.equals("ui_login_logo")) {
				v.setBackgroundResource(ORANGE_THEME_UI_LOGIN_LOGO);
			} else if (viewName.equals("ui_backbtn")) {
				v.setBackgroundResource(ORANGE_THEME_TITLE_LEFT);
			} else if (viewName.equals("ui_login_outbtn")) {
				v.setBackgroundResource(ORANGE_THEME_TITLE_RIGHT);
			} else if (viewName.equals("ui_title_formview_btn")) {
				v.setBackgroundResource(ORANGE_THEME_FORMVIEW);
			} else if (viewName.equals("wf_detail_operate_bar")) {
				v.setBackgroundResource(ORANGE_THEME_OPERATE_BAR);
			} else if (viewName.equals("list_unread")) {
				v.setBackgroundResource(ORANGE_THEME_LIST_UNREAD);
			} else if (viewName.equals("list_recycle")) {
				v.setBackgroundResource(ORANGE_THEME_LIST_RECYLE);
			} else if (viewName.equals("list_notice")) {
				v.setBackgroundResource(ORANGE_THEME_LIST_NOTICE);
			} else if (viewName.equals("list_cancle")) {
				v.setBackgroundResource(ORANGE_THEME_LIST_CANCLE);
			} else if (viewName.equals("ui_login_setting")) {
				v.setBackgroundResource(ORANGE_THEME_UI_LOGIN_SETTING);
			} else if (viewName.equals("ui_title_save_btn")) {
				v.setBackgroundResource(ORANGE_THEME_TITLE_SAVE);
			} else {
				v.setBackgroundResource(defaultRes);
			}
		} else if (bg_uri.equals("black")) {
			if (viewName.equals("gridview")) {
				v.setBackgroundResource(BLACK_THEME_GRIDVIEW);
			} else if (viewName.equals("top_title")) {
				v.setBackgroundResource(BLACK_THEME_TITLE);
			} else if (viewName.equals("loginbtn")) {
				v.setBackgroundResource(BLACK_THEME_UI_LOGIN_BTN);
			} else if (viewName.equals("ui_login_logo")) {
				v.setBackgroundResource(BLACK_THEME_UI_LOGIN_LOGO);
			} else if (viewName.equals("ui_backbtn")) {
				v.setBackgroundResource(BLACK_THEME_TITLE_LEFT);
			} else if (viewName.equals("ui_login_outbtn")) {
				v.setBackgroundResource(BLACK_THEME_TITLE_RIGHT);
			} else if (viewName.equals("ui_title_formview_btn")) {
				v.setBackgroundResource(BLACK_THEME_FORMVIEW);
			} else if (viewName.equals("wf_detail_operate_bar")) {
				v.setBackgroundResource(BLACK_THEME_OPERATE_BAR);
			} else if (viewName.equals("list_unread")) {
				v.setBackgroundResource(BLACK_THEME_LIST_UNREAD);
			} else if (viewName.equals("list_recycle")) {
				v.setBackgroundResource(BLACK_THEME_LIST_RECYLE);
			} else if (viewName.equals("list_notice")) {
				v.setBackgroundResource(BLACK_THEME_LIST_NOTICE);
			} else if (viewName.equals("list_cancle")) {
				v.setBackgroundResource(BLACK_THEME_LIST_CANCLE);
			} else if (viewName.equals("ui_login_setting")) {
				v.setBackgroundResource(BLACK_THEME_UI_LOGIN_SETTING);
			} else if (viewName.equals("ui_title_save_btn")) {
				v.setBackgroundResource(BLACK_THEME_TITLE_SAVE);
			} else {
				v.setBackgroundResource(defaultRes);
			}
		}
	}

	/**
	 * 设置背景图
	 * 
	 * @param v
	 * @param defaultRes
	 */
	public void setGridviewBG(Context conn, View v, String viewName, int defaultRes, TileMode tileMode) {
		Uri BG_URI = UISettingActivity.getMainBG_URI(conn);
		String bg_uri = BG_URI.toString();
		if (bg_uri == null) {
			v.setBackgroundResource(defaultRes);
		} else if (bg_uri.equals("blue")) {
			if (viewName.equals("gridview")) {
				v.setBackgroundResource(BLUE_THEME_GRIDVIEW);
			} else if (viewName.equals("top_title")) {
				v.setBackgroundResource(BLUE_THEME_TITLE);
			} else if (viewName.equals("loginbtn")) {
				v.setBackgroundResource(BLUE_THEME_UI_LOGIN_BTN);
			} else {
				v.setBackgroundResource(defaultRes);
			}
		} else if (bg_uri.equals("orange")) {
			if (viewName.equals("gridview")) {
				v.setBackgroundResource(ORANGE_THEME_GRIDVIEW);
			} else if (viewName.equals("top_title")) {
				v.setBackgroundResource(ORANGE_THEME_TITLE);
			} else if (viewName.equals("loginbtn")) {
				v.setBackgroundResource(ORANGE_THEME_UI_LOGIN_BTN);
			} else {
				v.setBackgroundResource(defaultRes);
			}
		} else if (bg_uri.equals("black")) {
			if (viewName.equals("gridview")) {
				v.setBackgroundResource(BLACK_THEME_GRIDVIEW);
			} else if (viewName.equals("top_title")) {
				v.setBackgroundResource(BLACK_THEME_TITLE);
			} else if (viewName.equals("loginbtn")) {
				v.setBackgroundResource(BLACK_THEME_UI_LOGIN_BTN);
			} else {
				v.setBackgroundResource(defaultRes);
			}
		} else {
			if (picMap.containsKey(BG_URI)) {
				BitmapDrawable cachaBd = picMap.get(BG_URI);
				if (cachaBd != null) {
					if (tileMode != null) {
						cachaBd.setTileModeXY(tileMode, tileMode);
						cachaBd.setDither(true);
					}
					v.setBackgroundDrawable(cachaBd);
				} else {
					try {
						Bitmap map;
						ContentResolver cr = conn.getContentResolver();
						InputStream in = cr.openInputStream(BG_URI);
						map = BitmapFactory.decodeStream(in);
						BitmapDrawable bd = new BitmapDrawable(map);
						if (tileMode != null) {
							bd.setTileModeXY(TileMode.REPEAT, TileMode.REPEAT);
							bd.setDither(true);
						}
						v.setBackgroundDrawable(bd);
					} catch (FileNotFoundException e) {
						v.setBackgroundResource(defaultRes);
					}
				}
			} else {
				try {
					Bitmap map;
					ContentResolver cr = conn.getContentResolver();
					InputStream in = cr.openInputStream(BG_URI);
					map = BitmapFactory.decodeStream(in);
					BitmapDrawable bd = new BitmapDrawable(map);
					if (tileMode != null) {
						bd.setTileModeXY(TileMode.REPEAT, TileMode.REPEAT);
						bd.setDither(true);
					}
					picMap.put(BG_URI, bd);
					v.setBackgroundDrawable(bd);
				} catch (FileNotFoundException e) {
					v.setBackgroundResource(defaultRes);
				}
			}
		}
	}

	public void setBackgroundImg(Context conn, View v, String viewName, int defaultRes, TileMode tileMode, int screenWidth, int screenHeight, String path) {
		Uri BG_URI = UISettingActivity.getMainBG_URI(conn);
		String bg_uri = BG_URI.toString();
		if (bg_uri == null) {
			v.setBackgroundResource(defaultRes);
		} else if (bg_uri.equals("blue")) {
			if (viewName.equals("gridview")) {
				v.setBackgroundResource(BLUE_THEME_GRIDVIEW);
			} else if (viewName.equals("top_title")) {
				v.setBackgroundResource(BLUE_THEME_TITLE);
			} else if (viewName.equals("loginbtn")) {
				v.setBackgroundResource(BLUE_THEME_UI_LOGIN_BTN);
			} else {
				v.setBackgroundResource(defaultRes);
			}
		} else if (bg_uri.equals("orange")) {
			if (viewName.equals("gridview")) {
				v.setBackgroundResource(ORANGE_THEME_GRIDVIEW);
			} else if (viewName.equals("top_title")) {
				v.setBackgroundResource(ORANGE_THEME_TITLE);
			} else if (viewName.equals("loginbtn")) {
				v.setBackgroundResource(ORANGE_THEME_UI_LOGIN_BTN);
			} else {
				v.setBackgroundResource(defaultRes);
			}
		} else if (bg_uri.equals("black")) {
			if (viewName.equals("gridview")) {
				v.setBackgroundResource(BLACK_THEME_GRIDVIEW);
			} else if (viewName.equals("top_title")) {
				v.setBackgroundResource(BLACK_THEME_TITLE);
			} else if (viewName.equals("loginbtn")) {
				v.setBackgroundResource(BLACK_THEME_UI_LOGIN_BTN);
			} else {
				v.setBackgroundResource(defaultRes);
			}
		} else {
			try {
				Bitmap map;
				Bitmap bitmap = null;
				ContentResolver cr = conn.getContentResolver();
				InputStream in = cr.openInputStream(BG_URI);
				BitmapFactory.Options options = new BitmapFactory.Options();
				// 虚拟调用，不返回实际的bitmap
				options.inJustDecodeBounds = true;
				BitmapFactory.decodeStream(in, null, options);
				try {
					in.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
				int mWidth = options.outWidth;// 源图片宽度
				int mHeight = options.outHeight;// 源图片高度

				if (mHeight == screenHeight && mWidth == screenWidth) {
					// 截屏图片（图片大小=屏幕大小）不做处理，直接读取图片
					options.inJustDecodeBounds = false;
					options = new BitmapFactory.Options();
					in = cr.openInputStream(BG_URI);
					bitmap = BitmapFactory.decodeStream(in, null, options);
				} else {
					int s = 1;
					while ((mWidth / s > screenWidth * 2) || (mHeight / s > screenHeight * 2)) {
						s *= 2;
					}
					options.inJustDecodeBounds = false;
					options = new BitmapFactory.Options();
					options.inSampleSize = s;
					in = cr.openInputStream(BG_URI);
					map = BitmapFactory.decodeStream(in, null, options);
					try {
						in.close();
					} catch (IOException e) {
						e.printStackTrace();
					}

					Matrix matrix = new Matrix();
					int degree = readPictureDegree(path);// 计算图片旋转角度
					matrix.setRotate(degree); // 旋转X度
					// 图片大小
					int width = map.getWidth();
					int height = map.getHeight();
					// 缩放比例
					float scaleWidth = ((float) screenWidth) / width;
					float scaleHeight = ((float) screenHeight) / height;

					if ((width > height && screenWidth < screenHeight) || (width < height && screenWidth > screenHeight)) {
						if (degree == 0 || degree == 180) {
							// 横向图片统一缩放比
							scaleHeight = scaleWidth;
						} else {
							// 纵向图片拉伸铺满桌面
							scaleWidth = ((float) screenWidth) / height;
							scaleHeight = ((float) screenHeight) / width;
						}
					}
					matrix.postScale(scaleWidth, scaleHeight);
					bitmap = Bitmap.createBitmap(map, 0, 0, width, height, matrix, true);// 重新生成图片
					map.recycle();
				}

				BitmapDrawable bmd = new BitmapDrawable(bitmap);
				if (tileMode != null) {
					bmd.setTileModeXY(TileMode.REPEAT, TileMode.REPEAT);
					bmd.setDither(true);
				}
				picMap.put(BG_URI, bmd);
				v.setBackgroundDrawable(bmd);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	/**
	 * 读取相片Exif旋转角度
	 * 
	 * @param path
	 * @return
	 */
	public static int readPictureDegree(String path) {
		int degree = 0;
		try {
			ExifInterface exifInterface = new ExifInterface(path);
			int orientation = exifInterface.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_NORMAL);
			switch (orientation) {
			case ExifInterface.ORIENTATION_ROTATE_90:
				degree = 90;
				break;
			case ExifInterface.ORIENTATION_ROTATE_180:
				degree = 180;
				break;
			case ExifInterface.ORIENTATION_ROTATE_270:
				degree = 270;
				break;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return degree;
	}

	/**
	 * 得到附件背景
	 * 
	 * @return
	 */
	public static int getAccessoryBg(String fileName) {
		if (fileName.endsWith("avi")) {
			return R.drawable.wf_accessory_avi;
		} else if (fileName.endsWith("bmp")) {
			return R.drawable.wf_accessory_bmp;
		} else if (fileName.endsWith("chm")) {
			return R.drawable.wf_accessory_chm;
		} else if (fileName.endsWith("dps")) {
			return R.drawable.wf_accessory_dps;
		} else if (fileName.endsWith("et")) {
			return R.drawable.wf_accessory_et;
		} else if (fileName.endsWith("excel")) {
			return R.drawable.wf_accessory_excel;
		} else if (fileName.endsWith("exe")) {
			return R.drawable.wf_accessory_exe;
		} else if (fileName.endsWith("gif")) {
			return R.drawable.wf_accessory_gif;
		} else if (fileName.endsWith("html")) {
			return R.drawable.wf_accessory_html;
		} else if (fileName.endsWith("jpg")) {
			return R.drawable.wf_accessory_jpg;
		} else if (fileName.endsWith("pdf")) {
			return R.drawable.wf_accessory_pdf;
		} else if (fileName.endsWith("ppt")) {
			return R.drawable.wf_accessory_ppt;
		} else if (fileName.endsWith("rar")) {
			return R.drawable.wf_accessory_rar;
		} else if (fileName.endsWith("txt")) {
			return R.drawable.wf_accessory_txt;
		} else if (fileName.endsWith("word")) {
			return R.drawable.wf_accessory_word;
		} else if (fileName.endsWith("wps")) {
			return R.drawable.wf_accessory_wps;
		} else if (fileName.endsWith("xml")) {
			return R.drawable.wf_accessory_xml;
		} else if (fileName.endsWith("zip")) {
			return R.drawable.wf_accessory_zip;
		} else {
			return R.drawable.wf_accessory_normal;
		}
	}

}
