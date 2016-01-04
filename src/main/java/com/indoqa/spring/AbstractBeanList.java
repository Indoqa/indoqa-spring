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

import static com.indoqa.lang.util.GenericsUtils.getGenericParameter;

import java.util.*;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.springframework.context.ApplicationContext;

/**
 * The AbstractBeanList collects all Spring beans that are of the same type as its generic type <code>T</code>.<br/>
 * <br/>
 * If <code>T</code> is of type {@link PrioritizedService} then the collected beans will be sorted according to their priority.
 * Otherwise the beans will be in no particular order.<br/>
 * {@link #sortBeans(List)} can be overridden to provide a custom sort algorithm.
 * 
 * @param <T> The type of beans to collect. Either an interface or a class.
 */
public abstract class AbstractBeanList<T> implements Iterable<T> {

    @Inject
    private ApplicationContext applicationContext;

    private List<T> beans;

    private Class<T> beanType;

    @PostConstruct
    @SuppressWarnings("unchecked")
    public void initialize() {
        this.beanType = (Class<T>) getGenericParameter(this.getClass(), AbstractBeanList.class, 0);
        Map<String, T> beanMap = this.applicationContext.getBeansOfType(this.beanType);
        this.beans = new ArrayList<T>(beanMap.values());

        this.sortBeans(this.beans);
    }

    @Override
    public Iterator<T> iterator() {
        return this.beans.iterator();
    }

    public int size() {
        return this.beans.size();
    }

    @SuppressWarnings("unchecked")
    protected void sortBeans(List<T> beanList) {
        if (PrioritizedService.class.isAssignableFrom(this.beanType)) {
            Collections.sort((List<? extends PrioritizedService>) beanList, new PrioritizedServiceComparator());
        }
    }
}
