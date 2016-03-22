package com.solarApi.refreshToken;

import java.util.List;

import javax.annotation.security.RolesAllowed;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.solarApi.annotations.GrantRefresh;
import com.solarApi.utils.ResponseUtil;

@Path("/public/refresh")
@GrantRefresh
public class GrantRefreshToken {
	
	@POST
	@RolesAllowed("SYSTEM")
	@Produces(MediaType.APPLICATION_JSON)
	public Response grantRefreshToken(@Context HttpHeaders headers) {
		ResponseUtil responseUtil = new ResponseUtil();
		List<String> authHeaders = headers.getRequestHeader(HttpHeaders.AUTHORIZATION);
		return responseUtil.okResponse().header("AUTHORIZATION", authHeaders.get(0)).build();
	}
}