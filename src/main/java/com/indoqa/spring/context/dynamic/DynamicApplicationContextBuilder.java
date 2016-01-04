/*
 * Licensed to the Indoqa Software Design und Beratung GmbH (Indoqa) under
 * one or more contributor license agreements. See the NOTICE file distributed
 * with this work for additional information regarding copyright ownership.
 * Indoqa licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.indoqa.spring.context.dynamic;

import static com.indoqa.lang.util.CollectionUtils.map;
import static com.indoqa.spring.context.dynamic.ComponentDefinition.Scope.*;
import static com.indoqa.spring.context.dynamic.ComponentDefinitionHelper.createSingleton;
import static org.springframework.beans.factory.config.ConfigurableBeanFactory.SCOPE_SINGLETON;

import java.util.HashSet;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.Executor;

import org.apache.commons.lang3.StringUtils;
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.beans.MutablePropertyValues;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.*;
import org.springframework.beans.factory.support.GenericBeanDefinition;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.transaction.annotation.TransactionManagementConfigurer;

import com.indoqa.lang.exception.InitializationFailedException;
import com.indoqa.spring.context.dynamic.ComponentDefinition.Scope;

/**
 * @deprecated Consider using Spring Java config.
 */
@Deprecated
public class DynamicApplicationContextBuilder {

    private AnnotationConfigApplicationContext applicationContext;

    private Set<String> externalBeanNames = new HashSet<String>(0);

    private boolean asyncEnabled;

    private boolean transactionsEnabled;

    public DynamicApplicationContextBuilder() {
        this.applicationContext = new AnnotationConfigApplicationContext();
    }

    private static BeanDefinition createBeanDefinition(ComponentDefinition componentDefinition) {
        GenericBeanDefinition beanDefinition = new GenericBeanDefinition();

        beanDefinition.setBeanClassName(componentDefinition.getComponentClass());
        beanDefinition.setScope(getScope(componentDefinition.getScope()));
        beanDefinition.setAutowireCandidate(true);
        beanDefinition.setAutowireMode(AutowireCapableBeanFactory.AUTOWIRE_BY_NAME);
        beanDefinition.setDependsOn(componentDefinition.getDependsOn());
        beanDefinition.setLazyInit(componentDefinition.isLazyInit());

        ConstructorArgumentValues constructorArgumentValues = new ConstructorArgumentValues();
        for (Object value : componentDefinition.getConstructorArguments()) {
            constructorArgumentValues.addIndexedArgumentValue(constructorArgumentValues.getArgumentCount(), value);
        }
        beanDefinition.setConstructorArgumentValues(constructorArgumentValues);

        MutablePropertyValues propertyValues = new MutablePropertyValues();
        for (PropertyValue propertyValue : componentDefinition.getPropertyValues()) {
            propertyValues.add(propertyValue.getName(), propertyValue.getValue());
        }
        beanDefinition.setPropertyValues(propertyValues);

        return beanDefinition;
    }

    private static String getScope(Scope scope) {
        if (scope == PROTOTYPE) {
            return ConfigurableBeanFactory.SCOPE_PROTOTYPE;
        }

        if (scope == SINGLETON) {
            return SCOPE_SINGLETON;
        }

        throw new IllegalArgumentException("Unexpected scope '" + scope + "'.");
    }

    public void add(ComponentDefinition componentDefinition) {
        BeanDefinition beanDefinition = createBeanDefinition(componentDefinition);
        this.applicationContext.registerBeanDefinition(componentDefinition.getName(), beanDefinition);
    }

    public void add(ComponentDefinitions componentDefinitions) {
        for (ComponentDefinition componentDefinition : componentDefinitions) {
            this.add(componentDefinition);
        }
    }

    public void addExternalBean(String name, Object externalBean) {
        if (!this.externalBeanNames.add(name)) {
            throw new IllegalStateException("External bean name '" + name + "' is already in use.");
        }

        this.applicationContext.getBeanFactory().registerSingleton(name, externalBean);
    }

    public void enableAsync(String executorThreadName, int maxPoolSize) {
        if (this.asyncEnabled) {
            throw new InitializationFailedException("Async handling has already been enabled!");
        }

        if (maxPoolSize < 1) {
            throw new InitializationFailedException("The max pool size must be at least 1!");
        }

        if (StringUtils.isBlank(executorThreadName)) {
            throw new InitializationFailedException("The executor thread name must not be empty or null!");
        }

        Properties properties = new Properties();
        properties.put(EnableAsyncConfiguration.PROPERTY_NAME, executorThreadName);
        properties.put(EnableAsyncConfiguration.PROPERTY_MAX_POOL_SIZE, String.valueOf(maxPoolSize));

        this.add(createSingleton(PropertyPlaceholderConfigurer.class, map("properties", properties)));

        this.applicationContext.register(EnableAsyncConfiguration.class);

        this.asyncEnabled = true;
    }

    public void enableTransactions() {
        if (this.transactionsEnabled) {
            throw new InitializationFailedException("Transaction handling has already been enabled!");
        }

        this.applicationContext.register(EnableTransactionConfiguration.class);

        this.transactionsEnabled = true;
    }

    public ApplicationContext getApplicationContext() {
        // refresh the application context to perform auto-wiring and/or post-processing between all internal beans
        this.applicationContext.refresh();

        // manually auto-wire external beans; Spring does not do this automatically, since they have no BeanDefinition
        for (String externalBeanName : this.externalBeanNames) {
            Object bean = this.applicationContext.getBean(externalBeanName);
            this.applicationContext.getAutowireCapableBeanFactory().autowireBean(bean);
        }

        return this.applicationContext;
    }

    public boolean isAsyncEnabled() {
        return this.asyncEnabled;
    }

    public void setParent(ApplicationContext parent) {
        this.applicationContext.setParent(parent);
    }

    @EnableAsync
    @Configuration
    public static class EnableAsyncConfiguration implements AsyncConfigurer {

        public static final String PROPERTY_NAME = "executor.thread-name-prefix";
        public static final String PROPERTY_MAX_POOL_SIZE = "executor.max-pool-size";

        @Value("${" + PROPERTY_NAME + "}")
        private String threadNamePrefix;

        @Value("${" + PROPERTY_MAX_POOL_SIZE + "}")
        private int maxPoolSize;

        @Override
        public Executor getAsyncExecutor() {
            ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();

            if (!this.threadNamePrefix.endsWith("-")) {
                this.threadNamePrefix += "-";
            }

            executor.setThreadNamePrefix(this.threadNamePrefix);
            executor.setCorePoolSize(this.maxPoolSize);
            executor.setMaxPoolSize(this.maxPoolSize);

            executor.initialize();

            return executor;
        }

        @Override
        public AsyncUncaughtExceptionHandler getAsyncUncaughtExceptionHandler() {
            return null;
        }
    }

    @EnableTransactionManagement
    @Configuration
    public static class EnableTransactionConfiguration implements TransactionManagementConfigurer, ApplicationContextAware {

        private ApplicationContext applicationContext;

        @Override
        public PlatformTransactionManager annotationDrivenTransactionManager() {
            return this.applicationContext.getBean(PlatformTransactionManager.class);
        }

        @Override
        public void setApplicationContext(ApplicationContext applicationContext) {
            this.applicationContext = applicationContext;
        }
    }
}
