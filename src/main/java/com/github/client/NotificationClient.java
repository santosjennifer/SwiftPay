package com.github.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;

import com.github.dto.NotificationDto;

@FeignClient(name = "notification", url = "${api.notification}")
public interface NotificationClient {
	
	@PostMapping
	public ResponseEntity<Void> sendNotification(NotificationDto notificationDto);

}
