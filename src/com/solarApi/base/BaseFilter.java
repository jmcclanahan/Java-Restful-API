package com.solarApi.base;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.List;

import javax.annotation.security.DenyAll;
import javax.annotation.security.PermitAll;
import javax.inject.Inject;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.container.ContainerRequestFilter;
import javax.ws.rs.container.ResourceInfo;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MultivaluedMap;

import com.solarApi.utils.ResponseUtil;

public class BaseFilter implements ContainerRequestFilter {
	public static final String AUTHENTICATION_PROPERTY = "Authentication";
	public static final String AUTHORIZATION_PROPERTY = "Authorization";
	
	@Context
	private ResourceInfo resourceInfo;
	
	@Inject
	private ResponseUtil responseUtil;
	
	private Method method;
	private MultivaluedMap<String, String> headers;
	private List<String> authorization;
	private String jwt;
	
	public void auth(ContainerRequestContext requestContext) throws IOException {}

	@Override
	public void filter(ContainerRequestContext requestContext) throws IOException {
		method = getResourceInfo().getResourceMethod();
		
		// Add @PermitAll to all end-points that don't need roles/permissions
		if (!method.isAnnotationPresent(PermitAll.class)) {

			// Add @DenyAll to all end-points which shouldn't be accessed
			if (method.isAnnotationPresent(DenyAll.class)) {
				requestContext.abortWith(responseUtil.forbiddenResponse()
						.build());
				return;
			}
			
			// Get request headers
			headers = requestContext.getHeaders();

			// Get authorization header
			authorization = headers.get(AUTHORIZATION_PROPERTY);

			// If no auth header return 404 Unauthorized
			if (authorization == null || authorization.isEmpty()) {
				requestContext.abortWith(responseUtil
						.unauthorizedResponse().build());
				return;
			}
			
			auth(requestContext);
		}
	}

	public ResourceInfo getResourceInfo() {
		return resourceInfo;
	}

	public Method getMethod() {
		return method;
	}

	public void setMethod(Method method) {
		this.method = method;
	}

	public MultivaluedMap<String, String> getHeaders() {
		return headers;
	}

	public void setHeaders(MultivaluedMap<String, String> headers) {
		this.headers = headers;
	}

	public List<String> getAuthorization() {
		return authorization;
	}

	public void setAuthorization(List<String> authorization) {
		this.authorization = authorization;
	}

	public String getJwt() {
		return jwt;
	}

	public void setJwt(String jwt) {
		this.jwt = jwt;
	}
}