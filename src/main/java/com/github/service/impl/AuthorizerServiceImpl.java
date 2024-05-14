package com.github.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.github.dto.AuthorizerDto;
import com.github.service.AuthorizerService;

@Service
public class AuthorizerServiceImpl implements AuthorizerService {

	@Value("${api.autorization}")
	private String authorizationUrl;
	
	private Logger log = LoggerFactory.getLogger(NotificationConsumerServiceImpl.class);
	
    public void setLogger(Logger logger) {
        this.log = logger;
    }

	private RestTemplate restTemplate;
	
	public AuthorizerServiceImpl(RestTemplate restTemplate) {
		this.restTemplate = restTemplate;
	}

	@Override
	public boolean authorizeTransaction() {
		try {
			ResponseEntity<AuthorizerDto> response = restTemplate.getForEntity(authorizationUrl, AuthorizerDto.class);
			if (response.getStatusCode() == HttpStatus.OK) {
				AuthorizerDto authorizerDto = response.getBody();

				if (authorizerDto == null) {
					log.error("A resposta do serviço de autorização está vazia.");
					return false;
				}

				return "Autorizado".equalsIgnoreCase(authorizerDto.getMessage());
			}
			return false;
		} catch (Exception e) {
			log.error("Erro ao validar autorização da transação.");
			return false;
		}
	}

}
