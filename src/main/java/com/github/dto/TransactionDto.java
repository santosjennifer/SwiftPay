package com.github.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.github.model.Transaction;

public class TransactionDto {
	
    private String id;
    private BigDecimal value;
    private Long payer;
    private Long payee;
    private LocalDateTime createdAt;
    
    public Transaction toTransaction() {
    	return new Transaction(id, value, payer, payee, createdAt);
    }
    
	public TransactionDto(BigDecimal value, Long payer, Long payee) {
		this.value = value;
		this.payer = payer;
		this.payee = payee;
	}

	public TransactionDto(String id, BigDecimal value, Long payer, Long payee, LocalDateTime createdAt) {
		this.id = id;
		this.value = value;
		this.payer = payer;
		this.payee = payee;
		this.createdAt = createdAt;
	}

	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
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
	public LocalDateTime getCreatedAt() {
		return createdAt;
	}
	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}

}
