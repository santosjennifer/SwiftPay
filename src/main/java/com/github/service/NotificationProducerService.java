package com.github.service;

import com.github.dto.NotificationDto;

public interface NotificationProducerService {

	void sendMessage(NotificationDto notification);
	
}
