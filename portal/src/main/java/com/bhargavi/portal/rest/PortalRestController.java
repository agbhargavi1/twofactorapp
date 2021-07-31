package com.bhargavi.portal.rest;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import com.bhargavi.portal.model.Login;
import com.bhargavi.portal.model.OTP;
import com.bhargavi.portal.model.ResponseMessage;
import com.bhargavi.portal.model.Token;

@RestController
public class PortalRestController {

	@Autowired
	RestTemplate restTemplate;


	@RequestMapping(value = "/formregister", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Object> formregister(@RequestBody Login login) {

		if (!StringUtils.hasLength(login.getUsername()) || !StringUtils.hasLength(login.getPassword())
				|| !StringUtils.hasLength(login.getFirstName()) || !StringUtils.hasLength(login.getLastName())) {
			return new ResponseEntity<>(new Error("Please Provide all mandatory fields"), HttpStatus.BAD_REQUEST);
		}
		HttpHeaders headers = new HttpHeaders();
		HttpEntity<Login> request = new HttpEntity<>(login, headers);
		try {
			restTemplate.postForEntity("http://userservice:8082/register", request, Object.class);
		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseEntity<>(new ResponseMessage("Exception. Please check with administrator", null),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return new ResponseEntity<>(new ResponseMessage(null, "success"),
				HttpStatus.OK);
	}
	
	
	@RequestMapping(value = "/login", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Object> login(@RequestBody Login login, HttpSession session) {

		if (session.getAttribute("collectOTP") != null) {
			return new ResponseEntity<>(new ResponseMessage("Current Session, waiting for OTP", null),
					HttpStatus.BAD_REQUEST);
		}

		if (!StringUtils.hasLength(login.getUsername()) || !StringUtils.hasLength(login.getPassword())) {
			return new ResponseEntity<>(new Error("Please Provide both Username and Password"), HttpStatus.BAD_REQUEST);
		}

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
		headers.setBasicAuth("bhargavi", "bhargavi");
		MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
		map.add("username", login.getUsername());
		map.add("password", login.getPassword());
		map.add("grant_type", "password");

		HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<MultiValueMap<String, String>>(map, headers);
		try {
			ResponseEntity<Token> token = restTemplate.postForEntity("http://authserver:8081/oauth/token", request,
					Token.class);
			session.setAttribute("collectOTP", token.getBody());
			return new ResponseEntity<>(new ResponseMessage(null, "collectOTP"), HttpStatus.OK);
		} catch (HttpClientErrorException e) {
			if (StringUtils.hasLength(e.getResponseBodyAsString())
					&& e.getResponseBodyAsString().contains("Bad credentials")) {
				return new ResponseEntity<>(new ResponseMessage("Invalid credentials", null), HttpStatus.BAD_REQUEST);
			}

		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseEntity<>(new ResponseMessage("Exception. Please check with administrator", null),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
		return null;
	}
	
	
	private void removeSessionAttributes(HttpSession session) {
		session.removeAttribute("waitingForActiveSessionResponse");
		session.removeAttribute("collectOTP");
	}
	
	
	@RequestMapping(value = "/logout", method = RequestMethod.POST)
	public ResponseEntity<Object> logout(@RequestHeader("Authorization") String token, @RequestParam(value = "isDeleteEntry", defaultValue = "") String isDeleteEntry) {
		
		if(isDeleteEntry.equals("true")) {
			HttpHeaders headers = new HttpHeaders();
			headers.setBearerAuth(token.substring(7));
			HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<MultiValueMap<String, String>>(null,
					headers);
			try {
				restTemplate.postForEntity("http://userservice:8082/reduceactivesession", request, String.class);
			} catch (Exception e) {
				e.printStackTrace();
				return new ResponseEntity<>(new ResponseMessage("Exception. Please check with administrator", null),
						HttpStatus.INTERNAL_SERVER_ERROR);
			}
		}
		return new ResponseEntity<>(new ResponseMessage(null, "success"), HttpStatus.OK);
	}
	
	@RequestMapping(value = "/cancel", method = RequestMethod.POST)
	public ResponseEntity<Object> cancel(HttpSession session) {
		Token token = (Token) session.getAttribute("collectOTP");
		if (token == null) {
			return new ResponseEntity<>(new ResponseMessage("Please login first.", null), HttpStatus.BAD_REQUEST);
		}
		HttpHeaders headers = new HttpHeaders();
		headers.setBearerAuth(token.getAccess_token());
		HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<MultiValueMap<String, String>>(null,
				headers);
		try {
			restTemplate.postForEntity("http://userservice:8082/reduceactivesession", request, String.class);
		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseEntity<>(new ResponseMessage("Exception. Please check with administrator", null),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
		removeSessionAttributes(session);
		return new ResponseEntity<>(new ResponseMessage(null, "success"), HttpStatus.OK);
	}
	
	@RequestMapping(value = "/proceed", method = RequestMethod.POST)
	public ResponseEntity<Object> proceed(HttpSession session) {
		Token token = (Token) session.getAttribute("collectOTP");
		if (token == null) {
			return new ResponseEntity<>(new ResponseMessage("Please login first.", null), HttpStatus.BAD_REQUEST);
		}
		HttpHeaders headers = new HttpHeaders();
		headers.setBearerAuth(token.getAccess_token());
		headers.set("jti", token.getJti());
		HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<MultiValueMap<String, String>>(null,
				headers);
		try {
			restTemplate.postForEntity("http://userservice:8082/logoutallprevioussession", request, String.class);
		} catch (Exception e) {
			e.printStackTrace();
			return new ResponseEntity<>(new ResponseMessage("Exception. Please check with administrator", null),
					HttpStatus.INTERNAL_SERVER_ERROR);
		}
		removeSessionAttributes(session);
		token.setOtp(null);
		return new ResponseEntity<>(token, HttpStatus.OK);
	}

	@RequestMapping(value = "/validateOTP", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Object> validateOTP(@RequestBody OTP otp, HttpSession session) {

		Token token = (Token) session.getAttribute("collectOTP");

		if (token == null) {
			return new ResponseEntity<>(new ResponseMessage("Please login first to validate the OTP", null),
					HttpStatus.BAD_REQUEST);
		}

		if (token.getOtp().equals(otp.getOtp())) {
			if (Integer.parseInt(token.getActiveSession()) > 1) {
				session.setAttribute("waitingForActiveSessionResponse", "true");
				return new ResponseEntity<>(new ResponseMessage("You have more than one session active", null),
						HttpStatus.BAD_REQUEST);
			}
			token.setOtp(null);
			token.setPreviousToken(null);
			session.removeAttribute("collectOTP");
			
			HttpHeaders headers = new HttpHeaders();
			headers.setBearerAuth(token.getAccess_token());
			headers.set("jti", token.getJti());
			HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<MultiValueMap<String, String>>(null,
					headers);
			try {
				restTemplate.postForEntity("http://userservice:8082/updateCurrentToken", request, String.class);
			} catch (Exception e) {
				e.printStackTrace();
				return new ResponseEntity<>(new ResponseMessage("Exception. Please check with administrator", null),
						HttpStatus.INTERNAL_SERVER_ERROR);
			}
			return new ResponseEntity<>(token, HttpStatus.OK);
		} 
		return new ResponseEntity<>(new ResponseMessage("Invalid OTP", null),
				HttpStatus.BAD_REQUEST);
	}
}