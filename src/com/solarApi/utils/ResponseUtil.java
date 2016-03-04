package com.solarApi.utils;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.ResponseBuilder;

public class ResponseUtil {
	
	public ResponseBuilder okResponse() {
		return Response.status(Response.Status.OK);
	}
	
	public ResponseBuilder forbiddenResponse() {
		return Response.status(Response.Status.FORBIDDEN);
	}

	public ResponseBuilder unauthorizedResponse() {
		return Response.status(Response.Status.UNAUTHORIZED);
	}
	
}