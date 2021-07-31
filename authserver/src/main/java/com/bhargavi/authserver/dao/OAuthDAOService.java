package com.bhargavi.authserver.dao;

import com.bhargavi.authserver.model.UserEntity;

public interface OAuthDAOService {

	public abstract UserEntity getUserDetails(String username);

	public abstract String updateAndGetActiveSessions(String username);

}

