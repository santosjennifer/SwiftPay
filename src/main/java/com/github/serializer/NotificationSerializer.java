package com.github.serializer;

import org.apache.kafka.common.serialization.Serializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.dto.NotificationDto;

public class NotificationSerializer implements Serializer<NotificationDto> {

	private static final Logger log = LoggerFactory.getLogger(NotificationSerializer.class);

	@Override
	public byte[] serialize(String topic, NotificationDto notification) {
		try {
			return new ObjectMapper().writeValueAsBytes(notification);
		} catch (JsonProcessingException e) {
			log.error("Erro ao serializar a mensagem: ", e.getMessage());
			return null;
		}
	}
	
}
