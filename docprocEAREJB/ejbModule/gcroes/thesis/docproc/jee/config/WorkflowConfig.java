/*
    Copyright 2013 KU Leuven Research and Development - iMinds - Distrinet

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

        http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.

    Administrative Contact: dnet-project-office@cs.kuleuven.be
    Technical Contact: bart.vanbrabant@cs.kuleuven.be
 */

package gcroes.thesis.docproc.jee.config;

import java.util.HashMap;
import java.util.Map;

/**
 * The configuration of an entire workflow.
 * 
 * @author Bart Vanbrabant <bart.vanbrabant@cs.kuleuven.be>
 */
public class WorkflowConfig {
    private String workflowName = null;
    private Map<String, Map<String, String>> steps = new HashMap<>();
    private String workflowStart = null;

    public WorkflowConfig(String workflow) {
        this.setWorkflowName(workflow);
    }

    /**
     * Parse the workflow configuration
     */
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public static Map<String, WorkflowConfig> parseWorkflows(Map data) {
        Map<String, WorkflowConfig> workflows = new HashMap<>();

        for (Object workflow : data.keySet()) {
            WorkflowConfig wf = new WorkflowConfig((String) workflow);
            workflows.put((String) workflow, wf);

            Map<String, Object> options = (Map<String, Object>) data.get(workflow);

            if (options.containsKey("steps")) {
                Map<String, Map<String, String>> steps = (Map<String, Map<String, String>>) options.get("steps");

                for (Map.Entry<String, Map<String, String>> step : steps.entrySet()) {
                    wf.addStep(step.getKey(), step.getValue());
                }
            }
            
            if (options.containsKey("start")) {
                wf.setWorkflowStart((String)options.get("start"));
            }
            
        }

        return workflows;
    }
    
    /**
     * Add a new step to the workflow
     * 
     * @param step
     *            The step in the workflow to define next steps for.
     * @param other
     *            The list of next steps
     */
    private void addStep(String key, Map<String, String> value) {
        this.steps.put(key, value);
    }

    /**
     * @return the workflowName
     */
    public String getWorkflowName() {
        return workflowName;
    }

    /**
     * @param workflowName
     *            the workflowName to set
     */
    public void setWorkflowName(String workflowName) {
        this.workflowName = workflowName;
    }

    /**
     * @return the workflowStart
     */
    public String getWorkflowStart() {
        return workflowStart;
    }

    /**
     * @param workflowStart
     *            the workflowStart to set
     */
    public void setWorkflowStart(String workflowStart) {
        this.workflowStart = workflowStart;
    }

    /**
     * @return the steps
     */
    public Map<String, Map<String, String>> getSteps() {
        return steps;
    }
    
    /**
     * Get the next step. Returns the stepname if no mapping is found.
     */
    public String getNextStep(String currentStep, String stepName) {
        if (!this.steps.containsKey(currentStep)) {
            return stepName;
        }
        Map<String,String> current = this.steps.get(currentStep);
        
        if (!current.containsKey(stepName)) {
            return stepName;
        }
        
        return current.get(stepName);
    }
}
