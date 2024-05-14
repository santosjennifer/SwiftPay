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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.client.RestTemplate;

import com.github.dto.AuthorizerDto;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
public class AuthorizerServiceImplTest {
	
	@Value("${api.autorization}")
	private String authorizationUrl;

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private AuthorizerServiceImpl service;
    
    @Mock
    private Logger logger;

    @BeforeEach
    public void setup() {
    	service.setLogger(logger);
        when(restTemplate.getForEntity(authorizationUrl, AuthorizerDto.class))
                .thenReturn(new ResponseEntity<>(HttpStatus.OK));
    }

    @Test
    @DisplayName("Deve autorizar transação com sucesso")
    public void authorizeTransactionTest() {
        AuthorizerDto authorizerDto = new AuthorizerDto();
        authorizerDto.setMessage("Autorizado");
        ResponseEntity<AuthorizerDto> responseEntity = new ResponseEntity<>(authorizerDto, HttpStatus.OK);
        when(restTemplate.getForEntity(authorizationUrl, AuthorizerDto.class)).thenReturn(responseEntity);

        boolean result = service.authorizeTransaction();

        assertTrue(result);
    }

    @Test
    @DisplayName("Deve retornar vazio ao validar autorização transação")
    public void authorizeTransactionEmptyResponseTest() {
        when(restTemplate.getForEntity(authorizationUrl, AuthorizerDto.class))
                .thenReturn(new ResponseEntity<>(null, HttpStatus.OK));

        boolean result = service.authorizeTransaction();

        assertFalse(result);
        verify(logger, times(1)).error("A resposta do serviço de autorização está vazia.");
    }

    @Test
    @DisplayName("Deve retornar erro ao validar autorização transação")
    public void authorizeTransactionErrorTest() {
        when(restTemplate.getForEntity(authorizationUrl, AuthorizerDto.class)).thenThrow(new RuntimeException());

        boolean result = service.authorizeTransaction();

        assertFalse(result);
        verify(logger, times(1)).error("Erro ao validar autorização da transação.");
    }
    
}
