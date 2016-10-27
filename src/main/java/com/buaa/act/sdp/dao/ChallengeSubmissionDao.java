package com.buaa.act.sdp.dao;

import com.buaa.act.sdp.bean.challenge.ChallengeSubmission;

/**
 * Created by yang on 2016/10/19.
 */
public interface ChallengeSubmissionDao {
    void insert(ChallengeSubmission [] challengeSubmission);
    ChallengeSubmission[] getChallengeSubmission(ChallengeSubmission challengeSubmission);
}
