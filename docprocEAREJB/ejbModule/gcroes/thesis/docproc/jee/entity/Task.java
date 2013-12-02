package gcroes.thesis.docproc.jee.entity;

import gcroes.thesis.docproc.jee.util.Serializer;

import java.io.IOException;
import java.io.Serializable;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlTransient;

import java.sql.Timestamp;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

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

    private static final String JOIN_PARAM = ".meta.join";

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
    
//    @OneToMany(cascade=CascadeType.ALL)
//    @JoinColumn(name = "task_id")
//    private List<Parameter> params;
    
    @ElementCollection
    @CollectionTable(name="param", joinColumns=@JoinColumn(name="task_id"))
    @Column(name="param_value")
    @MapKeyColumn(name="param_key")
    @Lob
    private Map<String, byte[]> params = new HashMap<String, byte[]>();

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

    public void setFinishedAt(Date date) {
        this.finishedAt = date;
    }

    public Date getStartedAt() {
        return this.startedAt;
    }

    public void setStartedAt() {
        this.startedAt = new Date();
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

    public void saveTiming() {
        // TODO Auto-generated method stub
        
    }
    
    public void addParam(String key, Object value){
        try {
            params.put(key, Serializer.serialize(value));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public void removeParam(String key){
        params.remove(key);
    }
    
    public Object getParamValue(String key){
        return params.get(key);
    }

    public void initJoin() {
        this.addParam(JOIN_PARAM, "");
    }

}