package com.tefutam.springbatchindustryplatform.healthcare;

import java.math.BigDecimal;

public record PrioritizedHealthcareClaim(
        String claimId,
        String patientId,
        String providerId,
        String diagnosisCode,
        BigDecimal claimAmount,
        String priority
) {
}
