package com.buaa.act.sdp.service.recommend.feature;

import com.buaa.act.sdp.common.Constant;
import com.buaa.act.sdp.model.challenge.ChallengeItem;
import com.buaa.act.sdp.service.statistics.TaskMsg;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by yang on 2017/9/26.
 */
@Component
public class TaskWorkerAttribute {

    @Autowired
    private TaskMsg taskMsg;

    public List<ChallengeItem> getItems(String type) {
        return taskMsg.getItems(type);
    }

    public List<String> getTaskWinners(String type) {
        return taskMsg.getWinners(type);
    }

    public double[] generateFeature(Set<String> set, ChallengeItem item) {
        int index = 0;
        double[] feature = new double[set.size() + 5];
        feature[index++] = item.getDetailedRequirements().length();
        feature[index++] = item.getChallengeName().length();
        String[] temp = item.getRegistrationStartDate().substring(0, 10).split("-");
        feature[index++] = Integer.parseInt(temp[0]) * 365 + Integer.parseInt(temp[1]) * 30 + Integer.parseInt(temp[2]);
        temp = item.getSubmissionEndDate().substring(0, 10).split("-");
        feature[index++] = Integer.parseInt(temp[0]) * 365 + Integer.parseInt(temp[1]) * 30 + Integer.parseInt(temp[2]);
        double award = 0;
        for (String str : item.getPrize()) {
            award += Double.parseDouble(str);
        }
        feature[index++] = award;
        feature[index++] = type(item.getChallengeType());
        Set<String> skill = new HashSet<>();
        for (String str : item.getTechnology()) {
            skill.add(str.toLowerCase());
        }
        for (String str : item.getPlatforms()) {
            skill.add(str.toLowerCase());
        }
        setWorkerSkills(index, feature, set, skill);
        return feature;
    }

    public Set<String> getAllSkills() {
        Set<String> skills = new HashSet<>();
        for (String str : Constant.TECHNOLOGIES) {
            skills.add(str.toLowerCase());
        }
        for (String str : Constant.PLATFORMS) {
            skills.add(str.toLowerCase());
        }
        return skills;
    }

    public void setWorkerSkills(int index, double[] feature, Set<String> skills, Set<String> skill) {
        for (String str : skills) {
            if (skill.contains(str)) {
                feature[index++] = 1.0;
            } else {
                feature[index++] = 0;
            }
        }
    }

    public int type(String type) {
        if ("Code".equals(type)) {
            return 1;
        } else if ("First2Finish".equals(type)) {
            return 2;
        }
        return 3;
    }
}
