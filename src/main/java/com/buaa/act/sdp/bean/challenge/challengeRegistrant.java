package com.buaa.act.sdp.bean.challenge;

/**
 * Created by YLT on 2016/10/18.
 */
public class ChallengeRegistrant {
    private String challengeID;
    private String handle;
    private String reliability;
    private String registrationDate;
    private String submissionDate;
    private String rating;


    public ChallengeRegistrant(String challengeID, String handle, String reliability, String registrationDate, String submissionDate, String rating) {
        this.challengeID = challengeID;
        this.handle = handle;
        this.reliability = reliability;
        this.registrationDate = registrationDate;
        this.submissionDate = submissionDate;
        this.rating = rating;
    }

    public String getChallengeID() {
        return challengeID;
    }

    public void setChallengeID(String challengeID) {
        this.challengeID = challengeID;
    }

    public String getHandle() {
        return handle;
    }

    public void setHandle(String handle) {
        this.handle = handle;
    }

    public String getReliability() {
        return reliability;
    }

    public void setReliability(String reliability) {
        this.reliability = reliability;
    }

    public String getRegistrationDate() {
        return registrationDate;
    }

    public void setRegistrationDate(String registrationDate) {
        this.registrationDate = registrationDate;
    }

    public String getSubmissionDate() {
        return submissionDate;
    }

    public void setSubmissionDate(String submissionDate) {
        this.submissionDate = submissionDate;
    }

    public String getRating() {
        return rating;
    }

    public void setRating(String rating) {
        this.rating = rating;
    }






}
