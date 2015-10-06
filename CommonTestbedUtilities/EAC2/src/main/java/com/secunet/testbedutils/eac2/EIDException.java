package com.secunet.testbedutils.eac2;

public class EIDException extends Exception {

	/**
	 * random uid
	 */
	private static final long serialVersionUID = -6977321446387888162L;

	public EIDException() {
		super();
	}

	public EIDException( String message, Throwable cause ) {
		super( message, cause );
	}

	public EIDException( String message ) {
		super( message );
	}

	public EIDException( Throwable cause ) {
		super( cause );
	}

}
