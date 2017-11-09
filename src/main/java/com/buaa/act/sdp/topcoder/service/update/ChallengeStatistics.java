package com.buaa.act.sdp.topcoder.service.update;

import com.buaa.act.sdp.topcoder.common.Constant;
import com.buaa.act.sdp.topcoder.dao.ChallengeItemDao;
import com.buaa.act.sdp.topcoder.dao.ChallengeRegistrantDao;
import com.buaa.act.sdp.topcoder.dao.ChallengeSubmissionDao;
import com.buaa.act.sdp.topcoder.model.challenge.ChallengeItem;
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

    public void updateChallenges() {
        List<ChallengeItem> items = challengeItemDao.getAllChallenges();
        String[] strings, string;
        int num;
        for (ChallengeItem item : items) {
            strings = item.getSubmissionEndDate().substring(0, 10).split("-");
            string = item.getPostingDate().substring(0, 10).split("-");
            if (strings != null && strings.length > 0 && string != null && string.length > 0) {
                num = (Integer.parseInt(strings[0]) - Integer.parseInt(string[0])) * 365 + (Integer.parseInt(strings[1]) - Integer.parseInt(string[1])) * 30 + (Integer.parseInt(strings[2]) - Integer.parseInt(string[2]));
                item.setDuration(num);
            }
            if (item.getNumRegistrants() == 0) {
                num = challengeRegistrantDao.getRegistrantCountById(item.getChallengeId());
                item.setNumRegistrants(num);
            }
            if (item.getNumSubmissions() == 0) {
                num = challengeSubmissionDao.getChallengeSubmissionCount(item.getChallengeId());
                item.setNumSubmissions(num);
            }
            item.setLanguages(getLanguages(item));
            challengeItemDao.updateChallenges(item);
        }
    }

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
