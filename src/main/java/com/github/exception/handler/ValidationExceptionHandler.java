package com.github.exception.handler;

import java.util.Arrays;
import java.util.stream.Collectors;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import com.github.enums.UserType;
import com.github.exception.BodyResponse;
import com.github.exception.TransactionNotFoundException;
import com.github.exception.BusinessException;
import com.github.exception.UserNotFoundException;

import jakarta.validation.ConstraintViolationException;

@ControllerAdvice
public class ValidationExceptionHandler {

	@ExceptionHandler(ConstraintViolationException.class)
	public ResponseEntity<BodyResponse> handlerValidationExceptions(ConstraintViolationException ex) {
		String errorMessage = ex.getConstraintViolations()
                .stream()
                .findFirst()
                .map(violation -> violation.getMessage())
                .orElse("Erro de validação");

		BodyResponse response = new BodyResponse(errorMessage);
		return new ResponseEntity<>(response, HttpStatus.UNPROCESSABLE_ENTITY);
	}
	
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<BodyResponse> handleValidationExceptions(MethodArgumentNotValidException ex) {
        FieldError fieldError = ex.getBindingResult().getFieldError();
        String errorMessage = fieldError.getDefaultMessage();

        BodyResponse response = new BodyResponse(errorMessage);
        return new ResponseEntity<>(response, HttpStatus.UNPROCESSABLE_ENTITY);
    }
	
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<BodyResponse> handleHttpMessageNotReadableException(HttpMessageNotReadableException ex) {
        String message = ex.getMessage();
        if (message.contains("UserType")) {
            String enumValues = Arrays.stream(UserType.values())
                    .map(Enum::name)
                    .collect(Collectors.joining(", "));

            message = String.format("Valores aceitos para o tipo de usuário: %s", enumValues);
        }

        BodyResponse response = new BodyResponse(message);
        return new ResponseEntity<>(response, HttpStatus.UNPROCESSABLE_ENTITY);
    }
    
    @ExceptionHandler({ BusinessException.DocumentOrEmailAlreadyExistsException.class })
    public ResponseEntity<BodyResponse> handleDocumentOrEmailAlreadyExistsException(BusinessException.DocumentOrEmailAlreadyExistsException ex) {
        BodyResponse response = new BodyResponse(ex.getMessage());
        return new ResponseEntity<>(response, HttpStatus.UNPROCESSABLE_ENTITY);
    }
    
    @ExceptionHandler({ BusinessException.SellerCannotTransferException.class })
    public ResponseEntity<BodyResponse> handleSellerCannotTransferException(BusinessException.SellerCannotTransferException ex) {
        BodyResponse response = new BodyResponse(ex.getMessage());
        return new ResponseEntity<>(response, HttpStatus.UNPROCESSABLE_ENTITY);
    }
    
    @ExceptionHandler({ BusinessException.PayerCannotBePayeeException.class })
    public ResponseEntity<BodyResponse> handlePayerCannotBePayeeException(BusinessException.PayerCannotBePayeeException ex) {
        BodyResponse response = new BodyResponse(ex.getMessage());
        return new ResponseEntity<>(response, HttpStatus.UNPROCESSABLE_ENTITY);
    }
    
    @ExceptionHandler({ BusinessException.NotEnoughBalanceException.class })
    public ResponseEntity<BodyResponse> handleNotEnoughBalanceException(BusinessException.NotEnoughBalanceException ex) {
        BodyResponse response = new BodyResponse(ex.getMessage());
        return new ResponseEntity<>(response, HttpStatus.UNPROCESSABLE_ENTITY);
    }
    
    @ExceptionHandler({ BusinessException.UnauthorizedTransactionException.class })
    public ResponseEntity<BodyResponse> handleUnauthorizedTransactionException(BusinessException.UnauthorizedTransactionException ex) {
        BodyResponse response = new BodyResponse(ex.getMessage());
        return new ResponseEntity<>(response, HttpStatus.UNPROCESSABLE_ENTITY);
    }
	
    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<BodyResponse> handleUserNotFoundException(UserNotFoundException ex) {
        BodyResponse response = new BodyResponse(ex.getMessage());
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }
    
    @ExceptionHandler(TransactionNotFoundException.class)
    public ResponseEntity<BodyResponse> handleTransactionNotFoundException(TransactionNotFoundException ex) {
        BodyResponse response = new BodyResponse(ex.getMessage());
        return new ResponseEntity<>(response, HttpStatus.NOT_FOUND);
    }

}
