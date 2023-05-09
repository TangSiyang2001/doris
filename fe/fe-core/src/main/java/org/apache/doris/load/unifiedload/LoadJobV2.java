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

import org.apache.doris.analysis.UserIdentity;
import org.apache.doris.common.io.Writable;
import org.apache.doris.qe.OriginStatement;
import org.apache.doris.transaction.AbstractTxnStateChangeCallback;

import java.util.Map;

public abstract class LoadJobV2 extends AbstractTxnStateChangeCallback implements Writable {

    protected long id;

    protected String label;

    protected long dbId;

    protected String comments;

    protected Map<String, String> jobProperties;

    protected OriginStatement origStmt;

    protected UserIdentity userInfo;

    // ------------------ time marks ------------------
    protected long createdTimestamp;

    protected long startedTimestamp;

    protected long finishedTimestamp;

    // ------------------------------------------------

    @Override
    public long getId() {
        return id;
    }

    public String getLabel() {
        return label;
    }

    public void setDbId(long dbId) {
        this.dbId = dbId;
    }

    public long getDbId() {
        return dbId;
    }

    public void setComments(String comments) {
        this.comments = comments;
    }

    public String getComments() {
        return comments;
    }

    public void setJobProperties(Map<String, String> properties) {
        this.jobProperties = properties;
    }

    public Map<String, String> getJobProperties() {
        return jobProperties;
    }

    public void setOrigStmt(OriginStatement origStmt) {
        this.origStmt = origStmt;
    }

    public OriginStatement getOrigStmt() {
        return origStmt;
    }

    public UserIdentity getUserInfo() {
        return userInfo;
    }

    public void setUserInfo(UserIdentity userInfo) {
        this.userInfo = userInfo;
    }

    public abstract LoadType getLoadType();

    public abstract boolean isInfoExpired();

    public abstract boolean isCompleted();
}
