package com.bhargavi.authserver.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessResourceFailureException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.bhargavi.authserver.config.CustomOAuth2Exception;
import com.bhargavi.authserver.dao.OAuthDAOService;
import com.bhargavi.authserver.model.CustomUser;
import com.bhargavi.authserver.model.UserEntity;


@Service
public class CustomUserDetailsService implements UserDetailsService {

	@Autowired
	OAuthDAOService oauthDaoService;

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

		UserEntity userEntity = null;
		try {
			userEntity = oauthDaoService.getUserDetails(username);
			if (userEntity != null && userEntity.getId() != null && !"".equalsIgnoreCase(userEntity.getId())) {
				CustomUser customUser = new CustomUser(userEntity);
				return customUser;
			} else {
				throw new UsernameNotFoundException("User " + username + " was not found in the database");
			}
		} catch (DataAccessResourceFailureException e) {
			e.printStackTrace();
			throw new CustomOAuth2Exception("Could not send query.Connection reset by peer.DataAccessResourceFailureException May have occurred",e);
		} catch (Exception e) {
			e.printStackTrace();
			throw new UsernameNotFoundException("User " + username + " was not found in the database");
		}
	}
}
