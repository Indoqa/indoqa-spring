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

import java.util.Comparator;

public final class PrioritizedServiceComparator implements Comparator<PrioritizedService> {

    @Override
    public int compare(PrioritizedService o1, PrioritizedService o2) {
        int priority1 = o1.getPriority();
        int priority2 = o2.getPriority();

        if (priority1 < priority2) {
            return -1;
        }

        if (priority1 > priority2) {
            return 1;
        }

        return 0;
    }
}
