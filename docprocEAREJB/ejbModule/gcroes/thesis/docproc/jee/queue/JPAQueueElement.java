package gcroes.thesis.docproc.jee.queue;

import gcroes.thesis.docproc.jee.entity.Task;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

@Entity
@Table(name="queueElement")
@NamedQueries(value = {
    @NamedQuery(name="JPAQueueElement.findElemWithTask", query="SELECT e FROM JPAQueueElement e WHERE e.task = :task")
})
public class JPAQueueElement {
    
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    @Column(name="id")
    private int id;
    
    @ManyToOne
    private Task task;
    
    @Column(name="removed")
    private boolean removed;
    
    @Temporal(TemporalType.DATE)
    @Column(name="leased_until")
    private Date leasedUntil;
    
    @ManyToOne
    @JoinColumn(name="queue_id")
    private JPAQueue queue;
    
    public JPAQueueElement(){
        
    }
    
    public JPAQueueElement(Task task){
        this(task, null);
    }
    
    public JPAQueueElement(Task task, Date leasedUntil){
        this.task = task;
        this.removed = false;
        this.leasedUntil = leasedUntil;
    }

}
