package com.tefutam.springbatchindustryplatform.finance;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.math.BigDecimal;
import org.junit.jupiter.api.Test;

class FinanceTransactionProcessorTest {

    private final FinanceTransactionProcessor processor = new FinanceTransactionProcessor();

    @Test
    void assignsHighRiskToLargeTransactions() throws Exception {
        ScreenedFinanceTransaction transaction = processor.process(
                new FinanceTransaction(" TRX-1 ", " ACC-1 ", "usd", new BigDecimal("15000")));

        assertThat(transaction.riskTier()).isEqualTo("HIGH");
        assertThat(transaction.currency()).isEqualTo("USD");
        assertThat(transaction.transactionId()).isEqualTo("TRX-1");
    }

    @Test
    void rejectsUnsupportedCurrencies() {
        assertThatThrownBy(() -> processor.process(
                new FinanceTransaction("TRX-1", "ACC-1", "ngn", new BigDecimal("10"))))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Unsupported transaction currency");
    }
}
