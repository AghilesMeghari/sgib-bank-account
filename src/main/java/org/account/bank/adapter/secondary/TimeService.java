package org.account.bank.adapter.secondary;

import org.account.bank.domain.port.secondary.ITimeService;

import java.time.LocalDate;
import java.time.ZoneId;

public class TimeService implements ITimeService {
    @Override
    public LocalDate utcNow() {
        return LocalDate.now(ZoneId.of("UTC"));
    }
}
