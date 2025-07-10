package com.fintech.authorization_service.config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import static org.springframework.security.config.Customizer.withDefaults;

     @Configuration
     public class SecurityConfig {
     	
      @Bean
      public SecurityFilterChain filterChain(HttpSecurity http)throws Exception{
      
    	  http.csrf(csrf -> csrf.disable())
          .authorizeHttpRequests(auth -> auth
              .requestMatchers(
                  "/auth/login",
                  "/auth/signup",
                  "/auth/logout",
                  "/auth/dashboard",
                  "/auth/admin/users",    
                  "/auth/admin/delete/**", 
                  "/auth/admin/add"        
              ).permitAll()
              .anyRequest().authenticated()
          )
          .httpBasic(withDefaults()); // Use basic HTTP auth (username/password dialog)

      return http.build();
      }
      
  
 
}
