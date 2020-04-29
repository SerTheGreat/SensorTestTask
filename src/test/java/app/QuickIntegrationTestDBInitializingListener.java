package app;

import liquibase.integration.spring.SpringLiquibase;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.TestContext;
import org.springframework.test.context.TestExecutionListener;

public class QuickIntegrationTestDBInitializingListener implements TestExecutionListener {

    @Autowired
    SpringLiquibase liquibase;

    @Override
    public void beforeTestMethod(TestContext testContext) throws Exception {
        testContext.getApplicationContext()
                .getAutowireCapableBeanFactory()
                .autowireBean(this);
        liquibase.afterPropertiesSet();
    }
}
