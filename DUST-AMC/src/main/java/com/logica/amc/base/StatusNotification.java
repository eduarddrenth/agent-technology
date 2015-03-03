package com.logica.amc.base;

import jade.content.Concept;
import jade.util.leap.List;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;

/**
 *
 * @author Eduard Drenth: Logica, 20-nov-2009
 * 
 */
public class StatusNotification implements Concept {

    public static final String START="start";
    public static final String SUCCESS="success";
    public static final String UPDATE="update";
    public static final String RESUBMIT="resubmit";
    public static final String ERROR="error";
    public static final String JOBSTART="jobstart";
    public static final String JOBERROR="joberror";
    public static final String JOBSUCCESS="jobsuccess";
    private static final java.util.List<String> types = Collections.unmodifiableList(
            Arrays.asList(
                new String[] {
                    START, SUCCESS, UPDATE, RESUBMIT, ERROR, JOBSTART, JOBERROR, JOBSUCCESS
                }
            )
        );


    private String type;
    private String message;
    private String workflow;
    private String jobid;
    private Date date;
    private String details;

    public List getEmails() {
        return emails;
    }

    public void setEmails(List emails) {
        this.emails = emails;
    }
    private List emails;

    public String getJobid() {
        return jobid;
    }

    public void setJobid(String jobid) {
        this.jobid = jobid;
    }

    public String getWorkflow() {
        return workflow;
    }

    public void setWorkflow(String workflow) {
        this.workflow = workflow;
    }

    /**
     * Get the value of type
     *
     * @return the value of type
     */
    public String getType() {
        return type;
    }

    /**
     * Set the value of type
     *
     * @param type new value of type
     */
    public void setType(String type) {
        if (!types.contains(type)) {
            throw new IllegalArgumentException(type + " not valid");
        }
        this.type = type;
    }

    /**
     * Get the value of message
     *
     * @return the value of message
     */
    public String getMessage() {
        return message;
    }

    /**
     * Set the value of message
     *
     * @param message new value of message
     */
    public void setMessage(String message) {
        this.message = message;
    }

    /**
     * Get the value of date
     *
     * @return the value of date
     */
    public Date getDate() {
        return date;
    }

    /**
     * Set the value of date
     *
     * @param date new value of date
     */
    public void setDate(Date date) {
        this.date = date;
    }

    /**
     * Get the value of details
     *
     * @return the value of details
     */
    public String getDetails() {
        return details;
    }

    /**
     * Set the value of details
     *
     * @param details new value of details
     */
    public void setDetails(String details) {
        this.details = details;
    }

    @Override
    public String toString() {
        String s = "";

        s += "date: " + date;
        s += "\ntype: " + type;
        s += "\nworkflow: " + workflow;
        s += "\njob: " + jobid;
        s += "\nmessage: " + message;
        s += "\ndetails:\n" + details;
        s += "\nemails: \n" + emails;

        return s;
    }

}
