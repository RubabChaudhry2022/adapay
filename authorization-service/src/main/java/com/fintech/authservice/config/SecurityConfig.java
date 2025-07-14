package com.fintech.authservice.config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

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
      
  
 
}
