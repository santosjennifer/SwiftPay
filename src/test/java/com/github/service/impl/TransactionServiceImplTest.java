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
import com.github.dto.UserDto;
import com.github.exception.BusinessException;
import com.github.exception.TransactionNotFoundException;
import com.github.model.Transaction;
import com.github.model.enums.UserType;
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
		TransactionDto transactionDto = createTransactionDto();
		UserDto payer = createUserDto();
		UserDto payee = createUserDto();
		payer.setId(10L);
		payer.setBalance(BigDecimal.valueOf(1000));
		transactionDto.setPayer(payer.getId());
		transactionDto.setPayee(payee.getId());

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
        TransactionDto transactionDto = createTransactionDto();
        transactionDto.setPayer(2L);
        transactionDto.setPayee(2L);

        assertThrows(BusinessException.PayerCannotBePayeeException.class,
                () -> transactionService.createTransaction(transactionDto));
    }

    @Test
    @DisplayName("Deve validar que o pagador é logista")
    public void payerIsSellerTest() {
        TransactionDto transactionDto = createTransactionDto();
        UserDto payer = createUserDto();
        payer.setUserType(UserType.SELLER);
        transactionDto.setPayer(payer.getId());

        when(userService.findUserById(payer.getId())).thenReturn(Optional.of(payer));

        assertThrows(BusinessException.SellerCannotTransferException.class,
                () -> transactionService.createTransaction(transactionDto));
    }
    
    @Test
    @DisplayName("Deve validar saldo insuficiente")
    public void notEnoughBalanceTest() {
        TransactionDto transactionDto = createTransactionDto();
        UserDto payer = createUserDto();
        payer.setBalance(BigDecimal.valueOf(1)); // Insufficient balance
        transactionDto.setPayer(payer.getId());

        when(userService.findUserById(payer.getId())).thenReturn(Optional.of(payer));

        assertThrows(BusinessException.NotEnoughBalanceException.class,
                () -> transactionService.createTransaction(transactionDto));
    }
    
    @Test
    @DisplayName("Deve validar transação não autorizada")
    public void unauthorizedTransactionTest() {
        TransactionDto transactionDto = createTransactionDto();
        UserDto payer = createUserDto();
        UserDto payee = createUserDto();
        payer.setId(2L);
        payer.setBalance(BigDecimal.valueOf(1000));
		transactionDto.setPayer(payer.getId());
		transactionDto.setPayee(payee.getId());

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
        String transactionId = "123";
        Transaction transaction = createTransactionDto().toTransaction();
        when(transactionRepository.findById(transactionId)).thenReturn(Optional.of(transaction));

        Optional<TransactionDto> foundTransaction = transactionService.findTransactionById(transactionId);

        assertTrue(foundTransaction.isPresent());
    }

    @Test
    @DisplayName("Deve retornar transação não encontrada")
    public void transactionNotFoundTest() {
        String transactionId = "1234";
        when(transactionRepository.findById(transactionId)).thenReturn(Optional.empty());

        assertThrows(TransactionNotFoundException.class,
                () -> transactionService.findTransactionById(transactionId));
    }
    
    @Test
    @DisplayName("Deve atualizar o saldo do pagador e do beneficiário")
    public void updateBalanceTest() {
        UserDto payer = createUserDto();
        payer.setBalance(BigDecimal.valueOf(100));
        payer.setId(2L);
        UserDto payee = createUserDto();
        payee.setBalance(BigDecimal.valueOf(50));
        BigDecimal value = BigDecimal.valueOf(20);

		when(userService.findUserById(payer.getId())).thenReturn(Optional.of(payer));
		when(userService.findUserById(payee.getId())).thenReturn(Optional.of(payee));

        transactionService.updateBalance(payer, payee, value);
        
        assertEquals(BigDecimal.valueOf(80), payer.getBalance());
        assertEquals(BigDecimal.valueOf(70), payee.getBalance());
    }

    @Test
    @DisplayName("Deve criar a mensagem de notificação")
    public void notificationMessageTest() {
    	String payerName = "Joana Goes";
        String payeeEmail = "joana.goes@gmail.com";
        BigDecimal value = BigDecimal.valueOf(50);
        
        transactionService.notificationMessage(payeeEmail, value, payerName);

        verify(notificationService).sendMessage(any());
    }

	private TransactionDto createTransactionDto() {
		TransactionDto transactionDto = new TransactionDto("123", BigDecimal.TEN, 2L, 4L, LocalDateTime.now());
		return transactionDto;
	}

	private UserDto createUserDto() {
		UserDto userDto = new UserDto(1L, "Vagner Silva", "000.000.000-00", "vagner.silva@bol.com", "123",
				UserType.CONSUMER, BigDecimal.ZERO);

		return userDto;
	}

}
