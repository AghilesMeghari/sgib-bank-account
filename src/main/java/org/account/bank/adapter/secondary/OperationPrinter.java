package org.account.bank.adapter.secondary;


import org.account.bank.domain.Operation;
import org.account.bank.domain.OperationType;
import org.account.bank.domain.port.secondary.IOperationPrinter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public class OperationPrinter implements IOperationPrinter {

    private final String header = "Client ID | OperationType | Amount | Date";

    @Override
    public String print(List<Operation> operations, String clientId) {

        StringBuilder output = new StringBuilder();
        output.append(header);
        output.append("\n");

        for (Operation operation : operations) {
            OperationType operationType = operation.operationType();
            BigDecimal amount = operation.amount();
            LocalDate date = operation.date();

            output.append(clientId).append(" | ").append(operationType).append(" | ")
                    .append(amount).append(" | ").append(date);
            output.append("\n");
        }

        return output.toString();
    }
}

