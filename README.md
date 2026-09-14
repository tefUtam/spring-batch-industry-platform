# spring-batch-industry-platform

Production-ready Spring Batch application templates for finance and healthcare domains.

## What is included

- Spring Boot 3 + Spring Batch application
- Finance transaction screening job
- Healthcare claim validation job
- Actuator health, metrics, and info endpoints
- Validated domain-specific configuration properties
- Sample CSV inputs and targeted tests

## Jobs

### Finance risk job

Reads a finance transaction CSV, validates core fields, normalizes currencies, and flags high-value transactions.

Required columns:

```csv
transactionId,accountNumber,currency,amount
```

### Healthcare claims job

Reads a healthcare claim CSV, validates required identifiers, normalizes diagnosis codes, and prioritizes high-value claims.

Required columns:

```csv
claimId,patientId,providerId,diagnosisCode,claimAmount
```

## Run locally

```bash
./mvnw spring-boot:run -Dspring-boot.run.arguments="--spring.main.web-application-type=none --spring.batch.job.enabled=true --spring.batch.job.name=financeRiskJob --app.jobs.finance.input=file:./samples/finance-transactions.csv --app.jobs.finance.output=file:./build/finance-screened.csv"
```

```bash
./mvnw spring-boot:run -Dspring-boot.run.arguments="--spring.main.web-application-type=none --spring.batch.job.enabled=true --spring.batch.job.name=healthcareClaimsJob --app.jobs.healthcare.input=file:./samples/healthcare-claims.csv --app.jobs.healthcare.output=file:./build/healthcare-prioritized.csv"
```

## Build and test

```bash
./mvnw test
```
