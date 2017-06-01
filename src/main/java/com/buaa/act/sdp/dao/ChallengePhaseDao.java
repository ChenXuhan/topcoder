package com.buaa.act.sdp.dao;

import com.buaa.act.sdp.model.challenge.ChallengePhase;

/**
 * Created by yang on 2016/10/19.
 */
public interface ChallengePhaseDao {
    void insert(ChallengePhase[]challengePhases);
    ChallengePhase[] getChallengePhase(ChallengePhase challengePhase);

}
