package com.github.model;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import com.github.dto.TransactionDto;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

@Entity(name = "transaction")
public class Transaction {
	
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @NotNull(message = "O valor deve ser informado.")
    @Positive(message = "O valor precisa ser maior que 0.")
    private BigDecimal value;

    @NotNull(message = "O pagador deve ser informado.")
    private Long payer;
    
    @NotNull(message = "O beneficiário deve ser informado.")
    private Long payee;

    private LocalDateTime createdAt;
    
    public Transaction() {}
    
    public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}

	public TransactionDto toDto() {
    	return new TransactionDto(id, value, payer, payee, createdAt);
    }

	public Transaction(String id, BigDecimal value, Long payer, Long payee, LocalDateTime createdAt) {
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
 
}
