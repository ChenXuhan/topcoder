package com.buaa.act.sdp.bean.challenge;

/**
 * Created by YLT on 2016/10/18.
 */
public class ChallengeSubmission {
    private int id;
    private int challengeID;
    private String handle;
    private String placement;
    private String submissionDate;
    private String submissionStatus;
    private String points;
    private String finalScore;
    private String screeningScore;
    private String initialScore;
    private String submissionDownloadLink;

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

    public String getHandle() {
        return handle;
    }

    public void setHandle(String handle) {
        this.handle = handle;
    }

    public String getPlacement() {
        return placement;
    }

    public void setPlacement(String placement) {
        this.placement = placement;
    }

    public String getSubmissionDate() {
        return submissionDate;
    }

    public void setSubmissionDate(String submissionDate) {
        this.submissionDate = submissionDate;
    }

    public String getSubmissionStatus() {
        return submissionStatus;
    }

    public void setSubmissionStatus(String submissionStatus) {
        this.submissionStatus = submissionStatus;
    }

    public String getPoints() {
        return points;
    }

    public void setPoints(String points) {
        this.points = points;
    }

    public String getFinalScore() {
        return finalScore;
    }

    public void setFinalScore(String finalScore) {
        this.finalScore = finalScore;
    }

    public String getScreeningScore() {
        return screeningScore;
    }

    public void setScreeningScore(String screeningScore) {
        this.screeningScore = screeningScore;
    }

    public String getInitialScore() {
        return initialScore;
    }

    public void setInitialScore(String initialScore) {
        this.initialScore = initialScore;
    }

    public String getSubmissionDownloadLink() {
        return submissionDownloadLink;
    }

    public void setSubmissionDownloadLink(String submissionDownloadLink) {
        this.submissionDownloadLink = submissionDownloadLink;
    }
}
