package com.buaa.act.sdp.topcoder.service.basic;

import com.buaa.act.sdp.topcoder.common.Constant;
import com.buaa.act.sdp.topcoder.dao.TaskItemDao;
import com.buaa.act.sdp.topcoder.model.developer.Registrant;
import com.buaa.act.sdp.topcoder.model.task.TaskItem;
import com.buaa.act.sdp.topcoder.service.recommend.feature.FeatureExtract;
import com.buaa.act.sdp.topcoder.service.statistics.TaskMsg;
import com.buaa.act.sdp.topcoder.service.statistics.TaskScores;
import com.buaa.act.sdp.topcoder.util.Maths;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * Created by yang on 2017/11/17.
 */
@Service
public class TaskService {

    private static final Logger logger = LoggerFactory.getLogger(TaskService.class);

    @Autowired
    private TaskItemDao taskItemDao;
    @Autowired
    private TaskScores taskScores;
    @Autowired
    private TaskMsg taskMsg;
    @Autowired
    private FeatureExtract featureExtract;

    public TaskItem getTaskById(int taskId) {
        logger.info("get task's detail msg from db,taskId=" + taskId);
        return taskItemDao.getTaskItemById(taskId);
    }

    public boolean projectExist(int projectId) {
        if (projectId <= 0) {
            return false;
        }
        int num = taskItemDao.projectExist(projectId);
        return num > 0 ? true : false;
    }

    public List<Integer> getProjectTasks(int projectId) {
        logger.info("query tasks in a project in db,projectId=" + projectId);
        if (projectId <= 0) {
            return new ArrayList<>();
        }
        return taskItemDao.getProjectTasks(projectId);
    }

    public List<Integer> getAllTasks() {
        logger.info("get all tasks' id from db");
        return taskItemDao.getTasksIds(Constant.TASK_TYPE);
    }

    public List<Registrant> getTaskRegistrants(int taskId) {
        logger.info("get task's registrants from db,taskId=" + taskId);
        Map<String, Double> score = taskScores.getTaskScore(taskId);
        Map<String, String> registerTime = taskScores.getRegisterDate(taskId);
        Map<String, String> submitTime = taskScores.getSubmitDate(taskId);
        String winner = taskScores.getWinner(taskId);
        List<Registrant> registrants = new ArrayList<>(score.size());
        for (Map.Entry<String, Double> entry : score.entrySet()) {
            Registrant registrant = new Registrant(entry.getKey(), registerTime.get(entry.getKey()), submitTime.get(entry.getKey()), entry.getValue(), entry.getKey().equals(winner));
            registrants.add(registrant);
        }
        return registrants;
    }

    public int getMaxTaskId() {
        return Math.max(taskItemDao.getMaxTaskId() + 1, Constant.MAX_TASK_ID);
    }

    public int getMaxProjectId() {
        return taskItemDao.getMaxProjectId() + 1;
    }

    public void uploadTask(TaskItem item) {
        int taskId = getMaxTaskId();
        item.setChallengeId(taskId);
        logger.info("upload new task into db, taskId=" + taskId);
        String[] strings, string;
        int num;
        strings = item.getSubmissionEndDate().substring(0, 10).split("-");
        string = item.getPostingDate().substring(0, 10).split("-");
        if (strings != null && strings.length > 0 && string != null && string.length > 0) {
            num = (Integer.parseInt(strings[0]) - Integer.parseInt(string[0])) * 365 + (Integer.parseInt(strings[1]) - Integer.parseInt(string[1])) * 30 + (Integer.parseInt(strings[2]) - Integer.parseInt(string[2]));
            item.setDuration(num);
        }
        taskItemDao.insert(item);
    }

    /**
     * 获取当前任务的相似任务列表
     *
     * @param item
     * @return
     */
    public List<Integer> getSimilerTask(TaskItem item) {
        logger.info("get task's similar tasks from db,type=" + item.getChallengeType() + ",taskId=" + item.getChallengeId());
        List<TaskItem> items = taskMsg.getItems(item.getChallengeType());
        List<Integer> result = new ArrayList<>(10);
        Map<Integer, Double> similarity = new HashMap<>();
        Set<String> skills = featureExtract.getSkills();
        double[] feature = featureExtract.generateVector(skills, item);
        double[] temp;
        double similar;
        for (TaskItem taskItem : items) {
            if (taskItem.getChallengeId() != item.getChallengeId()) {
                temp = featureExtract.generateVector(skills, taskItem);
                if ((similar = Maths.taskSimilariry(feature, temp)) >= 0.6) {
                    similarity.put(taskItem.getChallengeId(), similar);
                }
            }
        }
        List<Map.Entry<Integer, Double>> list = new ArrayList<>(similarity.size());
        list.addAll(similarity.entrySet());
        Collections.sort(list, new Comparator<Map.Entry<Integer, Double>>() {
            @Override
            public int compare(Map.Entry<Integer, Double> o1, Map.Entry<Integer, Double> o2) {
                return o2.getValue().compareTo(o1.getValue());
            }
        });
        for (int i = 0; i < list.size() && i < 10; i++) {
            result.add(list.get(i).getKey());
        }
        return result;
    }
}
