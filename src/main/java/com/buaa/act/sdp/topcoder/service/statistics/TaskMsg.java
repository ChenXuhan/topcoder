package com.buaa.act.sdp.topcoder.service.statistics;

import com.buaa.act.sdp.topcoder.dao.TaskItemDao;
import com.buaa.act.sdp.topcoder.dao.TaskSubmissionDao;
import com.buaa.act.sdp.topcoder.model.task.TaskItem;
import com.buaa.act.sdp.topcoder.model.task.TaskSubmission;
import com.buaa.act.sdp.topcoder.service.redis.RedisService;
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

    private static final String CODE_TASK = "code_task";
    private static final String FIRST_TO_FINISH_TASK = "first_to_finish_task";
    private static final String ASSEMBLY_TASK = "assembly_task";

    private static final String CODE_WINNER = "code_winner";
    private static final String FIRST_TO_FINISH_WINNER = "first_to_finish_winner";
    private static final String ASSEMBLY_WINNER = "assembly_winner";

    private static final String CODE_SCORE = "code_score";
    private static final String FIRST_TO_FINISH_SCORE = "first_to_finish_score";
    private static final String ASSEMBLY_SCORE = "assembly_score";

    @Autowired
    private TaskSubmissionDao taskSubmissionDao;
    @Autowired
    private TaskItemDao taskItemDao;
    @Autowired
    private MsgFilter msgFilter;
    @Autowired
    private TaskScores taskScores;
    @Autowired
    private RedisService redisService;

    /**
     * 获取项目内的待推荐任务，均分3份，取后2份
     *
     * @param set 项目内的任务
     * @return
     */
    public List<TaskItem> getTasks(Set<Integer> set) {
        List<TaskItem> items = new ArrayList<>(set.size());
        int start = 8;
        List<TaskItem> codeItems = getItems("Code");
        List<TaskItem> f2fItems = getItems("First2Finish");
        List<TaskItem> assemblyItems = getItems("Assembly Competition");
        for (int i = codeItems.size() / start; i < codeItems.size(); i++) {
            if (set.contains(codeItems.get(i).getChallengeId())) {
                items.add(codeItems.get(i));
            }
        }
        for (int i = f2fItems.size() / start; i < f2fItems.size(); i++) {
            if (set.contains(f2fItems.get(i).getChallengeId())) {
                items.add(f2fItems.get(i));
            }
        }
        for (int i = assemblyItems.size() / start; i < assemblyItems.size(); i++) {
            if (set.contains(assemblyItems.get(i).getChallengeId())) {
                items.add(assemblyItems.get(i));
            }
        }
        return items;
    }

    public void initCode() {
        synchronized (CODE_TASK) {
            List<TaskItem> items = redisService.getListCache(CODE_TASK);
            if (items.isEmpty()) {
                items = new ArrayList<>();
                List<String> winners = new ArrayList<>();
                List<Map<String, Double>> scores = new ArrayList<>();
                getWinnersAndScores("Code", items, winners, scores);
            }
        }
    }

    public void initAssmebly() {
        synchronized (ASSEMBLY_TASK) {
            List<TaskItem> items = redisService.getListCache(ASSEMBLY_TASK);
            if (items.isEmpty()) {
                List<String> winners = new ArrayList<>();
                List<Map<String, Double>> scores = new ArrayList<>();
                items = new ArrayList<>();
                getWinnersAndScores("Assembly Competition", items, winners, scores);
            }
        }
    }

    public void initFirst2Finish() {
        synchronized (FIRST_TO_FINISH_TASK) {
            List<TaskItem> items = redisService.getListCache(FIRST_TO_FINISH_TASK);
            if (items.isEmpty()) {
                items = new ArrayList<>();
                List<Map<String, Double>> score = new ArrayList<>();
                List<String> winner = new ArrayList<>();
                getWinnersAndScores("First2Finish", items, winner, score);
            }
        }
    }

    /**
     * double check获取缓存的任务数据
     *
     * @param type 任务类型
     * @return
     */
    public List<TaskItem> getItems(String type) {
        if (type.equals("Code")) {
            List<TaskItem> items = redisService.getListCache(CODE_TASK);
            if (!items.isEmpty()) {
                return items;
            }
            initCode();
            return redisService.getListCache(CODE_TASK);
        } else if (type.equals("First2Finish")) {
            List<TaskItem> items = redisService.getListCache(FIRST_TO_FINISH_TASK);
            if (!items.isEmpty()) {
                return items;
            }
            initFirst2Finish();
            return redisService.getListCache(FIRST_TO_FINISH_TASK);
        } else {
            List<TaskItem> items = redisService.getListCache(ASSEMBLY_TASK);
            if (!items.isEmpty()) {
                return items;
            }
            initAssmebly();
            return redisService.getListCache(ASSEMBLY_TASK);
        }
    }

    public List<String> getWinners(String type) {
        if (type.equals("Code")) {
            List<String> winners = redisService.getListCache(CODE_WINNER);
            if (!winners.isEmpty()) {
                return winners;
            }
            initCode();
            return redisService.getListCache(CODE_WINNER);
        } else if (type.equals("First2Finish")) {
            List<String> winners = redisService.getListCache(FIRST_TO_FINISH_WINNER);
            if (!winners.isEmpty()) {
                return winners;
            }
            initFirst2Finish();
            return redisService.getListCache(FIRST_TO_FINISH_WINNER);
        } else {
            List<String> winners = redisService.getListCache(ASSEMBLY_WINNER);
            if (!winners.isEmpty()) {
                return winners;
            }
            initAssmebly();
            return redisService.getListCache(ASSEMBLY_WINNER);
        }
    }

    public List<Map<String, Double>> getDeveloperScore(String type) {
        if (type.equals("Code")) {
            List<Map<String, Double>> scores = redisService.getListCache(CODE_SCORE);
            if (!scores.isEmpty()) {
                return scores;
            }
            initCode();
            return redisService.getListCache(CODE_SCORE);
        } else if (type.equals("First2Finish")) {
            List<Map<String, Double>> scores = redisService.getListCache(FIRST_TO_FINISH_SCORE);
            if (!scores.isEmpty()) {
                return scores;
            }
            initFirst2Finish();
            return redisService.getListCache(FIRST_TO_FINISH_SCORE);
        } else {
            List<Map<String, Double>> scores = redisService.getListCache(ASSEMBLY_SCORE);
            if (!scores.isEmpty()) {
                return scores;
            }
            initAssmebly();
            return redisService.getListCache(ASSEMBLY_SCORE);
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
        logger.info("save tasks information into redis,type=" + taskType);
        String[] types = getTypes(taskType);
        redisService.setListCache(types[0], items);
        redisService.setListCache(types[1], winners);
        redisService.setListCache(types[2], userScore);
    }

    private String[] getTypes(String type) {
        String[] types = new String[3];
        if (type.equals("Code")) {
            types[0] = CODE_TASK;
            types[1] = CODE_WINNER;
            types[2] = CODE_SCORE;
        } else if (type.equals("First2Finish")) {
            types[0] = FIRST_TO_FINISH_TASK;
            types[1] = FIRST_TO_FINISH_WINNER;
            types[2] = FIRST_TO_FINISH_SCORE;
        } else {
            types[0] = ASSEMBLY_TASK;
            types[1] = ASSEMBLY_WINNER;
            types[2] = ASSEMBLY_SCORE;
        }
        return types;
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
        logger.info("update 3 type tasks msg, every week");
        redisService.delete(CODE_TASK);
        redisService.delete(CODE_WINNER);
        redisService.delete(CODE_SCORE);
        redisService.delete(FIRST_TO_FINISH_TASK);
        redisService.delete(FIRST_TO_FINISH_WINNER);
        redisService.delete(FIRST_TO_FINISH_SCORE);
        redisService.delete(ASSEMBLY_TASK);
        redisService.delete(ASSEMBLY_WINNER);
        redisService.delete(ASSEMBLY_SCORE);
        initCode();
        initAssmebly();
        initFirst2Finish();
    }
}
