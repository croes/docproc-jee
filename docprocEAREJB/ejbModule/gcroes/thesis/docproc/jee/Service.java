package gcroes.thesis.docproc.jee;

import gcroes.thesis.docproc.jee.entity.Job;
import gcroes.thesis.docproc.jee.entity.Task;

import java.util.List;

import javax.ejb.LocalBean;
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
@LocalBean
@WebService
public class Service implements ServiceRemote {

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

}
