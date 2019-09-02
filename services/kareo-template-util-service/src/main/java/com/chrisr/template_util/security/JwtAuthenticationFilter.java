package com.chrisr.template_util.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

public class JwtAuthenticationFilter extends OncePerRequestFilter {

	private static final Logger logger = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

	private final JwtTokenProvider jwtTokenProvider;

	@Autowired
	private UserDetailsServiceImpl userDetailsServiceImpl;

	@Autowired
	public JwtAuthenticationFilter(JwtTokenProvider jwtTokenProvider) {
		this.jwtTokenProvider = jwtTokenProvider;
	}

	@Override
	protected void doFilterInternal(HttpServletRequest httpServletRequest, HttpServletResponse httpServletResponse, FilterChain filterChain) throws IOException, ServletException {

		// TODO: Q) the fact that it got here, means it's already expecting JWT after successful login ??? If so, throw exception if Bearer token header is missing
		// confirmed -- it comes down here NO MATTER WHAT even if the endpoint is permitted to all

		try {
			String jwt = getJwtFromRequest(httpServletRequest);

			if (StringUtils.hasText(jwt) && jwtTokenProvider.validateToken(jwt)) {
				String username = jwtTokenProvider.getUsernameFromJWT(jwt);
				UserDetails userDetails = userDetailsServiceImpl.loadUserByUsername(username);

				UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
				authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(httpServletRequest));

				SecurityContextHolder.getContext().setAuthentication(authentication);
			}
		} catch (Exception ex) {
			logger.error("Could not set user authentication in security context", ex);
		}

		filterChain.doFilter(httpServletRequest, httpServletResponse);
	}

	private String getJwtFromRequest(HttpServletRequest request) {
		String bearerToken = request.getHeader("Authorization");
		if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
			return bearerToken.substring(7);
		}
		return null;
	}
}
