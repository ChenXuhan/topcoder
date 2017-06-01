package com.buaa.act.sdp.service.api;

import com.buaa.act.sdp.model.challenge.ChallengeItem;
import com.buaa.act.sdp.dao.ChallengeItemDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.text.DecimalFormat;

/**
 * Created by YLT on 2016/11/22.
 */
@Service
public class DatabaseOpe extends Thread {

    @Autowired
    private ChallengeItemDao challengeItemDao;

    //保存历史项目的项目难度系数
    HashMap<Integer, Double> scores = new HashMap<Integer, Double>();

    /*
    * 找到相应属性下值的最大最小值
    */
    public int[] findMaxMinItem(String entryName,double percentage) {
        Integer[] items;
        if (entryName.equals("prize")) {
            String[] allPrizesStr = challengeItemDao.getAllPrizes();
            String[][] allPrizes = new String[allPrizesStr.length][5];
            items = new Integer[allPrizesStr.length];
            for (int i = 0; i < allPrizesStr.length; i++) {
                if (!allPrizesStr[i].equals("")) {
                    allPrizes[i] = allPrizesStr[i].split(",");
                }
            }
            for (int i = 0; i < allPrizes.length; i++) {
                items[i] = 0;
                for (int j = 0; j < allPrizes[i].length; j++) {
                    if (allPrizes[i][j] == null) {
                        break;
                    }
                    if (allPrizes[i][j].contains(".")) {
                        allPrizes[i][j] = allPrizes[i][j].substring(0, allPrizes[i][j].indexOf("."));
                    }
                    items[i] += Integer.parseInt(allPrizes[i][j]);
                }
            }
            return (maxMinIntFind(items,percentage));
        } else if (entryName.equals("reliabilityBonus")) {
            items = challengeItemDao.getAllReliabilityBonus();
            return (maxMinIntFind(items,percentage));
        } else if (entryName.equals("duration")) {
            items = challengeItemDao.getAllDuration();
            return (maxMinIntFind(items,percentage));
        } else if (entryName.equals("numRegistrants")) {
            items = challengeItemDao.getAllNumRegistrants();
            return (maxMinIntFind(items,percentage));
        } else if (entryName.equals("numSubmissions")) {
            items = challengeItemDao.getAllNumSubmissions();
            return (maxMinIntFind(items,percentage));
        }
        return null;
    }

    /*
    * 找到项目完成率的最大最小值
    * */
    public double[] maxMinPercentFind() {
        double max = Double.MIN_VALUE, min = Double.MAX_VALUE;
        double[] returnFloat = new double[2];
        Integer[] items1 = challengeItemDao.getAllNumRegistrants();
        Integer[] items2 = challengeItemDao.getAllNumSubmissions();
        double[] itemsFloat = new double[items1.length];
        for (int i = 0; i < items1.length; i++) {
            if (items1[i] != 0) {
                itemsFloat[i] = items2[i] / items1[i];
            } else {
                itemsFloat[i] = 0;
            }
        }
        for (int i = 0; i < itemsFloat.length; i++) {
            if (max < itemsFloat[i]) {
                max = itemsFloat[i];
            }
            if (min > itemsFloat[i]) {
                min = itemsFloat[i];
            }
        }
        returnFloat[0] = min;
        returnFloat[1] = max;
        return returnFloat;
    }

    /*
    * 求数组中的最大最小值
    * */
    public int[] maxMinIntFind(Integer[] items,double percentage) {
        int max = Integer.MIN_VALUE, min = Integer.MAX_VALUE;
        int[] returnInt = new int[2];
        for (int i = 0; i < items.length * percentage; i++) {
            if (max < items[i]) {
                max = items[i];
            }
            if (min > items[i]) {
                min = items[i];
            }
        }
        returnInt[0] = min;
        returnInt[1] = max;
        return (returnInt);
    }

    /*
    * 求项目总的难度系数过程
    * */
    public double numProcess(int reliability, int prize, int numRegistrants, int duration, int min1, int max1, int min2, int max2, int min3, int max3, int min4, int max4) {
        double para1 = toOne(reliability, min1, max1);
        double para2 = toOne(prize, min2, max2);
        double para3 = toOne(numRegistrants, min3, max3);
        double para4 = toOne(duration, min4, max4);
        double numScore = para1 + para2 + para3 + para4 ;
        DecimalFormat df = new DecimalFormat("#.####");
        numScore = Double.parseDouble(df.format(numScore));
        return numScore;
    }

    public double toOne(int number, int min, int max) {
        return (double) (number - min) / (max - min);
    }

    public void run() {
        int min1, min2, min3, min4, max1, max2, max3, max4;
        int[] result = new int[2];
        double percentage = 0.9;
        List<ChallengeItem> allChallenges = challengeItemDao.getAllChallenges();

        result = findMaxMinItem("reliabilityBonus",percentage);
        min1 = result[0];
        max1 = result[1];

        result = findMaxMinItem("prize",percentage);
        min2 = result[0];
        max2 = result[1];

        result = findMaxMinItem("numRegistrants",percentage);
        min3 = result[0];
        max3 = result[1];

        result = findMaxMinItem("duration",percentage);
        min4 = result[0];
        max4 = result[1];

        //遍历求每一个项目的难度系数
        for (int i = 0; i < allChallenges.size() * percentage; i++) {
            ChallengeItem item = allChallenges.get(i);
            String[] prizeStr = item.getPrize();
            int prize = 0;
            if (!(prizeStr.length == 1 && prizeStr[0] == "")) {
                for (int j = 0; j < prizeStr.length; j++) {
                    if (prizeStr[j].contains(".")) {
                        prizeStr[j] = prizeStr[j].substring(0, prizeStr[j].indexOf("."));
                    }
                    prize += Integer.parseInt(prizeStr[j]);
                }
            }
           /* if (item.getNumRegistrants() != 0) {
                percent = (double) item.getNumSubmissions() / item.getNumRegistrants();
                if (percent > 1) {
                    percent = 1;
                }
            } else {
                percent = 0;
            }*/
            scores.put(item.getChallengeId(), numProcess((int) item.getReliabilityBonus(), prize, item.getNumRegistrants(), item.getDuration(), min1, max1, min2, max2, min3, max3, min4, max4));
        }
        for(int i = (int)(Math.ceil(allChallenges.size() * percentage)); i < allChallenges.size();i ++){
            scores.put(allChallenges.get(i).getChallengeId(),0.0);
        }
        challengeItemDao.insertDifficultyDegree(scores);
    }
}
