package gcroes.thesis.docproc.jee.queue;

import gcroes.thesis.docproc.jee.entity.Job;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;

@Entity

public class JPAQueue {
    
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    @Column(name="id")
    private int id;
    
    @Column(name="name")
    private String name;
    
    @OneToMany(cascade=CascadeType.ALL, mappedBy="queue")
    private List<JPAQueueElement> queue;
    
    public JPAQueue(){
        
    }
    
    public JPAQueue(String name){
        this.name = name;
    }
    
    public JPAQueue(Job job, String workerName){
        this.name = getQueueName(job, workerName);
    }
    
    private String getQueueName(Job job, String workerName){
        return job.getJobId() + workerName;
    }

   public List<JPAQueueElement> getQueue(){
       return queue;   
   }

}
