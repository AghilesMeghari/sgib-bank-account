package org.account.bank.domain;

import org.account.bank.domain.exception.BankAccountNotFoundException;
import org.account.bank.domain.exception.InsufficientCreditException;
import org.account.bank.domain.exception.NegativeAmountException;
import org.account.bank.domain.port.primary.IOperationService;
import org.account.bank.domain.port.secondary.IAccountRepository;
import org.account.bank.domain.port.secondary.ITimeService;

import java.math.BigDecimal;
import java.util.Optional;

public class OperationService implements IOperationService {

    private static final String NEGATIVE_DEPOSIT_AMOUNT = "Impossible de crediter ou de débiter le compte avec un montant négatif : %f";

    private static final String INSUFFICIENT_CREDIT = "Crédit insuffisant pour retirer le montant : %f";


    private final ITimeService timeService;

    private final IAccountRepository accountRepository;


    public OperationService(ITimeService timeService, IAccountRepository accountRepository) {
        this.timeService = timeService;
        this.accountRepository = accountRepository;
    }

    @Override
    public void deposit(String clientId, BigDecimal amount) throws NegativeAmountException, BankAccountNotFoundException {

        checkAmountValue(amount);

        BigDecimal balance = getBalance(clientId);

        Operation newOperation = new Operation(timeService.utcNow(), amount, OperationType.DEPOSIT, balance.add(amount));
        accountRepository.addOperation(clientId, newOperation);

    }

    @Override
    public void withdraw(String clientId, BigDecimal amount) throws InsufficientCreditException, NegativeAmountException, BankAccountNotFoundException {
        checkAmountValue(amount);

        BigDecimal balance = getBalance(clientId);

        if (amount.compareTo(balance) <= 0) {
            Operation newOperation = new Operation(timeService.utcNow(), amount, OperationType.WITHDRAW, balance.subtract(amount));
            accountRepository.addOperation(clientId, newOperation);
        } else {
            String InsufficientCreditMessage = String.format(INSUFFICIENT_CREDIT, balance);
            throw new InsufficientCreditException(InsufficientCreditMessage);
        }
    }

    private void checkAmountValue(BigDecimal amount) throws NegativeAmountException {

        if (amount.compareTo(BigDecimal.ZERO) <= 0) {
            String amountMessage = String.format(NEGATIVE_DEPOSIT_AMOUNT, amount);
            throw new NegativeAmountException(amountMessage);
        }
    }

    private BigDecimal getBalance(String clientId) throws BankAccountNotFoundException {

        Optional<Operation> lastOperation = accountRepository.findLastOperationByClientId(clientId);
        return lastOperation.map(Operation::balance).orElse(BigDecimal.ZERO);
    }
}
