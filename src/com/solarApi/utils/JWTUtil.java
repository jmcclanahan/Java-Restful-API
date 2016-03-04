package com.solarApi.utils;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtBuilder;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import java.security.Key;
import java.util.Date;
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
	private static String API_KEY = "mySecret123!";
	
	public String createJWT(User user, long ttlMillis) {

		// The JWT signature algorithm we will be using to sign the token
		SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS256;

		long nowMillis = System.currentTimeMillis();
		Date now = new Date(nowMillis);

		// We will sign our JWT with our ApiKey secret
		byte[] apiKeySecretBytes = DatatypeConverter.parseBase64Binary(API_KEY);
		Key signingKey = new SecretKeySpec(apiKeySecretBytes,
				signatureAlgorithm.getJcaName());

		// Set the JWT Claims
		JwtBuilder builder = Jwts.builder().setId(user.getUsername())
				.claim("RolesAndPermissions", user.getRolesAndPermissions())
				.signWith(signatureAlgorithm, signingKey);

		// if it has been specified, let's add the expiration
		if (ttlMillis >= 0) {
			long expMillis = nowMillis + ttlMillis;
			Date exp = new Date(expMillis);
			builder.setExpiration(exp);
		}

		// Builds the JWT and serializes it to a compact, URL-safe string
		return builder.compact();
	}

	@SuppressWarnings("unchecked")
	public User parseJWT(String jwt) {
		
		User user = new User();
		
		// This line will throw an exception if it is not a signed JWS
		Claims claims = Jwts.parser()
				.setSigningKey(DatatypeConverter.parseBase64Binary(API_KEY))
				.parseClaimsJws(jwt).getBody();
		
		// Set the user and roles/permissions that can be injected
		user.setUsername(claims.getId());
		user.setRolesAndPermissions((Map<String, List<String>>) claims.get("RolesAndPermissions"));
		
		return user;
	}
}