package com.chrisr.template_util.config;

import com.chrisr.template_util.security.JwtTokenProvider;
import com.chrisr.template_util.security.UserDetailsServiceImpl;
import com.chrisr.template_util.security.JwtAuthenticationEntryPoint;
import com.chrisr.template_util.security.JwtAuthenticationFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.BeanIds;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity				// the primary spring security annotation that is used to enable web security in a project.
@EnableGlobalMethodSecurity(	// used to enable method level security based on annotations
	securedEnabled = true,		// ref) https://www.callicoder.com/spring-boot-spring-security-jwt-mysql-react-app-part-2/
	jsr250Enabled = true,
	prePostEnabled = true
)
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

	// WebSecurityConfigurerAdapter provides default security configurations
	// and allows other classes to extend it and customize the security configurations by overriding its methods.

	private final UserDetailsServiceImpl userDetailsServiceImpl;
	private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
	private final JwtTokenProvider jwtTokenProvider;

	private static final String[] AUTH_WHITELIST_SWAGGER = {
			// swagger
			"/v2/api-docs",
			"/swagger-ui.html",
			"/swagger-resources/**",
			"/webjars/**"
	};

	@Autowired
	public WebSecurityConfig(UserDetailsServiceImpl userDetailsServiceImpl,
							 JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint,
							 JwtTokenProvider jwtTokenProvider) {
		this.userDetailsServiceImpl = userDetailsServiceImpl;
		this.jwtAuthenticationEntryPoint = jwtAuthenticationEntryPoint;
		this.jwtTokenProvider = jwtTokenProvider;
	}

	@Bean
	public JwtAuthenticationFilter jwtAuthenticationFilter() {
		return new JwtAuthenticationFilter(jwtTokenProvider);
	}

	// I believe this is where the user credential check is configured.
	// AuthenticationManager in AuthRestControllerImpl uses UsernamePasswordAuthenticationToken,
	// which compares the username/password to DB retrieved user object by userDetailServiceImpl.loadUserByUsername()
	@Override
	public void configure(AuthenticationManagerBuilder authenticationManagerBuilder) throws Exception {
		authenticationManagerBuilder
				.userDetailsService(userDetailsServiceImpl)
				.passwordEncoder(passwordEncoder());
	}

	@Bean(BeanIds.AUTHENTICATION_MANAGER)
	@Override
	public AuthenticationManager authenticationManagerBean() throws Exception {
		return super.authenticationManagerBean();
	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Override
	protected void configure(HttpSecurity httpSecurity) throws Exception {
		httpSecurity
				.cors()
				.and()
				.csrf().disable()
				.exceptionHandling().authenticationEntryPoint(jwtAuthenticationEntryPoint)	// unauthorized handler
				.and()

				// don't create session
				.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS)
				.and()
				.authorizeRequests()

				// allow anonymous resource requests
//				.antMatchers("/templates/**").permitAll()
				.antMatchers(
//				        HttpMethod.GET,
						"/",
						"/favicon.ico",
						"/**/*.png",
						"/**/*.gif",
						"/**/*.svg",
						"/**/*.jpg",
						"/**/*.html",
						"/**/*.css",
						"/**/*.js").permitAll()
				.antMatchers("/auth/**").permitAll()
				.antMatchers("/public/**").permitAll()

				// swagger2 endpoints
				.antMatchers(AUTH_WHITELIST_SWAGGER).permitAll()

//				.antMatchers("/user/checkUsernameAvailability", "/user/checkEmailAvailability").permitAll()
//				.antMatchers(HttpMethod.GET, "/polls/**", "/users/**").permitAll()
				.anyRequest()
				.authenticated();

		// Add our custom JWT security filter
		httpSecurity.addFilterBefore(jwtAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);

		// disable page caching
//        httpSecurity.headers().cacheControl();
	}
}
