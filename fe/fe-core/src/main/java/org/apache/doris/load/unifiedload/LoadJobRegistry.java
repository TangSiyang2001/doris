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

import java.util.Collection;
import java.util.List;
import java.util.function.Predicate;

public interface LoadJobRegistry<J extends LoadJobV2> {

    void addLoadJob(J loadJob);

    J getLoadJob(long loadJobId);

    Collection<J> getLoadJobs(Predicate<J> cond);

    Collection<J> getLoadJobs(long dbId, String label, Predicate<J> cond);

    void removeLoadJob(long loadJobId);

    void removeLoadJob(Predicate<J> cond);

    long getLoadJobNum(Predicate<J> cond);

}
