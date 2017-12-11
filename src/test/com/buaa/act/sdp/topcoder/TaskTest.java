package com.buaa.act.sdp.topcoder;

import com.buaa.act.sdp.topcoder.common.Constant;
import com.buaa.act.sdp.topcoder.dao.TaskItemDao;
import com.buaa.act.sdp.topcoder.dao.TaskRegistrantDao;
import com.buaa.act.sdp.topcoder.dao.TaskSubmissionDao;
import com.buaa.act.sdp.topcoder.model.task.TaskItem;
import com.buaa.act.sdp.topcoder.model.task.TaskRegistrant;
import com.buaa.act.sdp.topcoder.model.task.TaskSubmission;
import com.buaa.act.sdp.topcoder.service.api.UpdateTasksAndDevelopers;
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
public class TaskTest {

    @Autowired(required = false)
    private TaskSubmissionDao taskSubmissionDao;

    @Autowired(required = false)
    private TaskRegistrantDao taskRegistrantDao;

    @Autowired
    private UpdateTasksAndDevelopers updateTasksAndDevelopers;

    @Autowired
    private TaskScores taskScores;

    @Autowired
    private TaskItemDao taskItemDao;

    @Autowired
    private TaskMsg taskMsg;

    @Test
    public void testGetChallengeId() {
        System.out.println(taskItemDao.getTasksIds(Constant.TASK_TYPE).size());
    }

    @Test
    public void testGetWorkerScores() {
        Map<Integer, Map<String, Double>> scores = taskScores.getDevelopersScores();
        Iterator<Map.Entry<Integer, Map<String, Double>>> entries = scores.entrySet().iterator();
        while (entries.hasNext()) {
            Map.Entry<Integer, Map<String, Double>> entry = entries.next();
            System.out.println("Key = " + entry.getKey() + ", Value = " + entry.getValue());
        }
    }

    @Test
    public void getAllTasksAndWinners() {
        List<TaskItem> items = taskMsg.getItems("Assembly Competition");
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
        List<TaskSubmission> submissions = taskSubmissionDao.getTaskSubmissionMsg();
        List<TaskRegistrant> registrants = taskRegistrantDao.getAllTaskRegistrants();
        for (TaskSubmission taskSubmission : submissions) {
            if (submissionMap.containsKey(taskSubmission.getChallengeID())) {
                submissionMap.get(taskSubmission.getChallengeID()).add(taskSubmission.getHandle());
            } else {
                Set<String> set = new HashSet<>();
                set.add(taskSubmission.getHandle());
                submissionMap.put(taskSubmission.getChallengeID(), set);
            }
        }
        for (TaskRegistrant taskRegistrant : registrants) {
            if (registerMap.containsKey(taskRegistrant.getChallengeID())) {
                registerMap.get(taskRegistrant.getChallengeID()).add(taskRegistrant.getHandle());
            } else {
                Set<String> set = new HashSet<>();
                set.add(taskRegistrant.getHandle());
                registerMap.put(taskRegistrant.getChallengeID(), set);
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

    @Test
    public void trainningSet(){
        System.out.println(taskMsg.getItems("Code").get(1).getChallengeId());
        System.out.println(taskMsg.getItems("First2Finish").get(1).getChallengeId());
        System.out.println(taskMsg.getItems("Assembly Competition").get(1).getChallengeId());
    }

    @Test
    public void testGetFinishedTaskCount(){
        System.out.println(updateTasksAndDevelopers.getCompletedTaskCount());
    }
}
