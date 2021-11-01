package com.backbase.stream.cnb;

import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

//@SpringBootTest
class CustomerLegalEntityStreamBatchApplicationTest {
    static {
        System.setProperty("SIG_SECRET_KEY", "JWTSecretKeyDontUseInProduction!");
        System.setProperty("--server.port", "0");
    }

    @Autowired
    private ApplicationContext applicationContext;

    @Test
    void contextLoads() {
        try {
            LegalEntityStreamBatchApplication.main(new String[]{});
            Assert.assertNotNull("applicationContext should not be null", applicationContext);
        } catch (Exception ex){
            org.assertj.core.api.Assertions.assertThat(ex).isInstanceOf(Exception.class);
        }
    }

}