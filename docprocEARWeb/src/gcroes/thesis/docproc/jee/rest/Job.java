package gcroes.thesis.docproc.jee.rest;

import java.util.HashMap;
import java.util.Map;

import gcroes.thesis.docproc.jee.Service;
import gcroes.thesis.docproc.jee.entity.Task;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.json.Json;
import javax.json.JsonObject;
import javax.json.JsonObjectBuilder;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

@Path("/job/{id}")
@Stateless
public class Job {
    
    @EJB
    Service service;
    
    /**
     * Method handling HTTP GET requests.
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public String getJob(@PathParam("id")int id) {
            
        gcroes.thesis.docproc.jee.entity.Job job = service.findJobByID(id);
        
        JsonObjectBuilder builder = Json.createObjectBuilder()
                        .add("id", job.getJobId())
                .add("workflow", job.getWorkflowName())
                .add("start_after", job.getStartAfter() != null ? job.getStartAfter().getTime() / 1000 : 0)
                .add("finish_before", job.getFinishBefore() != null ? job.getFinishBefore().getTime() / 1000 : 0)
                .add("started", job.isStarted())
                .add("finished", job.isFinished())
                .add("failed", job.isFailed());
        
        if (job.isStarted()) {
                builder.add("started_at", job.getStartedAt().getTime() / 1000);
        }
        if (job.isFinished()) {
                builder.add("finished_at", job.getFinishedAt().getTime() / 1000);
        }
        
                Map<String, Integer> stats = new HashMap<>();
                for (Task task : job.getHistory()) {
                        if (!stats.containsKey(task.getWorkerName())) {
                                stats.put(task.getWorkerName(), 0);
                        }
                        int value = stats.get(task.getWorkerName()) + 1;
                        stats.put(task.getWorkerName(), value);
                }
                
                JsonObjectBuilder statsBuilder = Json.createObjectBuilder();
                for (String worker : stats.keySet()) {
                        statsBuilder.add(worker, stats.get(worker));
                }
                builder.add("history", statsBuilder);
                
        JsonObject jsonJob = builder.build();
        return jsonJob.toString();
    }
}