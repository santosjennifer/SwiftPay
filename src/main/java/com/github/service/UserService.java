package com.github.service;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.github.dto.UserDto;

public interface UserService {
	
	UserDto createUser(UserDto userDto);
	Page<UserDto> findUsers(Pageable pageable);
	Optional<UserDto> findUserById(Long id);
	UserDto saveUser(UserDto userDto);

}
