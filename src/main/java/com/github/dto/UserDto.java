package com.github.dto;

import java.math.BigDecimal;

import com.github.controller.payload.UserResponse;
import com.github.model.User;
import com.github.model.enums.UserType;

public class UserDto {

	private Long id;
	private String name;
	private String document;
	private String email;
	private String password;
	private UserType userType;
	private BigDecimal balance;
	
	public UserResponse toResponse() {
		return new UserResponse(id, name, document, email, userType, balance);
	}
	
	public User toUser() {
		return new User(id, name, document, email, password, userType, balance);
	}
	
	public UserDto(Long id, String name, String document, String email, String password, UserType userType, BigDecimal balance) {
		this.id = id;
		this.name = name;
		this.document = document;
		this.email = email;
		this.password = password;
		this.userType = userType;
		this.balance = balance;
	}
	
	public UserDto(String name, String document, String email, String password, UserType userType, BigDecimal balance) {
		this.name = name;
		this.document = document;
		this.email = email;
		this.password = password;
		this.userType = userType;
		this.balance = balance;
	}

	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
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