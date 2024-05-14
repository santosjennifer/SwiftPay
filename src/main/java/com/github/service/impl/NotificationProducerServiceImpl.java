package com.github.service.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import com.github.dto.NotificationDto;
import com.github.service.NotificationProducerService;

@Service
public class NotificationProducerServiceImpl implements NotificationProducerService {
	
	@Value("${api.notification}")
	private String notificationUrl;
	
	private final KafkaTemplate<String, NotificationDto> kafkaTemplate;
	
	private Logger log = LoggerFactory.getLogger(NotificationProducerServiceImpl.class);
	
    public void setLogger(Logger logger) {
        this.log = logger;
    }
    
    public NotificationProducerServiceImpl(KafkaTemplate<String, NotificationDto> kafkaTemplate) {
		this.kafkaTemplate = kafkaTemplate;
	}

	@Override
	public void sendMessage(NotificationDto notification) {
		try {
			kafkaTemplate.send("transaction-notification", notification);
			log.info("Notificação de pagamento enviada com sucesso.");
		} catch (Exception e) {
			log.error("Erro ao enviar notificação de pagamento. " + e.getMessage());
		}
	}

}
