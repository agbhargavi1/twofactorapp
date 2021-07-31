package com.bhargavi.authserver.config;

import org.springframework.security.oauth2.common.exceptions.OAuth2Exception;

public class CustomOAuth2Exception extends OAuth2Exception {

	private static final long serialVersionUID = 1L;

	public CustomOAuth2Exception(String msg, Throwable t) {
		super(msg, t);
	}

	@Override
	public String getOAuth2ErrorCode() {
		return "mysql_connect";
	}

	@Override
	public int getHttpErrorCode() {
		return 500;
	}

}

