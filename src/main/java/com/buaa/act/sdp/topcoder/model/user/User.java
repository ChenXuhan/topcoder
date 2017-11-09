package com.buaa.act.sdp.topcoder.model.user;

/**
 * Created by yang on 2016/10/15.
 */

/**
 * 开发者基本信息
 */
public class User {

    private int id;
    private String handle;
    private String country;
    private String memberSince;
    private String quote;
    private String photoLink;
    private boolean copilot;
    private String[] skills;
    private int competitionNums;
    private int submissionNums;
    private int winNums;
    private String skillDegree;

    public int getSubmissionNums() {
        return submissionNums;
    }

    public void setSubmissionNums(int submissionNums) {
        this.submissionNums = submissionNums;
    }

    public int getCompetitionNums() {
        return competitionNums;
    }

    public void setCompetitionNums(int competitionNums) {
        this.competitionNums = competitionNums;
    }

    public int getWinNums() {
        return winNums;
    }

    public void setWinNums(int winNums) {
        this.winNums = winNums;
    }

    public String[] getSkills() {
        return skills;
    }

    public void setSkills(String[] skills) {
        this.skills = skills;
    }

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

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getMemberSince() {
        return memberSince;
    }

    public void setMemberSince(String memberSince) {
        this.memberSince = memberSince;
    }

    public String getQuote() {
        return quote;
    }

    public void setQuote(String quote) {
        this.quote = quote;
    }

    public String getPhotoLink() {
        return photoLink;
    }

    public void setPhotoLink(String photoLink) {
        this.photoLink = photoLink;
    }

    public boolean isCopilot() {
        return copilot;
    }

    public void setCopilot(boolean copilot) {
        this.copilot = copilot;
    }

    public String getSkillDegree() {
        return skillDegree;
    }

    public void setSkillDegree(String skillDegree) {
        this.skillDegree = skillDegree;
    }

}
