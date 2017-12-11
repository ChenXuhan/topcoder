package com.buaa.act.sdp.topcoder.service.statistics;

import com.buaa.act.sdp.topcoder.model.developer.WorkerDynamicMsg;
import com.buaa.act.sdp.topcoder.model.task.TaskItem;
import com.buaa.act.sdp.topcoder.util.Maths;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by yang on 2017/11/12.
 */
@Component
public class DeveloperMsg {

    private static final Logger logger = LoggerFactory.getLogger(DeveloperMsg.class);

    private Map<Integer, Map<String, WorkerDynamicMsg>> msgMap;

    public DeveloperMsg() {
        msgMap = new HashMap<>();
    }


    /**
     * 计算并保存开发者的动态特征
     *
     * @param scores
     * @param winners
     * @param list
     * @param taskItem
     * @return
     */
    public Map<String, WorkerDynamicMsg> getDeveloperDynamicMsg(Map<Integer, Map<String, Double>> scores, Map<Integer, String> winners, List<TaskItem> list, TaskItem taskItem) {
        Map<String, WorkerDynamicMsg> map = msgMap.get(taskItem.getChallengeId());
        if (map == null) {
            logger.info("compute developers' dynamic message caches");
            map = new HashMap<>();
            WorkerDynamicMsg msg;
            for (TaskItem item : list) {
                if (item.getChallengeId() >= taskItem.getChallengeId()) {
                    break;
                }
                Map<String, Double> temp = scores.get(item.getChallengeId());
                for (Map.Entry<String, Double> entry : temp.entrySet()) {
                    msg = map.getOrDefault(entry.getKey(), null);
                    if (msg == null) {
                        msg = new WorkerDynamicMsg();
                    }
                    msg.addNumRegTask();
                    if (entry.getValue() > 0) {
                        msg.addNumsSubTask();
                    }
                    if (Maths.isSimilar(taskItem, item)) {
                        msg.addNumRegTaskSimilar();
                        if (entry.getValue() > 0) {
                            msg.addNumSubTaskSimilar();
                        }
                        msg.addScoreTotal(entry.getValue());
                    }
                    if (Maths.dataDistance(taskItem, item) <= 30) {
                        msg.addNumRegTaskTDays();
                        if (entry.getValue() > 0) {
                            msg.addNumSubTaskTDays();
                        }
                        if (entry.getKey().equals(winners.get(item.getChallengeId()))) {
                            msg.addNumsWinTaskTDays();
                        }
                        double price = 0;
                        for (String str : item.getPrize()) {
                            price += Double.parseDouble(str);
                        }
                        msg.addPriceTotal(price);
                    }
                    map.put(entry.getKey(), msg);
                }
            }
            msgMap.put(taskItem.getChallengeId(), map);
        }
        return map;
    }

    public Map<String, WorkerDynamicMsg> getDeveloperDynamicMsg(int challengeId) {
        return msgMap.get(challengeId);
    }

}
