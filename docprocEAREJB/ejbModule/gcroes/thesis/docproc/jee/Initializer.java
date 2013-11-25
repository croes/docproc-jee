package gcroes.thesis.docproc.jee;

import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Initializer implements ServletContextListener{
    
    private static final Logger logger = LogManager.getLogger(Initializer.class.getClass().getName());

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        logger.info("Servlet context initialized. Ready to set up environment.");
        
    }

}
