package com.chrisr.template_util;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/*

@SpringBootApplication tells Spring to load Spring Boot.

@EnableResourceServer configures the Spring Boot app to authenticate requests via an OAuth token (as opposed to, perhaps, OAuth 2.0 Single Sign-On).
	if you want to configure the resource server, you need to define a ResourceServerConfigurerAdapter bean; and a WebSecurityConfigurerAdapter bean is added with a hard-coded order of 3.

	in a more complex web application, you’re gonna want to configure the permissions using both a  ResourceServerConfigurerAdapter and a WebSecurityConfigurerAdapter.
	This is a change from simply using the WebSecurityConfigurerAdapter, as you do when you use the @EnableOAuth2Sso annotation, so I thought I’d warn you about it.

	Typically the resource server endpoints would start with /api or something and would be configured and protected by the ResourceServerConfigurerAdapter,
	 while any other plain HTML endpoints would be configured by the WebSecurityConfigurerAdapter.
	 However, you’ll need to add @Order(Ordered.HIGHEST_PRECEDENCE) to the WebSecurityConfigurerAdapter to have it take precedence over the default one with the hard-coded order.

 */

@SpringBootApplication
public class KareoTemplateUtilServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(KareoTemplateUtilServiceApplication.class, args);
	}

}
