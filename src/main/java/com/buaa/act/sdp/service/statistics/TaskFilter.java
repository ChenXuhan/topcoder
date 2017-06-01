package com.buaa.act.sdp.service.statistics;

import com.buaa.act.sdp.model.challenge.ChallengeItem;
import org.springframework.stereotype.Component;

/**
 * Created by yang on 2017/5/31.
 */
@Component
public class TaskFilter {

    //对challenge进行过滤
    public boolean filterChallenge(ChallengeItem challengeItem, String challengeType) {
        if (!challengeItem.getCurrentStatus().equals("Completed")) {
            return false;
        }
        String str = challengeItem.getChallengeType();
        if (!str.equals(challengeType)) {
            return false;
        }
        if (challengeItem.getDetailedRequirements() == null || challengeItem.getDetailedRequirements().length() == 0) {
            return false;
        }
        if (challengeItem.getTechnology() == null || challengeItem.getTechnology().length == 0 || challengeItem.getTechnology()[0].isEmpty()) {
            return false;
        }
        if (challengeItem.getChallengeName() == null || challengeItem.getChallengeName().length() == 0) {
            return false;
        }
        if (challengeItem.getDuration() == 0) {
            return false;
        }
        if (challengeItem.getPrize() == null || challengeItem.getPrize().length == 0 || challengeItem.getPrize()[0].isEmpty()) {
            return false;
        }
        return true;
    }
}
