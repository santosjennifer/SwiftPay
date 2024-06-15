package com.github.service.impl;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import com.github.client.NotificationClient;
import com.github.dto.NotificationDto;
import com.github.service.NotificationConsumerService;

import feign.FeignException;

@Service
public class NotificationConsumerServiceImpl implements NotificationConsumerService {

	private Logger log = LoggerFactory.getLogger(NotificationConsumerServiceImpl.class);
	
    public void setLogger(Logger logger) {
        this.log = logger;
    }

    private NotificationClient notificationClient;
    
	public NotificationConsumerServiceImpl(NotificationClient notificationClient) {
		this.notificationClient = notificationClient;
	}

	@Override
	@KafkaListener(topics = "transaction-notification", groupId = "payments")
	public void consumeMessage(ConsumerRecord<String, NotificationDto> payload) {
		try {
			log.info("Mensagem de notificação recebida: " + payload.value().getMessage());

			ResponseEntity<Void> response = notificationClient.sendNotification(payload.value());
			if (response.getStatusCode() == HttpStatus.NO_CONTENT) {
				log.info("Notificação enviada com sucesso para o e-mail " + payload.value().getEmail());
			} else {
				log.error("Erro ao enviar notificação.");
			}
		} catch (FeignException e) {
			log.error("Serviço de notificação não está disponível, tente mais tarde!");
		} catch (Exception e) {
			log.error("Erro ao processar ou enviar mensagem de notificação. ", e);
		}
	}

}
