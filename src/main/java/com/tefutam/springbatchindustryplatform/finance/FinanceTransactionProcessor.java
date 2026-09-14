package com.tefutam.springbatchindustryplatform.finance;

import java.math.BigDecimal;
import java.util.Set;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
public class FinanceTransactionProcessor implements ItemProcessor<FinanceTransaction, ScreenedFinanceTransaction> {

    private static final Set<String> SUPPORTED_CURRENCIES = Set.of("USD", "EUR", "GBP");
    private static final BigDecimal HIGH_VALUE_THRESHOLD = new BigDecimal("10000");

    @Override
    public ScreenedFinanceTransaction process(FinanceTransaction item) {
        if (!StringUtils.hasText(item.transactionId()) || !StringUtils.hasText(item.accountNumber())) {
            throw new IllegalArgumentException("Transaction and account identifiers are required");
        }
        if (item.amount() == null || item.amount().signum() <= 0) {
            throw new IllegalArgumentException("Transaction amount must be positive");
        }

        String currency = item.currency() == null ? "" : item.currency().trim().toUpperCase();
        if (!SUPPORTED_CURRENCIES.contains(currency)) {
            throw new IllegalArgumentException("Unsupported transaction currency: " + currency);
        }

        String riskTier = item.amount().compareTo(HIGH_VALUE_THRESHOLD) >= 0 ? "HIGH" : "STANDARD";
        return new ScreenedFinanceTransaction(
                item.transactionId().trim(),
                item.accountNumber().trim(),
                currency,
                item.amount(),
                riskTier
        );
    }
}
