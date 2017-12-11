package com.buaa.act.sdp.topcoder.service.api.statistics;

import com.buaa.act.sdp.topcoder.dao.DeveloperDao;
import com.buaa.act.sdp.topcoder.dao.DevelopmentHistoryDao;
import com.buaa.act.sdp.topcoder.model.developer.Developer;
import com.buaa.act.sdp.topcoder.model.developer.DevelopmentHistory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by yang on 2017/1/16.
 */
@Service
public class DeveloperStatistics {

    @Autowired
    private DeveloperDao developerDao;

    @Autowired
    private DevelopmentHistoryDao developmentHistoryDao;

    private static final Logger logger = LoggerFactory.getLogger(DeveloperStatistics.class);

    /**
     * 计算开发者的竞争、提交、获胜数目
     */
    public void updateTaskCount(String userName) {
        logger.info("update developer's registered and submission count, userName=" + userName);
        List<DevelopmentHistory> developmentHistories;
        int count, submission, win;
        Developer developer = developerDao.getDeveloperByName(userName);
        developmentHistories = developmentHistoryDao.getDevelopmentHistoryByName(userName);
        count = 0;
        win = 0;
        submission = 0;
        for (DevelopmentHistory developmentHistory : developmentHistories) {
            count += developmentHistory.getCompetitions();
            submission += developmentHistory.getSubmissions();
            win += developmentHistory.getWins();
        }
        developer.setCompetitionNums(count);
        developer.setSubmissionNums(submission);
        developer.setWinNums(win);
        developerDao.updateTask(developer);
    }

}
