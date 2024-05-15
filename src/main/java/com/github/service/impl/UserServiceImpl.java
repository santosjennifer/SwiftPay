package com.github.service.impl;

import java.math.BigDecimal;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.github.dto.UserBalanceDto;
import com.github.dto.UserDto;
import com.github.enums.TransactionType;
import com.github.exception.BusinessException;
import com.github.exception.UserNotFoundException;
import com.github.model.User;
import com.github.model.validator.UserDocumentFormat;
import com.github.repository.UserRepository;
import com.github.service.UserService;

import jakarta.transaction.Transactional;

@Service
public class UserServiceImpl implements UserService {

	private UserRepository repository;

	public UserServiceImpl(UserRepository repository) {
		this.repository = repository;
	}

	@Override
	@Transactional
	public UserDto createUser(UserDto userDto) {
		String formatDocument = UserDocumentFormat.replaceDocument(userDto.getDocument());
		 
		if (repository.existsByDocument(formatDocument) || repository.existsByEmail(userDto.getEmail())) {
			throw new BusinessException.DocumentOrEmailAlreadyExistsException();
		}

		User user = userDto.toUser();
		user.setDocument(formatDocument);
		user = repository.save(user);

		return user.toDto();
	}

	@Override
	public Page<UserDto> findUsers(Pageable pageable) {
		Page<User> users = repository.findAll(pageable);

		return users.map(User::toDto);
	}

	@Override
	public Optional<UserDto> findUserById(Long id) {
		User user = repository.findById(id).orElseThrow(() 
				-> new UserNotFoundException(id));

		return Optional.of(user.toDto());
	}

	@Override
	@Transactional
	public UserBalanceDto updateUserBalance(Long userId, BigDecimal value, TransactionType transactionType) {
		User user = repository.findById(userId).orElseThrow(() 
				-> new UserNotFoundException(userId));

		switch (transactionType) {
		case CREDIT:
			user.setBalance(user.getBalance().add(value));
			break;
		case DEBIT:
			if (user.getBalance().compareTo(value) < 0) {
				throw new BusinessException.NotEnoughBalanceException();
			}
			user.setBalance(user.getBalance().subtract(value));
			break;
		default:
			throw new IllegalArgumentException("Tipo de transação inválido.");
		}

		user = repository.save(user);
		UserBalanceDto userBalanceDto = new UserBalanceDto(userId, value);

		return userBalanceDto;
	}

}
