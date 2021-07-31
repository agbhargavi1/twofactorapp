package com.bhargavi.authserver.dao;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import java.sql.ResultSet;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Repository;

import com.bhargavi.authserver.model.UserEntity;

@Repository
public class OAuthDAOServiceImpl implements OAuthDAOService {

	@Autowired
	private JdbcTemplate jdbcTemplate;

	private static final String USERS_SELECT_SQL = "SELECT * FROM TBL_USER WHERE EMAIL_ID=?";

	public UserEntity getUserDetails(String username) {
		Collection<GrantedAuthority> grantedAuthoritiesList = new ArrayList<>();
		List<UserEntity> list = jdbcTemplate.query(USERS_SELECT_SQL, new String[] { username },
				(ResultSet rs, int rowNum) -> {
					UserEntity user = new UserEntity();
					user.setEmailId(username);
					user.setFirstName(rs.getString("FIRST_NAME"));
					user.setLastName(rs.getString("LAST_NAME"));
					user.setId(rs.getString("ID"));

					user.setPassword(rs.getString("PASSWORD"));
					user.setPreviousToken(rs.getString("PREVIOUS_TOKEN"));
					return user;
				});

		if (!list.isEmpty()) {
			GrantedAuthority grantedAuthority = new SimpleGrantedAuthority("APP_2FA_USER");
			grantedAuthoritiesList.add(grantedAuthority);
			list.get(0).setGrantedAuthoritiesList(grantedAuthoritiesList);
			return list.get(0);

		}
		return null;
	}

	@Override
	public String updateAndGetActiveSessions(String username) {
		try {
			jdbcTemplate.update("UPDATE TBL_USER SET ACTIVE_SESSION=ACTIVE_SESSION+1 WHERE EMAIL_ID=?",
					new Object[] { username });
			return jdbcTemplate.queryForObject("SELECT ACTIVE_SESSION FROM TBL_USER WHERE EMAIL_ID=?",
					new Object[] { username }, String.class);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
}
