package koa.android.demo;
/**
 * Activity 接口
 */
public interface KoaActivity {
	/**
	 * 初始化数据
	 */
	void init();
	
	/**
	 * 刷新UI
	 */
	void refresh(Object... params);
}
