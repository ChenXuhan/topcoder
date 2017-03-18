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

    private List<Integer> neighborIndex;

    public List<Integer> getNeighborIndex(double[][] features,int position) {
        neighborIndex=Maths.getSimilarityChallenges(features,position);
        return neighborIndex;
    }

    public List<Integer>getNeighbors(){
        return neighborIndex;
    }
    public Map<String, Double> getRecommendResult(String challengeType, double[][] features, int position, List<String> winners) {
//        List<Integer> neighborIndex = Maths.getNeighbors(features,position, neighbors);
        List<Integer>neighbors=new ArrayList<>(getNeighborIndex(features,position));
        neighbors.add(position);
        int k = neighbors.size();
        double[][] data = new double[k][features[0].length];
        List<String> winner = new ArrayList<>(k);
        Maths.copy(features, data, winners, winner, neighbors);
        Maths.normalization(data, 5);
        return tcBayes.getRecommendResult(Constant.LOCAL_DIRECTORY + challengeType + "/" + position, data, k - 1, winner);
    }
}
