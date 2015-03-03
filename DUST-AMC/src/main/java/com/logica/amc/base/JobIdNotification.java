package com.logica.amc.base;

import jade.content.Predicate;

/**
 *
 * @author Eduard Drenth: Logica, 23-nov-2009
 * 
 */
public class JobIdNotification implements Predicate {
    private String jobid = null;

    public String getJobid() {
        return jobid;
    }

    public void setJobid(String jobid) {
        this.jobid = jobid;
    }

    

}
