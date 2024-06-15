package com.github.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.github.client.AuthorizationClient;
import com.github.dto.AuthorizerDto;
import com.github.service.AuthorizerService;

@Service
public class AuthorizerServiceImpl implements AuthorizerService {
	
	private Logger log = LoggerFactory.getLogger(NotificationConsumerServiceImpl.class);
	
    public void setLogger(Logger logger) {
        this.log = logger;
    }

	private AuthorizationClient authorizationClient;
	
	public AuthorizerServiceImpl(AuthorizationClient authorizationClient) {
		this.authorizationClient = authorizationClient;
	}

	@Override
	public boolean authorizeTransaction() {
		try {
			ResponseEntity<AuthorizerDto> response = authorizationClient.isAuthorized();
			if (response.getStatusCode() == HttpStatus.OK) {
				AuthorizerDto authorizerDto = response.getBody();

				if (authorizerDto == null || authorizerDto.getData() == null) {
					log.error("A resposta do serviço de autorização está vazia ou incompleta.");
					return false;
				}

				return "success".equalsIgnoreCase(authorizerDto.getStatus()) && authorizerDto.getData().isAuthorization();
			}
			return false;
		} catch (Exception e) {
			log.error("Erro ao autorizar transação.");
			return false;
		}
	}

}
