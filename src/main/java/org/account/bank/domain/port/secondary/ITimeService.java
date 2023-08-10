package org.account.bank.domain.port.secondary;

import java.time.LocalDate;

public interface ITimeService {
    LocalDate utcNow();
}
