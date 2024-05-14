package com.github.controller.payload;

import java.math.BigDecimal;

import com.github.model.enums.UserType;

public class UserResponse {

	private Long id;
	private String name;
	private String document;
	private String email;
	private UserType userType;
	private BigDecimal balance;
	
	public UserResponse(Long id, String name, String document, String email, UserType userType, BigDecimal balance) {
		this.id = id;
		this.name = name;
		this.document = document;
		this.email = email;
		this.userType = userType;
		this.balance = balance;
	}
	public Long getId() {
		return id;
	}
	public String getName() {
		return name;
	}
	public String getDocument() {
		return document;
	}
	public String getEmail() {
		return email;
	}
	public UserType getUserType() {
		return userType;
	}
	public BigDecimal getBalance() {
		return balance;
	}
	
}
