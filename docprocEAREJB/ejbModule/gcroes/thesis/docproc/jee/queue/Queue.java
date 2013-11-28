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

package gcroes.thesis.docproc.jee.queue;

import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import gcroes.thesis.docproc.jee.entity.Job;
import gcroes.thesis.docproc.jee.entity.Task;

/**
* A queue implementation on top of cassandra
*
* @author Bart Vanbrabant <bart.vanbrabant@cs.kuleuven.be>
*/
public class Queue {
    
        private static Logger logger = LogManager
            .getLogger(Queue.class.getName());
        
        @PersistenceContext
        EntityManager em;
        
        private JPAQueue queue;
        
        public Queue(String queueName) {
               this.queue = new JPAQueue(queueName);
        }
        
        public void persist(){
            logger.debug("persisting queue");
            em.persist(queue);
        }
        
        public void addTask(Task task){
            List<JPAQueueElement> jpaQueue = queue.getQueue();
            jpaQueue.add(new JPAQueueElement(task));
        }
        
        public void finishTask(Task task){
            JPAQueueElement elem = em.createNamedQuery("JPAQueueElement.findElemWithTask", JPAQueueElement.class)
                                        .setParameter("task", task)
                                        .getSingleResult();
            List<JPAQueueElement> jpaQueue = queue.getQueue();
            jpaQueue.remove(elem);
        }
        
        /**
         * Lease a task from the queue
         *
         * @param lease
         * How long is the task leased
         * @param unit
         * The timeunit of the lease time
         * @param limit
         * How many task are to be leased
         * @param taskType
         * The type of task (the name / type of the worker)
         * @param jobId
         * The workflow the task belongs to
         * @return A list of taskhandles
         * @throws ConnectionException
         */
        public void leaseTasks(long lease, TimeUnit unit, int limit, String taskType, Job job){
            
        }

}