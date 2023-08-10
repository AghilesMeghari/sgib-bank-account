package org.account.bank.domain.port.secondary;

import org.account.bank.domain.Operation;

import java.util.List;

public interface IOperationPrinter {
    String print(List<Operation> operations, String clientId);
}
