package com.zero.votes.cronjobs;

import javax.ejb.Singleton;
import javax.ejb.Startup;

@Startup
@Singleton
public class TaskManager {
    
    public String doTask() {
        throw new RuntimeException("Yes!");
    }

}
