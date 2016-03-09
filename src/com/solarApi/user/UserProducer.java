package com.solarApi.user;


import javax.enterprise.context.RequestScoped;
import javax.enterprise.event.Observes;
import javax.enterprise.inject.Produces;

import com.solarApi.annotations.AuthenticatedUser;

@RequestScoped
public class UserProducer {
	
	@Produces
	@RequestScoped
	@AuthenticatedUser
	private User user = new User();
	
	public void handleAuthenticationEvent(@Observes User user) {
		// Set the User to be injected
		System.out.println("AUTH EVENT - " + user.getUsername());
		this.user = user;
	}
}