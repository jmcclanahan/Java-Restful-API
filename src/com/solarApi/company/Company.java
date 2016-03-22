package com.solarApi.company;

import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.google.gson.Gson;
import com.solarApi.annotations.AuthenticatedUser;
import com.solarApi.annotations.Authorized;
import com.solarApi.annotations.PermissionsAllowed;
import com.solarApi.user.User;
import com.solarApi.utils.ResponseUtil;

@Path("/secured/company")
@Authorized
public class Company {
	
	@Inject
	@AuthenticatedUser
	private User user;
	
	@Path("companyName")
	@RolesAllowed({"ADMIN", "USER"})
	@PermissionsAllowed({"CAN_VIEW", "CAN_EDIT"})
	@GET
	@Produces(MediaType.APPLICATION_JSON)
	public Response getCompanyName() {
		// Check to see that the user is properly set and injected
		System.out.println("USER(CompanyName) - " + user.getUsername());
		Gson gson = new Gson();
		ResponseUtil responseUtil = new ResponseUtil();
		return responseUtil.okResponse().entity(gson.toJson("ABC Company")).build();
	}
}