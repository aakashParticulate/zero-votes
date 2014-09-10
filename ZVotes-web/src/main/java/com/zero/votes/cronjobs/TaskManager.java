package com.zero.votes.cronjobs;

import javax.ejb.Singleton;
import javax.ejb.Startup;
import java.util.logging.Level;
import java.util.logging.Logger;

@Startup
@Singleton
public class TaskManager {
    
    private static final Logger log = Logger.getLogger("TaskManager");
    
    public void doTask() {
        log.log(Level.WARNING, "[TaskManager] doTask");
    }

}
