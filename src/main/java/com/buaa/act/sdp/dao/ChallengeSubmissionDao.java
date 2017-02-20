package com.buaa.act.sdp.dao;

import com.buaa.act.sdp.bean.challenge.ChallengeSubmission;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

/**
 * Created by yang on 2016/10/19.
 */
public interface ChallengeSubmissionDao {
    void insert(ChallengeSubmission [] challengeSubmission);
    ChallengeSubmission[] getChallengeSubmission(ChallengeSubmission challengeSubmission);
    List<Map<String,String>> getUserSubmissons();
    List<ChallengeSubmission>getChallengeAndScore();
    int getChallengeSubmissionCount(@Param("challengeId") int challengeId);
    List<ChallengeSubmission>getChallengeWinner();
}
