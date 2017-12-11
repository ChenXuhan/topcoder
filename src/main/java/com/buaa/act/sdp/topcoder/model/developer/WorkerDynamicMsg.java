package com.buaa.act.sdp.topcoder.model.developer;

/**
 * Created by yang on 2017/9/26.
 */

/**
 * 开发者动态信息
 */
public class WorkerDynamicMsg {

    private int numRegTask;
    private int numSubTask;
    private int numRegTaskSimilar;
    private int numSubTaskSimilar;
    private int numRegTaskTDays;
    private int numSubTaskTDays;
    private int numWinTaskTDays;
    private double priceTotal;
    private double scoreTotal;

    public int getNumRegTask() {
        return numRegTask;
    }

    public void addNumRegTask() {
        numRegTask += 1;
    }

    public int getNumSubTask() {
        return numSubTask;
    }

    public void addNumsSubTask() {
        this.numSubTask += 1;
    }

    public int getNumRegTaskSimilar() {
        return numRegTaskSimilar;
    }

    public void addNumRegTaskSimilar() {
        this.numRegTaskSimilar += 1;
    }

    public int getNumSubTaskSimilar() {
        return numSubTaskSimilar;
    }

    public void addNumSubTaskSimilar() {
        this.numSubTaskSimilar += 1;
    }

    public int getNumRegTaskTDays() {
        return numRegTaskTDays;
    }

    public void addNumRegTaskTDays() {
        this.numRegTaskTDays += 1;
    }

    public int getNumSubTaskTDays() {
        return numSubTaskTDays;
    }

    public void addNumSubTaskTDays() {
        this.numSubTaskTDays += 1;
    }

    public int getNumWinTaskTDays() {
        return numWinTaskTDays;
    }

    public void addNumsWinTaskTDays() {
        this.numWinTaskTDays += 1;
    }

    public double getPriceTotal() {
        return priceTotal;
    }

    public void addPriceTotal(double priceTotal) {
        this.priceTotal += priceTotal;
    }

    public double getScoreTotal() {
        return scoreTotal;
    }

    public void addScoreTotal(double scoreTotal) {
        this.scoreTotal += scoreTotal;
    }
}
