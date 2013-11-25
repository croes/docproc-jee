package gcroes.thesis.docproc.jee.rest;

import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.json.JsonObjectBuilder;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import gcroes.thesis.docproc.jee.Service;
import gcroes.thesis.docproc.jee.entity.Job;

@Path("/jobs")
@Stateless // Can't reference EJB without this... GF issue
public class Jobs {

    @EJB
    Service service;

    /**
     * Method handling HTTP GET requests.
     */
    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public String getJobs() {
        List<Job> jobs = service.getAllJobs();

        JsonArrayBuilder builder = Json.createArrayBuilder();

        for (Job job : jobs) {
            JsonObjectBuilder jobBuilder = Json
                    .createObjectBuilder()
                    .add("id", job.getJobId())
                    .add("workflow", job.getWorkflowName())
                    .add("start_after",
                            job.getStartAfter() != null ? job.getStartAfter()
                                    .getTime() / 1000 : 0)
                    .add("finish_before",
                            job.getFinishBefore() != null ? job
                                    .getFinishBefore().getTime() / 1000 : 0)
                    .add("started", job.isStarted())
                    .add("finished", job.isFinished())
                    .add("failed", job.isFailed());

            if (job.isStarted()) {
                jobBuilder.add("started_at",
                        job.getStartedAt().getTime() / 1000);
            }
            if (job.isFinished()) {
                jobBuilder.add("finished_at",
                        job.getFinishedAt().getTime() / 1000);
            }
            builder.add(jobBuilder);
        }

        return builder.build().toString();
    }
}