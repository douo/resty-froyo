/**
 * 
 */
package us.monoid.web;

import info.dourok.util.MiscUtils;

import java.io.*;
import java.net.*;

import us.monoid.util.EncoderUtil;

import android.util.Log;

/**
 * Abstract base class for all resource handlers you want to use with Resty.
 * 
 * It gives access to the underlying URLConnection and the current inputStream
 * 
 * @author beders
 * 
 */
public abstract class AbstractResource extends Resty {
	protected URLConnection urlConnection;
	protected InputStream inputStream;

	public AbstractResource(Option... options) {
		super(options);
	}

	abstract String getAcceptedTypes();

	void fill(URLConnection anUrlConnection) throws IOException {
		urlConnection = anUrlConnection;
		try {
			inputStream = anUrlConnection.getInputStream();
		} catch (IOException e) {
			// e.printStackTrace();

			// Per
			// http://docs.oracle.com/javase/1.5.0/docs/guide/net/http-keepalive.html
			// (comparable documentation exists for later java versions)
			// if an HttpURLConnection was used clear the errorStream and close
			// it
			// so that keep alive can keep doing its work
			if (anUrlConnection instanceof HttpURLConnection) {
				HttpURLConnection conn = (HttpURLConnection) anUrlConnection;

				// hack for 401 by douo
				// http://stackoverflow.com/questions/10431202/java-io-ioexception-received-authentication-challenge-is-null
				//  for version before jb (4.1)
				if ("Received authentication challenge is null".equals(e
						.getMessage())) {
					throw new RestyAuthenticationException(e.getMessage(),
							conn.getRequestMethod());
				}

				String rMethod = conn.getRequestMethod();
				int rCode = conn.getResponseCode();
				String rMessage = conn.getResponseMessage();
				Log.d("CODE", rCode + "");
				if(rCode ==401){
					throw new RestyAuthenticationException(e.getMessage(),
							conn.getRequestMethod());
				}
				InputStream is = conn.getErrorStream();
				if (is != null) {
					String error = EncoderUtil.convertStreamToString(conn
							.getErrorStream());
					conn.getErrorStream().close();
					throw new RestyIOException(error, rMethod, rCode, rMessage);
				} else {
					throw new RestyIOException(e, rMethod, rCode, rMessage);
				}
			} else {
				throw e;
			}
		}
	}

	public URLConnection getUrlConnection() {
		return urlConnection;
	}

	public HttpURLConnection http() {
		return (HttpURLConnection) urlConnection;
	}

	public InputStream stream() {
		return inputStream;
	}

	/**
	 * Check if the URLConnection has returned the specified responseCode
	 * 
	 * @param responseCode
	 * @return
	 */
	public boolean status(int responseCode) {
		if (urlConnection instanceof HttpURLConnection) {
			HttpURLConnection http = (HttpURLConnection) urlConnection;
			try {
				return http.getResponseCode() == responseCode;
			} catch (IOException e) {
				e.printStackTrace();
				return false;
			}
		} else
			return false;
	}

	/**
	 * Get the location header as URI. Returns null if there is no location
	 * header.
	 * 
	 */
	public URI location() {
		String loc = http().getHeaderField("Location");
		if (loc != null) {
			return URI.create(loc);
		}
		return null;
	}
}
