package gcroes.thesis.docproc.jee;

import gcroes.thesis.docproc.jee.entity.Job;
import gcroes.thesis.docproc.jee.entity.Task;
import gcroes.thesis.docproc.jee.schedule.WeightedRoundRobin;
import java.io.Serializable;
import java.util.List;

import javax.ejb.Stateless;
import javax.jws.WebService;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Session Bean implementation class Service
 */
@Stateless
@WebService
public class Service implements ServiceRemote, Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 820984664601318701L;

    private static Logger logger = LogManager
            .getLogger(Service.class.getName());

    @PersistenceContext
    EntityManager em;

    /**
     * Default constructor.
     */
    public Service() {
    }
    
    @Override
    public List<Job> getAllJobs() {
        logger.debug("Fetching all jobs");
        return em.createNamedQuery("Job.findAll", Job.class).getResultList();
    }

    @Override
    public int getNbOfJobs() {
        logger.debug("Fetching number of jobs");
        return em.createNamedQuery("Job.findAllCount", Number.class)
                .getSingleResult().intValue();
    }

    @Override
    public boolean addAJob(String workflowName) {
        logger.debug("Fetching workflow: " + workflowName);
        Job j = new Job(workflowName);
        Task startTask = new Task(j, null, "zipDocs");
        j.setStartTask(startTask);
        em.persist(j);
        em.flush();
        return true;
    }

    @Override
    public void addJob(Job job) {
        logger.debug("Adding job: " + job);
        em.persist(job);
        em.flush();
    }
    
    @Override
    public Job findJobByID(int id) {
        logger.debug("Fetching job. ID: " + id);
        return em.createNamedQuery("Job.findByID", Job.class)
                 .setParameter("id", id)
                 .getSingleResult();
    }

    public String getNextWorker(int workflowId, String name, String nextWorker) {
        // TODO Auto-generated method stub
        return null;
    }

//    public void addWorkflowStateListener(JobStateListener listener) {
//        // TODO Auto-generated method stub
//        
//    }
//    
//    public void removeWorkflowStateListener(JobStateListener listener) {
//        // TODO Auto-generated method stub
//        
//    }

    public WeightedRoundRobin getPriorities(String workerType) {
        // TODO Auto-generated method stub
        return null;
    }

    public void removeJobPriority(Job job, List<String> workerTypes) {
        // TODO Auto-generated method stub
        
    }

    public void setPriorities(String workerType, WeightedRoundRobin wrr) {
        // TODO Auto-generated method stub
        
    }

    public Task getTask(String name) {
        // TODO Auto-generated method stub
        return null;
    }

    public void jobFinished(Job job) {
        // TODO Auto-generated method stub
        
    }

    public void killJob(Job job) {
        // TODO Auto-generated method stub
        
    }

    public void deleteTask(Task task) {
        logger.debug("deleteTask()");
    }

    public void queueTask(Task task) {
        logger.debug("queueTask()");
    }

}
