package koa.android.tools;
/**
 * 还原JSON中的特殊字符
 * @author chenM
 *
 */
public class parseXMLsymbol{
	//存在SD卡中的文件夹
	private static parseXMLsymbol instance;
	private static Object lock = new Object();
	
	public static parseXMLsymbol getInstance() {
		if (instance == null) {
			synchronized (lock) {
				if (instance == null) {
					instance = new parseXMLsymbol();
				}
			}
		}
		return instance;
	}
	
	public String parseXML(String oldStr){
		if(oldStr.contains("&lt;")){
			oldStr = oldStr.replace("&lt;", "<");
		}
		if(oldStr.contains("&gt;")){
			oldStr = oldStr.replace("&gt;", ">");
		}
		if(oldStr.contains("&amp;")){
			oldStr = oldStr.replace("&amp;", "&");
		}
		if(oldStr.contains("&apos;")){
			oldStr = oldStr.replace("&apos;", "'");
		}
		if(oldStr.contains("&quot;")){
			oldStr = oldStr.replace("&quot;", "\"");
		}
		return oldStr;
	}
	
}
