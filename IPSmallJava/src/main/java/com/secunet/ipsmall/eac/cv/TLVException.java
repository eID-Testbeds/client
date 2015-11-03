package com.secunet.ipsmall.eac.cv;

public class TLVException extends RuntimeException {

	private static final long serialVersionUID = -3759270862904628733L;

	public TLVException() {
		super();
	}

	public TLVException( String message, Throwable cause ) {
		super( message, cause );
	}

	public TLVException( String message ) {
		super( message );
	}

	public TLVException( Throwable cause ) {
		super( cause );
	}

}
