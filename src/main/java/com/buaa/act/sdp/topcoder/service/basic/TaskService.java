package com.buaa.act.sdp.topcoder.service.basic;

import com.buaa.act.sdp.topcoder.common.Constant;
import com.buaa.act.sdp.topcoder.dao.ChallengeItemDao;
import com.buaa.act.sdp.topcoder.model.challenge.ChallengeItem;
import com.buaa.act.sdp.topcoder.model.user.Registrant;
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
    private ChallengeItemDao challengeItemDao;
    @Autowired
    private TaskScores taskScores;
    @Autowired
    private TaskMsg taskMsg;
    @Autowired
    private FeatureExtract featureExtract;

    public ChallengeItem getChallengeById(int challengeId) {
        logger.info("get task's detail msg from db,taskId=" + challengeId);
        return challengeItemDao.getChallengeItemById(challengeId);
    }

    public boolean projectExist(int projectId) {
        if (projectId <= 0) {
            return false;
        }
        int num = challengeItemDao.projectExist(projectId);
        return num > 0 ? true : false;
    }

    public List<Integer> getProjectTasks(int projectId) {
        logger.info("query tasks in a project in db,projectId=" + projectId);
        if (projectId <= 0) {
            return new ArrayList<>();
        }
        return challengeItemDao.getProjectTasks(projectId);
    }

    public List<Integer> getAllTasks() {
        logger.info("get all tasks' id from db");
        return challengeItemDao.getChallengeSpecificIds(Constant.TASK_TYPE);
    }

    public List<Registrant> getTaskRegistrants(int challengeId) {
        logger.info("get task's registrants from db,taskId=" + challengeId);
        Map<String, Double> score = taskScores.getTaskScore(challengeId);
        Map<String, String> registerTime = taskScores.getRegisterDate(challengeId);
        Map<String, String> submitTime = taskScores.getSubmitDate(challengeId);
        String winner = taskScores.getWinner(challengeId);
        List<Registrant> registrants = new ArrayList<>(score.size());
        for (Map.Entry<String, Double> entry : score.entrySet()) {
            Registrant registrant = new Registrant(entry.getKey(), registerTime.get(entry.getKey()), submitTime.get(entry.getKey()), entry.getValue(), entry.getKey().equals(winner));
            registrants.add(registrant);
        }
        return registrants;
    }

    public int getMaxTaskId() {
        return Math.max(challengeItemDao.getMaxTaskId() + 1, Constant.MAX_TASK_ID);
    }

    public int getMaxProjectId() {
        return challengeItemDao.getMaxProjectId() + 1;
    }

    public void uploadTask(ChallengeItem item) {
        int challengeId = getMaxTaskId();
        item.setChallengeId(challengeId);
        logger.info("upload new task into db, taskId=" + challengeId);
        String[] strings, string;
        int num;
        strings = item.getSubmissionEndDate().substring(0, 10).split("-");
        string = item.getPostingDate().substring(0, 10).split("-");
        if (strings != null && strings.length > 0 && string != null && string.length > 0) {
            num = (Integer.parseInt(strings[0]) - Integer.parseInt(string[0])) * 365 + (Integer.parseInt(strings[1]) - Integer.parseInt(string[1])) * 30 + (Integer.parseInt(strings[2]) - Integer.parseInt(string[2]));
            item.setDuration(num);
        }
        challengeItemDao.insert(item);
    }

    /**
     * 获取当前任务的相似任务列表
     *
     * @param item
     * @return
     */
    public List<Integer> getSimilerTask(ChallengeItem item) {
        logger.info("get task's similar tasks from db,type=" + item.getChallengeType() + ",taskId=" + item.getChallengeId());
        List<ChallengeItem> items = taskMsg.getItems(item.getChallengeType());
        List<Integer> result = new ArrayList<>(10);
        Map<Integer, Double> similarity = new HashMap<>();
        Set<String> skills = featureExtract.getSkills();
        double[] feature = featureExtract.generateVector(skills, item);
        double[] temp;
        double similar;
        for (ChallengeItem challengeItem : items) {
            if (challengeItem.getChallengeId() != item.getChallengeId()) {
                temp = featureExtract.generateVector(skills, challengeItem);
                if ((similar = Maths.taskSimilariry(feature, temp)) >= 0.6) {
                    similarity.put(challengeItem.getChallengeId(), similar);
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
