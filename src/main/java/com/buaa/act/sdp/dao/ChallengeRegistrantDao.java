package com.buaa.act.sdp.dao;

import com.buaa.act.sdp.bean.challenge.ChallengeRegistrant;
import org.apache.ibatis.annotations.Param;

import java.util.HashSet;
import java.util.List;

/**
 * Created by yang on 2016/10/19.
 */
public interface ChallengeRegistrantDao {
    void insert(ChallengeRegistrant[]challengeRegistrants);
    ChallengeRegistrant[] getChallengeRegistrant(ChallengeRegistrant challengeRegistrant);
    String[] getUsers();
    int getRegistrantCountById(@Param("challengeId") int challengeId);
    ChallengeRegistrant[] getRegistrantById(@Param("challengeId") int challengeId);
    List<ChallengeRegistrant> getAllRegistrant();
    int getUserRegers(String handle);
    ChallengeRegistrant[] getRegistrantByHandle(@Param("handle") String handle);
}
