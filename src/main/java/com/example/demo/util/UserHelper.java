package com.example.demo.util;

import com.example.demo.dto.UserDto;

public class UserHelper {
	public static String getFullName(UserDto user) {
		return user.getFirstName() + " " + user.getLastName();
	}
}
