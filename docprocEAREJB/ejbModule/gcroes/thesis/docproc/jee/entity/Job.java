package gcroes.thesis.docproc.jee.entity;

import gcroes.thesis.docproc.jee.config.Config;
import gcroes.thesis.docproc.jee.config.WorkflowConfig;
import gcroes.thesis.docproc.jee.monitoring.Statistic;
import gcroes.thesis.docproc.jee.util.Serializer;

import java.io.IOException;
import java.io.Serializable;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlRootElement;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * The persistent class for the job database table.
 * 
 */
@Entity
@Table(name = "job")
@NamedQueries({
        @NamedQuery(name = "Job.findAllCount", query = "SELECT COUNT(j) FROM Job j"),
        @NamedQuery(name = "Job.findAll", query = "SELECT j FROM Job j"),
        @NamedQuery(name = "Job.findByID", query = "SELECT j FROM Job j WHERE j.jobId = :id") })
@XmlRootElement
public class Job implements Serializable {

    /**
	 * 
	 */
    private static final long serialVersionUID = 4759988173095022931L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "job_id")
    private int jobId;

    private boolean failed;

    @Temporal(TemporalType.DATE)
    @Column(name = "finish_before")
    private Date finishBefore;

    private boolean finished;

    @Temporal(TemporalType.DATE)
    @Column(name = "finished_at")
    private Date finishedAt;

    @Temporal(TemporalType.DATE)
    @Column(name = "start_after")
    private Date startAfter;

    private boolean started;

    @Temporal(TemporalType.DATE)
    @Column(name = "started_at")
    private Date startedAt;

    @Lob
    private byte[] stats;

    @Lob
    @Column(name = "workflow_name")
    private String workflowName;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "start_task_id")
    private Task startTask;

    @OneToMany(mappedBy = "job")
    private List<Task> tasks;

    public Job() {
    }

    public Job(String workflowName) {
        this.failed = false;
        this.finishBefore = null;
        this.finished = false;
        this.finishedAt = null;
        this.startAfter = null;
        this.started = false;
        this.startedAt = null;
        this.stats = null;
        this.workflowName = workflowName;
        this.startTask = null;
        this.tasks = new ArrayList<Task>();
    }

    public int getJobId() {
        return this.jobId;
    }

    public void setJobId(int jobId) {
        this.jobId = jobId;
    }

    public boolean isFailed() {
        return this.failed;
    }

    public void setFailed(boolean failed) {
        this.failed = failed;
    }

    public Date getFinishBefore() {
        return this.finishBefore;
    }

    public void setFinishBefore(Date finishBefore) {
        this.finishBefore = finishBefore;
    }

    public boolean isFinished() {
        return this.finished;
    }

    public void setFinished(boolean finished) {
        this.finished = finished;
    }

    public Date getFinishedAt() {
        return this.finishedAt;
    }

    public void setFinishedAt(Date finishedAt) {
        this.finishedAt = finishedAt;
    }

    public Date getStartAfter() {
        return this.startAfter;
    }

    public void setStartAfter(Date startAfter) {
        this.startAfter = startAfter;
    }

    public boolean isStarted() {
        return this.started;
    }

    public void setStarted(boolean started) {
        this.started = started;
    }

    public Date getStartedAt() {
        return this.startedAt;
    }

    public void setStartedAt(Date startedAt) {
        this.startedAt = startedAt;
    }

    @SuppressWarnings("unchecked")
    public List<Statistic> getStats() {
        try {
            return (List<Statistic>) Serializer.deserialize(this.stats);
        } catch (ClassNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }

    public void setStats(Object stats) {
        try {
            this.stats = Serializer.serialize(stats);
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public String getWorkflowName() {
        return this.workflowName;
    }

    public void setWorkflowName(String workflowName) {
        this.workflowName = workflowName;
    }

    public Task getStartTask() {
        return this.startTask;
    }

    public void setStartTask(Task task) {
        this.startTask = task;
    }

    public List<Task> getTasks() {
        return this.tasks;
    }

    public void setTasks(List<Task> tasks) {
        this.tasks = tasks;
    }

    public Task addTask(Task task) {
        getTasks().add(task);
        task.setJob(this);

        return task;
    }

    public Task removeTask(Task task) {
        getTasks().remove(task);
        task.setJob(null);

        return task;
    }

    public Task newStartTask() {
        this.startTask = new Task(this, null, getWorkflowConfig().getWorkflowStart());
        this.startTask.initJoin();
        return this.startTask;
    }

    public List<Task> getHistory() {
        ArrayList<Task> result = new ArrayList<Task>();
        if (this.startTask != null && this.startTask.isFinished())
            result.add(this.startTask);
        return result;
    }

    public void calcStats() {
        // TODO Auto-generated method stub

    }
    
    public WorkflowConfig getWorkflowConfig(){
        return Config.getConfig().getWorkflow(workflowName);
    }

}