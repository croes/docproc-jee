package gcroes.thesis.docproc.jee.entity;

import java.io.Serializable;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlTransient;

import java.sql.Timestamp;
import java.util.Date;

/**
 * The persistent class for the task database table.
 * 
 */
@Entity
@Table(name = "task")
@NamedQueries({ @NamedQuery(name = "Task.findAll", query = "SELECT t FROM Task t") })
public class Task implements Serializable {

    /**
	 * 
	 */
    private static final long serialVersionUID = 8377642051361748432L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Temporal(TemporalType.DATE)
    @Column(name = "created_at")
    private Date createdAt;

    @Temporal(TemporalType.DATE)
    @Column(name = "finished_at")
    private Date finishedAt;

    @Temporal(TemporalType.DATE)
    @Column(name = "started_at")
    private Date startedAt;

    @Lob
    @Column(name = "worker_name")
    private String workerName;

    @ManyToOne
    @JoinColumn(name = "job_id")
    private Job job;

    @ManyToOne
    @JoinColumn(name = "parent_id")
    private Task parentTask;

    public Task() {
    }

    public Task(Job job, Task parentTask, String workerName) {
        this.job = job;
        this.parentTask = parentTask != null ? parentTask : null;
        this.workerName = workerName;
        this.createdAt = new Date();
        this.finishedAt = null;
        this.startedAt = null;
    }

    public int getId() {
        return this.id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Date getCreatedAt() {
        return this.createdAt;
    }

    public void setCreatedAt(Timestamp createdAt) {
        this.createdAt = createdAt;
    }

    public Date getFinishedAt() {
        return this.finishedAt;
    }
    
    public boolean isFinished(){
        return this.finishedAt != null;
    }

    public void setFinishedAt(Timestamp finishedAt) {
        this.finishedAt = finishedAt;
    }

    public Date getStartedAt() {
        return this.startedAt;
    }

    public void setStartedAt(Timestamp startedAt) {
        this.startedAt = startedAt;
    }

    public String getWorkerName() {
        return this.workerName;
    }

    public void setWorkerName(String workerName) {
        this.workerName = workerName;
    }

    @XmlTransient //to avoid infinite xml
    public Job getJob() {
        return this.job;
    }

    public void setJob(Job job) {
        this.job = job;
    }

    public Task getParentTask() {
        return this.parentTask;
    }

    public void setParentTask(Task task) {
        this.parentTask = task;
    }

}