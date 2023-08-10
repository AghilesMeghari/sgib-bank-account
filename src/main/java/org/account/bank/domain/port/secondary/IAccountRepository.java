package org.account.bank.domain.port.secondary;

import org.account.bank.domain.Operation;
import org.account.bank.domain.exception.BankAccountNotFoundException;

import java.util.List;
import java.util.Optional;

public interface IAccountRepository {

    Optional<Operation> findLastOperationByClientId(String accountId) throws BankAccountNotFoundException;

    void addOperation(String clientId, Operation operation) throws BankAccountNotFoundException;

    List<Operation> findOperationsByClientId(String accountId) throws BankAccountNotFoundException;


}
