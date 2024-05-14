package com.github.model;

import java.math.BigDecimal;

import org.hibernate.validator.constraints.br.CNPJ;
import org.hibernate.validator.constraints.br.CPF;
import org.hibernate.validator.group.GroupSequenceProvider;

import com.github.dto.UserDto;
import com.github.model.enums.UserType;
import com.github.model.validator.CNPJGroup;
import com.github.model.validator.CPFGroup;
import com.github.model.validator.UserDocumentSequenceProvider;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

@Entity(name = "users")
@GroupSequenceProvider(value = UserDocumentSequenceProvider.class)
public class User {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;
	
	@NotBlank(message = "O nome deve ser informado.")
	private String name;
	
	@Column(unique = true)
	@NotBlank(message = "O CPF/CNPJ deve ser informado.")
	@CPF(message = "O CPF deve ser válido.", groups = CPFGroup.class)
	@CNPJ(message = "O CNPJ deve ser válido.", groups = CNPJGroup.class)
	private String document;
	
	@Column(unique = true)
	@Email(message = "O e-mail deve ser válido.")
	@NotBlank(message = "O e-mail deve ser informado.")
	private String email;
	
	@NotBlank(message = "A senha deve ser informada.")
	private String password;
	
	@Enumerated(EnumType.STRING)
	@NotNull(message = "O tipo do usuário deve ser informado.")
	private UserType userType;
	
	@NotNull(message = "O saldo deve ser informado.")
	@DecimalMin(value = "0", message = "O saldo deve ser no mínimo igual a 0.")
	private BigDecimal balance;
	
	public User() {}
	
	public UserDto toDto() {
		return new UserDto(id, name, document, email, password, userType, balance);
	}
	
	public User(Long id, String name, String document, String email, String password, UserType userType, BigDecimal balance) {
		this.id = id;
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
