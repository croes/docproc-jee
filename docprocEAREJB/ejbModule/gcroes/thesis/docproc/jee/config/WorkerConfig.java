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

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * The configuration of a worker.
 *
 * @author Bart Vanbrabant <bart.vanbrabant@cs.kuleuven.be>
 */
public class WorkerConfig {
    private String workerName = null;
    private String workerClass = null;
    private int threads = 1;
    private static Logger logger = Logger.getLogger(Config.class.getCanonicalName());
    
    private URLClassLoader urlLoader = null;
    private String code = null;
    
    public WorkerConfig(String workerName, String workerClass, String code) {
        this.workerClass = workerClass;
        this.workerName = workerName;
        this.code = code;
    }
    
    private URLClassLoader getLoader() {
        if (urlLoader == null) {
            try {
                File jarFile = new File(this.code);
                
                if (!jarFile.isFile()) {
                    logger.log(Level.SEVERE, "Unable to locate jar " + jarFile);
                    return null;
                }
                
                this.urlLoader = new URLClassLoader(new URL[]{jarFile.toURI().toURL()}, 
                    gcroes.thesis.docproc.jee.Worker.class.getClassLoader());
            } catch(IOException e) {
                logger.log(Level.SEVERE, "Bad url for worker code path", e);
            }

        }
        
        return this.urlLoader;
    }
    
    /**
     * @return the workerName
     */
    public String getWorkerName() {
        return workerName;
    }
    /**
     * @param workerName the workerName to set
     */
    public void setWorkerName(String workerName) {
        this.workerName = workerName;
    }
    /**
     * @return the workerClass
     */
    public String getWorkerClass() {
        return workerClass;
    }
    /**
     * @param workerClass the workerClass to set
     */
    public void setWorkerClass(String workerClass) {
        this.workerClass = workerClass;
    }
    
    
    /**
     * Get an instance of the given worker
     * 
     * @return An instance of the worker class
     */
    @SuppressWarnings("unchecked")
    public gcroes.thesis.docproc.jee.Worker getWorkerInstance() {
        try {
            Class<gcroes.thesis.docproc.jee.Worker> workerCls = (Class<gcroes.thesis.docproc.jee.Worker>)getLoader().loadClass(this.getWorkerClass());
            //Class<drm.taskworker.Worker> workerCls = (Class<drm.taskworker.Worker>)Class.forName(this.getWorkerClass());
            Constructor<gcroes.thesis.docproc.jee.Worker> workerCtor = workerCls.getConstructor(String.class);
            return workerCtor.newInstance(this.getWorkerName());
        } catch (ClassNotFoundException e) {
            logger.severe("Unable to load class " + this.getWorkerClass() + " for worker " + this.getWorkerName());
        } catch (NoSuchMethodException e) {
            throw new IllegalArgumentException("Class " + this.workerClass + " should have a constructor that accepts the name of the worker.");
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
        return null;
    }
    
    /**
     * Create a list of worker
     */
    @SuppressWarnings("unchecked")
    public static Map<String,WorkerConfig> parseWorkers(@SuppressWarnings("rawtypes") List<Map> workers) {
        Map<String,WorkerConfig> results = new HashMap<String, WorkerConfig>();
        if (workers != null) {
            for (Map<String,Object> map : workers) {
                if (!map.containsKey("name") || !map.containsKey("class") || !map.containsKey("code")) {
                    throw new IllegalArgumentException("Each worker should have name, class and code attributes set in the config file.");
                }
                
                WorkerConfig obj = new WorkerConfig((String)map.get("name"), (String)map.get("class"), (String)map.get("code"));
                obj.setThreads(Integer.valueOf((Integer)map.get("threads")));
                
                results.put((String)map.get("name"), obj);
            }
        }
            
        return results;
    }
    
    /**
     * Set the number of threads that should be started
     */
    public void setThreads(int threads) {
        this.threads  = threads;
    }
    
    /**
     * Get the number of threads
     */
    public int getThreads() {
        return this.threads;
    }
}
