package com.fintech.authservice.config;

import com.fintech.authservice.service.JwtService;
import com.fintech.authservice.service.PermissionService;
import com.fintech.authservice.service.TokenBlacklistService;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {

	private final JwtService jwtService;
	private final PermissionService permissionService;
	private final TokenBlacklistService tokenBlacklistService;

	@Override
	protected boolean shouldNotFilter(HttpServletRequest request) {
		String path = request.getRequestURI();
		return path.equals("/v1/auth/signup") || path.equals("/v1/auth/login");
	}

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {

		String authHeader = request.getHeader("Authorization");

		if (authHeader == null || !authHeader.startsWith("Bearer ")) {
			response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
			response.setContentType("application/json");
			response.getWriter()
					.write("{\"error\": \"Unauthorized\", \"message\": \"Missing or invalid Authorization header\"}");
			return;
		}

		String token = authHeader.substring(7);
		if (tokenBlacklistService.isBlacklisted(token)) {
			response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
			response.setContentType("application/json");
			response.getWriter().write("{\"error\": \"Token is blacklisted. Please log in again.\"}");
			return;
		}

		String path = request.getServletPath();

		try {
			if ("/v1/auth/logout".equals(path)) {
				if (!StringUtils.hasText(token) || !jwtService.isAccessToken(token)
						|| tokenBlacklistService.isBlacklisted(token)) {
					response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
					response.setContentType("application/json");
					response.getWriter().write("{\"error\": \"Invalid or blacklisted access token\"}");
					return;
				}
				// Allow logout for authenticated user
				Long userId = Long.valueOf(jwtService.extractUserId(token));
				String role = jwtService.extractRole(token);
				List<GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("ROLE_" + role));
				SecurityContextHolder.getContext()
						.setAuthentication(new UsernamePasswordAuthenticationToken(userId, null, authorities));
				filterChain.doFilter(request, response);
				return;
			}

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

			Long userId = Long.valueOf(jwtService.extractUserId(token));
			String role = jwtService.extractRole(token);
			System.out.println("Extracted Role: " + role);
			List<GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority("ROLE_" + role));

			UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(userId, null,
					authorities);
			SecurityContextHolder.getContext().setAuthentication(auth);
			String method = request.getMethod();
			String fullPath = request.getRequestURI(); // /v1/auth/users/5
			List<String> roles = List.of(role.split(",")); // "ADMIN,USER"

			boolean allowed = roles.stream().anyMatch(r -> permissionService.isAllowed(method, fullPath, r));

			if (!allowed) {
				response.setStatus(HttpServletResponse.SC_FORBIDDEN);
				response.setContentType("application/json");
				response.getWriter().write(
						"{\"error\": \"Access Denied\", \"message\": \"You do not have permission to access this resource\"}");
				return;
			}
			filterChain.doFilter(request, response);

		} catch (Exception ex) {
			response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
			response.setContentType("application/json");
			response.getWriter().write("{\"error\": \"Invalid or expired token\"}");
		}
	}
}
