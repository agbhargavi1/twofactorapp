package com.bhargavi.userservice.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.provider.authentication.OAuth2AuthenticationDetails;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import com.bhargavi.userservice.dao.DAOService;
import com.bhargavi.userservice.model.AccessTokenMapper;
import com.bhargavi.userservice.model.Login;

@RestController
public class UserRestController {

	@Autowired
	DAOService daoService;

	@RequestMapping(value = "/reduceactivesession", method = RequestMethod.POST)
	public ResponseEntity<Object> reduceactivesession() {

		AccessTokenMapper accessTokenMapper = (AccessTokenMapper) ((OAuth2AuthenticationDetails) SecurityContextHolder
				.getContext().getAuthentication().getDetails()).getDecodedDetails();

		daoService.reduceActiveSession(accessTokenMapper.getUserName());
		return new ResponseEntity<>(HttpStatus.OK);
	}
	
	@RequestMapping(value = "/register", method = RequestMethod.POST)
	public ResponseEntity<Object> register(@RequestBody Login login) {
		daoService.register(login);
		return new ResponseEntity<>(HttpStatus.CREATED);
	}

	@RequestMapping(value = "/logoutallprevioussession", method = RequestMethod.POST)
	public ResponseEntity<Object> logoutallprevioussession(@RequestHeader("jti") String token) {

		AccessTokenMapper accessTokenMapper = (AccessTokenMapper) ((OAuth2AuthenticationDetails) SecurityContextHolder
				.getContext().getAuthentication().getDetails()).getDecodedDetails();

		daoService.reduceAllSession(accessTokenMapper.getUserName(),token);
		return new ResponseEntity<>(HttpStatus.OK);
	}
	
	@RequestMapping(value = "/logout", method = RequestMethod.POST)
	public ResponseEntity<Object> logout() {

		AccessTokenMapper accessTokenMapper = (AccessTokenMapper) ((OAuth2AuthenticationDetails) SecurityContextHolder
				.getContext().getAuthentication().getDetails()).getDecodedDetails();

		daoService.logout(accessTokenMapper.getUserName());
		return new ResponseEntity<>(HttpStatus.OK);
	}
	
	@RequestMapping(value = "/updateCurrentToken", method = RequestMethod.POST)
	public ResponseEntity<Object> updateCurrentToken(@RequestHeader("jti") String token) {

		AccessTokenMapper accessTokenMapper = (AccessTokenMapper) ((OAuth2AuthenticationDetails) SecurityContextHolder
				.getContext().getAuthentication().getDetails()).getDecodedDetails();
		daoService.updateCurrentToken(accessTokenMapper.getUserName(), token);
		return new ResponseEntity<>(HttpStatus.OK);
	}
	
	
}
