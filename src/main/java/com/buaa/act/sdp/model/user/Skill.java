package com.buaa.act.sdp.model.user;

/**
 * Created by yang on 2016/10/21.
 */
public class Skill {
    private String tagName;
    private boolean hidden;
    private int score;
    private  String[]sources;

    public String getTagName() {
        return tagName;
    }

    public void setTagName(String tagName) {
        this.tagName = tagName;
    }

    public boolean isHidden() {
        return hidden;
    }

    public void setHidden(boolean hidden) {
        this.hidden = hidden;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public String[] getSources() {
        return sources;
    }

    public void setSources(String[] sources) {
        this.sources = sources;
    }
}
