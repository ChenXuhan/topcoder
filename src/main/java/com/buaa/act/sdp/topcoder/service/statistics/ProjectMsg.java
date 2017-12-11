package com.buaa.act.sdp.topcoder.service.statistics;

import com.buaa.act.sdp.topcoder.common.Constant;
import com.buaa.act.sdp.topcoder.dao.TaskItemDao;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by yang on 2017/6/1.
 */
@Component
public class ProjectMsg {

    private static final Logger logger = LoggerFactory.getLogger(ProjectMsg.class);

    @Autowired
    private TaskItemDao taskItemDao;

    private Map<Integer, List<Integer>> projectIdToTaskIds;
    private Map<Integer, Integer> taskToProject;

    public ProjectMsg() {
        taskToProject = new HashMap<>();
        projectIdToTaskIds = new HashMap<>();
    }

    /**
     * project中所有的taskId
     *
     * @return
     */
    public synchronized Map<Integer, List<Integer>> getProjectToTasksMapping() {
        if (projectIdToTaskIds.isEmpty()) {
            taskProjectMapping();
        }
        return projectIdToTaskIds;
    }

    /**
     * task和project对应关系
     */
    public void taskProjectMapping() {
        logger.info("match tasks and the corresponding project,taskIds to projectId");
        List<Map<String, Object>> list = taskItemDao.getProjectId();
        List<Integer> taskIds;
        int taskId, projectId;
        String type;
        for (Map<String, Object> map : list) {
            taskId = Integer.parseInt(map.get("taskId").toString());
            projectId = Integer.parseInt(map.get("projectId").toString());
            type = map.get("taskType").toString();
            if (Constant.TASK_TYPE.contains(type)) {
                taskIds = projectIdToTaskIds.getOrDefault(projectId, null);
                if (taskIds != null) {
                    taskIds.add(taskId);
                } else {
                    taskIds = new ArrayList<>();
                    taskIds.add(taskId);
                    projectIdToTaskIds.put(projectId, taskIds);
                }
            }
            taskToProject.put(taskId, projectId);
        }
    }

    /**
     * challenge对应的项目
     *
     * @return
     */
    public synchronized Map<Integer, Integer> getTaskToProjectMapping() {
        if (taskToProject.isEmpty()) {
            taskProjectMapping();
        }
        return taskToProject;
    }

    public synchronized void update() {
        logger.info("update the cache,taskIds-projectId matching, every week");
        projectIdToTaskIds.clear();
        taskToProject.clear();
        taskProjectMapping();
    }
}
