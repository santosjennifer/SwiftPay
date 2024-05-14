package com.github.controller;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.github.controller.payload.TransactionRequest;
import com.github.dto.TransactionDto;
import com.github.service.TransactionService;

import io.swagger.v3.oas.annotations.OpenAPIDefinition;
import io.swagger.v3.oas.annotations.info.Info;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/transaction")
@Tag(name = "Transaction")
@OpenAPIDefinition(info = @Info(title = "Payments API", version = "v1.0", description = "Documentation of Payments API"))
public class TransactionController {
	
	private TransactionService service;
	
	public TransactionController(TransactionService service) {
		this.service = service;
	}
	
	@PostMapping
	public ResponseEntity<TransactionDto> createTransaction(@RequestBody @Valid TransactionRequest request){
		TransactionDto transaction = request.toDto();
		transaction = service.createTransaction(transaction);
		
		return ResponseEntity.ok(transaction);
	}
	
	@GetMapping
	public ResponseEntity<List<TransactionDto>> findTransactions(
		    @RequestParam(defaultValue = "0") int page,
		    @RequestParam(defaultValue = "10") int size
		) {
		Pageable pageable = PageRequest.of(page, size);
		Page<TransactionDto> transaction = service.findTransactions(pageable);
		
	    if(transaction.isEmpty()) {
	        return ResponseEntity.ok(Collections.emptyList());
	    }
	    
	    List<TransactionDto> response = transaction.getContent().stream()
	    		.collect(Collectors.toList());
		
		return ResponseEntity.ok(response);
	}
	
	@GetMapping("{id}")
	public ResponseEntity<TransactionDto> findTransactionById(@PathVariable String id){
		Optional<TransactionDto> transaction = service.findTransactionById(id);
		
		if (transaction.isPresent()) {
			return ResponseEntity.ok(transaction.get());
		}
		
		return ResponseEntity.notFound().build();
	}
}
