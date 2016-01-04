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

import java.util.ArrayList;
import java.util.Iterator;
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
public class ComponentDefinitions implements Iterable<ComponentDefinition> {

    private List<ComponentDefinition> componentDefinitions = new ArrayList<ComponentDefinition>();

    public void add(ComponentDefinition componentDefinition) {
        this.componentDefinitions.add(componentDefinition);
    }

    public void add(@SuppressWarnings("hiding") ComponentDefinitions componentDefinitions) {
        for (ComponentDefinition componentDefinition : componentDefinitions) {
            this.add(componentDefinition);
        }
    }

    public int getSize() {
        return this.componentDefinitions.size();
    }

    @Override
    public Iterator<ComponentDefinition> iterator() {
        return this.componentDefinitions.iterator();
    }
}
