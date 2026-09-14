package com.tefutam.springbatchindustryplatform.finance;

import java.math.BigDecimal;

public record FinanceTransaction(
        String transactionId,
        String accountNumber,
        String currency,
        BigDecimal amount
) {
}
