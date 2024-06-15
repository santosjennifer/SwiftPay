package com.github.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;

import com.github.dto.AuthorizerDto;

@FeignClient(name = "authorization", url = "${api.authorization}")
public interface AuthorizationClient {

	@GetMapping
	public ResponseEntity<AuthorizerDto> isAuthorized();
	
}
