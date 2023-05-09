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

import org.apache.doris.analysis.BrokerDesc;
import org.apache.doris.analysis.CancelLoadStmt;
import org.apache.doris.analysis.InsertStmt;
import org.apache.doris.analysis.ResourceDesc;
import org.apache.doris.catalog.Database;
import org.apache.doris.catalog.Env;
import org.apache.doris.common.DdlException;
import org.apache.doris.common.LabelAlreadyUsedException;
import org.apache.doris.common.util.ReadWriteLockWrapper;
import org.apache.doris.load.loadv2.JobState;

import com.google.common.base.Preconditions;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.concurrent.locks.ReentrantReadWriteLock;

public class BulkLoadManager implements LoadManager {

    private static final Logger LOG = LogManager.getLogger(BulkLoadManager.class);

    private final ReadWriteLockWrapper rwLock;

    private final LoadJobRegistry<BulkLoadJobV2> jobRegistry;

    private final LoadScheduler<BulkLoadJobV2> loadJobScheduler;

    private BulkLoadManager() {
        this.jobRegistry = new UnprotectedJobRegistry<>();
        this.rwLock = new ReadWriteLockWrapper(new ReentrantReadWriteLock());
        this.loadJobScheduler = new BulkLoadScheduler();
    }

    public static BulkLoadManager getInstance() {
        return new BulkLoadManager();
    }

    @Override
    public void init() {
        loadJobScheduler.start();
    }

    @Override
    public void startLoadWithStmt(InsertStmt insertStmt) throws DdlException {
        BulkLoadJobV2 loadJob;
        rwLock.writeLock();
        try {
            checkLoadFromStmt(insertStmt);
            loadJob = LoadJobFactory.getInstance().createJobFromStmt(insertStmt);
            jobRegistry.addLoadJob(loadJob);
        } finally {
            rwLock.writeUnlock();
        }
        // TODO(tsy): write edit log

        // The job must be submitted after edit log.
        // It guarantees that load job has not been changed before edit log.
        loadJobScheduler.submit(loadJob);
    }

    private void checkLoadFromStmt(InsertStmt insertStmt) throws DdlException {
        // TODO(tsy): maybe better to use dbname from data_desc, which will make label more flexible
        final Database database = Env.getCurrentInternalCatalog()
                .getDbOrDdlException(insertStmt.getLoadLabel().getDbName());
        final long dbId = database.getId();
        final String label = insertStmt.getLabel();
        final ResourceDesc resourceDesc = insertStmt.getResourceDesc();
        if (resourceDesc instanceof BrokerDesc && ((BrokerDesc) resourceDesc).isMultiLoadBroker()) {
            Preconditions.checkState(Env.getCurrentEnv().getLoadInstance()
                            .isUncommittedLabel(dbId, insertStmt.getLoadLabel().getLabelName()),
                    "label: " + insertStmt.getLoadLabel().getLabelName() + " not found!");
        } else {
            checkLabelUsed(dbId, label);
        }
    }

    private void checkLabelUsed(long dbId, String label) throws DdlException {
        // if label has been used in old load jobs
        Env.getCurrentEnv().getLoadInstance().isLabelUsed(dbId, label);
        // if label has been used in v2 of load jobs
        final boolean used = jobRegistry
                .getLoadJobs(dbId, label, job -> job.getState() != JobState.CANCELLED)
                .isEmpty();
        if (used) {
            LOG.warn("Failed to add load job when label {} has been used.", label);
            throw new LabelAlreadyUsedException(label);
        }
    }


    @Override
    public void cancelLoadWithStmt(CancelLoadStmt cancelLoadStmt) {
    }

    @Override
    public void clearExpiredJobsInfo() {
        rwLock.writeLock();
        try {
            jobRegistry.removeLoadJob(BulkLoadJobV2::isInfoExpired);
        } finally {
            rwLock.writeUnlock();
        }
    }

}
