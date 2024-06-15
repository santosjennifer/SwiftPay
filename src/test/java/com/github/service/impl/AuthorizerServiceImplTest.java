package com.github.service.impl;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;

import com.github.client.AuthorizationClient;
import com.github.dto.AuthorizerDto;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
public class AuthorizerServiceImplTest {
	
    @Mock
    private AuthorizationClient authorizationClient;

    @InjectMocks
    private AuthorizerServiceImpl service;
    
    @Mock
    private Logger logger;

    @BeforeEach
    public void setup() {
    	service.setLogger(logger);
    }

    @Test
    @DisplayName("Deve autorizar transação com sucesso")
    public void authorizeTransactionTest() {
        AuthorizerDto authorizerDto = new AuthorizerDto();
        authorizerDto.setStatus("success");
        
        AuthorizerDto.DataDto dataDto = new AuthorizerDto.DataDto();
        dataDto.setAuthorization(true);
        authorizerDto.setData(dataDto);
        
        ResponseEntity<AuthorizerDto> response = new ResponseEntity<>(authorizerDto, HttpStatus.OK);
        when(authorizationClient.isAuthorized()).thenReturn(response);

        boolean result = service.authorizeTransaction();

        assertTrue(result);
    }
    
    @Test
    @DisplayName("Deve retornar status não autorizado ao validar transação")
    public void authorizeTransactionForbiddenTest() {
    	when(authorizationClient.isAuthorized()).thenReturn(new ResponseEntity<>(null, HttpStatus.FORBIDDEN));

        boolean result = service.authorizeTransaction();

        assertFalse(result);
    }

    @Test
    @DisplayName("Deve retornar vazio ao validar autorização transação")
    public void authorizeTransactionEmptyResponseTest() {
        when(authorizationClient.isAuthorized()).thenReturn(new ResponseEntity<>(null, HttpStatus.OK));

        boolean result = service.authorizeTransaction();

        assertFalse(result);
        verify(logger, times(1)).error("A resposta do serviço de autorização está vazia ou incompleta.");
    }

    @Test
    @DisplayName("Deve retornar erro ao validar autorização transação")
    public void authorizeTransactionErrorTest() {
        when(authorizationClient.isAuthorized()).thenThrow(new RuntimeException());

        boolean result = service.authorizeTransaction();

        assertFalse(result);
        verify(logger, times(1)).error("Erro ao autorizar transação.");
    }
    
}
