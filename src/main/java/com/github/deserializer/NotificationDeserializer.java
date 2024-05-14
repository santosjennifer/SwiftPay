package com.github.deserializer;

import org.apache.kafka.common.serialization.Deserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.dto.NotificationDto;

public class NotificationDeserializer implements Deserializer<NotificationDto>{
	
	private static final Logger log = LoggerFactory.getLogger(NotificationDeserializer.class);

	@Override
	public NotificationDto deserialize(String topic, byte[] notification) {
		try {
			return new ObjectMapper().readValue(notification, NotificationDto.class);
		} catch (Exception e) {
			log.error("Erro ao desserializar a mensagem: ", e);
			return null;
		}
	}

}
