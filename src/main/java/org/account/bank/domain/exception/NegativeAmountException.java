package org.account.bank.domain.exception;

public class NegativeAmountException extends Exception  {
    public NegativeAmountException(String message) {
        super(message);
    }
}
