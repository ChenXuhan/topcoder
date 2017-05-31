package com.buaa.act.sdp.bean.challenge;

/**
 * Created by YLT on 2017/4/15.
 */
public class CompetitionRelation {

    private String handle1;
    private String handle2;
    private String score1;
    private String score2;
    private int challengeId;

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

    public int getChallengeId() {
        return challengeId;
    }

    public void setChallengeId(int challengeId) {
        this.challengeId = challengeId;
    }
}
