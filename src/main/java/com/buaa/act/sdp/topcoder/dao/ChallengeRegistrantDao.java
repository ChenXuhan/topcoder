package com.buaa.act.sdp.topcoder.dao;

import com.buaa.act.sdp.topcoder.model.challenge.ChallengeRegistrant;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * Created by yang on 2016/10/19.
 */
public interface ChallengeRegistrantDao {

    void insertBatch(ChallengeRegistrant[] challengeRegistrants);

    int getRegistrantCountByTaskId(@Param("challengeId") int challengeId);

    List<ChallengeRegistrant> getAllChallengeRegistrants();

    List<Integer> getUserRegistrantTasks(@Param("handle") String handle);

}
