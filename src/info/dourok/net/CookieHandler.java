package info.dourok.net;

import java.util.Map;
import java.util.List;
import java.io.IOException;
import java.net.URI;

/**
 * A CookieHandler object provides a callback mechanism to hook up a HTTP state
 * management policy implementation into the HTTP protocol handler. The HTTP
 * state management mechanism specifies a way to create a stateful session with
 * HTTP requests and responses.
 * 
 * <p>
 * A system-wide CookieHandler that to used by the HTTP protocol handler can be
 * registered by doing a CookieHandler.setDefault(CookieHandler). The currently
 * registered CookieHandler can be retrieved by calling
 * CookieHandler.getDefault().
 * 
 * For more information on HTTP state management, see <a
 * href="http://www.ietf.org/rfc/rfc2965.txt""><i>RFC&nbsp;2965: HTTP State
 * Management Mechanism</i></a>
 * 
 * @version 1.4, 03/08/09
 * @author Yingxian Wang
 * @since 1.5
 */
public abstract class CookieHandler {
	/**
	 * The system-wide cookie handler that will apply cookies to the request
	 * headers and manage cookies from the response headers.
	 * 
	 * @see setDefault(CookieHandler)
	 * @see getDefault()
	 */
	private static CookieHandler cookieHandler;

	/**
	 * Gets the system-wide cookie handler.
	 * 
	 * @throws SecurityException
	 *             If a security manager has been installed and it denies
	 *             {@link NetPermission}<tt>("getCookieHandler")</tt>
	 * @see #setDefault(CookieHandler)
	 */
	public synchronized static CookieHandler getDefault() {

		return cookieHandler;
	}

	/**
	 * Sets (or unsets) the system-wide cookie handler.
	 * 
	 * Note: non-standard http protocol handlers may ignore this setting.
	 * 
	 * @param cHandler
	 *            The HTTP cookie handler, or <code>null</code> to unset.
	 * @throws SecurityException
	 *             If a security manager has been installed and it denies
	 *             {@link NetPermission}<tt>("setCookieHandler")</tt>
	 * @see #getDefault()
	 */
	public synchronized static void setDefault(CookieHandler cHandler) {
		cookieHandler = cHandler;
	}

	/**
	 * Gets all the applicable cookies from a cookie cache for the specified uri
	 * in the request header.
	 * 
	 * HTTP protocol implementers should make sure that this method is called
	 * after all request headers related to choosing cookies are added, and
	 * before the request is sent.
	 * 
	 * @param uri
	 *            a <code>URI</code> to send cookies to in a request
	 * @param requestHeaders
	 *            - a Map from request header field names to lists of field
	 *            values representing the current request headers
	 * @return an immutable map from state management headers, with field names
	 *         "Cookie" or "Cookie2" to a list of cookies containing state
	 *         information
	 * 
	 * @throws IOException
	 *             if an I/O error occurs
	 * @throws IllegalArgumentException
	 *             if either argument is null
	 * @see #put(URI, Map)
	 */
	public abstract Map<String, List<String>> get(URI uri,
			Map<String, List<String>> requestHeaders) throws IOException;

	/**
	 * Sets all the applicable cookies, examples are response header fields that
	 * are named Set-Cookie2, present in the response headers into a cookie
	 * cache.
	 * 
	 * @param uri
	 *            a <code>URI</code> where the cookies come from
	 * @param responseHeaders
	 *            an immutable map from field names to lists of field values
	 *            representing the response header fields returned
	 * @throws IOException
	 *             if an I/O error occurs
	 * @throws IllegalArgumentException
	 *             if either argument is null
	 * @see #get(URI, Map)
	 */
	public abstract void put(URI uri, Map<String, List<String>> responseHeaders)
			throws IOException;
}