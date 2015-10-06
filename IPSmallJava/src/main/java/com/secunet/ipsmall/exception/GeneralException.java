package com.secunet.ipsmall.exception;

public class GeneralException extends Exception {

    public GeneralException(String msg) {
        super(msg);
    }
    
    public GeneralException(Throwable thrown) {
        super(thrown);
    }

    private static final long serialVersionUID = -6277962978521845487L;
    
}
