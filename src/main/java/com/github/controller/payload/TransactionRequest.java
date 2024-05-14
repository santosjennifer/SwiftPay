package com.github.controller.payload;

import java.math.BigDecimal;

import com.github.dto.TransactionDto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

public class TransactionRequest {

    @NotNull(message = "O valor deve ser informado.")
    @Positive(message = "O valor precisa ser maior que 0.")
    private BigDecimal value;
    
    @NotNull(message = "O pagador deve ser informado.")
    private Long payer;
    
    @NotNull(message = "O beneficiário deve ser informado.")
    private Long payee;
    
    public TransactionDto toDto() {
    	return new TransactionDto(value, payer, payee);
    }
    
	public BigDecimal getValue() {
		return value;
	}
	public void setValue(BigDecimal value) {
		this.value = value;
	}
	public Long getPayer() {
		return payer;
	}
	public void setPayer(Long payer) {
		this.payer = payer;
	}
	public Long getPayee() {
		return payee;
	}
	public void setPayee(Long payee) {
		this.payee = payee;
	}
    
}
