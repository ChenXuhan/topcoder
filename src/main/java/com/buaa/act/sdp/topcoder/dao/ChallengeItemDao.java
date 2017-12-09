package com.buaa.act.sdp.topcoder.dao;

import com.buaa.act.sdp.topcoder.model.challenge.ChallengeItem;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by yang on 2016/10/19.
 */

public interface ChallengeItemDao {

    void insert(ChallengeItem challengeItem);

    ChallengeItem getChallengeItemById(@Param("challengeId") int challengeId);

    List<Integer> getChallengeIds();

    List<Integer> getChallengeSpecificIds(@Param("set") Set<String> set);

    List<ChallengeItem> getAllChallenges();

    void update(ChallengeItem item);

    List<Map<String, Object>> getProjectId();

    int projectExist(@Param("projectId") int projectId);

    List<Integer> getProjectTasks(@Param("projectId") int projectId);

    int getMaxTaskId();

    int getMaxProjectId();
}
