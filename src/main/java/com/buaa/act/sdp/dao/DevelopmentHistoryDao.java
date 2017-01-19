package com.buaa.act.sdp.dao;

import com.buaa.act.sdp.bean.user.DevelopmentHistory;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * Created by yang on 2016/10/15.
 */
public interface DevelopmentHistoryDao {
    void insert(List<DevelopmentHistory>list);
    List<DevelopmentHistory>getChallengeCountByHandle(@Param("handle") String handle);
}
