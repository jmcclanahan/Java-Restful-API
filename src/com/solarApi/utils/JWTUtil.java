package com.solarApi.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import java.security.Key;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;

import com.solarApi.user.User;

public class JWTUtil {
	
	/*
	 * TODO Figure out where we want to store secret
	 * and how we want to generate it.
	 * For now we are hard-coding a sample key
	 */
	private static String ACCESS_TOKEN_KEY = "accessTokenSecret!";
	private static String REFRESH_TOKEN_KEY = "refreshTokenSecret!";
	
	// The JWT signature algorithm we will be using to sign the token
	private SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;
	
	public String createAccessJWT(User user, Date expiration) {
		// We will sign our JWT with our ApiKey secret
		byte[] apiKeySecretBytes = DatatypeConverter.parseBase64Binary(ACCESS_TOKEN_KEY);
		Key signingKey = new SecretKeySpec(apiKeySecretBytes,
				signatureAlgorithm.getJcaName());
		
		Map<String, List<String>> rolesAndPermissions = new HashMap<>();
		List<String> permissions = new ArrayList<>();
		permissions.add("CAN_EDIT");
		permissions.add("CAN_VIEW");
		rolesAndPermissions.put("ADMIN", permissions);
		user.setRolesAndPermissions(rolesAndPermissions);

		// Set the JWT Claims
		JwtBuilder builder = Jwts.builder().setId(user.getUsername())
				.claim("RolesAndPermissions", user.getRolesAndPermissions())
				.signWith(signatureAlgorithm, signingKey)
				.setExpiration(expiration);

		// Builds the JWT and serializes it to a compact, URL-safe string
		return builder.compact();
	}
	
	public String createRefreshJWT(User user, Date expiration) {
		Map<String, List<String>> rolesAndPermissions = new HashMap<>();
		rolesAndPermissions.put("SYSTEM", new ArrayList<String>());
		
		// We will sign our JWT with our ApiKey secret
		byte[] apiKeySecretBytes = DatatypeConverter.parseBase64Binary(REFRESH_TOKEN_KEY);
		Key signingKey = new SecretKeySpec(apiKeySecretBytes,
				signatureAlgorithm.getJcaName());
		
		JwtBuilder builder = Jwts.builder().setId(user.getUsername())
				.claim("RolesAndPermissions", rolesAndPermissions)
				.signWith(signatureAlgorithm, signingKey)
				.setExpiration(expiration);
		
		return builder.compact();
	}

	@SuppressWarnings("unchecked")
	public User decodeJWT(String jwt, boolean isRefreshToken) {
		User user = new User();
		String token = isRefreshToken ? REFRESH_TOKEN_KEY : ACCESS_TOKEN_KEY;
		
		// This line will throw an exception if it is not a signed JWS
		Claims claims = Jwts.parser()
				.setSigningKey(DatatypeConverter.parseBase64Binary(token))
				.parseClaimsJws(jwt).getBody();
		
		// Set the user and roles/permissions that can be injected
		user.setUsername(claims.getId());
		user.setRolesAndPermissions((Map<String, List<String>>) claims.get("RolesAndPermissions"));
		for (String s : user.getRolesAndPermissions().keySet()) {
			System.out.println("s " + s);
		}
		
		return user;
	}
}