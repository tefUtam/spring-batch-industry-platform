package com.tefutam.springbatchindustryplatform.config;

import com.tefutam.springbatchindustryplatform.finance.FinanceTransaction;
import com.tefutam.springbatchindustryplatform.finance.FinanceTransactionProcessor;
import com.tefutam.springbatchindustryplatform.finance.ScreenedFinanceTransaction;
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
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
public class FinanceBatchConfiguration {

    @Bean
    public Job financeRiskJob(JobRepository jobRepository, @Qualifier("financeRiskStep") Step financeRiskStep) {
        return new JobBuilder("financeRiskJob", jobRepository)
                .incrementer(new RunIdIncrementer())
                .start(financeRiskStep)
                .build();
    }

    @Bean
    public Step financeRiskStep(
            JobRepository jobRepository,
            PlatformTransactionManager transactionManager,
            IndustryJobProperties properties,
            FlatFileItemReader<FinanceTransaction> financeTransactionReader,
            FinanceTransactionProcessor financeTransactionProcessor,
            FlatFileItemWriter<ScreenedFinanceTransaction> financeTransactionWriter) {
        return new StepBuilder("financeRiskStep", jobRepository)
                .<FinanceTransaction, ScreenedFinanceTransaction>chunk(properties.getFinance().getChunkSize(), transactionManager)
                .reader(financeTransactionReader)
                .processor(financeTransactionProcessor)
                .writer(financeTransactionWriter)
                .build();
    }

    @Bean
    public FlatFileItemReader<FinanceTransaction> financeTransactionReader(
            IndustryJobProperties properties,
            ResourceLoader resourceLoader) {
        return new FlatFileItemReaderBuilder<FinanceTransaction>()
                .name("financeTransactionReader")
                .delimited()
                .names("transactionId", "accountNumber", "currency", "amount")
                .fieldSetMapper(fieldSet -> new FinanceTransaction(
                        fieldSet.readString("transactionId"),
                        fieldSet.readString("accountNumber"),
                        fieldSet.readString("currency"),
                        fieldSet.readBigDecimal("amount")))
                .linesToSkip(1)
                .resource(resourceLoader.getResource(properties.getFinance().getInput()))
                .build();
    }

    @Bean
    public FlatFileItemWriter<ScreenedFinanceTransaction> financeTransactionWriter(
            IndustryJobProperties properties,
            ResourceLoader resourceLoader) {
        BeanWrapperFieldExtractor<ScreenedFinanceTransaction> fieldExtractor = new BeanWrapperFieldExtractor<>();
        fieldExtractor.setNames(new String[]{"transactionId", "accountNumber", "currency", "amount", "riskTier"});
        fieldExtractor.afterPropertiesSet();

        DelimitedLineAggregator<ScreenedFinanceTransaction> lineAggregator = new DelimitedLineAggregator<>();
        lineAggregator.setDelimiter(",");
        lineAggregator.setFieldExtractor(fieldExtractor);

        return new FlatFileItemWriterBuilder<ScreenedFinanceTransaction>()
                .name("financeTransactionWriter")
                .resource((WritableResource) resourceLoader.getResource(properties.getFinance().getOutput()))
                .headerCallback(writer -> writer.write("transactionId,accountNumber,currency,amount,riskTier"))
                .lineAggregator(lineAggregator)
                .shouldDeleteIfExists(true)
                .build();
    }

    @Bean
    public PlatformTransactionManager transactionManager(DataSource dataSource) {
        return new DataSourceTransactionManager(dataSource);
    }
}
