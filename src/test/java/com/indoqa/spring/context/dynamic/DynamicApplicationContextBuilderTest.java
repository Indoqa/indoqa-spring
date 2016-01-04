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

import static com.indoqa.spring.context.dynamic.ComponentDefinitionHelper.createSingleton;
import static junit.framework.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.indoqa.lang.exception.InitializationFailedException;

public class DynamicApplicationContextBuilderTest {

    @Test
    public void executeFailingAsyncServiceCall() throws InterruptedException {
        DynamicApplicationContextBuilder builder = new DynamicApplicationContextBuilder();
        // "forget" to enable async handling
        // builder.enableAsync("Unit-Test", 5);
        builder.add(createSingleton(AsyncServiceImpl.class));

        ApplicationContext applicationContext = builder.getApplicationContext();

        AsyncService asyncService = applicationContext.getBean(AsyncService.class);

        asyncService.execute();
        // service must not be executed asynchronously, so isExecuted() should return "true" immediately
        assertTrue(asyncService.isExecuted());
        Thread.sleep(100);
        assertTrue(asyncService.isExecuted());
    }

    @Test
    public void executeFailingTransactionalMethod() throws Exception {
        DynamicApplicationContextBuilder builder = new DynamicApplicationContextBuilder();

        ClassPathXmlApplicationContext parentContext = new ClassPathXmlApplicationContext("/database.xml");
        builder.setParent(parentContext);
        // "forget" to enable transactions
        // builder.enableTransactions();

        builder.add(createSingleton(TransactionalServiceImpl.class));

        ApplicationContext applicationContext = builder.getApplicationContext();

        TransactionalService transactionalService = applicationContext.getBean(TransactionalService.class);
        transactionalService.execute();

        // service must not be transactional
        assertFalse("Service was executed transactional.", transactionalService.foundTransaction());
    }

    @Test
    public void executeSuccessfulAsyncServiceCall() throws InterruptedException {
        DynamicApplicationContextBuilder builder = new DynamicApplicationContextBuilder();
        builder.enableAsync("Unit-Test", 5);
        builder.add(createSingleton(AsyncServiceImpl.class));

        ApplicationContext applicationContext = builder.getApplicationContext();

        AsyncService asyncService = applicationContext.getBean(AsyncService.class);

        asyncService.execute();
        assertFalse(asyncService.isExecuted());
        Thread.sleep(100);
        assertTrue(asyncService.isExecuted());
    }

    @Test
    public void executeSuccessfulTransactionalMethod() throws Exception {
        DynamicApplicationContextBuilder builder = new DynamicApplicationContextBuilder();

        ClassPathXmlApplicationContext parentContext = new ClassPathXmlApplicationContext("/database.xml");
        builder.setParent(parentContext);
        builder.enableTransactions();

        builder.add(createSingleton(TransactionalServiceImpl.class));

        ApplicationContext applicationContext = builder.getApplicationContext();

        TransactionalService transactionalService = applicationContext.getBean(TransactionalService.class);
        transactionalService.execute();

        assertTrue("Service was not executed transactional.", transactionalService.foundTransaction());
    }

    @Test(expected = InitializationFailedException.class)
    public void misconfigureName() {
        DynamicApplicationContextBuilder builder = new DynamicApplicationContextBuilder();
        builder.enableAsync(null, 5);
    }

    @Test(expected = InitializationFailedException.class)
    public void misconfigurePoolSize() {
        DynamicApplicationContextBuilder builder = new DynamicApplicationContextBuilder();
        builder.enableAsync("Unit-Test", -5);
    }

    @Test(expected = InitializationFailedException.class)
    public void misconfigureTwice() {
        DynamicApplicationContextBuilder builder = new DynamicApplicationContextBuilder();
        builder.enableAsync("Name", 5);
        builder.enableAsync("Name", 5);
    }
}
