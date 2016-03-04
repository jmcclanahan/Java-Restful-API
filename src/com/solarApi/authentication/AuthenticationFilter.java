package com.solarApi.authentication;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.ResourceInfo;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.Provider;

import org.jboss.resteasy.util.Base64;

import com.solarApi.annotations.Authenticated;
import com.solarApi.user.User;
import com.solarApi.utils.JWTUtil;
import com.solarApi.utils.ResponseUtil;

@Provider
@Authenticated
public class AuthenticationFilter implements ContainerRequestFilter {
	private static final String AUTHENTICATION_SCHEME = "Basic";
	private static final String AUTHORIZATION_PROPERTY = "Authorization";

	@Context
	private ResourceInfo resourceInfo;

	@Override
	public void filter(ContainerRequestContext requestContext) {
		ResponseUtil responseUtil = new ResponseUtil();
		JWTUtil jwtUtil = new JWTUtil();
		String usernameAndPassword = null;

		// Get request headers
		final MultivaluedMap<String, String> headers = requestContext.getHeaders();

		// Get authorization header
		final List<String> authorization = headers.get(AUTHORIZATION_PROPERTY);
		
		// If no auth header return 404 Unauthorized
		if (authorization == null || authorization.isEmpty()) {
			requestContext.abortWith(responseUtil.unauthorizedResponse().build());
			return;
		}

		// Get encoded username and password
		final String encodedUsernamePassword = authorization.get(0).replaceFirst(AUTHENTICATION_SCHEME + " ", "");

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
		Map<String, List<String>> rolesAndPermissions = new HashMap<>();
		List<String> permissions = new ArrayList<>();
		permissions.add("CAN_EDIT");
		permissions.add("CAN_VIEW");
		rolesAndPermissions.put("ADMIN", permissions);
		user.setUsername(username);
		user.setRolesAndPermissions(rolesAndPermissions);
		
		// Remove Basic Auth Header and add JWT token to be
		// passed back to the front-end
		headers.remove(AUTHORIZATION_PROPERTY);
		headers.add(AUTHORIZATION_PROPERTY,"Bearer " + jwtUtil.createJWT(user, 10000000));
	}
}