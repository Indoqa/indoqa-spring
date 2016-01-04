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

import java.util.LinkedList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * @deprecated Consider using Spring Java config.
 */
@Deprecated
@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class ComponentDefinition {

    private String name;
    private String componentClass;
    private Scope scope = Scope.SINGLETON;
    private PropertyValues propertyValues = new PropertyValues();
    private List<Object> constructorArguments = new LinkedList<Object>();
    private String[] dependsOn = new String[0];
    private boolean lazyInit;

    public void add(PropertyValue propertyValue) {
        this.propertyValues.add(propertyValue);
    }

    public void addConstructorArgument(Object value) {
        this.constructorArguments.add(value);
    }

    public String getComponentClass() {
        return this.componentClass;
    }

    public List<Object> getConstructorArguments() {
        return this.constructorArguments;
    }

    public String[] getDependsOn() {
        return this.dependsOn;
    }

    public String getName() {
        return this.name;
    }

    public PropertyValues getPropertyValues() {
        return this.propertyValues;
    }

    public Scope getScope() {
        return this.scope;
    }

    public boolean isLazyInit() {
        return this.lazyInit;
    }

    public void setComponentClass(Class<?> componentClass) {
        this.componentClass = componentClass.getName();
    }

    public void setComponentClass(String componentClass) {
        this.componentClass = componentClass;
    }

    public void setConstructorArguments(List<Object> constructorArguments) {
        this.constructorArguments = constructorArguments;
    }

    public void setDependsOn(String... dependsOn) {
        this.dependsOn = dependsOn;
    }

    public void setLazyInit(boolean lazyInit) {
        this.lazyInit = lazyInit;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPropertyValues(PropertyValues propertyValues) {
        this.propertyValues = propertyValues;
    }

    public void setScope(Scope scope) {
        this.scope = scope;
    }

    public enum Scope {
        PROTOTYPE, SINGLETON;
    }
}
