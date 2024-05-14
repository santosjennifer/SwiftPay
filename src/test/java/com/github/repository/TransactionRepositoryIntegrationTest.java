package com.github.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.github.model.Transaction;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@DataJpaTest
public class TransactionRepositoryIntegrationTest {

    @Autowired
    private TransactionRepository repository;
    
    Long payer = 1L;
    Long payee = 2L;
    BigDecimal value = BigDecimal.TEN;
    LocalDateTime date = LocalDateTime.now();
    
    @Test
	@DisplayName("Deve salvar uma transação com sucesso")
    public void saveTransactionTest() {
        Transaction transaction = new Transaction();
        transaction.setPayer(payer);
        transaction.setPayee(payee);
        transaction.setValue(value);
        transaction.setCreatedAt(date);
        
        Transaction transactionSave = repository.save(transaction);
        
        Transaction findTransaction = repository.findById(transactionSave.getId()).orElse(null);
        assertNotNull(findTransaction);
        assertEquals(payer, findTransaction.getPayer());
        assertEquals(payee, findTransaction.getPayee());
        assertEquals(value, findTransaction.getValue());
        assertEquals(date, findTransaction.getCreatedAt());
    }
	
}
