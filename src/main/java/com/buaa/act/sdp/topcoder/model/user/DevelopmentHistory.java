package com.buaa.act.sdp.topcoder.model.user;

/**
 * Created by yang on 2016/10/15.
 */

/**
 * 开发者历史完成任务信息
 */
public class DevelopmentHistory {

    private int id;
    private String handle;
    private String developType;
    private int competitions;
    private int submissions;
    private String submissionRate;
    private int inquiries;
    private int passedScreening;
    private String screeningSuccessRate;
    private int passedReview;
    private String reviewSuccessRate;
    private int appeals;
    private String appealSuccessRate;
    private int wins;
    private String winPercentage;
    private double maximumScore;
    private double minimumScore;
    private double averageScore;
    private double averagePlacement;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getHandle() {
        return handle;
    }

    public void setHandle(String handle) {
        this.handle = handle;
    }

    public String getDevelopType() {
        return developType;
    }

    public void setDevelopType(String developType) {
        this.developType = developType;
    }

    public int getCompetitions() {
        return competitions;
    }

    public void setCompetitions(int competitions) {
        this.competitions = competitions;
    }

    public int getSubmissions() {
        return submissions;
    }

    public void setSubmissions(int submissions) {
        this.submissions = submissions;
    }

    public String getSubmissionRate() {
        return submissionRate;
    }

    public void setSubmissionRate(String submissionRate) {
        this.submissionRate = submissionRate;
    }

    public int getInquiries() {
        return inquiries;
    }

    public void setInquiries(int inquiries) {
        this.inquiries = inquiries;
    }

    public int getPassedScreening() {
        return passedScreening;
    }

    public void setPassedScreening(int passedScreening) {
        this.passedScreening = passedScreening;
    }

    public String getScreeningSuccessRate() {
        return screeningSuccessRate;
    }

    public void setScreeningSuccessRate(String screeningSuccessRate) {
        this.screeningSuccessRate = screeningSuccessRate;
    }

    public int getPassedReview() {
        return passedReview;
    }

    public void setPassedReview(int passedReview) {
        this.passedReview = passedReview;
    }

    public String getReviewSuccessRate() {
        return reviewSuccessRate;
    }

    public void setReviewSuccessRate(String reviewSuccessRate) {
        this.reviewSuccessRate = reviewSuccessRate;
    }

    public int getAppeals() {
        return appeals;
    }

    public void setAppeals(int appeals) {
        this.appeals = appeals;
    }

    public String getAppealSuccessRate() {
        return appealSuccessRate;
    }

    public void setAppealSuccessRate(String appealSuccessRate) {
        this.appealSuccessRate = appealSuccessRate;
    }

    public int getWins() {
        return wins;
    }

    public void setWins(int wins) {
        this.wins = wins;
    }

    public String getWinPercentage() {
        return winPercentage;
    }

    public void setWinPercentage(String winPercentage) {
        this.winPercentage = winPercentage;
    }

    public double getMaximumScore() {
        return maximumScore;
    }

    public void setMaximumScore(double maximumScore) {
        this.maximumScore = maximumScore;
    }

    public double getMinimumScore() {
        return minimumScore;
    }

    public void setMinimumScore(double minimumScore) {
        this.minimumScore = minimumScore;
    }

    public double getAverageScore() {
        return averageScore;
    }

    public void setAverageScore(double averageScore) {
        this.averageScore = averageScore;
    }

    public double getAveragePlacement() {
        return averagePlacement;
    }

    public void setAveragePlacement(double averagePlacement) {
        this.averagePlacement = averagePlacement;
    }
}
