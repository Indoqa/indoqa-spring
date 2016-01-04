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

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.TransactionDefinition;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import org.springframework.transaction.support.DefaultTransactionStatus;

public class TransactionalServiceImpl implements TransactionalService {

    @Autowired
    private PlatformTransactionManager transactionManager;

    private boolean foundTransaction;

    @Override
    @Transactional
    public void execute() throws Exception {
        DefaultTransactionDefinition defaultTransactionDefinition = new DefaultTransactionDefinition();
        defaultTransactionDefinition.setPropagationBehavior(TransactionDefinition.PROPAGATION_SUPPORTS);

        DefaultTransactionStatus transactionStatus = (DefaultTransactionStatus) this.transactionManager
            .getTransaction(defaultTransactionDefinition);
        this.foundTransaction = transactionStatus.hasTransaction();
        this.transactionManager.commit(transactionStatus);
    }

    @Override
    public boolean foundTransaction() {
        return this.foundTransaction;
    }
}
