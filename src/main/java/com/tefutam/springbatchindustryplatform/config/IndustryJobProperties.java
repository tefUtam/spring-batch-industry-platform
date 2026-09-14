package com.tefutam.springbatchindustryplatform.config;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

@Validated
@ConfigurationProperties(prefix = "app.jobs")
public class IndustryJobProperties {

    @Valid
    private final FileJobProperties finance = new FileJobProperties();

    @Valid
    private final FileJobProperties healthcare = new FileJobProperties();

    public FileJobProperties getFinance() {
        return finance;
    }

    public FileJobProperties getHealthcare() {
        return healthcare;
    }

    public static class FileJobProperties {

        @NotBlank
        private String input = "file:./samples/input.csv";

        @NotBlank
        private String output = "file:./build/output.csv";

        @Min(1)
        private int chunkSize = 50;

        public String getInput() {
            return input;
        }

        public void setInput(String input) {
            this.input = input;
        }

        public String getOutput() {
            return output;
        }

        public void setOutput(String output) {
            this.output = output;
        }

        public int getChunkSize() {
            return chunkSize;
        }

        public void setChunkSize(int chunkSize) {
            this.chunkSize = chunkSize;
        }
    }
}
