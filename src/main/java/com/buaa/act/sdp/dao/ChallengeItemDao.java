package com.buaa.act.sdp.dao;

import com.buaa.act.sdp.model.challenge.ChallengeItem;
import org.apache.ibatis.annotations.Param;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by yang on 2016/10/19.
 */

public interface ChallengeItemDao {
    void insert(ChallengeItem challengeItem);

    ChallengeItem getChallengeItemById(@Param("challengeId") int challengeId);

    String getChallengeItemRequirementById(@Param("challengeId") int challengeId);

    void setHandledRequirements(@Param("requirements") String requirements, @Param("challengeId") int challengeId);

    List<Integer> getChallenges();

    List<ChallengeItem> getAllChallenges();

    void updateChallenges(ChallengeItem item);

    String[] getAllPrizes();

    Integer[] getAllReliabilityBonus();

    Integer[] getAllDuration();

    Integer[] getAllNumRegistrants();

    Integer[] getAllNumSubmissions();

    double getDifficultyDegree(@Param("challengeId") int challengeId);

    List<Map<String, Integer>> getProjectId();

    void insertDifficultyDegree(@Param("relationMap") HashMap<Integer, Double> map);

    String[] getAllPlatforms();

    String[] getAllTechnology();
}
