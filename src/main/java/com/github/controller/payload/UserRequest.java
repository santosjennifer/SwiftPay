package com.github.controller.payload;

import java.math.BigDecimal;

import com.github.dto.UserDto;
import com.github.enums.UserType;

public class UserRequest {

	private String name;
	private String document;
	private String email;
	private String password;
	private UserType userType;
	private BigDecimal balance;
	
	public UserDto toDto() {
		return new UserDto(name, document, email, password, userType, balance);
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDocument() {
		return document;
	}

	public void setDocument(String document) {
		this.document = document;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public UserType getUserType() {
		return userType;
	}

	public void setUserType(UserType userType) {
		this.userType = userType;
	}

	public BigDecimal getBalance() {
		return balance;
	}

	public void setBalance(BigDecimal balance) {
		this.balance = balance;
	}
	
}
