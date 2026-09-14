package com.tefutam.springbatchindustryplatform.config;

import com.tefutam.springbatchindustryplatform.healthcare.HealthcareClaim;
import com.tefutam.springbatchindustryplatform.healthcare.HealthcareClaimProcessor;
import com.tefutam.springbatchindustryplatform.healthcare.PrioritizedHealthcareClaim;
import javax.sql.DataSource;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.job.builder.JobBuilder;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.step.builder.StepBuilder;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.FlatFileItemWriter;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.builder.FlatFileItemWriterBuilder;
import org.springframework.batch.item.file.transform.BeanWrapperFieldExtractor;
import org.springframework.batch.item.file.transform.DelimitedLineAggregator;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.io.WritableResource;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
public class HealthcareBatchConfiguration {

    @Bean
    public Job healthcareClaimsJob(JobRepository jobRepository, @Qualifier("healthcareClaimsStep") Step healthcareClaimsStep) {
        return new JobBuilder("healthcareClaimsJob", jobRepository)
                .incrementer(new RunIdIncrementer())
                .start(healthcareClaimsStep)
                .build();
    }

    @Bean
    public Step healthcareClaimsStep(
            JobRepository jobRepository,
            PlatformTransactionManager transactionManager,
            IndustryJobProperties properties,
            FlatFileItemReader<HealthcareClaim> healthcareClaimReader,
            HealthcareClaimProcessor healthcareClaimProcessor,
            FlatFileItemWriter<PrioritizedHealthcareClaim> healthcareClaimWriter) {
        return new StepBuilder("healthcareClaimsStep", jobRepository)
                .<HealthcareClaim, PrioritizedHealthcareClaim>chunk(properties.getHealthcare().getChunkSize(), transactionManager)
                .reader(healthcareClaimReader)
                .processor(healthcareClaimProcessor)
                .writer(healthcareClaimWriter)
                .build();
    }

    @Bean
    public FlatFileItemReader<HealthcareClaim> healthcareClaimReader(
            IndustryJobProperties properties,
            ResourceLoader resourceLoader) {
        return new FlatFileItemReaderBuilder<HealthcareClaim>()
                .name("healthcareClaimReader")
                .delimited()
                .names("claimId", "patientId", "providerId", "diagnosisCode", "claimAmount")
                .fieldSetMapper(fieldSet -> new HealthcareClaim(
                        fieldSet.readString("claimId"),
                        fieldSet.readString("patientId"),
                        fieldSet.readString("providerId"),
                        fieldSet.readString("diagnosisCode"),
                        fieldSet.readBigDecimal("claimAmount")))
                .linesToSkip(1)
                .resource(resourceLoader.getResource(properties.getHealthcare().getInput()))
                .build();
    }

    @Bean
    public FlatFileItemWriter<PrioritizedHealthcareClaim> healthcareClaimWriter(
            IndustryJobProperties properties,
            ResourceLoader resourceLoader) {
        BeanWrapperFieldExtractor<PrioritizedHealthcareClaim> fieldExtractor = new BeanWrapperFieldExtractor<>();
        fieldExtractor.setNames(new String[]{"claimId", "patientId", "providerId", "diagnosisCode", "claimAmount", "priority"});
        fieldExtractor.afterPropertiesSet();

        DelimitedLineAggregator<PrioritizedHealthcareClaim> lineAggregator = new DelimitedLineAggregator<>();
        lineAggregator.setDelimiter(",");
        lineAggregator.setFieldExtractor(fieldExtractor);

        return new FlatFileItemWriterBuilder<PrioritizedHealthcareClaim>()
                .name("healthcareClaimWriter")
                .resource((WritableResource) resourceLoader.getResource(properties.getHealthcare().getOutput()))
                .headerCallback(writer -> writer.write("claimId,patientId,providerId,diagnosisCode,claimAmount,priority"))
                .lineAggregator(lineAggregator)
                .shouldDeleteIfExists(true)
                .build();
    }
}
