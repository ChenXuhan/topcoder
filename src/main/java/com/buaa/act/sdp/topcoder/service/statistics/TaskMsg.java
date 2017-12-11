package com.buaa.act.sdp.topcoder.service.statistics;

import com.buaa.act.sdp.topcoder.dao.TaskItemDao;
import com.buaa.act.sdp.topcoder.dao.TaskSubmissionDao;
import com.buaa.act.sdp.topcoder.model.task.TaskItem;
import com.buaa.act.sdp.topcoder.model.task.TaskSubmission;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * Created by yang on 2017/5/31.
 */
@Component
public class TaskMsg {

    private static final Logger logger = LoggerFactory.getLogger(TaskMsg.class);

    @Autowired
    private TaskSubmissionDao taskSubmissionDao;
    @Autowired
    private TaskItemDao taskItemDao;
    @Autowired
    private MsgFilter msgFilter;
    @Autowired
    private TaskScores taskScores;

    /**
     * 3种不同类型的tasks
     */
    private List<TaskItem> codeItems;
    private List<TaskItem> assemblyItems;
    private List<TaskItem> f2fItems;

    /**
     * 不同类型task 对应的winner
     */
    private List<String> codeWinners;
    private List<String> assemblyWinners;
    private List<String> f2fWinners;

    /**
     * 不同类型任务的developer得分情况
     */
    private List<Map<String, Double>> codeScore;
    private List<Map<String, Double>> assemblyScore;
    private List<Map<String, Double>> f2fScore;

    public TaskMsg() {
        codeItems = new ArrayList<>();
        assemblyItems = new ArrayList<>();
        f2fItems = new ArrayList<>();
        codeWinners = new ArrayList<>();
        assemblyWinners = new ArrayList<>();
        f2fWinners = new ArrayList<>();
        codeScore = new ArrayList<>();
        assemblyScore = new ArrayList<>();
        f2fScore = new ArrayList<>();
    }

    public void initCode() {
        logger.info("init code type tasks");
        if (codeItems.isEmpty()) {
            synchronized (codeItems) {
                if (codeItems.isEmpty()) {
                    getWinnersAndScores("Code", codeItems, codeWinners, codeScore);
                }
            }
        }
    }

    public void initF2f() {
        logger.info("init first2finish type tasks");
        if (f2fItems.isEmpty()) {
            synchronized (f2fItems) {
                if (f2fItems.isEmpty()) {
                    getWinnersAndScores("First2Finish", f2fItems, f2fWinners, f2fScore);
                }
            }
        }
    }

    public void initAssembly() {
        logger.info("init assembly type tasks");
        if (assemblyItems.isEmpty()) {
            synchronized (assemblyItems) {
                if (assemblyItems.isEmpty()) {
                    getWinnersAndScores("Assembly Competition", assemblyItems, assemblyWinners, assemblyScore);
                }
            }
        }
    }

    /**
     * 获取项目内的待推荐任务，均分3份，取后2份
     *
     * @param set 项目内的任务
     * @return
     */
    public List<TaskItem> getTasks(Set<Integer> set) {
        List<TaskItem> items = new ArrayList<>(set.size());
        int start = 3;
        if (codeItems.isEmpty()) {
            initCode();
        }
        for (int i = codeItems.size() / start; i < codeItems.size(); i++) {
            if (set.contains(codeItems.get(i).getChallengeId())) {
                items.add(codeItems.get(i));
            }
        }
        if (f2fItems.isEmpty()) {
            initF2f();
        }
        for (int i = f2fItems.size() / start; i < f2fItems.size(); i++) {
            if (set.contains(f2fItems.get(i).getChallengeId())) {
                items.add(f2fItems.get(i));
            }
        }
        if (assemblyItems.isEmpty()) {
            initAssembly();
        }
        for (int i = assemblyItems.size() / start; i < assemblyItems.size(); i++) {
            if (set.contains(assemblyItems.get(i).getChallengeId())) {
                items.add(assemblyItems.get(i));
            }
        }
        return items;
    }

    public List<TaskItem> getItems(String type) {
        if (type.equals("Code")) {
            if (codeItems.isEmpty()) {
                initCode();
            }
            return codeItems;
        } else if (type.equals("First2Finish")) {
            if (f2fItems.isEmpty()) {
                initF2f();
            }
            return f2fItems;
        } else {
            if (assemblyItems.isEmpty()) {
                initAssembly();
            }
            return assemblyItems;
        }
    }

    public List<String> getWinners(String type) {
        if (type.equals("First2Finish")) {
            if (f2fWinners.isEmpty()) {
                initF2f();
            }
            return f2fWinners;
        } else if (type.equals("Code")) {
            if (codeWinners.isEmpty()) {
                initCode();
            }
            return codeWinners;
        } else {
            if (assemblyWinners.isEmpty()) {
                initAssembly();
            }
            return assemblyWinners;
        }
    }

    public List<Map<String, Double>> getDeveloperScore(String type) {
        if (type.equals("Assembly Competition")) {
            if (assemblyScore.isEmpty()) {
                initAssembly();
            }
            return assemblyScore;
        } else if (type.equals("Code")) {
            if (codeScore.isEmpty()) {
                initCode();
            }
            return codeScore;
        } else {
            if (f2fScore.isEmpty()) {
                initF2f();
            }
            return f2fScore;
        }
    }

    /**
     * 从所有的任务中进行筛选，过滤出一部分任务，计算winner、tasks，以及开发者所得分数
     *
     * @param taskType
     * @param items
     * @param winners
     * @param userScore
     */
    public void getWinnersAndScores(String taskType, List<TaskItem> items, List<String> winners, List<Map<String, Double>> userScore) {
        logger.info("get a specific type tasks and the winners, developers' scores, taskType=" + taskType);
        List<TaskSubmission> list = taskSubmissionDao.getTaskSubmissionMsg();
        Map<String, Integer> map = new HashMap<>();
        Set<Integer> challengeSet = new HashSet<>();
        Map<Integer, String> user = new HashMap<>();
        Set<Integer> set = new HashSet<>();
        TaskItem taskItem;
        List<TaskItem> taskItems = new ArrayList<>();
        for (TaskSubmission taskSubmission : list) {
            if (set.contains(taskSubmission.getChallengeID())) {
                continue;
            }
            if (challengeSet.contains(taskSubmission.getChallengeID())) {
                if (taskSubmission.getPlacement() != null && taskSubmission.getPlacement().equals("1") && Double.parseDouble(taskSubmission.getFinalScore()) >= 80) {
                    user.put(taskSubmission.getChallengeID(), taskSubmission.getHandle());
                }
            } else {
                taskItem = taskItemDao.getTaskItemById(taskSubmission.getChallengeID());
                if (msgFilter.filterTask(taskItem, taskType)) {
                    challengeSet.add(taskItem.getChallengeId());
                    taskItems.add(taskItem);
                    if (taskSubmission.getPlacement() != null && taskSubmission.getPlacement().equals("1") && Double.parseDouble(taskSubmission.getFinalScore()) >= 80) {
                        user.put(taskSubmission.getChallengeID(), taskSubmission.getHandle());
                    }
                } else {
                    set.add(taskSubmission.getChallengeID());
                }
            }
        }
        for (Map.Entry<Integer, String> entry : user.entrySet()) {
            if (map.containsKey(entry.getValue())) {
                map.put(entry.getValue(), map.get(entry.getValue()) + 1);
            } else {
                map.put(entry.getValue(), 1);
            }
        }
        Collections.sort(taskItems, new Comparator<TaskItem>() {
            @Override
            public int compare(TaskItem o1, TaskItem o2) {
                return o1.getChallengeId() - o2.getChallengeId();
            }
        });
        Map<Integer, Map<String, Double>> scores = taskScores.getDevelopersScores();
        for (int i = 0; i < taskItems.size(); i++) {
            String win = user.get(taskItems.get(i).getChallengeId());
            if (map.containsKey(win) && map.get(win) >= 5) {
                items.add(taskItems.get(i));
                winners.add(win);
                userScore.add(scores.get(taskItems.get(i).getChallengeId()));
            }
        }
    }

    /**
     * 获取三种类型任务
     *
     * @return
     */
    public List<TaskItem> getTasks() {
        logger.info("get all 3 type tasks");
        List<TaskItem> list = new ArrayList<>();
        list.addAll(getItems("Code"));
        list.addAll(getItems("First2Finish"));
        list.addAll(getItems("Assembly Competition"));
        Collections.sort(list, new Comparator<TaskItem>() {
            @Override
            public int compare(TaskItem o1, TaskItem o2) {
                return o1.getChallengeId() - o2.getChallengeId();
            }
        });
        return list;
    }

    public synchronized void update() {
        logger.info("update 3 type tasks' message, every week");
        codeItems.clear();
        codeWinners.clear();
        codeScore.clear();
        f2fItems.clear();
        f2fWinners.clear();
        f2fScore.clear();
        assemblyItems.clear();
        assemblyWinners.clear();
        assemblyScore.clear();
        initF2f();
        initCode();
        initAssembly();
    }
}
