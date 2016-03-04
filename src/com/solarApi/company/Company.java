package com.solarApi.company;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.annotation.security.RolesAllowed;
import javax.inject.Inject;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.solarApi.annotations.AuthenticatedUser;
import com.solarApi.annotations.Authorized;
import com.solarApi.annotations.PermissionsAllowed;
import com.solarApi.user.User;

@Path("/company")
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
	public String getCompanyName() {
		String companyName = null;
		
		// Check to see that the user is properly set and injected
		System.out.println("USER(CompanyName) - " + user.getUsername());
		
		// TODO This will be replaced with the database you use
		try {
	          Context ctx = new InitialContext();
	          DataSource ds = (DataSource)ctx.lookup("java:comp/env/jdbc/solardb");
	          Connection conn = ds.getConnection();
	          Statement stat = conn.createStatement();
	          System.out.println("Running Query!");
	          ResultSet rs = stat.executeQuery("select name from companies");
	          companyName = rs.getString(1);
	          System.out.println("Company Name - " + companyName);
	    } catch (SQLException se) {
	          System.out.println(se.toString());
	    } catch (NamingException ne) {
	          System.out.println(ne.toString());
	    }
		
		return companyName;
	}
}