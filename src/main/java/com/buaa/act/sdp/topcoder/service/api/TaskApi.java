package com.buaa.act.sdp.topcoder.service.api;

/**
 * Created by YLT on 2016/10/18.
 */

import com.buaa.act.sdp.topcoder.dao.TaskItemDao;
import com.buaa.act.sdp.topcoder.dao.TaskPhaseDao;
import com.buaa.act.sdp.topcoder.dao.TaskRegistrantDao;
import com.buaa.act.sdp.topcoder.dao.TaskSubmissionDao;
import com.buaa.act.sdp.topcoder.model.task.*;
import com.buaa.act.sdp.topcoder.service.api.statistics.DeveloperStatistics;
import com.buaa.act.sdp.topcoder.service.api.statistics.TaskStatistics;
import com.buaa.act.sdp.topcoder.util.JsonUtil;
import com.buaa.act.sdp.topcoder.util.RequestUtil;
import com.google.gson.JsonElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Set;

/**
 * Created by yang on 2016/9/30.
 */
@Service
public class TaskApi {

    @Autowired
    private TaskItemDao taskItemDao;
    @Autowired
    private TaskSubmissionDao taskSubmissionDao;
    @Autowired
    private TaskPhaseDao taskPhaseDao;
    @Autowired
    private TaskRegistrantDao taskRegistrantDao;
    @Autowired
    private DeveloperApi developerApi;
    @Autowired
    private TaskStatistics taskStatistics;
    @Autowired
    private DeveloperStatistics developerStatistics;

    private static final Logger logger = LoggerFactory.getLogger(TaskApi.class);

    private static final String TASK_URL_PREFIX = "http://api.topcoder.com/v2/challenges/";
    private static final String REGISTRANT_URL_PREFIX = "http://api.topcoder.com/v2/challenges/registrants/";
    private static final String PHRASE_URL_PREFIX = "http://api.topcoder.com/v2/challenges/phases/";
    private static final String SUBMISSION_URL_PREFIX = "http://api.topcoder.com/v2/develop/challenges/result/";

    /**
     * 获取task基本信息
     *
     * @param taskId
     * @return
     */
    public TaskItem getTaskById(int taskId) {
        logger.info("get task from topcoder api,taskId=" + taskId);
        String str = null;
        try {
            str = RequestUtil.request(TASK_URL_PREFIX + taskId);
        } catch (Exception e) {
            logger.error("error occurred in getting task through api,taskId=" + taskId, e);
        }
        if (str != null) {
            return JsonUtil.fromJson(str, TaskItem.class);
        }
        return null;
    }

    /**
     * 获取task的注册开发者信息
     *
     * @param taskId
     * @return
     */
    public TaskRegistrant[] getTaskRegistrantsById(int taskId) {
        logger.info("get task's registrants from topcoder api,taskId=" + taskId);
        String str = null;
        try {
            str = RequestUtil.request(REGISTRANT_URL_PREFIX + taskId);
        } catch (Exception e) {
            logger.error("error occurred in getting task's registrants,taskId=" + taskId, e);
        }
        if (str != null) {
            return JsonUtil.fromJson(str, TaskRegistrant[].class);
        }
        return null;
    }

    /**
     * 获取task的发布时间信息
     *
     * @param taskId
     * @return
     */
    public TaskPhase[] getTaskPhasesById(int taskId) {
        logger.info("get task's phrase from topcoder api,taskId=" + taskId);
        String str = null;
        try {
            str = RequestUtil.request(PHRASE_URL_PREFIX + taskId);
        } catch (Exception e) {
            logger.error("error occurred in getting task's phrase,taskId=" + taskId, e);
        }
        if (str != null) {
            JsonElement jsonElement = JsonUtil.getJsonElement(str, "phases");
            if (jsonElement != null) {
                return JsonUtil.fromJson(jsonElement, TaskPhase[].class);
            }
        }
        return null;
    }

    /**
     * 获取task的提交信息
     *
     * @param taskId
     * @return
     */
    public TaskSubmission[] getTaskSubmissionsById(int taskId) {
        logger.info("get task's submission from topcoder api,taskId=" + taskId);
        String str = null;
        try {
            str = RequestUtil.request(SUBMISSION_URL_PREFIX + taskId);
        } catch (Exception e) {
            logger.error("error occurred in getting task's submission,taskId=" + taskId, e);
        }
        if (str != null) {
            JsonElement jsonElement = JsonUtil.getJsonElement(str, "results");
            if (jsonElement != null) {
                return JsonUtil.fromJson(jsonElement, TaskSubmission[].class);
            }
        }
        return null;
    }

    /**
     * 获取task及task相关的开发者信息
     *
     * @param taskItem
     * @param username
     */
    public void saveFinishedTask(TaskItem taskItem, Set<String> username) {
        logger.info("save new task and developer's information into db,taskId=" + taskItem.getChallengeId());
        int challengeId = taskItem.getChallengeId();
        int registerCount = 0, submissionCount = 0;
        TaskRegistrant[] taskRegistrant = getTaskRegistrantsById(challengeId);
        if (taskRegistrant != null && taskRegistrant.length != 0) {
            logger.info("save new task's registrants into db,taskId=" + taskItem.getChallengeId());
            taskRegistrantGenerate(challengeId, taskRegistrant);
            taskRegistrantDao.insertBatch(taskRegistrant);
            for (int i = 0; i < taskRegistrant.length; i++) {
                if (!username.contains(taskRegistrant[i].getHandle())) {
                    username.add(taskRegistrant[i].getHandle());
                    developerApi.saveDeveloperMsg(taskRegistrant[i].getHandle());
                } else {
                    developerApi.updateDeveloperMsg(taskRegistrant[i].getHandle());
                }
                developerStatistics.updateTaskCount(taskRegistrant[i].getHandle());
            }
            registerCount = taskRegistrant.length;
        }
        TaskSubmission[] taskSubmissions = getTaskSubmissionsById(challengeId);
        if (taskSubmissions != null && taskSubmissions.length != 0) {
            logger.info("save new task's submission into db,taskId=" + taskItem.getChallengeId());
            taskSubmissionGenerate(challengeId, taskSubmissions);
            taskSubmissionDao.insertBatch(taskSubmissions);
            submissionCount = taskSubmissions.length;
        }
        TaskPhase[] taskPhases = getTaskPhasesById(challengeId);
        if (taskPhases != null && taskPhases.length != 0) {
            logger.info("save new task's phrase into db,taskId=" + taskItem.getChallengeId());
            taskPhaseGenerate(challengeId, taskPhases);
            taskPhaseDao.insertBatch(taskPhases);
        }
        taskStatistics.updateTask(taskItem, registerCount, submissionCount);
        taskItemDao.insert(taskItem);
    }

    public void taskItemGenerate(TaskItem item, PastTask pastTask) {
        item.setNumRegistrants(pastTask.getNumRegistrants());
        item.setNumSubmissions(pastTask.getNumSubmissions());
        item.setRegistrationStartDate(pastTask.getRegistrationStartDate());
    }

    public void taskRegistrantGenerate(int taskId, TaskRegistrant[] taskRegistrants) {
        for (int i = 0; i < taskRegistrants.length; i++) {
            taskRegistrants[i].setChallengeID(taskId);
        }
    }

    public void taskSubmissionGenerate(int taskId, TaskSubmission[] taskSubmissions) {
        for (int i = 0; i < taskSubmissions.length; i++) {
            taskSubmissions[i].setChallengeID(taskId);
        }
    }

    public void taskPhaseGenerate(int taskId, TaskPhase[] taskPhases) {
        for (int i = 0; i < taskPhases.length; i++) {
            taskPhases[i].setChallengeID(taskId);
        }
    }

}
