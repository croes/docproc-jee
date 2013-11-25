/**
 *
 *     Copyright 2013 KU Leuven Research and Development - iMinds - Distrinet
 *
 *     Licensed under the Apache License, Version 2.0 (the "License");
 *     you may not use this file except in compliance with the License.
 *     You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 *     Unless required by applicable law or agreed to in writing, software
 *     distributed under the License is distributed on an "AS IS" BASIS,
 *     WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *     See the License for the specific language governing permissions and
 *     limitations under the License.
 *
 *     Administrative Contact: dnet-project-office@cs.kuleuven.be
 *     Technical Contact: bart.vanbrabant@cs.kuleuven.be
 */
package gcroes.thesis.docproc.jee.config;

import java.lang.reflect.InvocationTargetException;
import java.util.Map;

import gcroes.thesis.docproc.jee.schedule.IScheduler;

@SuppressWarnings("rawtypes")
public class SchedulerConfig {

    private String schedulerClass = null;
    private Map arguments = null;
    

    public SchedulerConfig(String workerClass, Map arguments) {
        super();
        this.schedulerClass = workerClass;
        this.arguments = arguments;
    }
    
    /**
     * Parse the scheduler configuration
     */
    public static SchedulerConfig parseScheduler(Map map) {
        if(map == null)
            return null;
        String clazz = (String) map.remove("class");
        return new SchedulerConfig(clazz,map);
    }

    public String getSchedulerClass() {
        return schedulerClass;
    }

    protected void setSchedulerClass(String workerClass) {
        this.schedulerClass = workerClass;
    }

    public Map getArguments() {
        return arguments;
    }

    protected void setArguments(Map arguments) {
        this.arguments = arguments;
    }
    
    
    public void create(){
        try {
            @SuppressWarnings("unchecked")
            Class<IScheduler> workerCls = (Class<IScheduler>)Class.forName(this.getSchedulerClass());
            IScheduler sched = workerCls.getConstructor().newInstance();
            sched.enable(getArguments());
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            throw new IllegalArgumentException("Class " + this.schedulerClass + " should have a default constructor.");
        } catch (SecurityException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
        
    }
}
