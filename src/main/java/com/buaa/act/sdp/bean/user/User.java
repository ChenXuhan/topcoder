package com.buaa.act.sdp.bean.user;

/**
 * Created by yang on 2016/10/15.
 */
public class User {
    private int id;
    private String handle;
    private String country;
    private String memberSince;
    private String quote;
    private String photoLink;
    private boolean copilot;
    private String[]skills;

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
}
