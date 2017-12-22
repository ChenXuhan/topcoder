package com.buaa.act.sdp.topcoder.service.statistics;

import com.buaa.act.sdp.topcoder.common.Constant;
import com.buaa.act.sdp.topcoder.dao.TaskItemDao;
import com.buaa.act.sdp.topcoder.service.redis.RedisService;
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

    private static final String PROJECT_TO_TASK = "project_to_task";
    private static final String TASK_TO_PROJECT = "task_to_project";

    @Autowired
    private TaskItemDao taskItemDao;
    @Autowired
    private RedisService redisService;

    /**
     * project中所有的taskId
     *
     * @return
     */
    public synchronized Map<Integer, List<Integer>> getProjectToTasksMapping() {
        Map<Integer, List<Integer>> projectIdToTaskIds = redisService.getMapCaches(PROJECT_TO_TASK);
        if (!projectIdToTaskIds.isEmpty()) {
            return projectIdToTaskIds;
        }
        taskProjectMapping();
        return redisService.getMapCaches(PROJECT_TO_TASK);
    }

    /**
     * task和project对应关系
     */
    public void taskProjectMapping() {
        logger.info("match tasks and the corresponding project, save the mapping into redis");
        List<Map<String, Object>> list = taskItemDao.getProjectId();
        Map<Integer, List<Integer>> projectIdToTaskIds = new HashMap<>();
        List<Integer> taskIds;
        int taskId, projectId;
        String type;
        for (Map<String, Object> map : list) {
            taskId = Integer.parseInt(map.get("challengeId").toString());
            projectId = Integer.parseInt(map.get("projectId").toString());
            type = map.get("challengeType").toString();
            if (Constant.TASK_TYPE.contains(type)) {
                taskIds = projectIdToTaskIds.getOrDefault(projectId, null);
                if (taskIds != null) {
                    taskIds.add(taskId);
                } else {
                    taskIds = new ArrayList<>();
                    taskIds.add(taskId);
                    projectIdToTaskIds.put(projectId, taskIds);
                }
                redisService.setMapCache(TASK_TO_PROJECT, taskId, projectId);
            }
        }
        redisService.setMapCaches(PROJECT_TO_TASK, projectIdToTaskIds);
    }

    /**
     * challenge对应的项目
     *
     * @return
     */
    public synchronized Map<Integer, Integer> getTaskToProjectMapping() {
        Map<Integer, Integer> taskToProject = redisService.getMapCaches(TASK_TO_PROJECT);
        if (!taskToProject.isEmpty()) {
            return taskToProject;
        }
        taskProjectMapping();
        return redisService.getMapCaches(TASK_TO_PROJECT);
    }

    public synchronized void update() {
        logger.info("update the cache,tasks-project matching, every week");
        redisService.delete(PROJECT_TO_TASK);
        redisService.delete(TASK_TO_PROJECT);
        taskProjectMapping();
    }
}
