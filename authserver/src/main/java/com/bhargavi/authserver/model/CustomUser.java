package com.bhargavi.authserver.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import org.springframework.security.core.userdetails.User;

@JsonIgnoreProperties
@JsonInclude(Include.NON_NULL)
public class CustomUser extends User {

	private static final long serialVersionUID = 1L;
	private String id;
	private String firstName;
	private String lastName;
	private String activeSession;
	private String previousToken;
	

	public CustomUser(UserEntity user) {
		super(user.getEmailId(), user.getPassword(), user.getGrantedAuthoritiesList());
		this.id = user.getId();
		this.firstName = user.getFirstName();
		this.lastName = user.getLastName();
		this.activeSession = user.getActiveSession();
		this.previousToken = user.getPreviousToken();
		
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

	public String getActiveSession() {
		return activeSession;
	}

	public void setActiveSession(String activeSession) {
		this.activeSession = activeSession;
	}

	public String getPreviousToken() {
		return previousToken;
	}

	public void setPreviousToken(String previousToken) {
		this.previousToken = previousToken;
	}

}

