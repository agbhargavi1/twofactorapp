package com.bhargavi.userservice.model;

public class AccessTokenMapper {

	private String access_token;
	private String id;
	private String firstName;
	private String lastName;
	private String userName;
	private String previousToken;

	public String getAccess_token() {
		return access_token;
	}

	public void setAccess_token(String access_token) {
		this.access_token = access_token;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getFirstName() {
		return firstName;
	}

	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}

	public String getLastName() {
		return lastName;
	}

	public void setLastName(String lastName) {
		this.lastName = lastName;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getPreviousToken() {
		return previousToken;
	}

	public void setPreviousToken(String previousToken) {
		this.previousToken = previousToken;
	}
}
