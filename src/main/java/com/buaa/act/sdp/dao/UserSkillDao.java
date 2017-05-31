package com.buaa.act.sdp.dao;

import com.buaa.act.sdp.bean.user.UserSkill;

import java.util.List;

/**
 * Created by YLT on 2017/4/19.
 */
public interface UserSkillDao {
    void insert(List<UserSkill> userSkill);
    void insertEach(UserSkill userSkill);
}
