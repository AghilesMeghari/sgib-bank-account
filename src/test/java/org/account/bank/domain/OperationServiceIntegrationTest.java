package org.account.bank.domain;

import org.account.bank.adapter.secondary.OperationPrinter;
import org.account.bank.domain.exception.BankAccountNotFoundException;
import org.account.bank.domain.exception.InsufficientCreditException;
import org.account.bank.domain.exception.NegativeAmountException;
import org.account.bank.domain.port.primary.IOperationService;
import org.account.bank.domain.port.secondary.IOperationPrinter;
import org.account.bank.domain.port.secondary.ITimeService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("OperationService Integration Tests")
public class OperationServiceIntegrationTest {

    private static final String CLIENT_ID = "client01";

    private IOperationService operationService;

    private InMemoryAccountRepository accountRepository;

    private ITimeService timeService;

    private IOperationPrinter printer;

    @BeforeEach
    void setUp() {
        accountRepository = new InMemoryAccountRepository();
        accountRepository.addClient(CLIENT_ID);

        timeService = new ITimeService() {
            @Override
            public LocalDate utcNow() {
                return LocalDate.of(2023, 8, 10);
            }
        };
        printer = new OperationPrinter();
        operationService = new OperationService(timeService, accountRepository, printer);
    }

    @ParameterizedTest
    @CsvSource({"100, 40, 30, 30", "200, 150, 50, 0"})
    @DisplayName("Lorsque l'on consulte l'historique des opérations effectuées sur le compte '01'")
    void testGetOperations(BigDecimal firstDeposit, BigDecimal firstWithdraw, BigDecimal secondWithdraw, BigDecimal finalBalance) throws NegativeAmountException, BankAccountNotFoundException, InsufficientCreditException {

        operationService.deposit(CLIENT_ID, firstDeposit);
        operationService.withdraw(CLIENT_ID, firstWithdraw);
        operationService.withdraw(CLIENT_ID, secondWithdraw);

        assertThat(accountRepository.findLastOperationByClientId(CLIENT_ID).get().balance()).isEqualTo(finalBalance);

        String operationsHistory = operationService.printOperations(CLIENT_ID);

        String expected = String.format("""
                Client ID | OperationType | Amount | Date
                client01 | WITHDRAW | %s | 2023-08-10
                client01 | WITHDRAW | %s | 2023-08-10
                client01 | DEPOSIT | %s | 2023-08-10
                """, secondWithdraw, firstWithdraw, firstDeposit);

        assertThat(operationsHistory).isEqualTo(expected);

    }
}
