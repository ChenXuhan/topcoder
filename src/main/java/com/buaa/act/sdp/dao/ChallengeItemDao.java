package com.buaa.act.sdp.dao;

import com.buaa.act.sdp.bean.challenge.ChallengeItem;

/**
 * Created by yang on 2016/10/19.
 */
public interface ChallengeItemDao {
     void insert(ChallengeItem challengeItem);
     ChallengeItem[] getChallengeItem(int challengeId);

}
