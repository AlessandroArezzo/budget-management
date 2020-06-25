package com.balance.exception;

public class ClientNotFoundException extends DatabaseException{

    private static final long serialVersionUID = 1L;

    public ClientNotFoundException(String message) {
        super(message);
    }
	
	
}
