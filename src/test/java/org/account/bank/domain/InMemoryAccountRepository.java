package org.account.bank.domain;

import org.account.bank.domain.exception.BankAccountNotFoundException;
import org.account.bank.domain.port.secondary.IAccountRepository;

import java.util.*;

public class InMemoryAccountRepository implements IAccountRepository {

    private final HashMap<String, List<Operation>> bankAccounts = new HashMap<>();

    private final static String ACCOUNT_NOT_FOUND = "Compte inexistant : %s";

    @Override
    public Optional<Operation> findLastOperationByClientId(String clientId) throws BankAccountNotFoundException {
        if(!bankAccounts.containsKey(clientId))
            throw new BankAccountNotFoundException(String.format(ACCOUNT_NOT_FOUND, clientId));

        List<Operation> operations = bankAccounts.get(clientId);

        return !operations.isEmpty() ? Optional.of(operations.get(operations.size() - 1)): Optional.empty();
    }

    @Override
    public void addOperation(String clientId, Operation operation) throws BankAccountNotFoundException {
        if(!bankAccounts.containsKey(clientId)) {
            throw new BankAccountNotFoundException(String.format(ACCOUNT_NOT_FOUND, clientId));
        }

        bankAccounts.get(clientId).add(operation);
    }

    @Override
    public List<Operation> findOperationsByClientId(String clientId) throws BankAccountNotFoundException {
        if(!bankAccounts.containsKey(clientId))
            throw new BankAccountNotFoundException(String.format(ACCOUNT_NOT_FOUND, clientId));

        return Collections.unmodifiableList(bankAccounts.get(clientId));
    }

    public void addClient(String clientId) {
        bankAccounts.put(clientId, new ArrayList<>());
    }
}
