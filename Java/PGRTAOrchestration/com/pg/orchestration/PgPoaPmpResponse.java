package com.pg.orchestration;

public class PgPoaPmpResponse {
	
	String code;
	String message;
	
	public PgPoaPmpResponse(String errorCode,String errorMessage) {
		this.code = errorCode;
		this.message = errorMessage;
	}

}
