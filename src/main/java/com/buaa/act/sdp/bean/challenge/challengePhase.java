package com.buaa.act.sdp.bean.challenge;

/**
 * Created by YLT on 2016/10/18.
 */
public class ChallengePhase {
    private String challengeID;
    private String type;
    private String status;
    private String scheduledStartTime;
    private String actualStartTime;
    private String scheduledEndTime;
    private String actualendTime;

    public ChallengePhase(String challengeID, String type, String status, String scheduledStartTime, String actualStartTime, String scheduledEndTime, String actualendTime) {
        this.challengeID = challengeID;
        this.type = type;
        this.status = status;
        this.scheduledStartTime = scheduledStartTime;
        this.actualStartTime = actualStartTime;
        this.scheduledEndTime = scheduledEndTime;
        this.actualendTime = actualendTime;
    }

    public String getChallengeID() {
        return challengeID;
    }

    public void setChallengeID(String challengeID) {
        this.challengeID = challengeID;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getScheduledStartTime() {
        return scheduledStartTime;
    }

    public void setScheduledStartTime(String scheduledStartTime) {
        this.scheduledStartTime = scheduledStartTime;
    }

    public String getActualStartTime() {
        return actualStartTime;
    }

    public void setActualStartTime(String actualStartTime) {
        this.actualStartTime = actualStartTime;
    }

    public String getScheduledEndTime() {
        return scheduledEndTime;
    }

    public void setScheduledEndTime(String scheduledEndTime) {
        this.scheduledEndTime = scheduledEndTime;
    }

    public String getActualendTime() {
        return actualendTime;
    }

    public void setActualendTime(String actualendTime) {
        this.actualendTime = actualendTime;
    }



}
