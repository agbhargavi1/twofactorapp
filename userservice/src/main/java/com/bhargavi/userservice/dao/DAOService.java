package com.bhargavi.userservice.dao;

import com.bhargavi.userservice.model.Login;

public interface DAOService {

	public void reduceActiveSession(String username);
	public void reduceAllSession(String username, String token);
	public void logout(String userName);
	public void updateCurrentToken(String userName, String access_token);
	public void register(Login login);
}

