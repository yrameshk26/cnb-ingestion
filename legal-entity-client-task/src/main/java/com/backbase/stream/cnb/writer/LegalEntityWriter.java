package com.backbase.stream.cnb.writer;

import com.backbase.stream.LegalEntitySaga;
import com.backbase.stream.LegalEntityTask;
import com.backbase.stream.cnb.config.CustomerIngestionConfigurationProperties;
import com.backbase.stream.legalentity.model.LegalEntity;
import com.backbase.stream.worker.model.StreamTask;
import java.util.Collections;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.cloud.task.configuration.EnableTask;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import reactor.core.publisher.Flux;

import java.util.List;

@Slf4j
@EnableTask
@Configuration
@AllArgsConstructor
public class LegalEntityWriter {

    private final LegalEntitySaga legalEntitySaga;
    private final CustomerIngestionConfigurationProperties configurationProperties;

    @Bean
    public CommandLineRunner commandLineRunner() {
        return this::run;
    }

    private void run(String... args) {

        LegalEntity legalEntity = configurationProperties.getLegalEntity();

        if (legalEntity == null) {
            log.error("Failed to load Legal Entity Structure");
            System.exit(1);
        } else {
            log.info("Customer Legal Entity Structure: {}", legalEntity.getName());
            List<LegalEntity> aggregates = Collections.singletonList(legalEntity);

            Flux.fromIterable(aggregates)
                .map(LegalEntityTask::new)
                .flatMap(legalEntitySaga::executeTask)
                .doOnNext(StreamTask::logSummary)
                .collectList()
                .block();
            log.info("Finished Cutomer Legal Entity Structure");
            System.exit(0);
        }
    }

}
