package com.github.exception;

@SuppressWarnings("serial")
public class TransactionNotFoundException extends RuntimeException {

	public TransactionNotFoundException() {
		super("Transação não encontrada.");
	}
}
