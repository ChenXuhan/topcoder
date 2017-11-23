package com.buaa.act.sdp.topcoder.service.api.statistics;

import com.buaa.act.sdp.topcoder.common.Constant;
import com.buaa.act.sdp.topcoder.dao.ChallengeItemDao;
import com.buaa.act.sdp.topcoder.dao.ChallengeRegistrantDao;
import com.buaa.act.sdp.topcoder.dao.ChallengeSubmissionDao;
import com.buaa.act.sdp.topcoder.model.challenge.ChallengeItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by yang on 2017/1/16.
 */
@Service
public class ChallengeStatistics {
    @Autowired
    private ChallengeItemDao challengeItemDao;

    @Autowired
    private ChallengeRegistrantDao challengeRegistrantDao;

    @Autowired
    private ChallengeSubmissionDao challengeSubmissionDao;

    private static final Logger logger = LoggerFactory.getLogger(ChallengeStatistics.class);

    /**
     * 任务的注册开发者统计
     */
    public void updateChallenge(ChallengeItem item, int resigterCount, int submissionCount) {
        logger.info("update task's registrant and submission count,taskId=" + item.getChallengeId());
        String[] strings, string;
        int num;
        strings = item.getSubmissionEndDate().substring(0, 10).split("-");
        string = item.getPostingDate().substring(0, 10).split("-");
        if (strings != null && strings.length > 0 && string != null && string.length > 0) {
            num = (Integer.parseInt(strings[0]) - Integer.parseInt(string[0])) * 365 + (Integer.parseInt(strings[1]) - Integer.parseInt(string[1])) * 30 + (Integer.parseInt(strings[2]) - Integer.parseInt(string[2]));
            item.setDuration(num);
        }
        item.setNumRegistrants(resigterCount);
        item.setNumSubmissions(submissionCount);
        item.setLanguages(getLanguages(item));
    }

    /**
     * 任务的语言统计
     *
     * @param item
     * @return
     */
    public String[] getLanguages(ChallengeItem item) {
        String[] tech = item.getTechnology();
        if (tech == null || tech.length == 0) {
            return new String[]{};
        }
        String[] language = Constant.LANGUAGES;
        Set<String> set = new HashSet<>();
        List<String> lang = new ArrayList<>();
        for (String str : language) {
            set.add(str);
        }
        for (String str : tech) {
            if (set.contains(str)) {
                lang.add(str);
            }
        }
        String[] result = new String[lang.size()];
        result = lang.toArray(result);
        return result;
    }
}
