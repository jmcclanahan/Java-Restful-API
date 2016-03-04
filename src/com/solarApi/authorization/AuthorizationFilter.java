package com.solarApi.authorization;

import io.jsonwebtoken.SignatureException;

import java.io.IOException;
import java.lang.reflect.Method;
import java.util.List;

import javax.annotation.security.DenyAll;
import javax.annotation.security.PermitAll;
import javax.annotation.security.RolesAllowed;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.ResourceInfo;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.ext.Provider;

import com.solarApi.annotations.AuthenticatedUser;
import com.solarApi.annotations.Authorized;
import com.solarApi.annotations.PermissionsAllowed;
import com.solarApi.user.User;
import com.solarApi.utils.JWTUtil;
import com.solarApi.utils.ResponseUtil;

@Provider
@Authorized
public class AuthorizationFilter implements ContainerRequestFilter {
	private static final String AUTHENTICATION_PROPERTY = "Authentication";
	
	@Context
	private ResourceInfo resourceInfo;
	
	@Inject
	@AuthenticatedUser
	Event<User> userAuthenticatedEvent;

	@Override
	public void filter(ContainerRequestContext requestContext) throws IOException {
		Method method = resourceInfo.getResourceMethod();
		ResponseUtil responseUtil = new ResponseUtil();
		AuthorizationVerifier authVerifier = new AuthorizationVerifier();
		JWTUtil jwtUtil = new JWTUtil();
		User user = new User();
		String jwt = null;

		// Add @PermitAll to all end-points that don't need roles/permissions
		if (!method.isAnnotationPresent(PermitAll.class)) {
			
			// Add @DenyAll to all end-points which shouldn't be accessed
			if (method.isAnnotationPresent(DenyAll.class)) {
				requestContext.abortWith(responseUtil.forbiddenResponse().build());
				return;
			}

			// Get request headers
			final MultivaluedMap<String, String> headers = requestContext
					.getHeaders();

			// Get authorization header
			final List<String> authorization = headers.get(AUTHENTICATION_PROPERTY);
			
			// If no auth header return 404 Unauthorized
			if (authorization == null || authorization.isEmpty()) {
				requestContext.abortWith(responseUtil.unauthorizedResponse().build());
				return;
			}
			
			// Get Token from auth header
			jwt = authorization.get(0).replaceFirst("Bearer ", "");
			
			// Parse the token
			try {
				user = jwtUtil.parseJWT(jwt);
			} catch (SignatureException e) {
				requestContext.abortWith(responseUtil.unauthorizedResponse().build());
				return;
			}
			
			// Set injectable user with the values from the token
			userAuthenticatedEvent.fire(user);

			// Check user roles
			if (method.isAnnotationPresent(RolesAllowed.class)) {
				if (!authVerifier.verifyRoles(method, user)){
					requestContext.abortWith(responseUtil.unauthorizedResponse().build());
					return;
				}
			}
			
			// Check user permissions
			if (method.isAnnotationPresent(PermissionsAllowed.class)) {
				if (!authVerifier.verifyPermissions(method, user)) {
					requestContext.abortWith(responseUtil.unauthorizedResponse().build());
					return;
				}
			}
		}
	}
}