package com.buaa.act.sdp.topcoder.dao;

import com.buaa.act.sdp.topcoder.model.user.Development;

import java.util.List;

/**
 * Created by yang on 2016/10/15.
 */
public interface DevelopmentDao {

    void insertBatch(List<Development> list);

    void updateBatch(List<Development> list);
}
