package org.spo.fw.exception;

public class TestResourceServerException extends SPOException {

	/**
	 * 
	 */
	private static final long serialVersionUID = -1004017273963002014L;

	public TestResourceServerException(String  message){
		//wrappedException=e;
		this.message=message;

	}
	
	public TestResourceServerException(Throwable e){
		wrappedException=e;
		message="Connection to TestResource server failed";

	}
	
}
