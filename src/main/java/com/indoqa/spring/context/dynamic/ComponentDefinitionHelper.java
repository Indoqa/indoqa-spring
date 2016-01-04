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

import static com.indoqa.spring.context.dynamic.ComponentDefinition.Scope.PROTOTYPE;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.indoqa.spring.context.dynamic.ComponentDefinition.Scope;

/**
 * @deprecated Consider using Spring Java config.
 */
@Deprecated
public final class ComponentDefinitionHelper {

    private ComponentDefinitionHelper() {
        // no instantiation
    }

    public static ComponentDefinition createLazySingleton(Class<?> componentClass) {
        return createLazySingleton(componentClass.getName());
    }

    public static ComponentDefinition createLazySingleton(String componentClass) {
        return createLazySingleton(componentClass, componentClass);
    }

    public static ComponentDefinition createLazySingleton(String name, String componentClass) {
        return createLazySingleton(name, componentClass, null);
    }

    public static ComponentDefinition createLazySingleton(String name, String componentClass,
            Map<String, ? extends Object> parameters) {
        ComponentDefinition singleton = createSingleton(name, componentClass, parameters);
        singleton.setLazyInit(true);
        return singleton;
    }

    public static <K, V> Map<K, V> createMap(K key, V value) {
        Map<K, V> map = new HashMap<K, V>();
        map.put(key, value);
        return map;
    }

    public static PropertyValues createPropertyValues(Map<String, ? extends Object> parameters) {
        PropertyValues propertyValues = new PropertyValues();

        if (parameters != null) {
            for (Entry<String, ? extends Object> entry : parameters.entrySet()) {
                propertyValues.add(new PropertyValue(entry.getKey(), entry.getValue()));
            }
        }

        return propertyValues;
    }

    public static ComponentDefinition createPrototype(String name, Class<?> componentClass) {
        return createPrototype(name, componentClass.getName(), null);
    }

    public static ComponentDefinition createPrototype(String name, Class<?> componentClass, Map<String, ? extends Object> parameters) {
        return createPrototype(name, componentClass.getName(), parameters);
    }

    public static ComponentDefinition createPrototype(String name, String componentClass) {
        return createPrototype(name, componentClass, null);
    }

    public static ComponentDefinition createPrototype(String name, String componentClass, Map<String, ? extends Object> parameters) {
        ComponentDefinition componentDefinition = new ComponentDefinition();

        componentDefinition.setComponentClass(componentClass);
        componentDefinition.setName(name);
        componentDefinition.setScope(PROTOTYPE);
        componentDefinition.setPropertyValues(createPropertyValues(parameters));

        return componentDefinition;
    }

    public static ComponentDefinition createSingleton(Class<?> componentClass) {
        return createSingleton(componentClass.getName());
    }

    public static ComponentDefinition createSingleton(Class<?> componentClass, Map<String, ? extends Object> parameters) {
        return createSingleton(componentClass.getName(), parameters);
    }

    public static ComponentDefinition createSingleton(String componentClass) {
        return createSingleton(componentClass, componentClass);
    }

    public static ComponentDefinition createSingleton(String name, Class<?> componentClass) {
        return createSingleton(name, componentClass.getName());
    }

    public static ComponentDefinition createSingleton(String name, Class<?> componentClass, Map<String, ? extends Object> parameters) {
        return createSingleton(name, componentClass.getName(), parameters);
    }

    public static ComponentDefinition createSingleton(String componentClass, Map<String, ? extends Object> parameters) {
        return createSingleton(componentClass, componentClass, parameters);
    }

    public static ComponentDefinition createSingleton(String name, String componentClass) {
        return createSingleton(name, componentClass, null);
    }

    public static ComponentDefinition createSingleton(String name, String componentClass, Map<String, ? extends Object> parameters) {
        ComponentDefinition componentDefinition = new ComponentDefinition();

        componentDefinition.setComponentClass(componentClass);
        componentDefinition.setName(name);
        componentDefinition.setScope(Scope.SINGLETON);
        componentDefinition.setPropertyValues(createPropertyValues(parameters));

        return componentDefinition;
    }
}
