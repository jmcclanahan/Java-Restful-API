package com.solarApi.authorization;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.security.RolesAllowed;

import com.solarApi.annotations.PermissionsAllowed;
import com.solarApi.user.User;

public class AuthorizationVerifier {

	public boolean verifyRoles(Method method, User user) {
		boolean isAllowed = false;
		RolesAllowed rolesAnnotation = method.getAnnotation(RolesAllowed.class);
		Set<String> rolesSet = new HashSet<String>(Arrays.asList(rolesAnnotation.value()));
		
		// Is user allowed?
		for (String role : user.getRolesAndPermissions().keySet()) {
			// verify user role
			if (rolesSet.contains(role)) {
				isAllowed = true;
				break;
			}
		}

		return isAllowed;
	}
	
	public boolean verifyPermissions(Method method, User user) {
		boolean isAllowed = false;
		PermissionsAllowed permissionsAnnotation = method.getAnnotation(PermissionsAllowed.class);
		Set<String> permissionsSet = new HashSet<String>(Arrays.asList(permissionsAnnotation.value()));

		// Is user allowed?
		for (List<String> permissionList : user.getRolesAndPermissions().values()) {
			
			for (String permission : permissionList) {
				// verify user permission
				if (permissionsSet.contains(permission)) {
					isAllowed = true;
					break;
				}
			}
			
			if (isAllowed) {
				break;
			}
		}

		return isAllowed;
	}
	
}