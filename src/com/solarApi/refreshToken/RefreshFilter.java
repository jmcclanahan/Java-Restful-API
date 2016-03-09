package com.solarApi.refreshToken;

import javax.enterprise.event.Event;
import javax.inject.Inject;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.ext.Provider;

import com.solarApi.annotations.AuthenticatedUser;
import com.solarApi.annotations.GrantRefresh;
import com.solarApi.base.BaseFilter;
import com.solarApi.user.User;

import io.jsonwebtoken.SignatureException;

@Provider
@GrantRefresh
public class RefreshFilter extends BaseFilter {
	
	@Inject
	@AuthenticatedUser
	Event<User> userAuthenticatedEvent;
	
	private User user = new User();
	
	@Override
	public void auth(ContainerRequestContext requestContext) {
		setJwt(getAuthorization().get(0).replaceFirst("Refresh ", ""));

		try {
			user = jwtUtil.decodeJWT(getJwt(), true);
		} catch (SignatureException e) {
			requestContext.abortWith(responseUtil.unauthorizedResponse()
					.build());
			return;
		}

		setJwt(jwtUtil.createAccessJWT(user, timeCalcUtil.minutesFromNow(30)));
		System.out.println("Creating new JWT - " + getJwt());

		getHeaders().remove(AUTHORIZATION_PROPERTY);
		getHeaders().add(AUTHORIZATION_PROPERTY, "Bearer " + getJwt());
	}
}