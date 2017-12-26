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

    List<TaskItem> getTasksByIds(@Param("taskIds") List<Integer> taskIds, @Param("set") Set<String> set);

    List<TaskItem> getTasks(@Param("offSet") int offSet, @Param("pageSize") int pageSize, @Param("set") Set<String> set);

    void update(TaskItem item);

    List<Map<String, Object>> getProjectId();

    int projectExist(@Param("projectId") int projectId);

    List<TaskItem> getProjectTasks(@Param("projectId") int projectId, @Param("set") Set<String> set);

    int getMaxTaskId();

    int getMaxProjectId();

    int getTasksTotalNum(@Param("set") Set<String> set);
}
