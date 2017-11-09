package com.buaa.act.sdp.topcoder.service.recommend.cluster;

import weka.core.EuclideanDistance;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.Option;
import weka.core.neighboursearch.PerformanceStats;

import java.util.Enumeration;

/**
 * Created by yang on 2017/3/20.
 */
public class Distince extends EuclideanDistance {

    protected Instances m_Data = null;

    public Distince() {
    }

    public Distince(Instances m_Data) {
        setInstances(m_Data);
    }

    @Override
    public Instances getInstances() {
        return m_Data;
    }

    @Override
    public void setInstances(Instances insts) {
        m_Data = insts;
    }

    @Override
    public String getAttributeIndices() {
        return null;
    }

    @Override
    public void setAttributeIndices(String value) {

    }

    @Override
    public boolean getInvertSelection() {
        return false;
    }

    @Override
    public void setInvertSelection(boolean value) {

    }

    @Override
    public double distance(Instance first, Instance second) {
        return distance(first, second, Double.POSITIVE_INFINITY, null);
    }

    @Override
    public double distance(Instance first, Instance second, PerformanceStats stats) {
        return distance(first, second, Double.POSITIVE_INFINITY, stats);
    }

    @Override
    public double distance(Instance first, Instance second, double cutOffValue) {
        return distance(first, second, cutOffValue, null);
    }

    /**
     * 自定义距离公式
     * @param first
     * @param second
     * @param cutOffValue
     * @param stats
     * @return
     */
    @Override
    public double distance(Instance first, Instance second, double cutOffValue, PerformanceStats stats) {
        double distance = 0, a = 0, b = 0, c = 0;
        int firstNumValues = first.numValues();
        distance = Math.abs(first.valueSparse(2) - second.valueSparse(2));
        if (distance > 366) {
            distance = distance / 366;
        } else {
            distance = 0;
        }
        for (int p1 = 5; p1 < firstNumValues; p1++) {
            a = a + first.valueSparse(p1) * second.valueSparse(p1);
            b = b + first.valueSparse(p1) * first.valueSparse(p1);
            c = c + second.valueSparse(p1) * second.valueSparse(p1);
        }
        return distance + 1 - a / Math.sqrt(b * c);
    }

    @Override
    public void postProcessDistances(double[] distances) {

    }

    @Override
    public void update(Instance ins) {

    }

    @Override
    public void clean() {
        m_Data = new Instances(m_Data, 0);
    }

    @Override
    protected double updateDistance(double currDist, double diff) {
        return currDist + diff;
    }

    protected double difference(double val1, double val2) {
        return val1 * val2;
    }

    @Override
    public Enumeration<Option> listOptions() {
        return null;
    }

    @Override
    public String[] getOptions() {
        return new String[0];
    }

    @Override
    public void setOptions(String[] options) throws Exception {

    }
}
