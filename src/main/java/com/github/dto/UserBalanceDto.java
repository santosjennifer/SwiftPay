package com.github.dto;

import java.math.BigDecimal;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;

public class UserBalanceDto {
	
	@NotNull(message = "O id do usuário deve ser informado.")
	private Long user;
	
	@NotNull(message = "O valor deve ser informado.")
	@DecimalMin(value = "0.01", message = "O valor deve ser no mínimo igual a 0.01.")
	private BigDecimal amount;
	
	public UserBalanceDto(Long user, BigDecimal amount) {
		this.user = user;
		this.amount = amount;
	}
	
	public Long getUser() {
		return user;
	}
	
	public BigDecimal getAmount() {
		return amount;
	}

}
