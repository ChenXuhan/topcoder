package com.buaa.act.sdp.bean.challenge;

/**
 * Created by YLT on 2016/10/17.
 */
public class ChallengeItem {
    private String challengeID;
    private String challengeName;
    private String challengeType;
    private String projectId;
    private String forumId;
    private String screeningScorecardId;
    private String reviewScorecardId;
    private String numberOfCheckpointsPrizes;
    private String topCheckPointPrize;
    private String currentStatus;
    private String postingDate;
    private String registrationEndDate;
    private String submissionEndDate;
    private String finalFixEndDate;
    private String appealsEndDate;
    private String checkpointSubmissionEndDate;
    private String forumLink;
    private String registrationStartDate;
    private String digitalRunPoints;
    private String reliabilityBonus;
    private String challengeCommunity;
    private String technology;
    private String prize;
    private String platforms;
    private int numRegistrants;
    private int numSubmissions;

    public ChallengeItem(String challengeID, String challengeName, String challengeType, String projectId, String forumId, String screeningScorecardId, String reviewScorecardId, String numberOfCheckpointsPrizes, String topCheckPointPrize, String currentStatus, String postingDate, String registrationEndDate, String submissionEndDate, String finalFixEndDate, String appealsEndDate, String checkpointSubmissionEndDate, String forumLink, String registrationStartDate, String digitalRunPoints, String reliabilityBonus, String challengeCommunity, String technology, String prize, String platforms, int numRegistrants, int numSubmissions) {
        this.challengeID = challengeID;
        this.challengeName = challengeName;
        this.challengeType = challengeType;
        this.projectId = projectId;
        this.forumId = forumId;
        this.screeningScorecardId = screeningScorecardId;
        this.reviewScorecardId = reviewScorecardId;
        this.numberOfCheckpointsPrizes = numberOfCheckpointsPrizes;
        this.topCheckPointPrize = topCheckPointPrize;
        this.currentStatus = currentStatus;
        this.postingDate = postingDate;
        this.registrationEndDate = registrationEndDate;
        this.submissionEndDate = submissionEndDate;
        this.finalFixEndDate = finalFixEndDate;
        this.appealsEndDate = appealsEndDate;
        this.checkpointSubmissionEndDate = checkpointSubmissionEndDate;
        this.forumLink = forumLink;
        this.registrationStartDate = registrationStartDate;
        this.digitalRunPoints = digitalRunPoints;
        this.reliabilityBonus = reliabilityBonus;
        this.challengeCommunity = challengeCommunity;
        this.technology = technology;
        this.prize = prize;
        this.platforms = platforms;
        this.numRegistrants = numRegistrants;
        this.numSubmissions = numSubmissions;
    }

    public String getChallengeName() {
        return challengeName;
    }

    public void setChallengeName(String challengeName) {
        this.challengeName = challengeName;
    }

    public String getChallengeType() {
        return challengeType;
    }

    public void setChallengeType(String challengeType) {
        this.challengeType = challengeType;
    }

    public String getProjectId() {
        return projectId;
    }

    public void setProjectId(String projectId) {
        this.projectId = projectId;
    }

    public String getForumId() {
        return forumId;
    }

    public void setForumId(String forumId) {
        this.forumId = forumId;
    }

    public String getScreeningScorecardId() {
        return screeningScorecardId;
    }

    public void setScreeningScorecardId(String screeningScorecardId) {
        this.screeningScorecardId = screeningScorecardId;
    }

    public String getReviewScorecardId() {
        return reviewScorecardId;
    }

    public void setReviewScorecardId(String reviewScorecardId) {
        this.reviewScorecardId = reviewScorecardId;
    }

    public String getNumberOfCheckpointsPrizes() {
        return numberOfCheckpointsPrizes;
    }

    public void setNumberOfCheckpointsPrizes(String numberOfCheckpointsPrizes) {
        this.numberOfCheckpointsPrizes = numberOfCheckpointsPrizes;
    }

    public String getTopCheckPointPrize() {
        return topCheckPointPrize;
    }

    public void setTopCheckPointPrize(String topCheckPointPrize) {
        this.topCheckPointPrize = topCheckPointPrize;
    }

    public String getCurrentStatus() {
        return currentStatus;
    }

    public void setCurrentStatus(String currentStatus) {
        this.currentStatus = currentStatus;
    }

    public String getPostingDate() {
        return postingDate;
    }

    public void setPostingDate(String postingDate) {
        this.postingDate = postingDate;
    }

    public String getRegistrationEndDate() {
        return registrationEndDate;
    }

    public void setRegistrationEndDate(String registrationEndDate) {
        this.registrationEndDate = registrationEndDate;
    }

    public String getSubmissionEndDate() {
        return submissionEndDate;
    }

    public void setSubmissionEndDate(String submissionEndDate) {
        this.submissionEndDate = submissionEndDate;
    }

    public String getFinalFixEndDate() {
        return finalFixEndDate;
    }

    public void setFinalFixEndDate(String finalFixEndDate) {
        this.finalFixEndDate = finalFixEndDate;
    }

    public String getAppealsEndDate() {
        return appealsEndDate;
    }

    public void setAppealsEndDate(String appealsEndDate) {
        this.appealsEndDate = appealsEndDate;
    }

    public String getCheckpointSubmissionEndDate() {
        return checkpointSubmissionEndDate;
    }

    public void setCheckpointSubmissionEndDate(String checkpointSubmissionEndDate) {
        this.checkpointSubmissionEndDate = checkpointSubmissionEndDate;
    }

    public String getForumLink() {
        return forumLink;
    }

    public void setForumLink(String forumLink) {
        this.forumLink = forumLink;
    }

    public String getRegistrationStartDate() {
        return registrationStartDate;
    }

    public void setRegistrationStartDate(String registrationStartDate) {
        this.registrationStartDate = registrationStartDate;
    }

    public String getDigitalRunPoints() {
        return digitalRunPoints;
    }

    public void setDigitalRunPoints(String digitalRunPoints) {
        this.digitalRunPoints = digitalRunPoints;
    }

    public String getReliabilityBonus() {
        return reliabilityBonus;
    }

    public void setReliabilityBonus(String reliabilityBonus) {
        this.reliabilityBonus = reliabilityBonus;
    }

    public String getChallengeCommunity() {
        return challengeCommunity;
    }

    public void setChallengeCommunity(String challengeCommunity) {
        this.challengeCommunity = challengeCommunity;
    }

    public String getTechnology() {
        return technology;
    }

    public void setTechnology(String technology) {
        this.technology = technology;
    }

    public String getPrize() {
        return prize;
    }

    public void setPrize(String prize) {
        this.prize = prize;
    }

    public String getPlatforms() {
        return platforms;
    }

    public void setPlatforms(String platforms) {
        this.platforms = platforms;
    }

    public int getNumRegistrants() {
        return numRegistrants;
    }

    public void setNumRegistrants(int numRegistrants) {
        this.numRegistrants = numRegistrants;
    }

    public int getNumSubmissions() {
        return numSubmissions;
    }

    public void setNumSubmissions(int numSubmissions) {
        this.numSubmissions = numSubmissions;
    }

    public String getChallengeID() {
        return challengeID;
    }

    public void setChallengeID(String challengeID) {
        this.challengeID = challengeID;
    }
}
