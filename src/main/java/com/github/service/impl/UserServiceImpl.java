package com.github.service.impl;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.github.dto.UserDto;
import com.github.exception.BusinessException;
import com.github.exception.UserNotFoundException;
import com.github.model.User;
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
		if (repository.existsByDocument(userDto.getDocument()) || repository.existsByEmail(userDto.getEmail())) {
			throw new BusinessException.DocumentOrEmailAlreadyExistsException();
		}

		return saveUser(userDto);
	}
	
	@Override
	public Page<UserDto> findUsers(Pageable pageable) {
		Page<User> users = repository.findAll(pageable);

		return users.map(User::toDto);
	}

	@Override
	public Optional<UserDto> findUserById(Long id) {
		User user = repository.findById(id)
				.orElseThrow(() -> new UserNotFoundException(id));

		return Optional.of(user.toDto());
	}
	
	@Override
	@Transactional
	public UserDto saveUser(UserDto userDto) {
		User user = userDto.toUser();
		user = repository.save(user);

		return user.toDto();
	}

}
