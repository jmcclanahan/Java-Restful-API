package com.solarApi.authentication;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import javax.inject.Inject;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.ext.Provider;

import org.jboss.resteasy.util.Base64;

import com.solarApi.annotations.Authenticated;
import com.solarApi.base.BaseFilter;
import com.solarApi.user.User;
import com.solarApi.utils.JWTUtil;
import com.solarApi.utils.ResponseUtil;
import com.solarApi.utils.TimeCalcUtil;

@Provider
@Authenticated
public class AuthenticationFilter extends BaseFilter {
	public static final String AUTHENTICATION_SCHEME = "Basic";
	
	@Inject
	private ResponseUtil responseUtil;
	@Inject
	private TimeCalcUtil timeCalcUtil;
	@Inject
	private JWTUtil jwtUtil;
	
	@Override
	public void auth(ContainerRequestContext requestContext) {
		String usernameAndPassword = null;

		// Get encoded username and password
		final String encodedUsernamePassword = getAuthorization().get(0).replaceFirst(AUTHENTICATION_SCHEME + " ", "");

		// Decode username and password
		try {
			usernameAndPassword = new String(Base64.decode(encodedUsernamePassword.getBytes()));
		} catch (IOException e) {
			requestContext.abortWith(responseUtil.unauthorizedResponse().build());
			return;
		}

		// Split username and password tokens
		final StringTokenizer tokenizer = new StringTokenizer(usernameAndPassword, ":");
		final String username = tokenizer.nextToken();
		final String password = tokenizer.nextToken();

		/*
		 * TODO method here to authenticate user against database.
		 * For now we hard-code some roles and permissions
		 */
		User user = new User();
		user.setUsername(username);
		
		// Remove Basic Auth Header and add JWT token to be passed back to the front-end
		// Access token is set to expire in 30 minutes
		// Refresh token is set to expire at midnight of the day of creation
		List<String> jwts = new ArrayList<String>();
		jwts.add("Bearer " + jwtUtil.createAccessJWT(user, timeCalcUtil.minutesFromNow(30)));
		//jwts.add("Refresh " + jwtUtil.createRefreshJWT(user, timeCalcUtil.minutesFromNow(1)));
		jwts.add("Refresh " + jwtUtil.createRefreshJWT(user, timeCalcUtil.getMidnightDate()));
		
		getHeaders().remove(AUTHORIZATION_PROPERTY);
		getHeaders().addAll(AUTHORIZATION_PROPERTY, jwts);
	}
}