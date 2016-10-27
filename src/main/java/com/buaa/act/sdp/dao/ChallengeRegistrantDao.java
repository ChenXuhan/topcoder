package com.buaa.act.sdp.dao;

import com.buaa.act.sdp.bean.challenge.ChallengeRegistrant;

import java.util.HashSet;

/**
 * Created by yang on 2016/10/19.
 */
public interface ChallengeRegistrantDao {
    void insert(ChallengeRegistrant[]challengeRegistrants);
    ChallengeRegistrant[] getChallengeRegistrant(ChallengeRegistrant challengeRegistrant);
    String[] getUsers();
}
