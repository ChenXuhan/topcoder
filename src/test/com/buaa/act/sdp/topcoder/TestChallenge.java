package com.buaa.act.sdp.topcoder;

import com.buaa.act.sdp.topcoder.common.Constant;
import com.buaa.act.sdp.topcoder.dao.ChallengeItemDao;
import com.buaa.act.sdp.topcoder.dao.ChallengeRegistrantDao;
import com.buaa.act.sdp.topcoder.dao.ChallengeSubmissionDao;
import com.buaa.act.sdp.topcoder.model.challenge.ChallengeItem;
import com.buaa.act.sdp.topcoder.model.challenge.ChallengeRegistrant;
import com.buaa.act.sdp.topcoder.model.challenge.ChallengeSubmission;
import com.buaa.act.sdp.topcoder.service.api.ChallengeApi;
import com.buaa.act.sdp.topcoder.service.recommend.experiment.TaskRecommend;
import com.buaa.act.sdp.topcoder.service.recommend.cbm.ContentBase;
import com.buaa.act.sdp.topcoder.service.recommend.feature.FeatureExtract;
import com.buaa.act.sdp.topcoder.service.statistics.ProjectMsg;
import com.buaa.act.sdp.topcoder.service.statistics.TaskScores;
import com.buaa.act.sdp.topcoder.service.api.statistics.ChallengeStatistics;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.*;

/**
 * Created by yang on 2016/10/19.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = "classpath:conf/applicationContext.xml")
public class TestChallenge {

    @Autowired
    private ChallengeApi challengeApi;

    @Autowired(required = false)
    private ChallengeSubmissionDao challengeSubmissionDao;

    @Autowired(required = false)
    private ContentBase contentBase;

    @Autowired(required = false)
    private ChallengeItemDao challengeItemDao;

    @Autowired
    private ChallengeStatistics challengeStatistics;

    @Autowired
    private TaskRecommend recommendResult;

    @Autowired
    private FeatureExtract featureExtract;

    @Autowired
    private ProjectMsg projectMsg;


    @Autowired(required = false)
    private ChallengeRegistrantDao challengeRegistrantDao;

    @Autowired
    private TaskScores taskScores;

    @Test
    public void testProjectId() {
        System.out.println(projectMsg.getProjectToChallenges());
        System.out.println(projectMsg.getProjectToChallenges().size());
    }

    @Test
    public void testGetWorkerScores() {
        Map<Integer, Map<String, Double>> scores = taskScores.getAllWorkerScores();
        Iterator<Map.Entry<Integer, Map<String, Double>>> entries = scores.entrySet().iterator();
        while (entries.hasNext()) {
            Map.Entry<Integer, Map<String, Double>> entry = entries.next();
            System.out.println("Key = " + entry.getKey() + ", Value = " + entry.getValue());
        }
        System.out.println(taskScores.getAllWorkerScores().size());
    }

    @Test
    public void testChallenge() {
        featureExtract.getFeatures("Assembly Competition");
        List<ChallengeItem> items = featureExtract.getItems("Assembly Competition");
        List<String> winner = featureExtract.getWinners("Assembly Competition");
        System.out.println(items.size() + "\t" + winner.size());
        Map<String, List<Integer>> map = new HashMap<>();
        for (int i = 0; i < winner.size(); i++) {
            if (map.containsKey(winner.get(i))) {
                map.get(winner.get(i)).add(i);
            } else {
                List<Integer> list = new ArrayList<>();
                list.add(i);
                map.put(winner.get(i), list);
            }
        }
        for (Map.Entry<String, List<Integer>> entry : map.entrySet()) {
            List<Integer> list = entry.getValue();
            System.out.println(entry.getKey() + "\t" + list.size());
            for (int j = 0; j < list.size(); j++) {
                ChallengeItem item = items.get(list.get(j));
                System.out.println(item.getChallengeName() + " " + item.getPostingDate() + " " + item.getDuration() + " " + Arrays.toString(item.getPrize()) + "\t" + Arrays.toString(item.getTechnology()) + " " + Arrays.toString(item.getPlatforms()));
            }
        }
    }

    @Test
    public void testPhrase() {
    }

    @Test
    public void updateChallenges() {
        challengeStatistics.updateChallenges();
    }

    @Test
    public void challengeSkill() {
        Set<String> set = new HashSet<>();
        List<ChallengeItem> items = challengeItemDao.getAllChallenges();
        Set<String> sets = new HashSet<>();
        for (ChallengeItem item : items) {
            if (item.getTechnology() != null) {
                for (String s : item.getTechnology()) {
                    sets.add(s);
                }
            }
        }
        for (String s : Constant.TECHNOLOGIES) {
            set.add(s);
        }
        for (String s : sets) {
            boolean flag = false;
            for (String ss : set) {
                if (ss.startsWith(s)) {
                    flag = true;
                    break;
                }
            }
            if (!flag) {
                System.out.println(s);
            }
        }
    }

    @Test
    public void testNoSubmission() {
        Map<Integer, Set<String>> submissionMap = new HashMap<>();
        Map<Integer, Set<String>> registerMap = new HashMap<>();
        List<ChallengeSubmission> submissions = challengeSubmissionDao.getChallengeSubmissionMsg();
        List<ChallengeRegistrant> registrants = challengeRegistrantDao.getAllChallengeRegistrants();
        for (ChallengeSubmission challengeSubmission : submissions) {
            if (submissionMap.containsKey(challengeSubmission.getChallengeID())) {
                submissionMap.get(challengeSubmission.getChallengeID()).add(challengeSubmission.getHandle());
            } else {
                Set<String> set = new HashSet<>();
                set.add(challengeSubmission.getHandle());
                submissionMap.put(challengeSubmission.getChallengeID(), set);
            }
        }
        for (ChallengeRegistrant challengeRegistrant : registrants) {
            if (registerMap.containsKey(challengeRegistrant.getChallengeID())) {
                registerMap.get(challengeRegistrant.getChallengeID()).add(challengeRegistrant.getHandle());
            } else {
                Set<String> set = new HashSet<>();
                set.add(challengeRegistrant.getHandle());
                registerMap.put(challengeRegistrant.getChallengeID(), set);
            }
        }
        int regCount = 0, subCount = 0;
        for (Map.Entry<Integer, Set<String>> entry : submissionMap.entrySet()) {
            subCount += entry.getValue().size();
        }
        for (Map.Entry<Integer, Set<String>> entry : registerMap.entrySet()) {
            regCount += entry.getValue().size();
        }
        System.out.println(subCount + "\t" + regCount + "\t" + 1.0 * subCount / regCount);
    }
}
