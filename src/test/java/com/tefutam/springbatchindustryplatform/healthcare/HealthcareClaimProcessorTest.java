package com.tefutam.springbatchindustryplatform.healthcare;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.math.BigDecimal;
import org.junit.jupiter.api.Test;

class HealthcareClaimProcessorTest {

    private final HealthcareClaimProcessor processor = new HealthcareClaimProcessor();

    @Test
    void assignsHighPriorityToLargeClaims() throws Exception {
        PrioritizedHealthcareClaim claim = processor.process(
                new HealthcareClaim(" CLM-1 ", " PAT-1 ", " PRV-1 ", " e11.9 ", new BigDecimal("9000")));

        assertThat(claim.priority()).isEqualTo("HIGH");
        assertThat(claim.diagnosisCode()).isEqualTo("E11.9");
        assertThat(claim.claimId()).isEqualTo("CLM-1");
    }

    @Test
    void rejectsMissingDiagnosisCode() {
        assertThatThrownBy(() -> processor.process(
                new HealthcareClaim("CLM-1", "PAT-1", "PRV-1", " ", new BigDecimal("100"))))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("Diagnosis code is required");
    }
}
