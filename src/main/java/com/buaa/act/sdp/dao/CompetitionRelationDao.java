package com.buaa.act.sdp.dao;

import com.buaa.act.sdp.bean.challenge.CompetitionRelation;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * Created by YLT on 2017/4/15.
 */
public interface CompetitionRelationDao {
    void insert(CompetitionRelation competitionRelation);
    void deleteAll();
    List<CompetitionRelation> getCompetitions(@Param("handle1")String handle1);
}
