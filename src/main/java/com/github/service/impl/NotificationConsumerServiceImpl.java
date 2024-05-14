package com.github.service.impl;

import java.util.Objects;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.github.dto.NotificationDto;
import com.github.service.NotificationConsumerService;

@Service
public class NotificationConsumerServiceImpl implements NotificationConsumerService {
	
	@Value("${api.notification}")
	private String notificationUrl;

	private Logger log = LoggerFactory.getLogger(NotificationConsumerServiceImpl.class);
	
    public void setLogger(Logger logger) {
        this.log = logger;
    }

    private RestTemplate restTemplate;
    
	public NotificationConsumerServiceImpl(RestTemplate restTemplate) {
		this.restTemplate = restTemplate;
	}

	@Override
	@KafkaListener(topics = "transaction-notification", groupId = "payments")
	public void consumeMessage(ConsumerRecord<String, NotificationDto> payload) {
		try {
			log.info("Mensagem de notificação recebida: " + payload.value().getMessage());

			ResponseEntity<NotificationDto> response = restTemplate.getForEntity(notificationUrl, NotificationDto.class);
			if (response.getStatusCode() == HttpStatus.OK && Objects.requireNonNull(response.getBody().getMessage().equalsIgnoreCase("true"))) {
				log.info("Notificação enviada com sucesso para o e-mail " + payload.value().getEmail());
			} else {
				log.error("Erro ao enviar notificação");
			}
		} catch (Exception e) {
			log.error("Erro ao processar ou enviar mensagem de notificação. ", e);
		}
	}

}
