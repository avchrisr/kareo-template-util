package com.chrisr.template_util.security;

import org.springframework.security.core.userdetails.User;
import io.jsonwebtoken.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import java.util.Date;

@Component
public class JwtTokenProvider {

	private static final Logger logger = LoggerFactory.getLogger(JwtTokenProvider.class);

	@Value("${app.jwtSecret}")
	private String jwtSecret;

	@Value("${app.jwtExpirationInMilliseconds}")
	private int jwtExpirationInMilliseconds;


	public String generateToken(Authentication authentication) {

		User user = (User) authentication.getPrincipal();

		Date now = new Date();
		Date expirationDate = new Date(now.getTime() + jwtExpirationInMilliseconds);

		return Jwts.builder()
				.setSubject(user.getUsername())
				.setIssuedAt(now)
				.setExpiration(expirationDate)
				.signWith(SignatureAlgorithm.HS512, jwtSecret)
				.compact();
	}

	boolean validateToken(String jwt) {
		try {
			Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(jwt);
			return true;
		} catch (SignatureException ex) {
			logger.error("Invalid JWT signature");
		} catch (MalformedJwtException ex) {
			logger.error("Invalid JWT token");
		} catch (ExpiredJwtException ex) {
			logger.error("Expired JWT token");
		} catch (UnsupportedJwtException ex) {
			logger.error("Unsupported JWT token");
		} catch (IllegalArgumentException ex) {
			logger.error("JWT claims string is empty.");
		}
		return false;
	}

	String getUsernameFromJWT(String jwt) {
		Claims claims = Jwts.parser()
				.setSigningKey(jwtSecret)
				.parseClaimsJws(jwt)
				.getBody();
		return claims.getSubject();
	}
}
