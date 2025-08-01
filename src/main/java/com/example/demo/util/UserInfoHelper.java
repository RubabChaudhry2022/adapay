package com.example.demo.util;

import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;
import com.example.demo.constants.ServiceConstants;
import com.example.demo.dto.UserDto;

public class UserInfoHelper {
	public static UserDto getUserInfo(Long userId, String token, RestTemplate restTemplate) {
		String url = ServiceConstants.AUTH_SERVICE_BASE_URL + ServiceConstants.GET_USER_BY_ID_ENDPOINT + userId;
		ResponseEntity<UserDto> response = restTemplate.exchange(url, org.springframework.http.HttpMethod.GET,
				HttpHeaderHelper.createAuthHeaders(token), UserDto.class);
		return response.getBody();
	}
}
