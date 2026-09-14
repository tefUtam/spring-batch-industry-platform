package com.tefutam.springbatchindustryplatform.finance;

import java.math.BigDecimal;

public record ScreenedFinanceTransaction(
        String transactionId,
        String accountNumber,
        String currency,
        BigDecimal amount,
        String riskTier
) {
}
