package com.example.demo.service;

import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import com.example.demo.dto.TokenValidationResponse;
import com.example.demo.dto.UserDto;

@Service
public class AuthServiceClient {

	@Autowired
	private RestTemplate restTemplate;

	
	  @Value("${auth.service.base.url:http://192.168.100.149:8080/v1/auth}")
	  private String authServiceBaseUrl;
	 
		/*
		 * @Value("${auth.service.url:" + ServiceConstants.AUTH_SERVICE_BASE_URL + "}")
		 * private String authServiceUrl;
		 */

	public TokenValidationResponse validateTokenOnly(String token) {
		try {
			Map<String, String> request = new HashMap<>();
			request.put("token", token.startsWith("Bearer ") ? token.substring(7) : token);
			System.out.println("Validating token: " + request.get("token"));

			TokenValidationResponse response = restTemplate.postForObject(authServiceBaseUrl + "/validate-simple",
					request, TokenValidationResponse.class);

			return response != null ? response : createInvalidResponse("No response from auth service");

		} catch (Exception e) {
			return createInvalidResponse("Auth service unavailable: " + e.getMessage());
		}
	}

	private TokenValidationResponse createInvalidResponse(String message) {
		return TokenValidationResponse.builder().valid(false).message(message).hasPermission(false).build();
	}
	
	  public UserDto getUserById(Long userId, String token) { String url =
			  authServiceBaseUrl + "/users/" + userId;
	  
	  HttpHeaders headers = new HttpHeaders(); headers.setBearerAuth(token); 
	  HttpEntity<Void> requestEntity = new HttpEntity<>(headers);
	  
	  ResponseEntity<UserDto> response = restTemplate.exchange( url,
	  HttpMethod.GET, requestEntity, UserDto.class );
	  
	  return response.getBody(); }
	 
	

	    
	/*
	 * public UserDto getUserById(Long userId, String token) { String url =
	 * authServiceBaseUrl + ServiceConstants.GET_USER_BY_ID_ENDPOINT + userId;
	 * 
	 * HttpHeaders headers = new HttpHeaders(); headers.setBearerAuth(token);
	 * HttpEntity<Void> requestEntity = new HttpEntity<>(headers);
	 * 
	 * ResponseEntity<UserDto> response = restTemplate.exchange( url,
	 * HttpMethod.GET, requestEntity, UserDto.class );
	 * 
	 * return response.getBody(); }
	 */
}