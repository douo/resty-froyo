package info.dourok.net;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URLConnection;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import android.util.Log;

public class CookieKit {
	public static void storeCookies(URLConnection con)
			throws IOException, URISyntaxException {
		CookieHandler cookieHandler = CookieHandler.getDefault();
		if(cookieHandler==null){
			Log.e("CookieKit", "storeCookies fails:cookieHandler==null");
			return;
		}
		cookieHandler.put(con.getURL().toURI(), con.getHeaderFields());
	}

	public static void addCookies(URLConnection con) throws IOException, URISyntaxException {
		CookieHandler cookieHandler = CookieHandler.getDefault();
		if(cookieHandler==null){
			Log.e("CookieKit", "addCookies fails:cookieHandler==null");
			return;
		}
		Map<String, List<String>> cookies = cookieHandler.get(con.getURL()
				.toURI(), con.getRequestProperties());
		Iterator<String> itr = cookies.keySet().iterator();
		while (itr.hasNext()) {
			String key = itr.next();
			List<String> cs = cookies.get(key);
			StringBuffer buffer = new StringBuffer();
			if (cs.size() > 0) {
				buffer.append(cs.get(0));
				for (int i = 1; i < cs.size(); i++) {
					buffer.append("; ");
					buffer.append(cs.get(i));
				}
			}
			con.addRequestProperty(key, buffer.toString());
		}

	}
}
