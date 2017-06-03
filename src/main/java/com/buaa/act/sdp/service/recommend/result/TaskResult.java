package com.buaa.act.sdp.service.recommend.result;

import com.buaa.act.sdp.model.challenge.ChallengeItem;
import com.buaa.act.sdp.service.recommend.feature.FeatureExtract;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by yang on 2017/6/3.
 */
@Service
public class TaskResult {

    @Autowired
    private FeatureExtract featureExtract;

//    public List<String>recommendWorkers(ChallengeItem item){
//        if(item==null){
//            return null;
//        }
//
//    }
}
