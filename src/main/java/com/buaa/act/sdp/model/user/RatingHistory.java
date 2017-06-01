package com.buaa.act.sdp.model.user;

/**
 * Created by yang on 2016/10/15.
 */
public class RatingHistory {
    private int id;
    private String developType;
    private String handle;
    private int challengeId;
    private String challengeName;
    private String date;
    private int rating;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDevelopType() {
        return developType;
    }

    public void setDevelopType(String developType) {
        this.developType = developType;
    }

    public String getHandle() {
        return handle;
    }

    public void setHandle(String handle) {
        this.handle = handle;
    }

    public int getChallengeId() {
        return challengeId;
    }

    public void setChallengeId(int challengeId) {
        this.challengeId = challengeId;
    }

    public String getChallengeName() {
        return challengeName;
    }

    public void setChallengeName(String challengeName) {
        this.challengeName = challengeName;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }
}
