package com.buaa.act.sdp.topcoder.dao;

import com.buaa.act.sdp.topcoder.model.user.RatingHistory;

/**
 * Created by yang on 2016/10/15.
 */
public interface RatingHistoryDao {

    void insert(RatingHistory[] ratingHistories);
}
