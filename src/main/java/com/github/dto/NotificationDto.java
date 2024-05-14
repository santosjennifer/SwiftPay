package com.github.dto;

public class NotificationDto {

	private String email;
	private String message;
	
	public NotificationDto() {}
	
	public NotificationDto(String email, String message) {
		this.email = email;
		this.message = message;
	}

	public String getEmail() {
		return email;
	}

	public String getMessage() {
		return message;
	}
	
}
