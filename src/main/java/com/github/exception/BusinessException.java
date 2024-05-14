package com.github.exception;

@SuppressWarnings("serial")
public class BusinessException {
	
	public static class DocumentOrEmailAlreadyExistsException extends RuntimeException {
        public DocumentOrEmailAlreadyExistsException() {
            super("CPF/CNPJ ou e-mail já cadastrado.");
        }
    }

    public static class SellerCannotTransferException extends RuntimeException {
        public SellerCannotTransferException() {
            super("Logistas não podem efetuar transferência.");
        }
    }
    
    public static class PayerCannotBePayeeException extends RuntimeException {
        public PayerCannotBePayeeException() {
            super("O pagador não pode ser o mesmo que o beneficiário.");
        }
    }
    
    public static class NotEnoughBalanceException extends RuntimeException {
        public NotEnoughBalanceException() {
            super("Saldo insuficiente.");
        }
    }
    
    public static class UnauthorizedTransactionException extends RuntimeException {
        public UnauthorizedTransactionException() {
            super("Transação não autorizada.");
        }
    }
    
    
    
    
	
}
