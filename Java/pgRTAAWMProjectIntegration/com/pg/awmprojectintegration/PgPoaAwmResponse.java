package com.pg.awmprojectintegration;

public class PgPoaAwmResponse {
	
	String code;
	String message;
	
	public PgPoaAwmResponse(String errorCode,String errorMessage) {
		this.code = errorCode;
		this.message = errorMessage;
	}

}
