package com.buaa.act.sdp.topcoder.service.recommend.classification;

import com.buaa.act.sdp.topcoder.common.Constant;
import com.buaa.act.sdp.topcoder.util.Maths;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by yang on 2017/3/15.
 */
@Service
public class LocalClassifier {

    @Autowired
    private TcBayes tcBayes;
    private List<Integer> neighborIndex;

    /**
     * 获取相似的任务
     * @param features 特征向量
     * @param position 当前任务下标
     * @return
     */
    public List<Integer> getNeighborIndex(double[][] features, int position) {
        neighborIndex = Maths.getSimilarityChallenges(features, position);
        return neighborIndex;
    }

    public List<Integer> getNeighbors() {
        return neighborIndex;
    }

    /**
     * 待推荐任务的开发者获胜概率
     * @param challengeType 任务类型
     * @param features 特性
     * @param position 当前任务下标
     * @param winners 所有的获胜者
     * @return
     */
    public Map<String, Double> getRecommendResult(String challengeType, double[][] features, int position, List<String> winners) {
        List<Integer> neighbors = new ArrayList<>(getNeighborIndex(features, position));
        neighbors.add(position);
        int k = neighbors.size();
        double[][] data = new double[k][features[0].length];
        List<String> winner = new ArrayList<>(k);
        Maths.copy(features, data, winners, winner, neighbors);
        Maths.normalization(data, 5);
        return tcBayes.getRecommendResult(Constant.LOCAL_DIRECTORY + challengeType + "/" + position, data, k - 1, winner);
    }
}
