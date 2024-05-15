package com.github.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Optional;
import java.util.UUID;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.github.dto.TransactionDto;
import com.github.dto.UserBalanceDto;
import com.github.dto.UserDto;
import com.github.enums.TransactionType;
import com.github.enums.UserType;
import com.github.exception.BusinessException;
import com.github.exception.TransactionNotFoundException;
import com.github.model.Transaction;
import com.github.repository.TransactionRepository;
import com.github.service.UserService;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
public class TransactionServiceImplTest {

	@Mock
	private TransactionRepository transactionRepository;

	@Mock
	private UserService userService;

	@Mock
	private AuthorizerServiceImpl authorizerService;

	@Mock
	private NotificationProducerServiceImpl notificationService;

	private TransactionServiceImpl transactionService;

	@BeforeEach
	public void setUp() {
		MockitoAnnotations.openMocks(this);
		transactionService = new TransactionServiceImpl(transactionRepository, userService, authorizerService,
				notificationService);
	}

	@Test
	@DisplayName("Deve criar uma transação de pagamento")
	public void createTransactionTest() {
	    UserDto payer = new UserDto(10L, "Gustavo Payer", "02221228030", "gustavo@payer.com", "321", UserType.CONSUMER, BigDecimal.valueOf(1000));
	    UserDto payee = new UserDto(20L, "Fernanda Payee", "74414982014", "fernanda@payee.com", "123", UserType.CONSUMER, BigDecimal.ZERO);
	    TransactionDto transactionDto = new TransactionDto("1234", BigDecimal.TEN, 10L, 20L, LocalDateTime.now());

		when(userService.findUserById(payer.getId())).thenReturn(Optional.of(payer));
		when(userService.findUserById(payee.getId())).thenReturn(Optional.of(payee));
		when(authorizerService.authorizeTransaction()).thenReturn(true);
		when(transactionRepository.save(any(Transaction.class))).thenReturn(transactionDto.toTransaction());

		TransactionDto createdTransaction = transactionService.createTransaction(transactionDto);

		assertNotNull(createdTransaction);
        assertEquals(transactionDto.getId(), createdTransaction.getId());
        assertEquals(transactionDto.getValue(), createdTransaction.getValue());
        assertEquals(transactionDto.getPayer(), createdTransaction.getPayer());
        assertEquals(transactionDto.getPayee(), createdTransaction.getPayee());
        assertEquals(transactionDto.getCreatedAt(), createdTransaction.getCreatedAt());
	}
	
    @Test
    @DisplayName("Deve validar que o pagador é o mesmo que o beneficiário")
    public void payerIsPayeeTest() {
	    UserDto payer = new UserDto(2L, "Gustavo Payer", "02221228030", "gustavo@payer.com", "321", UserType.CONSUMER, BigDecimal.valueOf(1000));
	    TransactionDto transactionDto = new TransactionDto("1234", BigDecimal.TEN, 2L, 2L, LocalDateTime.now());

        when(userService.findUserById(2L)).thenReturn(Optional.of(payer));

        assertThrows(BusinessException.PayerCannotBePayeeException.class,
                () -> transactionService.createTransaction(transactionDto));
    }

    @Test
    @DisplayName("Deve validar que o pagador é logista")
    public void payerIsSellerTest() {
	    UserDto payer = new UserDto(3L, "Gustavo Payer", "22759052000189", "gustavo@payer.com", "321", UserType.SELLER, BigDecimal.valueOf(1000));
	    UserDto payee = new UserDto(5L, "Fernanda Payee", "74414982014", "fernanda@payee.com", "123", UserType.CONSUMER, BigDecimal.ZERO);
	    TransactionDto transactionDto = new TransactionDto("1234", BigDecimal.TEN, 3L, 5L, LocalDateTime.now());
        
    	when(userService.findUserById(payer.getId())).thenReturn(Optional.of(payer));
    	when(userService.findUserById(payee.getId())).thenReturn(Optional.of(payee));

        assertThrows(BusinessException.SellerCannotTransferException.class,
                () -> transactionService.createTransaction(transactionDto));
    }

    @Test
    @DisplayName("Deve validar saldo insuficiente")
    public void notEnoughBalanceTest() {
    	UserDto payer = new UserDto(2L, "Gustavo Payer", "02221228030", "gustavo@payer.com", "321", UserType.CONSUMER, BigDecimal.valueOf(9));
 	    UserDto payee = new UserDto(7L, "Fernanda Payee", "74414982014", "fernanda@payee.com", "123", UserType.CONSUMER, BigDecimal.ZERO);
	    TransactionDto transactionDto = new TransactionDto("1234", BigDecimal.TEN, 2L, 7L, LocalDateTime.now());

        when(userService.findUserById(payer.getId())).thenReturn(Optional.of(payer));
        when(userService.findUserById(payee.getId())).thenReturn(Optional.of(payee));

        assertThrows(BusinessException.NotEnoughBalanceException.class,
                () -> transactionService.createTransaction(transactionDto));
    }
    
    @Test
    @DisplayName("Deve validar transação não autorizada")
    public void unauthorizedTransactionTest() {
    	UserDto payer = new UserDto(2L, "Gustavo Payer", "02221228030", "gustavo@payer.com", "321", UserType.CONSUMER, BigDecimal.valueOf(600));
 	    UserDto payee = new UserDto(7L, "Fernanda Payee", "74414982014", "fernanda@payee.com", "123", UserType.CONSUMER, BigDecimal.ZERO);
	    TransactionDto transactionDto = new TransactionDto("1234", BigDecimal.TEN, 2L, 7L, LocalDateTime.now());

        when(userService.findUserById(payer.getId())).thenReturn(Optional.of(payer));
        when(userService.findUserById(payee.getId())).thenReturn(Optional.of(payee));
        when(authorizerService.authorizeTransaction()).thenReturn(false);

        assertThrows(BusinessException.UnauthorizedTransactionException.class,
                () -> transactionService.createTransaction(transactionDto));
    }
    
    @Test
    @DisplayName("Deve retornar uma lista de transações")
    public void findTransactionsTest() {
        Pageable pageable = mock(Pageable.class);
        Page<Transaction> transactionPage = new PageImpl<>(Collections.emptyList());

        when(transactionRepository.findAll(pageable)).thenReturn(transactionPage);

        Page<TransactionDto> foundTransactions = transactionService.findTransactions(pageable);

        assertNotNull(foundTransactions);
        assertTrue(foundTransactions.isEmpty());
    }

    @Test
    @DisplayName("Deve retornar uma transação por id")
    public void findTransactionByIdTest() {
    	String transactionId = UUID.randomUUID().toString();
    	TransactionDto transactionDto = new TransactionDto(transactionId, BigDecimal.TEN, 2L, 7L, LocalDateTime.now());
        Transaction transaction = transactionDto.toTransaction();
        when(transactionRepository.findById(transactionId)).thenReturn(Optional.of(transaction));

        Optional<TransactionDto> foundTransaction = transactionService.findTransactionById(transactionId);

        assertTrue(foundTransaction.isPresent());
    }

    @Test
    @DisplayName("Deve retornar transação não encontrada")
    public void transactionNotFoundTest() {
    	String transactionId = UUID.randomUUID().toString();
        when(transactionRepository.findById(transactionId)).thenReturn(Optional.empty());

        assertThrows(TransactionNotFoundException.class,
                () -> transactionService.findTransactionById(transactionId));
    }
    
    @Test
    @DisplayName("Deve atualizar o saldo do pagador e do beneficiário")
    public void updateBalanceTest() {
        Long payerId = 1L;
        Long payeeId = 2L;
        BigDecimal value = BigDecimal.TEN;

        when(userService.updateUserBalance(payerId, value, TransactionType.DEBIT))
        		.thenReturn(new UserBalanceDto(payerId, value.negate()));
        when(userService.updateUserBalance(payeeId, value, TransactionType.CREDIT))
        		.thenReturn(new UserBalanceDto(payeeId, value));

        transactionService.updateBalances(payerId, payeeId, value);

        verify(userService).updateUserBalance(payerId, value, TransactionType.DEBIT);
        verify(userService).updateUserBalance(payeeId, value, TransactionType.CREDIT);
    }

    @Test
    @DisplayName("Deve criar a mensagem de notificação")
    public void notificationMessageTest() {
    	String payerName = "Joana Goes";
        String payeeEmail = "joana.goes@gmail.com";
        BigDecimal value = BigDecimal.valueOf(50);
        
        transactionService.sendNotification(payeeEmail, value, payerName);

        verify(notificationService).sendMessage(any());
    }

}
