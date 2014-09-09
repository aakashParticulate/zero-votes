///*
// * To change this license header, choose License Headers in Project Properties.
// * To change this template file, choose Tools | Templates
// * and open the template in the editor.
// */
//
//package com.zero.votes.cronjobs;
//
//import java.util.concurrent.Executors;
//import java.util.concurrent.ScheduledExecutorService;
//import java.util.concurrent.TimeUnit;
//import javax.annotation.PostConstruct;
//import javax.annotation.PreDestroy;
//import javax.ejb.EJB;
//import javax.ejb.Startup;
//import javax.inject.Singleton;
//
//@Startup
//@Singleton
//public class TaskRunner {
//    
//    private ScheduledExecutorService taskRunner;
//    @EJB
//    private ZVotesScheduler zVotesScheduler;
//    
//    @PostConstruct
//    public void init() {
//        taskRunner = Executors.newSingleThreadScheduledExecutor();
//        taskRunner.scheduleAtFixedRate(zVotesScheduler, 0, 1, TimeUnit.MINUTES);
//    }
//
//    @PreDestroy
//    public void destroy() {
//        taskRunner.shutdownNow();
//    }
//
//}