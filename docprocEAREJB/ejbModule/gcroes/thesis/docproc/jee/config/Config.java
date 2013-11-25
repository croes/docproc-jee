package gcroes.thesis.docproc.jee.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.yaml.snakeyaml.Yaml;

/**
* The configuration of the workers
*
* @author Bart Vanbrabant <bart.vanbrabant@cs.kuleuven.be>
*/
public class Config {
        private static Config config = null;
        private Map<String, WorkerConfig> workers = null;
        private Map<String, WorkflowConfig> workflows = null;
        private SchedulerConfig scheduler = null;
        private static Logger logger = Logger.getLogger(Config.class.getCanonicalName());
        
        private Map<String,String> properties = new HashMap<>();
        
        /**
         * The default constructor for the configuration.
         */
        private Config() {
        }
        
        private void loadProperties(Properties props) {
                for (Entry<Object, Object> prop : props.entrySet()) {
                        this.properties.put((String)prop.getKey(), (String)prop.getValue());
                }
        }
        
        @SuppressWarnings({ "unchecked", "rawtypes" })
        private static Config loadConfig() {
                Config cfg = new Config();
                
                // load (default) properties
                try {
                        InputStream propstream = Config.class.getClassLoader().getResourceAsStream("config.properties");
                        Properties defaultProps = new Properties();
                        defaultProps.load(propstream);
                        cfg.loadProperties(defaultProps);
                } catch (IOException e) {
                        logger.severe("Unable to read properties file in jar");
                }
                
                // load props from config file (if exists)
                try {
                        String propsPath = System.getProperty("taskworker.properties", null);
                        
                        if (propsPath != null) {
                                InputStream propstream = new FileInputStream(propsPath);
                                Properties props = new Properties();
                                props.load(propstream);
                                cfg.loadProperties(props);
                        }
                } catch (IOException e) {
                        throw new IllegalStateException("Unable to read properties file");
                }
                
                // add system properties to the properties list as well (have precedence)
                cfg.loadProperties(System.getProperties());
                
                // load the workers config file
                String path = cfg.getProperty("taskworker.configfile", "config.yaml");
                logger.info("Loading configuration file " + path);
                
                File file = new File(path);
                if (!file.canRead()) {
                        System.err.println(path + " is not readable.");
                } else {
                        Yaml yaml = new Yaml();
                        Map data;
                        try {
                                data = (Map) yaml.load(new FileInputStream(file));
                                cfg.setWorkers(WorkerConfig.parseWorkers((List) data.get("workers")));
                                cfg.setWorkflows(WorkflowConfig.parseWorkflows((Map) data.get("workflows")));
                                cfg.setScheduler(SchedulerConfig.parseScheduler((Map) data.get("scheduler")));
                        } catch (FileNotFoundException e) {
                                logger.log(Level.SEVERE, "Unable to load config yaml file.");
                        }
                }

                return cfg;
        }
        
        
        public static Config getConfig() {
                if (config == null) {
                        config = loadConfig();
                }

                return config;
        }
        
        public static Config cfg() {
                return getConfig();
        }
        
        /**
         * @return the workflows
         */
        public Map<String, WorkflowConfig> getWorkflows() {
                return workflows;
        }

        /**
         * @param workflows the workflows to set
         */
        public void setWorkflows(Map<String, WorkflowConfig> workflows) {
                this.workflows = workflows;
        }

        /**
         * @return the workers
         */
        public Map<String, WorkerConfig> getWorkers() {
                return workers;
        }

        /**
         * @param workers the workers to set
         */
        public void setWorkers(Map<String, WorkerConfig> workers) {
                this.workers = workers;
        }

        

        public void setScheduler(SchedulerConfig sched) {
                this.scheduler = sched;
                
        }
        
        public SchedulerConfig getScheduler() {
                return scheduler;
        }

        /**
         * Get a property by the given name
         *
         * @param name The name of the property
         * @return The requested property or null if not found
         */
        public String getProperty(String name) {
                return this.getProperty(name, null);
        }
        
        /**
         * Get a property with the given name
         *
         * @param name The name of the property
         * @param default_value The value if the property is not found
         * @return The requested property
         */
        public String getProperty(String name, String default_value) {
                if (this.properties.containsKey(name)) {
                        return this.properties.get(name);
                }
                return default_value;
        }
        
        public boolean getProperty(String name, boolean default_value) {
                String value = getProperty(name);
                
                if (value == null) {
                        return default_value;
                }
                
                return Boolean.parseBoolean(value);
        }
        
        public int getProperty(String name, int default_value) {
                String value = getProperty(name);
                
                if (value == null) {
                        return default_value;
                }
                
                return Integer.parseInt(value);
        }
        
        /**
         * Get the workflow with the given name.
         * @param name
         * @return
         */
        public WorkflowConfig getWorkflow(String name) {
                if (!this.workflows.containsKey(name)) {
                        throw new IllegalArgumentException("There is no workflow with name " + name);
                }
                
                return this.workflows.get(name);
        }
}
