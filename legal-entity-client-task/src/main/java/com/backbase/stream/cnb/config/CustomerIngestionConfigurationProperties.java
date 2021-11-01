package com.backbase.stream.cnb.config;

import com.backbase.stream.legalentity.model.LegalEntity;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@EnableConfigurationProperties
@ConfigurationProperties(prefix = "customer.retail")
public class CustomerIngestionConfigurationProperties {
    private LegalEntity legalEntity;

}
