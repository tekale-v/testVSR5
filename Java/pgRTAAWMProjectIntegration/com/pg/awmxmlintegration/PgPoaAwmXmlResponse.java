package com.pg.awmxmlintegration;

public class PgPoaAwmXmlResponse {
	
	String code;
	String message;
	
	public PgPoaAwmXmlResponse(String errorCode,String errorMessage) {
		this.code = errorCode;
		this.message = errorMessage;
	}

}
