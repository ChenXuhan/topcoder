package com.buaa.act.sdp.bean.challenge;

/**
 * Created by YLT on 2017/3/19.
 */
public class CollaborationRelation {

    private String handle1;
    private String handle2;
    private String score1;
    private String score2;
    private int projectId;
    private int challengeId1;
    private int challengeId2;

    public CollaborationRelation(String handle1, String handle2, String score1, String score2, int projectId, int challengeId1, int challengeId2) {
        this.handle1 = handle1;
        this.handle2 = handle2;
        this.score1 = score1;
        this.score2 = score2;
        this.projectId = projectId;
        this.challengeId1 = challengeId1;
        this.challengeId2 = challengeId2;
    }

    public String getHandle1() {
        return handle1;
    }

    public void setHandle1(String handle1) {
        this.handle1 = handle1;
    }

    public String getHandle2() {
        return handle2;
    }

    public void setHandle2(String handle2) {
        this.handle2 = handle2;
    }

    public String getScore1() {
        return score1;
    }

    public void setScore1(String score1) {
        this.score1 = score1;
    }

    public String getScore2() {
        return score2;
    }

    public void setScore2(String score2) {
        this.score2 = score2;
    }

    public int getProjectId() {
        return projectId;
    }

    public void setProjectId(int projectId) {
        this.projectId = projectId;
    }

    public int getChallengeId1() {
        return challengeId1;
    }

    public void setChallengeId1(int challengeId1) {
        this.challengeId1 = challengeId1;
    }

    public int getChallengeId2() {
        return challengeId2;
    }

    public void setChallengeId2(int challengeId2) {
        this.challengeId2 = challengeId2;
    }

}
