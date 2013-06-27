package koa.android.tools;

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

public class IPAddressTools {

	public static String getLocalIpAddress(){
		String ipaddress="";
			try {
				for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();) {
					NetworkInterface intf = en.nextElement();
					for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements();) {
						InetAddress inetAddress = enumIpAddr.nextElement();
						if (!inetAddress.isLoopbackAddress()) {
							ipaddress=ipaddress+";"+ inetAddress.getHostAddress().toString();
						}
					}
				}
			} catch (SocketException ex) {
			}
			return ipaddress;
	}
}
