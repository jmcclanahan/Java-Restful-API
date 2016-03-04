package com.solarApi.user;

import java.util.List;
import java.util.Map;

public class User {
	private String username;
	private Map<String, List<String>> rolesAndPermissions;
	
	public User() {	
	}
	
	public User(String username, Map<String, List<String>> rolesAndPermissions) {
		this.username = username;
		this.rolesAndPermissions = rolesAndPermissions;
	}
	
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public Map<String, List<String>> getRolesAndPermissions() {
		return rolesAndPermissions;
	}
	public void setRolesAndPermissions(Map<String, List<String>> rolesAndPermissions) {
		this.rolesAndPermissions = rolesAndPermissions;
	}
}