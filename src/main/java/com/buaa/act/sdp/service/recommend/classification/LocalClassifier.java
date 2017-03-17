package com.buaa.act.sdp.service.recommend.classification;

import com.buaa.act.sdp.common.Constant;
import com.buaa.act.sdp.util.Maths;
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

    public Map<String, Double> getRecommendResult(String challengeType, double[][] features, int position, List<String> winners, int neighbors) {
//        List<Integer> neighborIndex = Maths.getNeighbors(features,position, neighbors);
        List<Integer> neighborIndex = Maths.getSimilarityChallenges(features, position);
        neighborIndex.add(position);
        int k = neighborIndex.size();
        double[][] data = new double[k][features[0].length];
        List<String> winner = new ArrayList<>(k);
        Maths.copy(features, data, winners, winner, neighborIndex);
        Maths.normalization(data,5);
        return tcBayes.getRecommendResult(Constant.LOCAL_DIRECTORY + challengeType + "/" + position, data, k-1, winner);
    }
}
