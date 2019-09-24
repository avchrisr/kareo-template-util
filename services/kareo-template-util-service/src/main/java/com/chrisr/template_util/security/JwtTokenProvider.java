package com.chrisr.template_util.security;

import com.chrisr.template_util.repository.entity.User;
import io.jsonwebtoken.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Component;

import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;
import java.security.Key;
import java.util.Date;
import java.util.UUID;

@Component
public class JwtTokenProvider {

	private static final Logger logger = LoggerFactory.getLogger(JwtTokenProvider.class);

	@Value("${app.jwtSecret}")
	private String jwtSecret;

	@Value("${app.jwtExpirationInMilliseconds}")
	private long jwtExpirationInMilliseconds;

	public String generateToken(Authentication authentication, User user) {
//		org.springframework.security.core.userdetails.User userPrincipal = (org.springframework.security.core.userdetails.User) authentication.getPrincipal();

		Date now = new Date();
		Date expirationDate = new Date(now.getTime() + jwtExpirationInMilliseconds);

		SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS512;
		byte[] apiKeySecretBytes = DatatypeConverter.parseBase64Binary(jwtSecret);
		Key signingKey = new SecretKeySpec(apiKeySecretBytes, signatureAlgorithm.getJcaName());

		return Jwts.builder()
				.setHeaderParam("userId", user.getId())
				.setHeaderParam("userFirstname", user.getFirstname())
				.setId(UUID.randomUUID().toString())
				.setSubject(user.getUsername())
				.setIssuedAt(now)
				.setExpiration(expirationDate)
				.signWith(signatureAlgorithm, signingKey)
				.compact();
	}

	boolean validateToken(String jwt) {
		SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS512;
		byte[] apiKeySecretBytes = DatatypeConverter.parseBase64Binary(jwtSecret);
		Key signingKey = new SecretKeySpec(apiKeySecretBytes, signatureAlgorithm.getJcaName());

		try {
			Jwts.parser().setSigningKey(signingKey).parseClaimsJws(jwt);
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
		SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS512;
		byte[] apiKeySecretBytes = DatatypeConverter.parseBase64Binary(jwtSecret);
		Key signingKey = new SecretKeySpec(apiKeySecretBytes, signatureAlgorithm.getJcaName());

		Claims claims = Jwts.parser()
				.setSigningKey(signingKey)
				.parseClaimsJws(jwt)
				.getBody();
		return claims.getSubject();
	}
}
