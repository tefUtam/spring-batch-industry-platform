package com.tefutam.springbatchindustryplatform.healthcare;

import java.math.BigDecimal;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

@Component
public class HealthcareClaimProcessor implements ItemProcessor<HealthcareClaim, PrioritizedHealthcareClaim> {

    private static final BigDecimal HIGH_PRIORITY_THRESHOLD = new BigDecimal("5000");

    @Override
    public PrioritizedHealthcareClaim process(HealthcareClaim item) {
        if (!StringUtils.hasText(item.claimId())
                || !StringUtils.hasText(item.patientId())
                || !StringUtils.hasText(item.providerId())) {
            throw new IllegalArgumentException("Claim, patient, and provider identifiers are required");
        }
        if (!StringUtils.hasText(item.diagnosisCode())) {
            throw new IllegalArgumentException("Diagnosis code is required");
        }
        if (item.claimAmount() == null || item.claimAmount().signum() <= 0) {
            throw new IllegalArgumentException("Claim amount must be positive");
        }

        String normalizedDiagnosisCode = item.diagnosisCode().trim().toUpperCase();
        String priority = item.claimAmount().compareTo(HIGH_PRIORITY_THRESHOLD) >= 0 ? "HIGH" : "STANDARD";
        return new PrioritizedHealthcareClaim(
                item.claimId().trim(),
                item.patientId().trim(),
                item.providerId().trim(),
                normalizedDiagnosisCode,
                item.claimAmount(),
                priority
        );
    }
}
