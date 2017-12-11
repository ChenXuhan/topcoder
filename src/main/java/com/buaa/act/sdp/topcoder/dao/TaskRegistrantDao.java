package com.buaa.act.sdp.topcoder.dao;

import com.buaa.act.sdp.topcoder.model.task.TaskRegistrant;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * Created by yang on 2016/10/19.
 */
public interface TaskRegistrantDao {

    void insertBatch(TaskRegistrant[] taskRegistrants);

    int getRegistrantCountByTaskId(@Param("challengeId") int challengeId);

    List<TaskRegistrant> getAllTaskRegistrants();

    List<Integer> getDeveloperRegistrantTasks(@Param("handle") String handle);

}
