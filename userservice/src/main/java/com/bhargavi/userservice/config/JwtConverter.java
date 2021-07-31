package com.bhargavi.userservice.config;

import java.util.Map;

import org.springframework.boot.autoconfigure.security.oauth2.resource.JwtAccessTokenConverterConfigurer;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.DefaultAccessTokenConverter;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;
import org.springframework.stereotype.Component;

import com.bhargavi.userservice.model.AccessTokenMapper;

@Component
public class JwtConverter extends DefaultAccessTokenConverter implements JwtAccessTokenConverterConfigurer {

	@Override
	public void configure(JwtAccessTokenConverter converter) {
		converter.setAccessTokenConverter(this);
	}

	@Override
	public OAuth2Authentication extractAuthentication(Map<String, ?> map) {
		OAuth2Authentication auth = super.extractAuthentication(map);
		com.bhargavi.userservice.model.AccessTokenMapper details = new AccessTokenMapper();

		if (map.get("id") != null)
			details.setId((String) map.get("id"));

		if (map.get("firstName") != null)
			details.setFirstName((String) map.get("firstName"));

		if (map.get("lastName") != null)
			details.setLastName((String) map.get("lastName"));

		if (map.get("userName") != null)
			details.setUserName((String) map.get("userName"));
		
		if (map.get("access_token") != null)
			details.setAccess_token((String) map.get("access_token"));

		auth.setDetails(details);
		return auth;
	}
}
