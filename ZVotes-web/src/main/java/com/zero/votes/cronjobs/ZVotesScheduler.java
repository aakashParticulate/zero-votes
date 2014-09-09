//package com.zero.votes.cronjobs;
//
//import java.io.Serializable;
//import java.util.Date;
//import java.util.HashMap;
//import javax.ejb.Startup;
//import javax.inject.Named;
//import javax.inject.Singleton;
//
//@Startup
//@Singleton
//@Named("zVotesScheduler")
//public class ZVotesScheduler implements Runnable, Serializable {
//    public HashMap<String, HashMap<Date, Runnable>> tasks = new HashMap<>();
//
//    @Override
//    public void run() {
//        for (HashMap.Entry pairs : tasks.entrySet()) {
//            String taskId = (String) pairs.getKey();
//            HashMap<Date, Runnable> taskMap = (HashMap<Date, Runnable>) pairs.getValue();
//            for (HashMap.Entry taskPairs : taskMap.entrySet()) {
//                Runnable task = (Runnable) taskPairs.getValue();
//                Date taskDate = (Date) taskPairs.getKey();
//                if (taskDate.before(new Date())) {
//                    task.run();
//                    taskMap.remove(taskDate);
//                }
//            }
//            if (taskMap.isEmpty()) {
//                tasks.remove(taskId);
//            }
//        }
//    }
//    
//    public void addTask(String taskId, HashMap<Date, Runnable> taskInfos) {
//        tasks.put(taskId, taskInfos);
//    }
//}
