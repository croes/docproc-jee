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

package gcroes.thesis.docproc.jee.tasks;

import gcroes.thesis.docproc.jee.entity.Task;

import java.util.ArrayList;
import java.util.List;

/**
* Instances of this class are used by workers to return a result.
*
* @author Bart Vanbrabant <bart.vanbrabant@cs.kuleuven.be>
*/
public class TaskResult {
        public enum Result {SUCCESS, ERROR, EXCEPTION, ARGUMENT_ERROR, FINISHED};
        
        private Result result = null;
        private List<Task> tasks = null;
        private Exception exception = null;
        private boolean fail = false;

        /**
         * Initialize a new taskresult
         */
        public TaskResult() {
                this.tasks = new ArrayList<Task>();
        }
        
        /**
         * Get the result of this task. When the result is exception, the exception
         * causing this result can be retrieved using getException()
         *
         * @return the result
         */
        public Result getResult() {
                return result;
        }

        /**
         * Set the result. When the result is EXCEPTION, also set the exception
         * that caused this result.
         *
         * @param result the result to set
         */
        public TaskResult setResult(Result result) {
                this.result = result;
                return this;
        }
        
        /**
         * Add a next task in this workflow.
         *
         * @param task
         */
        public void addNextTask(Task task) {
                this.tasks.add(task);
        }
        
        /**
         * Does this task fork the workflow?
         */
        public boolean isFork() {
                return this.tasks.size() > 1;
        }
        
        /**
         * Get a list of all the next tasks
         *
         * @return A list of tasks that are the result of this task
         */
        public List<Task> getNextTasks() {
                return this.tasks;
        }
        
        /**
         * Does this task split the job? A split is detected if more than one
         * new task is generated.
         * @return
         */
        public boolean isSplit() {
                return this.tasks.size() > 0;
        }

        /**
         * @return the exception
         */
        public Exception getException() {
                return exception;
        }

        /**
         * @param exception the exception to set
         */
        public void setException(Exception exception) {
                this.exception = exception;
        }
        
        /**
         * Mark the task as failed
         */
        public void fail() {
                this.fail = true;
        }
        
        /**
         * Is the failure fatal?
         * @return
         */
        public boolean isFatal() {
                return this.fail;
        }
}