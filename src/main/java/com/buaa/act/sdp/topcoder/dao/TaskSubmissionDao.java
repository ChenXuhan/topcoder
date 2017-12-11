package com.buaa.act.sdp.topcoder.dao;

import com.buaa.act.sdp.topcoder.model.task.TaskSubmission;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * Created by yang on 2016/10/19.
 */
public interface TaskSubmissionDao {

    void insertBatch(TaskSubmission[] taskSubmission);

    int getTaskSubmissionCount(@Param("taskId") int taskId);

    List<TaskSubmission> getTaskSubmissionMsg();

}
