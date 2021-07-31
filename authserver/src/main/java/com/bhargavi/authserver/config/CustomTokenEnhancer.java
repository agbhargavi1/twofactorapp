package com.bhargavi.authserver.config;

import java.util.Date;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ThreadLocalRandom;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.common.DefaultOAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.store.JwtAccessTokenConverter;

import com.bhargavi.authserver.dao.OAuthDAOService;
import com.bhargavi.authserver.model.CustomUser;

import javax.mail.Message;
import javax.mail.PasswordAuthentication;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

public class CustomTokenEnhancer extends JwtAccessTokenConverter {

	@Autowired
	OAuthDAOService oauthDAOService;

	@Override
	public OAuth2AccessToken enhance(OAuth2AccessToken accessToken, OAuth2Authentication authentication) {
		
		CustomUser user = (CustomUser) authentication.getPrincipal();
		String activeSession = oauthDAOService.updateAndGetActiveSessions(user.getUsername());
		String otp = sendEmail(user.getUsername());
		Map<String, Object> info = new LinkedHashMap<>(accessToken.getAdditionalInformation());
		if (user.getId() != null)
			info.put("id", user.getId());
		if (user.getFirstName() != null)
			info.put("firstName", user.getFirstName());
		if (user.getLastName() != null)
			info.put("lastName", user.getLastName());
		if (user.getUsername() != null)
			info.put("userName", user.getUsername());
		
		info.put("activeSession", activeSession);
		info.put("otp", otp);
		info.put("previousToken", user.getPreviousToken());
		
		DefaultOAuth2AccessToken customAccessToken = new DefaultOAuth2AccessToken(accessToken);
		customAccessToken.setAdditionalInformation(info);
		return super.enhance(customAccessToken, authentication);
	}

	private String sendEmail(String toEmailAddress) {

		String otp = null;
		Properties props = new Properties();

		props.put("mail.smtp.auth", "true");
		props.put("mail.smtp.starttls.enable", "true");
		props.put("mail.smtp.host", "smtp.gmail.com");
		props.put("mail.smtp.port", "587");

		try {
			Session session = Session.getInstance(props, new javax.mail.Authenticator() {
				protected PasswordAuthentication getPasswordAuthentication() {
					return new PasswordAuthentication("userid", "password");
				}
			});
			Message msg = new MimeMessage(session);
			msg.setFrom(new InternetAddress("userid", false));
			msg.setRecipients(Message.RecipientType.TO, InternetAddress.parse(toEmailAddress));
			msg.setSubject("OTP from Portal");
			otp = ""+ThreadLocalRandom.current().nextInt(999, 9999 + 1);
			msg.setContent("Your OTP is: "+otp, "text/html");
			msg.setSentDate(new Date());
			Transport.send(msg);
			System.out.println("Email sent successfully!");
		} catch (Exception e) {
			e.printStackTrace();
		}
		return otp;
	}
	
}
