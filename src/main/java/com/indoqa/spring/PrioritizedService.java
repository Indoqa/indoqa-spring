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

/**
 * Interface for defining a service with an attached priority.<br>
 * <br>
 * This is useful when implementing a Chain of Responsibility pattern like structure when the actual number and/or type of elements is
 * not predetermined.<br>
 * The priority of the service is then used to bring all service elements into proper order.
 *
 * This interface can be used with the {@link com.indoqa.spring.AbstractBeanList AbstractBeanList}
 */
public interface PrioritizedService {

    /**
     * Get the priority for this service.<br>
     * <br>
     * <b>Lower values mean higher priority!</b><br>
     * Negative values as valid!
     *
     * @return The priority of this service.
     */
    int getPriority();

}
