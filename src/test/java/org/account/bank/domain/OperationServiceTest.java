package org.account.bank.domain;

import org.account.bank.domain.exception.BankAccountNotFoundException;
import org.account.bank.domain.exception.InsufficientCreditException;
import org.account.bank.domain.exception.NegativeAmountException;
import org.account.bank.domain.port.secondary.IAccountRepository;
import org.account.bank.domain.port.secondary.ITimeService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@DisplayName("OperationService Tests")
@ExtendWith(MockitoExtension.class)
public class OperationServiceTest {

    private static final String CLIENT_01 = "client01";
    @InjectMocks
    private OperationService operationService;

    @Mock
    private IAccountRepository accountRepository;

    @Mock
    private ITimeService timeService;


    @Test
    @DisplayName("Lorsque l'on credite le compte du client '01' avec des montants positifs")
    void testSimpleDeposits() throws NegativeAmountException, BankAccountNotFoundException {

        BigDecimal depositAmount = BigDecimal.valueOf(50);

        Operation operation = new Operation(LocalDate.EPOCH, depositAmount, OperationType.DEPOSIT, depositAmount);
        when(accountRepository.findLastOperationByClientId(CLIENT_01)).thenReturn(Optional.empty());
        when(timeService.utcNow()).thenReturn(LocalDate.EPOCH);

        operationService.deposit(CLIENT_01, depositAmount);

        verify(accountRepository).addOperation(CLIENT_01, operation);
    }


    @Test
    @DisplayName("Lorsque l'on credite le compte client '01' avec des montants négatifs")
    void testDeposit_whenAmount_isNegative() {

        BigDecimal depositAmount = BigDecimal.valueOf(-500);

        assertThrows(NegativeAmountException.class, () -> {
            operationService.deposit(CLIENT_01, depositAmount);
        });
        verifyNoInteractions(accountRepository);

    }

    @Test
    @DisplayName("Lorsque l'on deposer des montants de valeur nulle depuis le compte client 01")
    void testDeposit_whenAmount_isNull() {

        BigDecimal depositAmount = BigDecimal.valueOf(0);
        assertThrows(NegativeAmountException.class, () -> {
            operationService.deposit(CLIENT_01, depositAmount);
        });
        verifyNoInteractions(accountRepository);
    }

    @Test
    @DisplayName("Lorsque l'on deposer des montants dans un compte client non existant")
    void testDeposit_whenAccount_isNotFound() throws BankAccountNotFoundException {

        BigDecimal depositAmount = BigDecimal.valueOf(10);
        when(accountRepository.findLastOperationByClientId(CLIENT_01))
                .thenThrow(new BankAccountNotFoundException(String.format("Compte inexistant : %s", CLIENT_01)));
        assertThrows(BankAccountNotFoundException.class, () -> {
            operationService.deposit(CLIENT_01, depositAmount);
        });
        verifyNoMoreInteractions(accountRepository);
    }

    @Test
    @DisplayName("Lorsque l'on débite le compte client '01' dans le crédit est suffisant")
    void testSimpleWithdraw() throws NegativeAmountException, BankAccountNotFoundException, InsufficientCreditException {

        Operation lastOperation = new Operation(LocalDate.EPOCH, BigDecimal.valueOf(500), OperationType.DEPOSIT, BigDecimal.valueOf(500));

        BigDecimal withdrawalAmount = BigDecimal.valueOf(400);

        Operation operation = new Operation(LocalDate.EPOCH, withdrawalAmount, OperationType.WITHDRAW, BigDecimal.valueOf(100));
        when(accountRepository.findLastOperationByClientId(CLIENT_01)).thenReturn(Optional.of(lastOperation));
        when(timeService.utcNow()).thenReturn(LocalDate.EPOCH);

        operationService.withdraw(CLIENT_01, withdrawalAmount);

        verify(accountRepository).addOperation(CLIENT_01, operation);
        verifyNoMoreInteractions(accountRepository);
    }

    @Test
    @DisplayName("Lorsque l'on retire des montants négatifs depuis le compte client 01")
    void testWithdraw_whenAmount_isNegative() {

        BigDecimal withdrawalAmount = BigDecimal.valueOf(-50);
        assertThrows(NegativeAmountException.class, () -> {
            operationService.withdraw(CLIENT_01, withdrawalAmount);
        });
        verifyNoInteractions(accountRepository);
    }

    @Test
    @DisplayName("Lorsque l'on retire des montants de valeur nulle depuis le compte client 01")
    void testWithdraw_whenAmount_isNull() {

        BigDecimal withdrawalAmount = BigDecimal.valueOf(0);
        assertThrows(NegativeAmountException.class, () -> {
            operationService.withdraw(CLIENT_01, withdrawalAmount);
        });
        verifyNoInteractions(accountRepository);
    }

    @Test
    @DisplayName("Lorsque l'on retire des montants supérieurs à la valeure existante dans le compte client 01")
    void testWithdrawInsufficientCredit() throws BankAccountNotFoundException {

        Operation lastOperation = new Operation(LocalDate.EPOCH, BigDecimal.valueOf(40), OperationType.DEPOSIT, BigDecimal.valueOf(40));
        BigDecimal withdrawalAmount = BigDecimal.valueOf(500);

        when(accountRepository.findLastOperationByClientId(CLIENT_01)).thenReturn(Optional.of(lastOperation));

        assertThrows(InsufficientCreditException.class, () -> {
            operationService.withdraw(CLIENT_01, withdrawalAmount);
        });

        verify(accountRepository).findLastOperationByClientId(CLIENT_01);
        verifyNoMoreInteractions(accountRepository);
    }

    @Test
    @DisplayName("Lorsque l'on retirer des montants dans un compte client non existant")
    void testWithdraw_whenAccount_isNotFound() throws BankAccountNotFoundException {

        BigDecimal withDrawAmount = BigDecimal.valueOf(10);
        when(accountRepository.findLastOperationByClientId(CLIENT_01))
                .thenThrow(new BankAccountNotFoundException(String.format("Compte inexistant : %s", CLIENT_01)));

        assertThrows(BankAccountNotFoundException.class, () -> {
            operationService.withdraw(CLIENT_01, withDrawAmount);
        });
        verify(accountRepository).findLastOperationByClientId(CLIENT_01);
        verifyNoMoreInteractions(accountRepository);
    }


}
