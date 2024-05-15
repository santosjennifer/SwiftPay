package com.github.service;

import java.math.BigDecimal;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.github.dto.UserBalanceDto;
import com.github.dto.UserDto;
import com.github.enums.TransactionType;

public interface UserService {
	
	UserDto createUser(UserDto userDto);
	Page<UserDto> findUsers(Pageable pageable);
	Optional<UserDto> findUserById(Long id);
	UserBalanceDto updateUserBalance(Long userId, BigDecimal value, TransactionType transactionType);

}
