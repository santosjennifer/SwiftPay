package com.github.exception;

@SuppressWarnings("serial")
public class UserNotFoundException extends RuntimeException {

	public UserNotFoundException(Long id) {
		super("Registro não encontrado para o id " + id);
	}
}
