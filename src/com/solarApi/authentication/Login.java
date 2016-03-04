package com.solarApi.authentication;

import java.util.List;

import javax.annotation.security.PermitAll;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.solarApi.annotations.Authenticated;
import com.solarApi.utils.ResponseUtil;

@Path("/login")
@Authenticated
public class Login {
	
	@POST
	@PermitAll
	@Produces(MediaType.APPLICATION_JSON)
	public Response login(@Context HttpHeaders headers) {
		ResponseUtil responseUtil = new ResponseUtil();
		List<String> authHeaders = headers.getRequestHeader(HttpHeaders.AUTHORIZATION);
		return responseUtil.okResponse().header("AUTHENTICATION", authHeaders.get(0)).build();
	}
}