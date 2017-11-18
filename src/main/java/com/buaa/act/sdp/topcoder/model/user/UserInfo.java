package com.buaa.act.sdp.topcoder.model.user;

import java.util.List;

/**
 * Created by yang on 2017/11/18.
 */
public class UserInfo {

    private User user;
    private List<Development> developments;
    private List<DevelopmentHistory> developmentHistories;

    public UserInfo() {
    }

    public UserInfo(User user, List<Development> developments, List<DevelopmentHistory> developmentHistories) {
        this.user = user;
        this.developments = developments;
        this.developmentHistories = developmentHistories;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public List<Development> getDevelopments() {
        return developments;
    }

    public void setDevelopments(List<Development> developments) {
        this.developments = developments;
    }

    public List<DevelopmentHistory> getDevelopmentHistories() {
        return developmentHistories;
    }

    public void setDevelopmentHistories(List<DevelopmentHistory> developmentHistories) {
        this.developmentHistories = developmentHistories;
    }
}
