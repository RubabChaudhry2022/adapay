package com.example.demo.util;

import com.example.demo.dto.UserDto;

public class AdminHelper {
	public static boolean isAdmin(UserDto user) {
		return user != null && "ADMIN".equalsIgnoreCase(user.getRole());
	}
}
