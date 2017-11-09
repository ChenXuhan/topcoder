package com.buaa.act.sdp.topcoder.dao;

import org.apache.ibatis.annotations.Param;

/**
 * Created by yang on 2016/12/22.
 */
public interface TimeOutDao {

    void insertTimeOutData(@Param("types") String types, @Param("datas") String datas);
}
