package com.buaa.act.sdp.topcoder;

import com.buaa.act.sdp.topcoder.dao.ChallengeItemDao;
import com.buaa.act.sdp.topcoder.dao.ChallengeRegistrantDao;
import com.buaa.act.sdp.topcoder.dao.ChallengeSubmissionDao;
import com.buaa.act.sdp.topcoder.model.challenge.ChallengeItem;
import com.buaa.act.sdp.topcoder.model.challenge.ChallengeRegistrant;
import com.buaa.act.sdp.topcoder.model.challenge.ChallengeSubmission;
import com.buaa.act.sdp.topcoder.service.api.ChallengeApi;
import com.buaa.act.sdp.topcoder.service.api.statistics.ChallengeStatistics;
import com.buaa.act.sdp.topcoder.service.basic.TaskService;
import com.buaa.act.sdp.topcoder.service.recommend.cbm.ContentBase;
import com.buaa.act.sdp.topcoder.service.recommend.experiment.TaskRecommendExperiment;
import com.buaa.act.sdp.topcoder.service.recommend.feature.FeatureExtract;
import com.buaa.act.sdp.topcoder.service.statistics.ProjectMsg;
import com.buaa.act.sdp.topcoder.service.statistics.TaskMsg;
import com.buaa.act.sdp.topcoder.service.statistics.TaskScores;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
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
    private TaskRecommendExperiment recommendResult;

    @Autowired
    private FeatureExtract featureExtract;

    @Autowired
    private ProjectMsg projectMsg;


    @Autowired(required = false)
    private ChallengeRegistrantDao challengeRegistrantDao;

    @Autowired
    private TaskScores taskScores;

    @Autowired
    private TaskService taskService;

    @Autowired
    private TaskMsg taskMsg;

    @Test
    public void testProjectId() {
        System.out.println(taskService.getProjectTasks(-2).size());
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
    public void getAllTasksAndWinners() {
        List<ChallengeItem> items = taskMsg.getItems("Assembly Competition");
        List<String> winners = taskMsg.getWinners("Assembly Competition");
        items.addAll(taskMsg.getItems("Code"));
        winners.addAll(taskMsg.getWinners("Code"));
        items.addAll(taskMsg.getItems("First2Finish"));
        winners.addAll(taskMsg.getWinners("First2Finish"));
        System.out.println(items.size() == winners.size());
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter("F:\\task.txt"));
            for (int i = 0; i < items.size(); i++) {
                writer.write(items.get(i).getChallengeId() + "\t" + items.get(i).getChallengeType() + "\t" + winners.get(i) + "\n");
            }
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
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
