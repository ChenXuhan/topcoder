package com.buaa.act.sdp.topcoder.dao;

import com.buaa.act.sdp.topcoder.model.challenge.ChallengeSubmission;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * Created by yang on 2016/10/19.
 */
public interface ChallengeSubmissionDao {

    void insert(ChallengeSubmission[] challengeSubmission);

    ChallengeSubmission[] getChallengeSubmission(ChallengeSubmission challengeSubmission);

    List<Map<String, String>> getUserSubmissons();

    int getChallengeSubmissionCount(@Param("challengeId") int challengeId);

    List<ChallengeSubmission> getChallengeWinner();

    ChallengeSubmission[] getSubmissionByHandle(@Param("handle") String handle);

    int getUserSubers(String handle);

    int getUserWiners(String handle);
}
