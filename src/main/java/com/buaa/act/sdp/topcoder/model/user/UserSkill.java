package com.buaa.act.sdp.topcoder.model.user;

/**
 * Created by YLT on 2017/4/19.
 */

/**
 * 开发者个人技能得分
 */
public class UserSkill {

    private String handle;
    private String skill;
    private String score;

    public UserSkill(String handle, String skill, String score) {
        this.handle = handle;
        this.skill = skill;
        this.score = score;
    }

    public String getHandle() {
        return handle;
    }

    public void setHandle(String handle) {
        this.handle = handle;
    }

    public String getSkill() {
        return skill;
    }

    public void setSkill(String skill) {
        this.skill = skill;
    }

    public String getScore() {
        return score;
    }

    public void setScore(String score) {
        this.score = score;
    }
}
