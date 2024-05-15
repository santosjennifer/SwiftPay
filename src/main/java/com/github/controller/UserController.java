package com.github.controller;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.github.controller.payload.UserRequest;
import com.github.controller.payload.UserResponse;
import com.github.dto.UserBalanceDto;
import com.github.dto.UserDto;
import com.github.enums.TransactionType;
import com.github.service.UserService;

import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/user")
@Tag(name = "User")
public class UserController {

	private UserService service;
	
	public UserController(UserService service) {
		this.service = service;
	}
	
	@PostMapping
	public ResponseEntity<UserResponse> createUser(@RequestBody @Valid UserRequest request){
		UserDto userDto = request.toDto();
		userDto = service.createUser(userDto);
		
		return new ResponseEntity<>(userDto.toResponse(), HttpStatus.CREATED);
	}
	
	@GetMapping
	public ResponseEntity<List<UserResponse>> findUsers(
		    @RequestParam(defaultValue = "0") int page,
		    @RequestParam(defaultValue = "10") int size
		) {
		Pageable pageable = PageRequest.of(page, size);
		Page<UserDto> users = service.findUsers(pageable);
		
	    if(users.isEmpty()) {
	        return ResponseEntity.ok(Collections.emptyList());
	    }
	    
	    List<UserResponse> response = users.getContent().stream()
	    		.map(UserDto::toResponse)
	    		.collect(Collectors.toList());
		
		return ResponseEntity.ok(response);
	}
	
	@GetMapping("{id}")
	public ResponseEntity<UserResponse> findUserById(@PathVariable Long id){
		Optional<UserDto> userDto = service.findUserById(id);
		
		if (userDto.isPresent()) {
			return ResponseEntity.ok(userDto.get().toResponse());
		}
		
		return ResponseEntity.notFound().build();
	}
	
	@PostMapping("deposit")
	public ResponseEntity<UserBalanceDto> addCredit(@RequestBody @Valid UserBalanceDto request){
		UserBalanceDto response = service.updateUserBalance(request.getUser(), request.getAmount(), TransactionType.CREDIT);
		
		return new ResponseEntity<>(response, HttpStatus.OK);
	}
}
