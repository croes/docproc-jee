/**
 *
 * Copyright 2013 KU Leuven Research and Development - iMinds - Distrinet
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Administrative Contact: dnet-project-office@cs.kuleuven.be
 * Technical Contact: bart.vanbrabant@cs.kuleuven.be
 */
package gcroes.thesis.docproc.jee.monitoring;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

public class Snapshot {

    private long timestamp;
    private List<Statistic> stats = new LinkedList<Statistic>();

    public Snapshot(Date timestamp) {
        super();
        this.timestamp = timestamp.getTime();
    }

    public long getTimestamp() {
        return timestamp;
    }

    public List<Statistic> getStats() {
        return stats;
    }

    @Override
    public String toString() {
        return "Snapshot [timestamp=" + timestamp + ", stats=" + stats + "]";
    }

}