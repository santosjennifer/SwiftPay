package com.github.service;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.github.dto.TransactionDto;

public interface TransactionService {

	TransactionDto createTransaction(TransactionDto transactionDto);
	Page<TransactionDto> findTransactions(Pageable pageable);
	Optional<TransactionDto> findTransactionById(String id);
	
}
