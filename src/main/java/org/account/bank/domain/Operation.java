package org.account.bank.domain;

import java.math.BigDecimal;
import java.time.LocalDate;

public record Operation(LocalDate date, BigDecimal amount, OperationType operationType, BigDecimal balance) {
}
