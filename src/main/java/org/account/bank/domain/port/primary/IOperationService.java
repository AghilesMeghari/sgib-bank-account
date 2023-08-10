package org.account.bank.domain.port.primary;

import org.account.bank.domain.exception.BankAccountNotFoundException;
import org.account.bank.domain.exception.InsufficientCreditException;
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

    /**
     * US 2
     * In order to retrieve some or all of my savings
     * As a bank client
     * I want to make a withdrawal from my account
     *
     * @param clientId : l'identifiant du compte client
     * @param amount    : le montant à retirer
     */
    void withdraw(String clientId, BigDecimal amount) throws InsufficientCreditException, NegativeAmountException, BankAccountNotFoundException;

    /**
     * In order to check my operations
     * As a bank client
     * I want to see the history (data, date, amount, balance) of my operations
     *
     * @param clientId : l'identifiant du compte client
     * @return La liste des opérations effectués sur le compte client
     */
    String printOperations(String clientId) throws BankAccountNotFoundException;

}
