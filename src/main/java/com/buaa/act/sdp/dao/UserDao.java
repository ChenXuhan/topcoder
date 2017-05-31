package com.buaa.act.sdp.dao;

import com.buaa.act.sdp.bean.user.User;
import org.apache.ibatis.annotations.Param;

import java.util.HashMap;
import java.util.List;

/**
 * Created by yang on 2016/10/15.
 */
public interface UserDao {
     void insert(User user);
     User getUserByName(@Param("name") String name);
     List<String> getUsers();
     List<User>getAllUsers();
     void updateUsers(User user);
     void insertSkillDegree(@Param("handle") String handle,@Param("skillDegree")String skillDegree);
    void insertSkillDegreeBatch (@Param("relationMap") HashMap<String,String> map);
}
