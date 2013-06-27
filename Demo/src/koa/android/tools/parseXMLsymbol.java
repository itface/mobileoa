package koa.android.tools;
/**
 * ��ԭJSON�е������ַ�
 * @author chenM
 *
 */
public class parseXMLsymbol{
	//����SD���е��ļ���
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
