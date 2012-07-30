package us.monoid.web;

public class RestyAuthenticationException extends RestyIOException{
	/**
	 * 
	 */
	private static final long serialVersionUID = -3856673810199189288L;

	public RestyAuthenticationException(String msg,String requestMethod) {
		super(msg,requestMethod,401,"Authentication needed");
	}
}
