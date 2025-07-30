package com.fintech.authservice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import com.fintech.authservice.model.User;

@Data
@AllArgsConstructor
public class UserResponse {
	private Long id;
	private String fullName;
	private String email;
	private String phoneNumber;

	public static UserResponse from(User user) {
		return new UserResponse(user.getId(), user.getFirstName() + " " + user.getLastName(), user.getEmail(),
				user.getPhoneNumber());
	}
}
