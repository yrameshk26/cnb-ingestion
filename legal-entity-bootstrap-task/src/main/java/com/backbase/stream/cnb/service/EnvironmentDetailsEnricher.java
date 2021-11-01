package com.backbase.stream.cnb.service;

import com.backbase.stream.legalentity.model.JobProfileUser;
import com.backbase.stream.legalentity.model.LegalEntity;
import com.backbase.stream.legalentity.model.ProductGroup;
import com.backbase.stream.legalentity.model.User;
import com.backbase.stream.cnb.config.EnvironmentConfigurationProperties;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * Enrich the details about installation and runtime to user's externalId in LegalEntity data
 */
@Slf4j
@AllArgsConstructor
@Service
public class EnvironmentDetailsEnricher {

    private static final String HYPHEN = "-";
    private final EnvironmentConfigurationProperties envDetails;

    public void enrich(LegalEntity legalEntity) {
        Optional<LegalEntity> legalEntityOptional = updateLegalEntityExternalId(legalEntity);
        legalEntityOptional.ifPresent(entity -> processSubsidiaries(entity.getSubsidiaries()));
    }

    private void processProductGroups(List<ProductGroup> productGroups) {
        productGroups.forEach(productGroup -> {
            productGroup.setName(appendEnvDetails(productGroup.getName()));
            productGroup.getCurrentAccounts().forEach(
                currentAccount -> {
                    currentAccount.setExternalId(appendEnvDetails(currentAccount.getExternalId()));
                    currentAccount.setBBAN(appendEnvDetails(currentAccount.getBBAN()));
                });
            productGroup.getSavingAccounts().forEach(
                savingsAccount -> {
                    savingsAccount.setExternalId(appendEnvDetails(savingsAccount.getExternalId()));
                    savingsAccount.setBBAN(appendEnvDetails(savingsAccount.getBBAN()));
                });
        });
    }

    private void processSubsidiaries(List<LegalEntity> subsidiaries) {
        Optional.ofNullable(subsidiaries)
            .orElseGet(Collections::emptyList)
            .stream()
            .filter(Objects::nonNull)
            .forEach(legalEntity -> {
                updateLegalEntityExternalId(legalEntity);
                if (legalEntity.getUsers() != null) {
                    processUsers(legalEntity.getUsers());
                }
                if (legalEntity.getAdministrators() != null) {
                    processAdministrators(legalEntity.getAdministrators());
                }
                if (legalEntity.getProductGroups() != null) {
                    processProductGroups(legalEntity.getProductGroups());
                }
                processSubsidiaries(legalEntity.getSubsidiaries());
            });
    }

    private void processUsers(List<JobProfileUser> users) {
        users.forEach(user -> user.getUser().setExternalId(appendEnvDetails(user.getUser().getExternalId())));
    }

    private void processAdministrators(List<User> users) {
        users.forEach(user -> user.setExternalId(appendEnvDetails(user.getExternalId())));
    }

    private Optional<LegalEntity> updateLegalEntityExternalId(LegalEntity legalEntity) {
        return Optional.ofNullable(legalEntity)
            .map(data -> {
                data.setExternalId(appendEnvDetails(data.getExternalId()));
                return data;
            });
    }

    private String appendEnvDetails(String externalId) {
        String prefix = envDetails.getInstallation().concat(HYPHEN).concat(envDetails.getRuntime().concat(HYPHEN));
        return prefix.concat(externalId);
    }

}
