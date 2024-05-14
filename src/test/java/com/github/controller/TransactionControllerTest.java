package com.github.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.controller.payload.TransactionRequest;
import com.github.dto.TransactionDto;
import com.github.exception.BusinessException;
import com.github.exception.TransactionNotFoundException;
import com.github.service.TransactionService;

@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
@WebMvcTest(controllers = TransactionController.class)
public class TransactionControllerTest {
	
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TransactionService service;
    
	String TRANSACTON_API = "/api/transaction";
	
	String id = "123";
	Long payer = 1L;
	Long payee = 4L;
	BigDecimal value = new BigDecimal(5.00);
	LocalDateTime createdAt = LocalDateTime.now();
	
	@Test
	@DisplayName("Deve criar uma transação de pagamento")
	public void createTransactionTest() throws Exception {
        TransactionDto mockTransactionDto = new TransactionDto(id, value, payer, payee, createdAt);
        when(service.createTransaction(any(TransactionDto.class))).thenReturn(mockTransactionDto);
        
        String json = new ObjectMapper().writeValueAsString(createTransactionRequest());
        
        mockMvc.perform(post(TRANSACTON_API)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json)) 
        		.andExpect(status().isOk())
        		.andExpect(jsonPath("id").value(id))
        		.andExpect(jsonPath("value").value(value))
        		.andExpect(jsonPath("payer").value(payer))
        		.andExpect(jsonPath("payee").value(payee))
        		.andExpect(jsonPath("createdAt").hasJsonPath())
        		.andReturn();
       
        verify(service, times(1)).createTransaction(any(TransactionDto.class));
	}
	
    @Test
    @DisplayName("Deve retornar todas as transações de pagamento")
    public void getAllTransactionsTest() throws Exception{
    	TransactionDto transaction = new TransactionDto(id, value, payer, payee, createdAt);
        
        when(service.findTransactions(Mockito.any(Pageable.class))).thenReturn(
        		new PageImpl<TransactionDto>(Arrays.asList(transaction), PageRequest.of(0,10), 1));

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .get(TRANSACTON_API)
                .accept(MediaType.APPLICATION_JSON);

        mockMvc
            .perform(request)
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.[0].id").value(id))
            .andExpect(jsonPath("$.[0].value").value(value))
            .andExpect(jsonPath("$.[0].payer").value(payer))
            .andExpect(jsonPath("$.[0].payee").value(payee))
            .andExpect(jsonPath("$.[0].createdAt").isNotEmpty());
    }
    
    @Test
    @DisplayName("Deve retornar uma lista vazia quando não existir nenhuma transação")
    public void noTransactionsTest() throws Exception {
    	when(service.findTransactions(Mockito.any(Pageable.class))).thenReturn(new PageImpl<>(Collections.emptyList()));
    	
        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .get(TRANSACTON_API)
                .accept(MediaType.APPLICATION_JSON);

        mockMvc
            .perform(request)
            .andExpect(status().isOk())
            .andExpect(content().json("[]"));
    }
    
    @Test
    @DisplayName("Deve retornar os dados da transação de pagamento")
    public void getUserSellerTest() throws Exception{
    	TransactionDto transaction = new TransactionDto(id, value, payer, payee, createdAt);

        BDDMockito.given(service.findTransactionById(id) ).willReturn(Optional.of(transaction));

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .get(TRANSACTON_API.concat("/" + id))
                .accept(MediaType.APPLICATION_JSON);

        mockMvc
            .perform(request)
            .andExpect(status().isOk())
    		.andExpect(jsonPath("id").value(id))
    		.andExpect(jsonPath("value").value(value))
    		.andExpect(jsonPath("payer").value(payer))
    		.andExpect(jsonPath("payee").value(payee))
    		.andExpect(jsonPath("createdAt").hasJsonPath());
    }
	
    @Test
    @DisplayName("Deve retornar status not found quando a transação pesquisada não existir")
    public void transactionNotFoundTest() throws Exception {
    	BDDMockito.given(service.findTransactionById(Mockito.anyString())).willThrow(new TransactionNotFoundException());

        MockHttpServletRequestBuilder request = MockMvcRequestBuilders
                .get(TRANSACTON_API.concat("/" + id))
                .accept(MediaType.APPLICATION_JSON);

        mockMvc
            .perform(request)
            .andExpect(status().isNotFound())
            .andExpect(jsonPath("message").value("Transação não encontrada."));
    }
    
    @Test
    @DisplayName("Não deve permitir que logistas façam transferências")
    public void sellerCannotTransferTest() throws Exception {
	    when(service.createTransaction(any(TransactionDto.class))).thenThrow(new BusinessException.SellerCannotTransferException());
		
		String json = new ObjectMapper().writeValueAsString(createTransactionRequest());

        mockMvc.perform(post(TRANSACTON_API)
        		.contentType(MediaType.APPLICATION_JSON)
        		.content(json))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("message").value("Logistas não podem efetuar transferência."))
                .andReturn();
    }
    
    @Test
    @DisplayName("Não deve permitir que o pagador seja o mesmo que o beneficiário")
    public void payerCannotBePayeeTest() throws Exception {
	    when(service.createTransaction(any(TransactionDto.class))).thenThrow(new BusinessException.PayerCannotBePayeeException());
		
		String json = new ObjectMapper().writeValueAsString(createTransactionRequest());

        mockMvc.perform(post(TRANSACTON_API)
        		.contentType(MediaType.APPLICATION_JSON)
        		.content(json))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("message").value("O pagador não pode ser o mesmo que o beneficiário."))
                .andReturn();
    }
    
    @Test
    @DisplayName("Não deve permitir transferir se o saldo não for suficiente")
    public void notEnoughBalanceTest() throws Exception {
	    when(service.createTransaction(any(TransactionDto.class))).thenThrow(new BusinessException.NotEnoughBalanceException());
		
		String json = new ObjectMapper().writeValueAsString(createTransactionRequest());

        mockMvc.perform(post(TRANSACTON_API)
        		.contentType(MediaType.APPLICATION_JSON)
        		.content(json))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("message").value("Saldo insuficiente."))
                .andReturn();
    }
    
    @Test
    @DisplayName("Não deve permitir transferir se a transação não for autorizada")
    public void unauthorizedTransactionTest() throws Exception {
	    when(service.createTransaction(any(TransactionDto.class))).thenThrow(new BusinessException.UnauthorizedTransactionException());
		
		String json = new ObjectMapper().writeValueAsString(createTransactionRequest());

        mockMvc.perform(post(TRANSACTON_API)
        		.contentType(MediaType.APPLICATION_JSON)
        		.content(json))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("message").value("Transação não autorizada."))
                .andReturn();
    }
    
    private TransactionRequest createTransactionRequest() {
		TransactionRequest request = new TransactionRequest();
        request.setPayee(payee);
        request.setPayer(payer);
        request.setValue(value);
        
        return request;
    }

}
