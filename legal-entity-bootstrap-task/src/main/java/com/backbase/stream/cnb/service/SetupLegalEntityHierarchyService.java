package com.backbase.stream.cnb.service;

import com.backbase.stream.LegalEntitySaga;
import com.backbase.stream.LegalEntityTask;
import com.backbase.stream.legalentity.model.LegalEntity;
import com.backbase.stream.productcatalog.ProductCatalogService;
import com.backbase.stream.productcatalog.model.ProductCatalog;
import com.backbase.stream.service.AccessGroupService;
import com.backbase.stream.cnb.config.RootLegalEntityBootstrapTaskConfigurationProperties;
import com.backbase.stream.cnb.service.DataIngestionValidatorService;
import com.backbase.stream.cnb.service.EnvironmentDetailsEnricher;
import com.backbase.stream.worker.exception.StreamTaskException;
import com.backbase.stream.worker.model.StreamTask;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.cloud.task.configuration.EnableTask;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

import java.util.Collections;
import java.util.List;

/**
 * @copyright (C) 2020, Backbase
 * @Version 1.0
 * @Since 23. Jun 2020 11:26 AM
 */
@Component
@EnableTask
@AllArgsConstructor
@Slf4j
public class SetupLegalEntityHierarchyService {

    private final LegalEntitySaga legalEntitySaga;
    private final LegalEntity legalEntityHierarchy;
    private final ProductCatalog productCatalog;
    private final ProductCatalogService productCatalogService;
    private final EnvironmentDetailsEnricher envDetailEnricher;
    private final DataIngestionValidatorService validatorService;

    @Bean
    public CommandLineRunner commandLineRunner() {
        return this::run;
    }

    private void run(String... args) throws InterruptedException  {
        if (productCatalog == null) {
            log.info("Product Catalog not found in bootstrap config. Skipping creation");
        } else {
            bootstrapProductCatalog(productCatalog);
        }

        if (legalEntityHierarchy == null) {
            log.error("Failed to load Legal Entity Structure");
        } else {
            envDetailEnricher.enrich(legalEntityHierarchy);
            bootstrapLegalEntities(legalEntityHierarchy);
            validatorService.validateIngestedData(legalEntityHierarchy, productCatalog);
        }
    }

    private void bootstrapLegalEntities(LegalEntity legalEntity) {
        log.info("Bootstrapping Root Legal Entity Structure: {}", legalEntity.getName());
        List<LegalEntity> aggregates = Collections.singletonList(legalEntity);

        Flux.fromIterable(aggregates)
            .map(LegalEntityTask::new)
            .flatMap(legalEntitySaga::executeTask)
            .doOnNext(StreamTask::logSummary)
            .doOnError(StreamTaskException.class, throwable -> {
                log.error("Failed to bootstrap legal entities: ", throwable);
                throwable.getTask().logSummary();
            })
            .collectList()
            .block();
        log.info("Finished bootstrapping Legal Entity Structure");
    }

    private void bootstrapProductCatalog(ProductCatalog productCatalog) {
        log.info("Bootstrapping Product Catalog");
        productCatalogService.setupProductCatalog(productCatalog);
        log.info("Successfully Bootstrapped Product Catalog");
    }

}
