package com.buaa.act.sdp.topcoder.service.api.statistics;

import com.buaa.act.sdp.topcoder.dao.DevelopmentHistoryDao;
import com.buaa.act.sdp.topcoder.dao.UserDao;
import com.buaa.act.sdp.topcoder.model.user.DevelopmentHistory;
import com.buaa.act.sdp.topcoder.model.user.User;
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

    /**
     * 计算开发者的竞争、提交、获胜数目
     */
    public void updateTaskCount() {
        List<User> list = userDao.getAllUsers();
        List<DevelopmentHistory> developmentHistories;
        int count, submission, win;
        for (User user : list) {
            developmentHistories = developmentHistoryDao.getDevelopmentHistoryByHandle(user.getHandle());
            count = 0;
            win = 0;
            submission = 0;
            for (DevelopmentHistory developmentHistory : developmentHistories) {
                count += developmentHistory.getCompetitions();
                submission += developmentHistory.getSubmissions();
                win += developmentHistory.getWins();
            }
            user.setCompetitionNums(count);
            user.setSubmissionNums(submission);
            user.setWinNums(win);
            userDao.updateTask(user);
        }
    }

}
