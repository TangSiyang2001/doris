// Licensed to the Apache Software Foundation (ASF) under one
// or more contributor license agreements.  See the NOTICE file
// distributed with this work for additional information
// regarding copyright ownership.  The ASF licenses this file
// to you under the Apache License, Version 2.0 (the
// "License"); you may not use this file except in compliance
// with the License.  You may obtain a copy of the License at
//
//   http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing,
// software distributed under the License is distributed on an
// "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
// KIND, either express or implied.  See the License for the
// specific language governing permissions and limitations
// under the License.

package org.apache.doris.load.unifiedload;

import org.apache.doris.task.MasterTask;
import org.apache.doris.task.MasterTaskExecutor;

import java.util.Collection;

/**
 * Schedule Load tasks(jobs) with thread pool.
 */
public class PooledLoadScheduler implements LoadScheduler<MasterTask> {

    private final MasterTaskExecutor masterTaskExecutor;

    public PooledLoadScheduler(String name, int threadNum, int queueSize, boolean needRegisterMetrics) {
        masterTaskExecutor = new MasterTaskExecutor(name, threadNum, Integer.MAX_VALUE, needRegisterMetrics);
    }

    @Override
    public Collection<MasterTask> getNeedScheduled() {
        throw new UnsupportedOperationException("Delegate to MasterTaskExecutor");
    }

    @Override
    public void schedule() throws Exception {
        throw new UnsupportedOperationException("Delegate to MasterTaskExecutor");
    }

    @Override
    public boolean submit(MasterTask task) {
        masterTaskExecutor.submit(task);
        return true;
    }

    @Override
    public boolean submitBatch(Collection<MasterTask> batch) {
        for (MasterTask masterTask : batch) {
            submit(masterTask);
        }
        return true;
    }

    @Override
    public void start() {
        masterTaskExecutor.start();
    }
}
