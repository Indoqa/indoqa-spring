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
package com.indoqa.spring;

import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public final class DependencyInjector {

    private DependencyInjector() {
        // hide utility class constructor
    }

    public static void injectDependencies(Object target, String... configLocations) {
        ClassPathXmlApplicationContext applicationContext = new ClassPathXmlApplicationContext(configLocations);
        applicationContext.registerShutdownHook();

        performAutowiring(target, applicationContext);
    }

    public static void injectDependencies(Object target, String[] configLocations, Class<?> loadingClass) {
        ClassPathXmlApplicationContext applicationContext = new ClassPathXmlApplicationContext(configLocations, loadingClass);
        applicationContext.registerShutdownHook();

        performAutowiring(target, applicationContext);
    }

    private static void performAutowiring(Object target, ApplicationContext applicationContext) {
        @SuppressWarnings("resource")
        AnnotationConfigApplicationContext annotationConfigApplicationContext = new AnnotationConfigApplicationContext();
        annotationConfigApplicationContext.setParent(applicationContext);
        annotationConfigApplicationContext.refresh();

        annotationConfigApplicationContext.getAutowireCapableBeanFactory().autowireBean(target);
    }
}
