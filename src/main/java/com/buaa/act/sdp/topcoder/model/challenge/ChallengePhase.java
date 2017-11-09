package com.buaa.act.sdp.topcoder.model.challenge;

/**
 * Created by YLT on 2016/10/18.
 */

/**
 * 众包任务时间
 */
public class ChallengePhase {

    private int id;
    private int challengeID;
    private String type;
    private String status;
    private String scheduledStartTime;
    private String actualStartTime;
    private String scheduledEndTime;
    private String actualendTime;


    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getChallengeID() {
        return challengeID;
    }

    public void setChallengeID(int challengeID) {
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
