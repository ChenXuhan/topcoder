package com.buaa.act.sdp.service.statistics;

import com.buaa.act.sdp.bean.user.DevelopmentHistory;
import com.buaa.act.sdp.bean.user.User;
import com.buaa.act.sdp.dao.DevelopmentHistoryDao;
import com.buaa.act.sdp.dao.UserDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by yang on 2017/1/16.
 */
@Service
public class UserStatistics {

    @Autowired
    private UserDao userDao;

    @Autowired
    private DevelopmentHistoryDao developmentHistoryDao;

    public void updateUsers(){
        List<User>list=userDao.getAllUsers();
        List<DevelopmentHistory>developmentHistories;
        int count,submission,win;
        for(User user:list){
            developmentHistories=developmentHistoryDao.getChallengeCountByHandle(user.getHandle());
            count=0;
            win=0;
            submission=0;
            for(DevelopmentHistory developmentHistory:developmentHistories){
                count+=developmentHistory.getCompetitions();
                submission+=developmentHistory.getSubmissions();
                win+=developmentHistory.getWins();
            }
            user.setCompetitionNums(count);
            user.setSubmissionNums(submission);
            user.setWinNums(win);
            userDao.updateUsers(user);
        }
    }

}
