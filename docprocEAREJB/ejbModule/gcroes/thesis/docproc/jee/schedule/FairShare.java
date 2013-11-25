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
package gcroes.thesis.docproc.jee.schedule;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.ejb.EJB;

import gcroes.thesis.docproc.jee.entity.Job;
import gcroes.thesis.docproc.jee.tasks.JobStateListener;
import gcroes.thesis.docproc.jee.Service;
import gcroes.thesis.docproc.jee.config.Config;

/**
* @author wouter
*
*
* fixme: preload list of open worklows after restart
*/
@SuppressWarnings("rawtypes")
public class FairShare implements IScheduler, JobStateListener {
    
        @EJB
        Service service;

        private List<String> workers;
        private List<Job> jobs = new LinkedList<Job>();

        @SuppressWarnings("unchecked")
        @Override
        public void enable(Map config) {
                List<String> workers = (List<String>) config.get("workers");
                if (workers == null) {
                        workers = new ArrayList<>(Config.getConfig().getWorkers().keySet());
                }

                this.workers = workers;
                service.addWorkflowStateListener(this); //FIXME service

                // attempt to recover old data
                WeightedRoundRobin old = null;

                for (String w : workers) {
                        old = service.getPriorities(w); //FIXME service
                        if (old != null) {
                                break;
                        }
                }

                if (old != null) {
                        jobs.addAll(Arrays.asList(old.getNames())); //FIXME
                }
                rebuild();
        }

        @Override
        public synchronized void jobStarted(Job job) {
                jobs.add(job);
                rebuild();
        }

        @Override
        public synchronized void jobFinished(Job job) {
                jobs.remove(job);
                service.removeJobPriority(job, workers);//FIXME service
                rebuild();
        }

        private void rebuild() {
                float[] weights = new float[jobs.size()];
                Arrays.fill(weights, 1.0f);

                WeightedRoundRobin wrr = new WeightedRoundRobin(
                                jobs.toArray(new String[jobs.size()]), weights);
                
                for (String worker : workers) {
                        service.setPriorities(worker, wrr);//FIXME service
                }
        }

}