package com.buaa.act.sdp.topcoder.service.statistics;

import com.buaa.act.sdp.topcoder.model.task.TaskItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by yang on 2017/5/31.
 */
@Component
public class MsgFilter {

    private static final Logger logger = LoggerFactory.getLogger(MsgFilter.class);

    @Autowired
    private ProjectMsg projectMsg;

    /**
     * 对challenge进行过滤,提取特征
     *
     * @param taskItem
     * @param taskType
     * @return
     */
    public boolean filterTask(TaskItem taskItem, String taskType) {
        if (!taskItem.getCurrentStatus().equals("Completed")) {
            return false;
        }
        String str = taskItem.getChallengeType();
        if (!str.equals(taskType)) {
            return false;
        }
        if (taskItem.getDetailedRequirements() == null || taskItem.getDetailedRequirements().length() == 0) {
            return false;
        }
        if (taskItem.getTechnology() == null || taskItem.getTechnology().length == 0 || taskItem.getTechnology()[0].isEmpty()) {
            return false;
        }
        if (taskItem.getChallengeName() == null || taskItem.getChallengeName().length() == 0) {
            return false;
        }
        if (taskItem.getDuration() == 0) {
            return false;
        }
        if (taskItem.getPrize() == null || taskItem.getPrize().length == 0 || taskItem.getPrize()[0].isEmpty()) {
            return false;
        }
        return true;
    }

    public boolean filterTask(TaskItem taskItem) {
        if (taskItem.getProjectId() <= 0) {
            return false;
        }
        if (taskItem.getDetailedRequirements() == null || taskItem.getDetailedRequirements().length() == 0) {
            return false;
        }
        if (taskItem.getTechnology() == null || taskItem.getTechnology().length == 0 || taskItem.getTechnology()[0].isEmpty()) {
            return false;
        }
        if (taskItem.getChallengeName() == null || taskItem.getChallengeName().length() == 0) {
            return false;
        }
        if (taskItem.getPlatforms() == null || taskItem.getPlatforms().length == 0) {
            return false;
        }
        if (taskItem.getPrize() == null || taskItem.getPrize().length == 0 || taskItem.getPrize()[0].isEmpty()) {
            return false;
        }
        if (taskItem.getPostingDate() == null || taskItem.getPostingDate().length() == 0) {
            return false;
        }
        if (taskItem.getSubmissionEndDate() == null || taskItem.getSubmissionEndDate().length() == 0) {
            return false;
        }
        return true;
    }

    /**
     * 获取一个project之前的project任务，只需要三种类型任务
     *
     * @param projectId
     * @return
     */
    public List<List<Integer>> getProjectAndTasks(int projectId) {
        logger.info("get all tasks id in projects before new project,projectId=" + projectId);
        List<List<Integer>> list = new ArrayList<>();
        Map<Integer, List<Integer>> projectIdToChallengeIds = projectMsg.getProjectToTasksMapping();
        for (Map.Entry<Integer, List<Integer>> entry : projectIdToChallengeIds.entrySet()) {
            if (entry.getKey() < projectId) {
                list.add(entry.getValue());
            }
        }
        return list;
    }
}
