package com.fintech.authservice.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

<<<<<<< HEAD
     @Configuration
     public class SecurityConfig {
    	 @Bean
    		public PasswordEncoder passwordEncoder() {
    		    return new BCryptPasswordEncoder();
    		}
     	@Bean
      public SecurityFilterChain filterChain(HttpSecurity http)throws Exception{
      
    	  http.csrf(csrf -> csrf.disable())
          .authorizeHttpRequests(auth -> auth
              .requestMatchers(
                  "/v1/auth/login",
                  "/v1/auth/signup","/v1/auth/refresh")
                .permitAll().requestMatchers("/v1/auth/**").permitAll().anyRequest().authenticated()
          );
      return http.build();
      }
      
  
 
=======
@Configuration
@RequiredArgsConstructor
@EnableMethodSecurity // Enables @PreAuthorize annotations
public class SecurityConfig {

	private final JwtFilter jwtFilter;

	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}

	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		return http.csrf(csrf -> csrf.disable())
				.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
				.authorizeHttpRequests(
						auth -> auth.requestMatchers("/v1/auth/signup", "/v1/auth/login", "/v1/auth/refresh")
								.permitAll().anyRequest().authenticated())
				.addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class).build();
	}

	@Bean
	public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
		return config.getAuthenticationManager();
	}
>>>>>>> 022c01f (Removed apache-maven files and updated .gitignore)
}
