package com.github.service.impl;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.github.dto.NotificationDto;
import com.github.dto.TransactionDto;
import com.github.dto.UserDto;
import com.github.enums.TransactionType;
import com.github.enums.UserType;
import com.github.exception.BusinessException;
import com.github.exception.TransactionNotFoundException;
import com.github.model.Transaction;
import com.github.repository.TransactionRepository;
import com.github.service.TransactionService;
import com.github.service.UserService;

import jakarta.transaction.Transactional;

@Service
public class TransactionServiceImpl implements TransactionService {

	private TransactionRepository repository;
	private UserService userService;
	private AuthorizerServiceImpl authorizerService;
	private NotificationProducerServiceImpl notificationService;

	public TransactionServiceImpl(TransactionRepository repository, UserService userService,
			AuthorizerServiceImpl authorizerService, NotificationProducerServiceImpl notificationService) {
		this.repository = repository;
		this.userService = userService;
		this.authorizerService = authorizerService;
		this.notificationService = notificationService;
	}
	
	@Override
	public Page<TransactionDto> findTransactions(Pageable pageable) {
		Page<Transaction> transactions = repository.findAll(pageable);

		return transactions.map(Transaction::toDto);
	}

	@Override
	public Optional<TransactionDto> findTransactionById(String id) {
		Transaction transaction = repository.findById(id).orElseThrow(() 
				-> new TransactionNotFoundException());

		return Optional.of(transaction.toDto());
	}
	
	@Override
	@Transactional
	public TransactionDto createTransaction(TransactionDto transactionDto) {
		Optional<UserDto> payer = userService.findUserById(transactionDto.getPayer());
		Optional<UserDto> payee = userService.findUserById(transactionDto.getPayee());

		validateTransaction(transactionDto, payer.get(), payee.get());
	    authorizeTransaction();
	    
	    Transaction transaction = saveTransaction(transactionDto);
	    updateBalances(payer.get().getId(), payee.get().getId(), transactionDto.getValue());
	    sendNotification(payee.get().getEmail(), transactionDto.getValue(), payer.get().getName());
	    
	    return transaction.toDto();
	}

	private void validateTransaction(TransactionDto transactionDto, UserDto payer, UserDto payee) {
	    if (payer.getId().equals(payee.getId())) {
	        throw new BusinessException.PayerCannotBePayeeException();
	    }

	    if (payer.getUserType() == UserType.SELLER) {
	        throw new BusinessException.SellerCannotTransferException();
	    }

	    if (payer.getBalance().compareTo(transactionDto.getValue()) < 0) {
	        throw new BusinessException.NotEnoughBalanceException();
	    }
	}

	private void authorizeTransaction() {
	    if (!authorizerService.authorizeTransaction()) {
	        throw new BusinessException.UnauthorizedTransactionException();
	    }
	}

	private Transaction saveTransaction(TransactionDto transactionDto) {
	    Transaction transaction = transactionDto.toTransaction();
	    transaction.setCreatedAt(LocalDateTime.now());
	    return repository.save(transaction);
	}
	
	protected void updateBalances(Long payerId, Long payeeId, BigDecimal value) {
	    userService.updateUserBalance(payerId, value, TransactionType.DEBIT);
	    userService.updateUserBalance(payeeId, value, TransactionType.CREDIT);
	}

	protected void sendNotification(String payeeEmail, BigDecimal value, String payerName) {
	    String message = "Você recebeu um pagamento no valor de R$" + value + " enviado por " + payerName;
	    NotificationDto notification = new NotificationDto(payeeEmail, message);
	    notificationService.sendMessage(notification);
	}
	
}
