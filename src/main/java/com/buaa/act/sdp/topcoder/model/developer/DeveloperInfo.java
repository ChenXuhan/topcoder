package com.buaa.act.sdp.topcoder.model.developer;

import java.util.List;

/**
 * Created by yang on 2017/11/18.
 */
public class DeveloperInfo {

    private Developer developer;
    private List<Development> developments;
    private List<DevelopmentHistory> developmentHistories;

    public DeveloperInfo() {
    }

    public DeveloperInfo(Developer developer, List<Development> developments, List<DevelopmentHistory> developmentHistories) {
        this.developer = developer;
        this.developments = developments;
        this.developmentHistories = developmentHistories;
    }

    public Developer getDeveloper() {
        return developer;
    }

    public void setDeveloper(Developer developer) {
        this.developer = developer;
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
