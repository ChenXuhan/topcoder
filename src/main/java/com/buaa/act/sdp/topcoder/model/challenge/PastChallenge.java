package com.buaa.act.sdp.topcoder.model.challenge;

/**
 * Created by yang on 2016/10/19.
 */

/**
 * 历史任务
 */
public class PastChallenge {

    private int challengeId;
    private String registrationStartDate;
    private int numSubmissions;
    private int numRegistrants;

    public int getChallengeId() {
        return challengeId;
    }

    public void setChallengeId(int challengeId) {
        this.challengeId = challengeId;
    }

    public String getRegistrationStartDate() {
        return registrationStartDate;
    }

    public void setRegistrationStartDate(String registrationStartDate) {
        this.registrationStartDate = registrationStartDate;
    }

    public int getNumSubmissions() {
        return numSubmissions;
    }

    public void setNumSubmissions(int numSubmissions) {
        this.numSubmissions = numSubmissions;
    }

    public int getNumRegistrants() {
        return numRegistrants;
    }

    public void setNumRegistrants(int numRegistrants) {
        this.numRegistrants = numRegistrants;
    }
}
