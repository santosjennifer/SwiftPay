package com.github.service;

import org.apache.kafka.clients.consumer.ConsumerRecord;

import com.github.dto.NotificationDto;

public interface NotificationConsumerService {

	void consumeMessage(ConsumerRecord<String, NotificationDto> payload);
	
}
