package com.github.service.impl;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
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
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.test.context.ActiveProfiles;

import com.github.dto.NotificationDto;

@ExtendWith(MockitoExtension.class)
@ActiveProfiles("test")
public class NotificationProducerServiceImplTest {

	@Mock
	private KafkaTemplate<String, NotificationDto> kafkaTemplate;

	@InjectMocks
	private NotificationProducerServiceImpl service;
	
    @Mock
    private Logger logger;

    @BeforeEach
    public void setup() {
        service.setLogger(logger);
    }
    
    String email = "thiago@bol.com";
    String message = "Você recebeu um pagamento de R$10.00 enviado por Gustavo Perreira";

	@Test
	@DisplayName("Produz mensagem de notificação no tópico")
	public void sendMessageTest() {
		NotificationDto notification = new NotificationDto(email, message);

		when(kafkaTemplate.send(anyString(), any(NotificationDto.class))).thenReturn(null);
		
		service.sendMessage(notification);
		
		verify(kafkaTemplate, times(1)).send("transaction-notification", notification);
		verify(logger, times(1)).info("Notificação de pagamento enviada com sucesso.");
	}
	
	@Test
	@DisplayName("Produz mensagem de notificação no tópico com erro")
	public void sendMessageWithErrorTest() {
	    NotificationDto notification = new NotificationDto(email, message);

	    when(kafkaTemplate.send(anyString(), any(NotificationDto.class))).thenThrow(new RuntimeException("Erro ao enviar a mensagem"));

	    service.sendMessage(notification);
	    
	    verify(logger, times(1)).error("Erro ao enviar notificação de pagamento. Erro ao enviar a mensagem");
	}
}
