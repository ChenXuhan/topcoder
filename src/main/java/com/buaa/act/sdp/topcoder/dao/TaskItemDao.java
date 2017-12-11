package com.buaa.act.sdp.topcoder.dao;

import com.buaa.act.sdp.topcoder.model.task.TaskItem;
import org.apache.ibatis.annotations.Param;

import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by yang on 2016/10/19.
 */

public interface TaskItemDao {

    void insert(TaskItem taskItem);

    TaskItem getTaskItemById(@Param("challengeId") int challengeId);

    List<Integer> getTaskIds();

    List<Integer> getTasksIds(@Param("set") Set<String> set);

    List<TaskItem> getAllTasks();

    void update(TaskItem item);

    List<Map<String, Object>> getProjectId();

    int projectExist(@Param("projectId") int projectId);

    List<Integer> getProjectTasks(@Param("projectId") int projectId);

    int getMaxTaskId();

    int getMaxProjectId();
}
