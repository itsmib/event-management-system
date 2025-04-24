package com.cts.EventManagementSystem.config;

import org.springframework.security.core.Authentication;

import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

import org.springframework.stereotype.Component;

import jakarta.servlet.http.HttpServletRequest;

import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@Component

public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,

			Authentication authentication) throws IOException {

		String role = authentication.getAuthorities().toString();

		if (role.contains("ROLE_ADMIN")) {

			response.sendRedirect("/admin/dashboard");

		} else if (role.contains("ROLE_USER")) {

			response.sendRedirect("/user/dashboard");

		} else {

			response.sendRedirect("/home");

		}

	}

}