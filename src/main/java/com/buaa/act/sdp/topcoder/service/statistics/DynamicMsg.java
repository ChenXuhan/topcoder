package com.buaa.act.sdp.topcoder.service.statistics;

import com.buaa.act.sdp.topcoder.common.Constant;
import com.buaa.act.sdp.topcoder.model.developer.WorkerDynamicMsg;
import com.buaa.act.sdp.topcoder.model.task.TaskItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by yang on 2017/9/26.
 */
@Component
public class DynamicMsg {

    public static final Logger logger = LoggerFactory.getLogger(DynamicMsg.class);

    @Autowired
    private TaskScores taskScore;

    @Autowired
    private DeveloperMsg developerMsg;

    public DynamicMsg() {
    }

    /**
     * 获取某一个任务中注册开发者的动态特征 ESEM
     *
     * @param list     当前任务先前的任务
     * @param taskItem 当前任务
     * @param workers  参与的开发者
     * @return
     */
    public List<double[]> getDeveloperDynamicFeature(List<TaskItem> list, TaskItem taskItem, List<String> workers) {
        logger.info("get task's registrants' dynamic features,taskId" + taskItem.getChallengeId());
        List<double[]> feature = new ArrayList<>();
        Map<Integer, Map<String, Double>> scores = taskScore.getDevelopersScores();
        Map<String, Double> score = scores.get(taskItem.getChallengeId());
        Map<Integer, String> winners = taskScore.getWinners();
        Map<String, WorkerDynamicMsg> map = developerMsg.getDeveloperDynamicMsg(scores, winners, list, taskItem);
        for (Map.Entry<String, Double> entry : score.entrySet()) {
            double[] temp = new double[10];
            if (map.containsKey(entry.getKey())) {
                generateDynamicFeature(map.get(entry.getKey()), temp);
                if (winners.get(taskItem.getChallengeId()).equals(entry.getKey())) {
                    temp[9] = Constant.WINNER;
                } else if (score.get(entry.getKey()) > 0) {
                    temp[9] = Constant.SUBMITTER;
                } else {
                    temp[9] = Constant.QUITTER;
                }
            }
            feature.add(temp);
            workers.add(entry.getKey());
        }
        return feature;
    }

    /**
     * 获取开发者动态特征
     *
     * @param list   当前任务先前的任务
     * @param item   当前任务
     * @param worker 参与的开发者
     * @return
     */
    public List<double[]> getDynamicFeatures(List<TaskItem> list, TaskItem item, List<String> worker) {
        logger.info("get developers' dynamic features before a new task,taskId" + item.getChallengeId());
        Map<String, WorkerDynamicMsg> map = developerMsg.getDeveloperDynamicMsg(item.getChallengeId());
        if (map == null) {
            getDeveloperDynamicFeature(list, item, new ArrayList<>());
            map = developerMsg.getDeveloperDynamicMsg(item.getChallengeId());
        }
        List<double[]> feature = new ArrayList<>(map.size());
        for (Map.Entry<String, WorkerDynamicMsg> entry : map.entrySet()) {
            double[] temp = new double[10];
            generateDynamicFeature(entry.getValue(), temp);
            temp[9] = 0;
            feature.add(temp);
            worker.add(entry.getKey());
        }
        return feature;
    }

    /**
     * 计算开发者动态特征
     *
     * @param msg     特征信息
     * @param feature 特征向量
     */
    public void generateDynamicFeature(WorkerDynamicMsg msg, double[] feature) {
        feature[0] = msg.getNumRegTask() == 0 ? 0 : 1.0 * msg.getNumSubTask() / msg.getNumRegTask();
        feature[1] = msg.getNumRegTaskSimilar() == 0 ? 0 : 1.0 * msg.getNumSubTaskSimilar() / msg.getNumRegTaskSimilar();
        feature[2] = msg.getNumRegTaskSimilar() == 0 ? 0 : msg.getScoreTotal() / msg.getNumRegTaskSimilar();
        feature[3] = msg.getNumRegTask();
        feature[4] = msg.getNumRegTaskTDays();
        feature[5] = msg.getNumSubTaskTDays();
        feature[6] = msg.getNumWinTaskTDays();
        feature[7] = msg.getNumRegTaskTDays() == 0 ? 0 : msg.getPriceTotal() / msg.getNumRegTaskTDays();
    }

}
