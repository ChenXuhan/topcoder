package com.buaa.act.sdp.topcoder.util;

import com.buaa.act.sdp.topcoder.common.Constant;
import com.buaa.act.sdp.topcoder.model.task.TaskItem;

import java.util.*;

/**
 * Created by yang on 2017/3/15.
 */
public class Maths {

    /**
     * 余弦相似度计算
     *
     * @param vectorOne
     * @param vectorTwo
     * @return
     */
    public static double taskSimilariry(double[] vectorOne, double[] vectorTwo) {
        double num = 0, a = 0, b = 0;
        for (int i = 0; i < vectorOne.length; i++) {
            num += vectorOne[i] * vectorTwo[i];
            a += vectorOne[i] * vectorOne[i];
            b += vectorTwo[i] * vectorTwo[i];
        }
        return num / Math.sqrt(a * b);
    }

    /**
     * 判断任务是否相似
     *
     * @param one
     * @param two
     * @return
     */
    public static boolean isSimilar(TaskItem one, TaskItem two) {
        if (one.getChallengeType().equals(two.getChallengeType())) {
            return true;
        }
        int count = 0;
        Set<String> skills = new HashSet<>();
        for (String str : one.getTechnology()) {
            skills.add(str.toLowerCase());
        }
        for (String str : two.getTechnology()) {
            if (skills.contains(str.toLowerCase())) {
                count++;
            }
        }
        double similar = 1.0 * count / Math.max(one.getTechnology().length, two.getTechnology().length);
        skills.clear();
        count = 0;
        for (String str : one.getPlatforms()) {
            skills.add(str.toLowerCase());
        }
        for (String str : two.getPlatforms()) {
            if (skills.contains(str.toLowerCase())) {
                count++;
            }
        }
        similar += 1.0 * count / Math.max(one.getPlatforms().length, two.getPlatforms().length);
        String[] temp;
        int a, b;
        if (one.getRegistrationStartDate() != null) {
            temp = one.getRegistrationStartDate().substring(0, 10).split("-");
            a = Integer.parseInt(temp[0]) * 365 + Integer.parseInt(temp[1]) * 30 + Integer.parseInt(temp[2]);
        } else {
            a = 0;
        }
        if (two.getRegistrationStartDate() != null) {
            temp = two.getRegistrationStartDate().substring(0, 10).split("-");
            b = Integer.parseInt(temp[0]) * 365 + Integer.parseInt(temp[1]) * 30 + Integer.parseInt(temp[2]);
        } else {
            b = 0;
        }
        similar += (a - b) / 5 / 365;
        if (one.getSubmissionEndDate() != null) {
            temp = one.getSubmissionEndDate().substring(0, 10).split("-");
            a = Integer.parseInt(temp[0]) * 365 + Integer.parseInt(temp[1]) * 30 + Integer.parseInt(temp[2]);
        } else {
            a = 0;
        }
        if (two.getSubmissionEndDate() != null) {
            temp = two.getSubmissionEndDate().substring(0, 10).split("-");
            b = Integer.parseInt(temp[0]) * 365 + Integer.parseInt(temp[1]) * 30 + Integer.parseInt(temp[2]);
        } else {
            b = 0;
        }
        similar += (a - b) / 5 / 365;
        double c = 0, d = 0;
        for (String str : one.getPrize()) {
            c += Double.parseDouble(str);
        }
        for (String str : two.getPrize()) {
            d += Double.parseDouble(str);
        }
        similar += Math.abs(c - d) / (c + d);
        return similar >= Constant.TASK_SIMILARITY;
    }

    /**
     * 获取某一任务较为相似的任务
     *
     * @param features
     * @param index
     * @return
     */
    public static List<Integer> getSimilarityTasks(double[][] features, int index) {
        Map<Integer, Double> map = new HashMap<>();
        double k, sum1, sum2;
        for (int i = 0; i < index; i++) {
            k = 0;
            sum1 = 0;
            sum2 = 0;
            if (Math.abs(features[i][2] - features[index][2]) > 366) {
                continue;
            }
            for (int j = 5; j < features[0].length; j++) {
                if (features[i][j] == 1.0) {
                    sum1++;
                }
                if (features[index][j] == 1.0) {
                    sum2++;
                }
                if (features[i][j] == features[index][j] && features[i][j] == 1.0) {
                    k++;
                }
            }
            if (k >= 1.0) {
                map.put(i, k / Math.sqrt(sum1 * sum2));
            }
        }
        List<Map.Entry<Integer, Double>> list = new ArrayList<>(map.entrySet());
        Collections.sort(list, new Comparator<Map.Entry<Integer, Double>>() {
            @Override
            public int compare(Map.Entry<Integer, Double> o1, Map.Entry<Integer, Double> o2) {
                return o2.getValue().compareTo(o1.getValue());
            }
        });
        List<Integer> result = new ArrayList<>(list.size());
        for (int i = 0; i < list.size(); i++) {
            result.add(list.get(i).getKey());
        }
        return result;
    }

    /**
     * 任务的发布时间距离
     *
     * @param one
     * @param two
     * @return
     */
    public static int dataDistance(TaskItem one, TaskItem two) {
        String[] temp;
        int a, b;
        if (one.getPostingDate() != null) {
            temp = one.getPostingDate().substring(0, 10).split("-");
            a = Integer.parseInt(temp[0]) * 365 + Integer.parseInt(temp[1]) * 30 + Integer.parseInt(temp[2]);
        } else {
            a = 0;
        }
        if (two.getPostingDate() != null) {
            temp = two.getPostingDate().substring(0, 10).split("-");
            b = Integer.parseInt(temp[0]) * 365 + Integer.parseInt(temp[1]) * 30 + Integer.parseInt(temp[2]);
        } else {
            b = 0;
        }
        return Math.abs(a - b);
    }

    /**
     * 向量归一化处理,[0-1]
     *
     * @param features
     * @param k
     */
    public static void normalization(double[][] features, int k) {
        double max, min;
        for (int i = 0; i < k; i++) {
            max = 0;
            min = Integer.MAX_VALUE;
            for (int j = 0; j < features.length; j++) {
                if (features[j][i] > max) {
                    max = features[j][i];
                }
                if (features[j][i] < min) {
                    min = features[j][i];
                }
            }
            for (int j = 0; j < features.length; j++) {
                features[j][i] = (features[j][i] - min) / (max - min);
            }
        }
    }

    /**
     * 从全部向量中复制出需要的部分数据进行分类
     *
     * @param features
     * @param data
     * @param winners
     * @param user
     * @param index
     */
    public static void copy(double[][] features, double[][] data, List<String> winners, List<String> user, List<Integer> index) {
        int row = index.size(), column = features[0].length;
        for (int i = 0; i < row; i++) {
            for (int j = 0; j < column; j++) {
                data[i][j] = features[index.get(i)][j];
            }
            user.add(winners.get(index.get(i)));
        }
    }
}
