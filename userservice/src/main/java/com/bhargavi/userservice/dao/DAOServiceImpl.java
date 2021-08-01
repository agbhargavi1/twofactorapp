package com.bhargavi.userservice.dao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Repository;

import com.bhargavi.userservice.model.Login;

@Repository
public class DAOServiceImpl implements DAOService {

	@Autowired
	private JdbcTemplate jdbcTemplate;

	@Override
	public void reduceActiveSession(String username) {
		try {
			jdbcTemplate.update("UPDATE TBL_USER SET ACTIVE_SESSION=ACTIVE_SESSION-1 WHERE EMAIL_ID=?",
					new Object[] { username });

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	@Override
	public void reduceAllSession(String username, String token) {
		try {
			jdbcTemplate.update("UPDATE TBL_USER SET ACTIVE_SESSION=1, PREVIOUS_TOKEN=? WHERE EMAIL_ID=?", new Object[] { token, username });

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	@Override
	public void logout(String userName) {
		try {
			jdbcTemplate.update("UPDATE TBL_USER SET ACTIVE_SESSION=0, PREVIOUS_TOKEN=NULL WHERE EMAIL_ID=?", new Object[] { userName });

		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	@Override
	public void updateCurrentToken(String userName, String access_token) {
		
		System.out.println(userName);
		System.out.println(access_token);
		
		try {
			jdbcTemplate.update("UPDATE TBL_USER SET PREVIOUS_TOKEN=? WHERE EMAIL_ID=?", new Object[] { access_token, userName });

		} catch (Exception e) {
			e.printStackTrace();
		}

		
	}

	@Override
	public void register(Login login) {
		try {
			jdbcTemplate.update("INSERT INTO TBL_USER (FIRST_NAME, LAST_NAME, EMAIL_ID, "
					+ "PASSWORD,ACTIVE_SESSION) VALUES (?,?,?,?,?)", new Object[] { login.getFirstName(), login.getLastName(), login.getUsername(), new BCryptPasswordEncoder().encode(login.getPassword()),0});

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
