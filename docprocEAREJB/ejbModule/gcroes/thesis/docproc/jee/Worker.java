/*
    Copyright 2013 KU Leuven Research and Development - iMinds - Distrinet

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

        http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.

    Administrative Contact: dnet-project-office@cs.kuleuven.be
    Technical Contact: bart.vanbrabant@cs.kuleuven.be
 */

package gcroes.thesis.docproc.jee;

import java.util.List;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.ejb.EJB;

import com.codahale.metrics.Timer.Context;

import gcroes.thesis.docproc.jee.monitoring.Metrics;
import gcroes.thesis.docproc.jee.entity.Task;
import gcroes.thesis.docproc.jee.tasks.TaskResult;
import gcroes.thesis.docproc.jee.tasks.TaskResult.Result;

/**
 * A work class that fetches work from a pull queue
 * 
 * TODO: add workflow abort 
 * 
 * @author Bart Vanbrabant <bart.vanbrabant@cs.kuleuven.be>
 */
public abstract class Worker implements Runnable {
    protected static final Logger logger = 
                            Logger.getLogger(Worker.class.getCanonicalName());

    private String name = null;
    
    @EJB
    Service service;

    /**
     * @return the name
     */
    public String getName() {
        return name;
    }

    private boolean working = true;
    private String nextWorker = "next";

    /**
     * Create a new work with a name
     * 
     * @param name
     *            The name of the work. This names should be unique
     */
    public Worker(String name) {
        this.name = name;
        logger.info("Worker started with name " + this.name);
    }

    /**
     * Get the name of the next worker if the workflow id is known
     */
    public String getNextWorker(int workflowId) {
        return service.getNextWorker(workflowId, this.getName(), this.nextWorker);
    }

    /**
     * Set the name of the next worker
     */
    protected void setNextWorker(String name) {
        this.nextWorker = name;
    }

    /**
     * Do the work for the given task.
     */
    public abstract TaskResult work(Task task);

    /**
     * Stop working so the thread ends clean
     */
    public void stopWorking() {
        this.working = false;
    }
    
    /**
     * The main loop that handles the tasks.
     */
    @Override
    public void run() {
        logger.info("Started worker " + this.toString());

        Service svc = service;
        int sleepTime = 200;

        while (this.working) {
            try {
                Context tcLease = Metrics.timer("worker.lease").time();
                Context tcNoLease = Metrics.timer("worker.nolease").time();
                Task task = svc.getTask(this.name);
                
                if (task != null) {
                    tcLease.stop();
                    Context tc = Metrics.timer("worker.work." + this.name).time();

                    trace("FETCHED",task);

                    // execute the task
                    TaskResult result = null;
                    task.setStartedAt();
                    try {
                        result = this.work((Task) task);
                    } catch (Exception e) {
                        result = new TaskResult();
                        
                        result.setException(e);
                        result.setResult(Result.EXCEPTION);
                        result.fail();
                    }
                    task.setFinishedAt();
                    task.saveTiming();

                    if (result == null) {
                        result = new TaskResult();
                        result.setResult(Result.ERROR);
                        result.fail();
                        logger.warning("Worker returns null. Ouch ...");
                    }

                    // process the result
                    if (result.getResult() == TaskResult.Result.FINISHED) {
                        svc.jobFinished(task.getJob());
                        
                    } else if (result.getResult() == TaskResult.Result.SUCCESS) {
                        trace("DONE", task);
                        List<Task> tasks = result.getNextTasks();
                        // is this is a split, do the split
                        if (tasks.size() > 1) {
                            // allocate a new uuid that will become the 
                            // taskid of the joined task
                            UUID joinId = UUID.randomUUID();
                            Job.storeJoin(task.getJobId(), joinId, tasks.size());                               
                            for (Task newTask : tasks) {
                                newTask.markSplit(joinId);
                                svc.queueTask(newTask);
                                trace("NEW", newTask);
                            }
                        } else if (tasks.size() == 1) {
                            svc.queueTask(tasks.get(0));
                        } else {
                            // do nothing
                        }
                        svc.deleteTask(task);
                        
                    } else {
                        trace("FAILED", task);
                        logger.warning(String.format("[%s] failed %s: %s", this.name, task.toString(), result.getResult().toString())); 
                        if (result.getResult() == TaskResult.Result.EXCEPTION) {
                            result.getException().printStackTrace();
                        }
                        
                        if (result.isFatal()) {
                            // if this task is fatal, kill the current workflow
                            svc.killJob(task.getJobId());
                        }
                    }
                    
                    sleepTime = sleepTime - 10;
                    if (sleepTime < 0) {
                        sleepTime = 0;
                    }
                    
                    tc.stop();
                } else {
                    tcNoLease.stop();
                    sleepTime += 10;
                    if (sleepTime > 200) {
                        sleepTime = 200;
                    }
                }

                Thread.sleep(sleepTime);
            } catch (Exception e) {
                logger.log(Level.SEVERE, getName() + " failed", e);
            }
        }
    }

    private void trace(String cmd, Task task) {
        logger.info(String.format("[%s] %s %s", this.name, cmd, task.toString()));
    }

//  /**
//   * This method is called when an endtask has been processed. It removes 
//   * the workflow for the current worker from the priorities table.
//   * 
//   * @param task
//   */
//  private void removeFromSchedule(Task task) {
//      Service svc = service;
//      WeightedRoundRobin wrr = svc.getPriorities(this.name);
//      int i = 0;
//      UUID workflowId = task.getJobId();
//      String[] names = wrr.getNames();
//      for (; i < names.length; i++) {
//          if (names[i].equals(workflowId.toString()))
//              break;
//      }
//      if (i == names.length)
//          return;
//      int size = names.length;
//
//      float[] weights = new float[size - 1];
//      Arrays.fill(weights, 1.0f);
//      wrr = new WeightedRoundRobin((String[]) ArrayUtils.remove(
//              wrr.getNames(), i), weights);
//      
//      svc.setPriorities(this.name, wrr);
//  }
}
