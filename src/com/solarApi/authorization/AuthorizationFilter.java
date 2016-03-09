package com.solarApi.authorization;

import io.jsonwebtoken.SignatureException;

import java.io.IOException;

import javax.annotation.security.RolesAllowed;
import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.ext.Provider;

import com.solarApi.annotations.AuthenticatedUser;
import com.solarApi.annotations.Authorized;
import com.solarApi.annotations.PermissionsAllowed;
import com.solarApi.base.BaseFilter;
import com.solarApi.user.User;

@Provider
@Authorized
public class AuthorizationFilter extends BaseFilter {

	@Inject
	@AuthenticatedUser
	Event<User> userAuthenticatedEvent;
	
	private User user = new User();

	@Override
	public void auth(ContainerRequestContext requestContext) throws IOException {
		AuthorizationVerifier authVerifier = new AuthorizationVerifier();

		// Get Token from auth header
		setJwt(getAuthorization().get(0).replaceFirst("Bearer ", ""));

		// Parse the token
		try {
			user = jwtUtil.decodeJWT(getJwt(), false);
		} catch (SignatureException e) {
			requestContext.abortWith(responseUtil.unauthorizedResponse()
					.build());
			return;
		}

		// Set injectable user with the values from the token
		userAuthenticatedEvent.fire(user);

		// Check user roles
		if (getMethod().isAnnotationPresent(RolesAllowed.class)) {
			if (!authVerifier.verifyRoles(getMethod(), user)) {
				requestContext.abortWith(responseUtil
						.unauthorizedResponse().build());
				return;
			}
		}

		// Check user permissions
		if (getMethod().isAnnotationPresent(PermissionsAllowed.class)) {
			if (!authVerifier.verifyPermissions(getMethod(), user)) {
				requestContext.abortWith(responseUtil
						.unauthorizedResponse().build());
				return;
			}
		}
	}
}