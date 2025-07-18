package com.fintech.authservice.config;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.stereotype.Component;

import com.fintech.authservice.service.JwtService;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;

import java.io.IOException;
import java.util.List;

@Component
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {
	private final JwtService jwtService;
	   @Override
	    protected boolean shouldNotFilter(HttpServletRequest request) {
	        String path = request.getRequestURI();

	        // Add all public endpoints here
	        return path.equals("/auth/signup") || path.equals("/auth/login") || path.equals("/auth/refresh");
	    }

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
	        throws ServletException, IOException {

	    String authHeader = request.getHeader("Authorization");

	    if (authHeader == null || !authHeader.startsWith("Bearer ")) {
	    	 response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
	    	    response.setContentType("application/json");
	    	    response.getWriter().write("{\"error\": \"Unauthorized\", \"message\": \"Missing or invalid Authorization header\"}");
	    	    return;
	    	    }

	    String token = authHeader.substring(7);
	    String path = request.getServletPath();

	    try {
	        if ("/v1/auth/refresh".equals(path)) {
	            if (!jwtService.isRefreshToken(token)) {
	                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
	                response.setContentType("application/json");
	                response.getWriter().write("{\"error\": \"Only refresh tokens are allowed on this endpoint\"}");
	                return;
	            }
	            filterChain.doFilter(request, response);
	            return;
	        }

	        if (!jwtService.isAccessToken(token)) {
	            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
	            response.setContentType("application/json");
	            response.getWriter().write("{\"error\": \"Refresh token cannot be used here\"}");
	            return;
	        }

	        // Extract email and role from token
	        String email = jwtService.extractEmail(token);
	        String role = jwtService.extractRole(token);

	        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
	                email,
	                null,
	                List.of(new SimpleGrantedAuthority(role))
	        );

	        SecurityContextHolder.getContext().setAuthentication(auth);

	        filterChain.doFilter(request, response);

	    } catch (Exception ex) {
	        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
	        response.setContentType("application/json");
	        response.getWriter().write("{\"error\": \"Invalid or expired token\"}");
	    }
	}}