package gcroes.thesis.docproc.jee.entity;

import java.io.Serializable;
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
import javax.xml.bind.annotation.XmlRootElement;

@Entity
@Table(name="queueElement")
@NamedQueries(value = {
    @NamedQuery(name="JPAQueueElement.findNotRemoved", query="SELECT e FROM JPAQueueElement e WHERE e.removed = false"),
    @NamedQuery(name="JPAQueueElement.findElemWithTask", query="SELECT e FROM JPAQueueElement e WHERE e.task = :task")
})
@XmlRootElement
public class JPAQueueElement implements Serializable{
    
    /**
     * 
     */
    private static final long serialVersionUID = 4488279669783467708L;

    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    @Column(name="id")
    private int id;
    
    @ManyToOne
    @JoinColumn(name="task_id")
    private Task task;
    
    @Column(name="removed")
    private boolean removed;
    
    @Temporal(TemporalType.DATE)
    @Column(name="leased_until")
    private Date leasedUntil;
    
    public Task getTask() {
        return task;
    }

    public void setTask(Task task) {
        this.task = task;
    }

    public Date getLeasedUntil() {
        return leasedUntil;
    }

    public void setLeasedUntil(Date leasedUntil) {
        this.leasedUntil = leasedUntil;
    }

    public boolean isRemoved() {
        return removed;
    }

    public void setRemoved(boolean removed) {
        this.removed = removed;
    }

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
