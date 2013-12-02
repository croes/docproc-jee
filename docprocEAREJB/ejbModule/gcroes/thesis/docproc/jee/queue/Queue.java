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

import gcroes.thesis.docproc.jee.entity.JPAQueueElement;
import gcroes.thesis.docproc.jee.entity.Job;
import gcroes.thesis.docproc.jee.entity.Task;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import javax.ejb.Stateless;
import javax.jws.WebService;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.sun.mail.iap.ConnectionException;

/**
* A queue implementation on top of cassandra
*
* @author Bart Vanbrabant <bart.vanbrabant@cs.kuleuven.be>
*/
@Stateless
@WebService
public class Queue implements Serializable{
    
     /**
     * 
     */
    private static final long serialVersionUID = -3132301977466297101L;

        private static Logger logger = LogManager
            .getLogger(Queue.class.getName());
        
        @PersistenceContext
        EntityManager em;
        
        public Queue(){}
        
        public List<JPAQueueElement> getOpenQueue(){
            logger.entry();
            List<JPAQueueElement> elems = em.createNamedQuery("JPAQueueElement.findNotRemoved", JPAQueueElement.class)
                                          .getResultList();
            return logger.exit(elems);
        }
        
        public void queueTask(Task task){
            logger.entry();
            JPAQueueElement elem = new JPAQueueElement(task);
            em.persist(elem);
            logger.exit();
        }
        
        public void finishTask(Task task){
            logger.entry();
            JPAQueueElement elem = em.createNamedQuery("JPAQueueElement.findElemWithTask", JPAQueueElement.class)
                                        .setParameter("task", task)
                                        .getSingleResult();
            elem.setRemoved(true);
            elem.getTask().setFinishedAt(new Date());
            logger.exit();
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
            logger.entry();
            logger.exit();
        }

}