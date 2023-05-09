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

import org.apache.doris.analysis.CancelLoadStmt;
import org.apache.doris.analysis.InsertStmt;
import org.apache.doris.load.loadv2.TokenManager;

public class UnifiedLoadManager implements LoadManager {

    private BulkLoadManager bulkLoadManager;

    private TokenManager tokenManager;

    @Override
    public void init() {

    }

    @Override
    public void startLoadWithStmt(InsertStmt insertStmt) {

    }

    @Override
    public void cancelLoadWithStmt(CancelLoadStmt cancelLoadStmt) {

    }

    @Override
    public void clearExpiredJobsInfo() {

    }

    public TokenManager getTokenManager() {
        return tokenManager;
    }
}
