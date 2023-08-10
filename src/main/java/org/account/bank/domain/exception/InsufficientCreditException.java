package org.account.bank.domain.exception;

public class InsufficientCreditException extends Exception{
    public InsufficientCreditException(String message) {
        super(message);
    }
}
