package com.buaa.act.sdp.topcoder.service.statistics;

import com.buaa.act.sdp.topcoder.dao.ChallengeItemDao;
import com.buaa.act.sdp.topcoder.dao.ChallengeSubmissionDao;
import com.buaa.act.sdp.topcoder.model.challenge.ChallengeItem;
import com.buaa.act.sdp.topcoder.model.challenge.ChallengeSubmission;
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
    private ChallengeSubmissionDao challengeSubmissionDao;
    @Autowired
    private ChallengeItemDao challengeItemDao;
    @Autowired
    private MsgFilter msgFilter;
    @Autowired
    private TaskScores taskScores;

    /**
     * 3种不同类型的challenges
     */
    private List<ChallengeItem> codeItems;
    private List<ChallengeItem> assemblyItems;
    private List<ChallengeItem> f2fItems;

    /**
     * 不同类型challenge 对应的winner
     */
    private List<String> codeWinners;
    private List<String> assemblyWinners;
    private List<String> f2fWinners;

    /**
     * 不同类型任务的worker得分情况
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

    public List<ChallengeItem> getChallenges(Set<Integer> set) {
        List<ChallengeItem> items = new ArrayList<>(set.size());
        if (codeItems.isEmpty()) {
            initCode();
        }
        for (ChallengeItem item : codeItems) {
            if (set.contains(item.getChallengeId())) {
                items.add(item);
            }
        }
        if (f2fItems.isEmpty()) {
            initF2f();
        }
        for (ChallengeItem item : f2fItems) {
            if (set.contains(item.getChallengeId())) {
                items.add(item);
            }
        }
        if (assemblyItems.isEmpty()) {
            initAssembly();
        }
        for (ChallengeItem item : assemblyItems) {
            if (set.contains(item.getChallengeId())) {
                items.add(item);
            }
        }
        return items;
    }

    public List<ChallengeItem> getItems(String type) {
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

    public List<Map<String, Double>> getUserScore(String type) {
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
     * @param challengeType
     * @param items
     * @param winners
     * @param userScore
     */
    public void getWinnersAndScores(String challengeType, List<ChallengeItem> items, List<String> winners, List<Map<String, Double>> userScore) {
        logger.info("get a specific type tasks and the winners, developers' scores, taskType=" + challengeType);
        List<ChallengeSubmission> list = challengeSubmissionDao.getChallengeSubmissionMsg();
        Map<String, Integer> map = new HashMap<>();
        Set<Integer> challengeSet = new HashSet<>();
        Map<Integer, String> user = new HashMap<>();
        Set<Integer> set = new HashSet<>();
        ChallengeItem challengeItem;
        List<ChallengeItem> challengeItems = new ArrayList<>();
        for (ChallengeSubmission challengeSubmission : list) {
            if (set.contains(challengeSubmission.getChallengeID())) {
                continue;
            }
            if (challengeSet.contains(challengeSubmission.getChallengeID())) {
                if (challengeSubmission.getPlacement() != null && challengeSubmission.getPlacement().equals("1") && Double.parseDouble(challengeSubmission.getFinalScore()) >= 80) {
                    user.put(challengeSubmission.getChallengeID(), challengeSubmission.getHandle());
                }
            } else {
                challengeItem = challengeItemDao.getChallengeItemById(challengeSubmission.getChallengeID());
                if (msgFilter.filterChallenge(challengeItem, challengeType)) {
                    challengeSet.add(challengeItem.getChallengeId());
                    challengeItems.add(challengeItem);
                    if (challengeSubmission.getPlacement() != null && challengeSubmission.getPlacement().equals("1") && Double.parseDouble(challengeSubmission.getFinalScore()) >= 80) {
                        user.put(challengeSubmission.getChallengeID(), challengeSubmission.getHandle());
                    }
                } else {
                    set.add(challengeSubmission.getChallengeID());
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
        Collections.sort(challengeItems, new Comparator<ChallengeItem>() {
            @Override
            public int compare(ChallengeItem o1, ChallengeItem o2) {
                return o1.getChallengeId() - o2.getChallengeId();
            }
        });
        Map<Integer, Map<String, Double>> scores = taskScores.getAllWorkerScores();
        for (int i = 0; i < challengeItems.size(); i++) {
            String win = user.get(challengeItems.get(i).getChallengeId());
            if (map.containsKey(win) && map.get(win) >= 5) {
                items.add(challengeItems.get(i));
                winners.add(win);
                userScore.add(scores.get(challengeItems.get(i).getChallengeId()));
            }
        }
    }

    /**
     * 获取三种类型任务
     *
     * @param order 是否需要排序
     * @return
     */
    public List<ChallengeItem> getTasks(boolean order) {
        logger.info("get all 3 type tasks");
        List<ChallengeItem> list = new ArrayList<>();
        list.addAll(getItems("Code"));
        list.addAll(getItems("First2Finish"));
        list.addAll(getItems("Assembly Competition"));
        if (order) {
            Collections.sort(list, new Comparator<ChallengeItem>() {
                @Override
                public int compare(ChallengeItem o1, ChallengeItem o2) {
                    return o1.getChallengeId() - o2.getChallengeId();
                }
            });
        }
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
