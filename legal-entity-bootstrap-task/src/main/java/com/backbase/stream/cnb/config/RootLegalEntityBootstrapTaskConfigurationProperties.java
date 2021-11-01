package com.backbase.stream.cnb.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "backbase.stream.bootstrap")
public class RootLegalEntityBootstrapTaskConfigurationProperties {

    private String legalEntityLocation;
    private String productCatalogLocation;

}
