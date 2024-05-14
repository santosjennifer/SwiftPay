package com.github.service.impl;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.HashMap;
import java.util.Map;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.client.RestTemplate;

import com.github.dto.NotificationDto;

@ExtendWith({SpringExtension.class, MockitoExtension.class})
@SpringBootTest
@ActiveProfiles("test")
@EmbeddedKafka(partitions = 1, brokerProperties = { "listeners=PLAINTEXT://localhost:9092", "port=9092" })
public class NotificationConsumerServiceImplTest {

    @Autowired
    private NotificationConsumerServiceImpl service;

    @MockBean
    private RestTemplate restTemplate;
    
    @Mock
    private Logger logger;

    @BeforeEach
    public void setup() {
        service.setLogger(logger);
    }
    
	@Value("${api.notification}")
	private String notificationUrl;
    
    @Test
    @DisplayName("Consume mensagem de notificação do tópico e retorna erro ao enviar notificação")
    public void testConsumeMessage() {
        String email = "test@example.com";
        String message = "Você recebeu um pagamento de R$10.00 enviado por Gustavo Perreira";

        NotificationDto notificationDto = new NotificationDto(email, message);
        
        Map<String, Object> headers = new HashMap<>();
        headers.put(KafkaHeaders.TOPIC, "transaction-notification");
        ConsumerRecord<String, NotificationDto> record = 
                new ConsumerRecord<>("transaction-notification", 0, 0, "key", notificationDto);
        
        ResponseEntity<NotificationDto> responseEntity = new ResponseEntity<>(notificationDto, HttpStatus.BAD_GATEWAY);
        
        when(restTemplate.getForEntity(notificationUrl, NotificationDto.class)).thenReturn(responseEntity);
        
        service.consumeMessage(record);

        verify(restTemplate, times(1)).getForEntity(notificationUrl, NotificationDto.class);
        verify(logger, times(1)).info("Mensagem de notificação recebida: " + message);
        verify(logger, times(1)).error("Erro ao enviar notificação");
        verify(logger, times(0)).info("Notificação enviada com sucesso para o e-mail " + email);
    }
    
    
    @Test
    @DisplayName("Consume mensagem de notificação do tópico e retorna sucesso ao enviar notificação")
    public void testConsumeMessageSuccess() {
        String email = "test@example.com";
        String messageReceived = "Você recebeu um pagamento de R$10.00 enviado por Gustavo Perreira";
        String messageSend = "true";

        NotificationDto notificationReceived = new NotificationDto(email, messageReceived);
        NotificationDto notificationSend = new NotificationDto(email, messageSend);
        
        Map<String, Object> headers = new HashMap<>();
        headers.put(KafkaHeaders.TOPIC, "transaction-notification");
        ConsumerRecord<String, NotificationDto> record = 
                new ConsumerRecord<>("transaction-notification", 0, 0, "key", notificationReceived);
        
        ResponseEntity<NotificationDto> responseEntity = new ResponseEntity<>(notificationSend, HttpStatus.OK);

        when(restTemplate.getForEntity(notificationUrl, NotificationDto.class)).thenReturn(responseEntity);
        
        service.consumeMessage(record);

        verify(restTemplate, times(1)).getForEntity(notificationUrl, NotificationDto.class);
        verify(logger, times(1)).info("Mensagem de notificação recebida: " + messageReceived);
        verify(logger, times(1)).info("Notificação enviada com sucesso para o e-mail " + email);
        verify(logger, times(0)).error("Erro ao enviar notificação");
    }
    
}
