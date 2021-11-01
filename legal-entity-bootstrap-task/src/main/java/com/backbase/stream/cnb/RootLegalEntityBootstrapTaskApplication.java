package com.backbase.stream.cnb;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.WebApplicationType;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import reactor.core.publisher.Hooks;


@SpringBootApplication(scanBasePackages = {"com.backbase.stream.cnb", "com.backbase.stream"})
public class RootLegalEntityBootstrapTaskApplication {

    public static void main(String[] args) {
        SpringApplication springApplication = new SpringApplication(RootLegalEntityBootstrapTaskApplication.class);
        springApplication.setWebApplicationType(WebApplicationType.NONE);
        springApplication.run(args);
    }
}