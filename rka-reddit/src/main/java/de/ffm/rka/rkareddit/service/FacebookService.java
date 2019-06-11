package de.ffm.rka.rkareddit.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.social.connect.Connection;
import org.springframework.social.facebook.api.Facebook;
import org.springframework.social.facebook.api.FacebookLink;
import org.springframework.social.facebook.api.FacebookObject;
import org.springframework.social.facebook.connect.FacebookConnectionFactory;
import org.springframework.social.oauth2.AccessGrant;
import org.springframework.social.oauth2.GrantType;
import org.springframework.social.oauth2.OAuth2Operations;
import org.springframework.social.oauth2.OAuth2Parameters;
import org.springframework.stereotype.Service;

import de.ffm.rka.rkareddit.controller.rest.FbController;
import de.ffm.rka.rkareddit.domain.FbPost;
import de.ffm.rka.rkareddit.domain.Link;

@Service
public class FacebookService {

	@Value("${spring.social.facebook.appId}")
	String facebookAppId;
	@Value("${spring.social.facebook.appSecret}")
	String facebookSecret;

	Facebook facebook;
	String accessToken;
	private static final Logger LOGGER = LoggerFactory.getLogger(FbController.class);

	public String createFacebookAuthorizationURL() {
		FacebookConnectionFactory connectionFactory = new FacebookConnectionFactory(facebookAppId, facebookSecret);
		OAuth2Operations oauthOperations = connectionFactory.getOAuthOperations();
		OAuth2Parameters params = new OAuth2Parameters();
		params.setRedirectUri("http://localhost:5550/jReditt/social/facebookAccess");
		params.setScope("public_profile,email,user_birthday,manage_pages,publish_pages");
		return oauthOperations.buildAuthorizeUrl(GrantType.AUTHORIZATION_CODE,params);
	}

	public String getFbId() {
		return this.facebookAppId;
	}

	public String getfbSec() {
		return this.facebookSecret;
	}

	public String createFacebookAccessToken(String code) {

		FacebookConnectionFactory connectionFactory = new FacebookConnectionFactory(facebookAppId, facebookSecret);
		AccessGrant accessGrant = connectionFactory.getOAuthOperations().exchangeForAccess(code,
				"http://localhost:5550/jReditt/social/facebookAccess", null);
		accessToken = accessGrant.getAccessToken();
		Connection<Facebook> connection = connectionFactory.createConnection(accessGrant);
        // bind the api
        facebook = connection.getApi();

		return accessToken;
	}

	public String postLinkOnFacebook(Link link, FbPost fbPost) {
		
		//Facebook facebook = new FacebookTemplate(facebookService.getfbSec(),facebookService.getFbId());
		FacebookLink fBlink = new FacebookLink("http://www.springsource.org/spring-social", "Spring Social",
			"The Spring Social Project", "Spring Social is an extension to Spring to enable "
						+ "applications to connect with service providers.");
		fbPost.setFbId(facebook.feedOperations().postLink("Awesome", fBlink));
		
		return "success";
	}
	
	public String getAccessToken() {
		return accessToken;

	}

}
