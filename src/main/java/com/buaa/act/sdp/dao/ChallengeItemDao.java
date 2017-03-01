package com.buaa.act.sdp.dao;

import com.buaa.act.sdp.bean.challenge.ChallengeItem;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by yang on 2016/10/19.
 */

public interface ChallengeItemDao {
     void insert(ChallengeItem challengeItem);
     ChallengeItem getChallengeItemById(@Param("challengeId") int challengeId);
     List<Integer> getChallenges();
     List<ChallengeItem>getAllChallenges();
     void updateChallenges(ChallengeItem item);
}
