package com.buaa.act.sdp.topcoder.dao;

import com.buaa.act.sdp.topcoder.model.challenge.ChallengeSubmission;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;

/**
 * Created by yang on 2016/10/19.
 */
public interface ChallengeSubmissionDao {

    void insertBatch(ChallengeSubmission[] challengeSubmission);

    int getChallengeSubmissionCount(@Param("challengeId") int challengeId);

    List<ChallengeSubmission> getChallengeSubmissionMsg();

}
