package org.account.bank.domain.port.primary;

import org.account.bank.domain.exception.BankAccountNotFoundException;
import org.account.bank.domain.exception.NegativeAmountException;

import java.math.BigDecimal;

public interface IOperationService {

    /**
     * US 1
     * In order to save money
     * As a bank client
     * I want to make a deposit in my account
     *
     * @param clientId : l'identifiant du compte client
     * @param amount    : le montant à deposer
     */

    void deposit(String clientId, BigDecimal amount) throws NegativeAmountException, BankAccountNotFoundException;

}
