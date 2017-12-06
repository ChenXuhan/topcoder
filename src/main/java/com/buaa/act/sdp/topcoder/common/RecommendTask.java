package com.buaa.act.sdp.topcoder.common;

import com.buaa.act.sdp.topcoder.model.challenge.ChallengeItem;
import com.buaa.act.sdp.topcoder.service.recommend.result.DeveloperRecommend;

import java.util.List;
import java.util.concurrent.Callable;

/**
 * Created by yang on 2017/12/5.
 */
public class RecommendTask implements Callable<List<String>> {

    private ChallengeItem item;
    private DeveloperRecommend developerRecommend;

    public RecommendTask() {
    }

    public RecommendTask(ChallengeItem item, DeveloperRecommend developerRecommend) {
        this.item = item;
        this.developerRecommend = developerRecommend;
    }

    @Override
    public List<String> call() throws Exception {
        return developerRecommend.recommendWorkers(item);
    }
}
