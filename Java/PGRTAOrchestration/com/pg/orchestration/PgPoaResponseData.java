package com.pg.orchestration;

public class PgPoaResponseData {
	String code;
	String message;
	public PgPoaResponseData(String errorCode,String errorMessage) {	
		this.code = errorCode;
		this.message = errorMessage;
	}
	
}
