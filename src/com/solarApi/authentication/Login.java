package com.solarApi.authentication;

import java.util.List;

import javax.inject.Inject;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.google.gson.Gson;
import com.solarApi.annotations.Authenticated;
import com.solarApi.annotations.AuthenticatedUser;
import com.solarApi.user.User;
import com.solarApi.utils.ResponseUtil;

@Path("/public/login")
@Authenticated
public class Login {
	
	@Inject
	@AuthenticatedUser
	private User user;
	
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	public Response login(@Context HttpHeaders headers) {
		System.out.println("INSIDE LOGIN");
		Gson gson = new Gson();
		ResponseUtil responseUtil = new ResponseUtil();
		List<String> authHeaders = headers.getRequestHeader(HttpHeaders.AUTHORIZATION);
		return responseUtil.okResponse().entity(gson.toJson("Login Successful!")).header("AUTHORIZATION", authHeaders).build();
	}
}