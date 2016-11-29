package com.buaa.act.sdp.dao;

import com.buaa.act.sdp.bean.challenge.ChallengeItem;
import org.apache.ibatis.annotations.Param;

/**
 * Created by yang on 2016/10/19.
 */
public interface ChallengeItemDao {
     void insert(ChallengeItem challengeItem);
     ChallengeItem[] getChallengeItem(int challengeId);
     ChallengeItem getChallengeItemById(@Param("challengeId") int challengeId);


}
