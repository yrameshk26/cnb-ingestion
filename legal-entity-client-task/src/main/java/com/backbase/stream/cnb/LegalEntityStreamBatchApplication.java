package com.backbase.stream.cnb;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication(scanBasePackages = {"com.backbase.stream.cnb", "com.backbase.stream"})
public class LegalEntityStreamBatchApplication {

    public static void main(String[] args) {
        SpringApplication springApplication = new SpringApplication(LegalEntityStreamBatchApplication.class);
        springApplication.setWebApplicationType(WebApplicationType.NONE);
        springApplication.run(args);
    }
}
