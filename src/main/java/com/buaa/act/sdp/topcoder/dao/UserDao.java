package com.buaa.act.sdp.topcoder.dao;

import com.buaa.act.sdp.topcoder.model.user.User;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * Created by yang on 2016/10/15.
 */
public interface UserDao {

    void insert(User user);

    void updateBasic(User user);

    List<String> getDistinctUsers();

    List<User> getAllUsers();

    void updateTask(User user);

    User getUserByName(@Param("userName") String userName);
}
