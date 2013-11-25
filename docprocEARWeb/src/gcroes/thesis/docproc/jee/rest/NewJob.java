package gcroes.thesis.docproc.jee.rest;

import java.util.Date;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.MediaType;

import gcroes.thesis.docproc.jee.entity.Task;
import gcroes.thesis.docproc.jee.entity.Job;
import gcroes.thesis.docproc.jee.Service;

@Path("/newjob/{workflow}")
@Stateless
public class NewJob {
    
    @EJB
    private Service service;

    @POST
    @Consumes(MediaType.APPLICATION_FORM_URLENCODED)
    public String newJob(@PathParam("workflow") String workflowName,
            @FormParam("start_after") String start_after,
            @FormParam("finish_before") String finish_before,
            @FormParam("arg0") String arg0) {
        // get the delay
        int after = Integer.valueOf(start_after);
        int before = Integer.valueOf(finish_before);

        // create a workflow and save it
        Job job = new Job(workflowName);
        if (after > 0) {
            job.setStartAfter(new Date(after));
        }
        if (before > 0) {
            job.setFinishBefore(new Date(before));
        }

        Task task = job.newStartTask();
        //FIXME task.addParam("arg0", arg0);

        service.addJob(job);

        return "[]";
    }
}
