package us.monoid.web;

import java.io.IOException;

public class RestyIOException extends IOException {
	/**
	 * 
	 */
	private static final long serialVersionUID = 4050075956034600591L;
	private String requestMethod;
	private int responseCode;
	private String responseMessage;



	public RestyIOException(IOException ex,String requestMethod, int responseCode,
			String responseMessage) {
		this(ex.getMessage(),requestMethod,responseCode,responseMessage);
	}
	
	public RestyIOException(IOException ex) {
		this(ex.getMessage());
	}
	
	public RestyIOException(String requestMethod, int responseCode,
			String responseMessage) {
		super();
		this.requestMethod = requestMethod;
		this.responseCode = responseCode;
		this.responseMessage = responseMessage;
	}
	
	public RestyIOException(String msg ,String requestMethod, int responseCode,
			String responseMessage) {
		super(msg);
		this.requestMethod = requestMethod;
		this.responseCode = responseCode;
		this.responseMessage = responseMessage;
	}


	public RestyIOException(String msg) {
		super(msg);
	}
	
	public RestyIOException() {
		super();
	}

	public String getRequestMethod() {
		return requestMethod;
	}

	public int getResponseCode() {
		return responseCode;
	}

	public String getResponseMessage() {
		return responseMessage;
	}

}
