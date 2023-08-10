package org.account.bank.domain;

import org.account.bank.domain.exception.BankAccountNotFoundException;
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


}
