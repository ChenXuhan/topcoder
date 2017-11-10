package com.buaa.act.sdp.topcoder.dao;

import com.buaa.act.sdp.topcoder.model.challenge.ChallengePhase;

/**
 * Created by yang on 2016/10/19.
 */
public interface ChallengePhaseDao {

    void insertBatch(ChallengePhase[] challengePhases);

}
