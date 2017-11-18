package com.buaa.act.sdp.topcoder.model.user;

/**
 * Created by yang on 2017/11/18.
 */
public class Registrant {

    private String name;
    private String registerTime;
    private String submissionTime;
    private double score;
    private boolean isWinner;

    public Registrant() {
        isWinner=false;
    }

    public Registrant(String name, String registerTime, String submissionTime, double score, boolean isWinner) {
        this.name = name;
        this.registerTime = registerTime;
        this.submissionTime = submissionTime;
        this.score = score;
        this.isWinner = isWinner;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getRegisterTime() {
        return registerTime;
    }

    public void setRegisterTime(String registerTime) {
        this.registerTime = registerTime;
    }

    public String getSubmissionTime() {
        return submissionTime;
    }

    public void setSubmissionTime(String submissionTime) {
        this.submissionTime = submissionTime;
    }

    public double getScore() {
        return score;
    }

    public void setScore(double score) {
        this.score = score;
    }

    public boolean isWinner() {
        return isWinner;
    }

    public void setWinner(boolean winner) {
        isWinner = winner;
    }
}
