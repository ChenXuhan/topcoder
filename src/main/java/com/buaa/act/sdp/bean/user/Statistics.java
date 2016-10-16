package com.buaa.act.sdp.bean.user;

import com.google.gson.annotations.SerializedName;

/**
 * Created by yang on 2016/10/15.
 */
public class Statistics {
    @SerializedName("Tracks")
    private Track track;
    @SerializedName("CompetitionHistory")
    private CompetitionHistory competitionHistory;

    public Track getTrack() {
        return track;
    }

    public void setTrack(Track track) {
        this.track = track;
    }

    public CompetitionHistory getCompetitionHistory() {
        return competitionHistory;
    }

    public void setCompetitionHistory(CompetitionHistory competitionHistory) {
        this.competitionHistory = competitionHistory;
    }
}
