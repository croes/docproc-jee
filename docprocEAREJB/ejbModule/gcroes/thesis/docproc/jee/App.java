package gcroes.thesis.docproc.jee;

import javax.annotation.PostConstruct;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import static gcroes.thesis.docproc.jee.config.Config.cfg;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@Singleton
@Startup
public class App {

    private static final Logger logger = LogManager.getLogger(App.class
            .getClass().getName());

    @PostConstruct
    public void init() {
        logger.entry();
        logger.info("Docproc initializing...");
        logger.info("Loading config..." + cfg());
        logger.info("Done loading config");
        logger.exit();
    }
}
