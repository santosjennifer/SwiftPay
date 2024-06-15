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
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.kafka.test.context.EmbeddedKafka;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.github.client.NotificationClient;
import com.github.dto.NotificationDto;

@ExtendWith({SpringExtension.class, MockitoExtension.class})
@SpringBootTest
@ActiveProfiles("test")
@EmbeddedKafka(partitions = 1, brokerProperties = { "listeners=PLAINTEXT://localhost:9092", "port=9092" })
public class NotificationConsumerServiceImplTest {

    @Autowired
    private NotificationConsumerServiceImpl service;

    @MockBean
    private NotificationClient notificationClient;
    
    @Mock
    private Logger logger;

    @BeforeEach
    public void setup() {
        service.setLogger(logger);
    }
    
    @Test
    @DisplayName("Consume mensagem de notificação do tópico e retorna erro ao enviar notificação")
    public void consumeMessageErrorTest() {
        String email = "test@example.com";
        String message = "Você recebeu um pagamento de R$10.00 enviado por Gustavo Perreira";

        NotificationDto notification = new NotificationDto(email, message);
        
        Map<String, Object> headers = new HashMap<>();
        headers.put(KafkaHeaders.TOPIC, "transaction-notification");
        ConsumerRecord<String, NotificationDto> record = 
                new ConsumerRecord<>("transaction-notification", 0, 0, "key", notification);

        ResponseEntity<Void> response = new ResponseEntity<>(HttpStatus.GATEWAY_TIMEOUT);

        when(notificationClient.sendNotification(notification)).thenReturn(response);
        
        service.consumeMessage(record);

        verify(notificationClient, times(1)).sendNotification(notification);
        verify(logger, times(1)).info("Mensagem de notificação recebida: " + message);
        verify(logger, times(1)).error("Erro ao enviar notificação.");
        verify(logger, times(0)).info("Notificação enviada com sucesso para o e-mail " + email);
    }
    
    
    @Test
    @DisplayName("Consume mensagem de notificação do tópico e retorna sucesso ao enviar notificação")
    public void consumeMessageSuccessTest() {
        String email = "test@example.com";
        String message = "Você recebeu um pagamento de R$10.00 enviado por Gustavo Perreira";

        NotificationDto notification = new NotificationDto(email, message);
        
        Map<String, Object> headers = new HashMap<>();
        headers.put(KafkaHeaders.TOPIC, "transaction-notification");
        ConsumerRecord<String, NotificationDto> record = 
                new ConsumerRecord<>("transaction-notification", 0, 0, "key", notification);
        
        ResponseEntity<Void> response = new ResponseEntity<>(HttpStatus.NO_CONTENT);

        when(notificationClient.sendNotification(notification)).thenReturn(response);
        
        service.consumeMessage(record);

        verify(notificationClient, times(1)).sendNotification(notification);
        verify(logger, times(1)).info("Mensagem de notificação recebida: " + message);
        verify(logger, times(1)).info("Notificação enviada com sucesso para o e-mail " + email);
        verify(logger, times(0)).error("Erro ao enviar notificação.");
        verify(logger, times(0)).error("Serviço de notificação não está disponível, tente mais tarde!");
    }
    
}
