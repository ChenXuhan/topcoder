package com.buaa.act.sdp.topcoder.service.api;

import com.buaa.act.sdp.topcoder.common.Constant;
import com.buaa.act.sdp.topcoder.dao.DeveloperDao;
import com.buaa.act.sdp.topcoder.dao.TaskItemDao;
import com.buaa.act.sdp.topcoder.model.task.PastTask;
import com.buaa.act.sdp.topcoder.model.task.TaskItem;
import com.buaa.act.sdp.topcoder.util.JsonUtil;
import com.buaa.act.sdp.topcoder.util.RequestUtil;
import com.google.gson.JsonElement;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by yang on 2017/11/10.
 */
@Component
public class UpdateTasksAndDevelopers {

    @Autowired
    private TaskApi taskApi;

    @Autowired
    private TaskItemDao taskItemDao;

    @Autowired
    private DeveloperDao developerDao;

    private static final Logger logger = LoggerFactory.getLogger(UpdateTasksAndDevelopers.class);

    private static final String TASK_COUNT_URL = "http://api.topcoder.com/v2/challenges/past?type=develop&pageIndex=1&pageSize=50";
    private static final String GET_COMPELETED_TASK_URL = "http://api.topcoder.com/v2/challenges/past?type=develop&pageIndex=";

    /**
     * 获取当前已经完成的任务数量
     *
     * @return
     */
    public int getCompletedTaskCount() {
        logger.info("get completed task count from topcoder api");
        String str = null;
        try {
            str = RequestUtil.request(TASK_COUNT_URL);
        } catch (Exception e) {
            logger.error("error occurred in getting finished task's count", e);
        }
        if (str != null) {
            JsonElement jsonElement = JsonUtil.getJsonElement(str, "total");
            if (jsonElement.isJsonPrimitive()) {
                return jsonElement.getAsInt();
            }
        }
        return 0;
    }

    /**
     * 分页获取历史完成任务
     *
     * @param pageIndex
     * @param pageSize
     * @return
     */
    public PastTask[] getPastTasks(int pageIndex, int pageSize) {
        logger.info("get completed task id from topcoder api");
        String str = null;
        try {
            str = RequestUtil.request(GET_COMPELETED_TASK_URL + pageIndex + "&pageSize=" + pageSize);
        } catch (Exception e) {
            logger.error("error occurred in getting finished tasks id");
        }
        if (str != null) {
            JsonElement jsonElement = JsonUtil.getJsonElement(str, "data");
            if (jsonElement != null) {
                PastTask[] pastTasks = JsonUtil.fromJson(jsonElement, PastTask[].class);
                return pastTasks;
            }
        }
        return null;
    }

    /**
     * 增量保存所有完成的task,更新开发者参与任务数
     */
    public void updateFinishedTasks() {
        logger.info("update and save completed tasks,evey one week");
        int count = getCompletedTaskCount();
        if (count == 0) {
            logger.info("zero new tasks have been crawled...");
            return;
        }
        int pageSize = Constant.PAGE_SIZE;
        int pages = count / pageSize;
        if (count % pageSize != 0) {
            pages++;
        }
        List<String> userList = developerDao.getDistinctDevelopers();
        Set<String> userSet = new HashSet<>();
        userSet.addAll(userList);
        List<Integer> challengeList = taskItemDao.getTaskIds();
        Set<Integer> challengeSet = new HashSet<>();
        challengeSet.addAll(challengeList);
        PastTask[] pastTasks;
        TaskItem taskItem;
        int challengeId;
        count = 0;
        for (int i = 1; i <= pages; i++) {
            pastTasks = getPastTasks(i, pageSize);
            if (pastTasks == null || pastTasks.length == 0) {
                continue;
            }
            for (int j = 0; j < pastTasks.length; j++) {
                challengeId = pastTasks[j].getChallengeId();
                if (!challengeSet.contains(challengeId)) {
                    taskItem = taskApi.getTaskById(challengeId);
                    if (taskItem != null) {
                        count++;
                        taskApi.taskItemGenerate(taskItem, pastTasks[j]);
                        taskApi.saveFinishedTask(taskItem, userSet);
                    }
                }
            }
        }
        logger.info(count + " new tasks have been saved in db");
    }

}
