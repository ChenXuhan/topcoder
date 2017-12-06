package com.buaa.act.sdp.topcoder.controller;

import com.buaa.act.sdp.topcoder.common.TCResponse;
import com.buaa.act.sdp.topcoder.model.challenge.ChallengeItem;
import com.buaa.act.sdp.topcoder.service.basic.TaskService;
import com.buaa.act.sdp.topcoder.service.recommend.result.DeveloperRecommend;
import com.buaa.act.sdp.topcoder.service.recommend.result.TeamRecommend;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

/**
 * Created by yang on 2017/11/17.
 */
@Controller
@RequestMapping("/recommend")
public class DeveloperRecommendController {

    private static final Logger logger = LoggerFactory.getLogger(DeveloperRecommendController.class);

    @Autowired
    private TaskService taskService;
    @Autowired
    private DeveloperRecommend developerRecommend;
    @Autowired
    private TeamRecommend teamRecommend;

    @ResponseBody
    @RequestMapping("/task")
    public TCResponse<List<String>> recommendDevelopersForTask(@RequestParam("taskId") int taskId) {
        logger.info("recommend developers for a single task，taskId=" + taskId);
        TCResponse<List<String>> response = new TCResponse<>();
        try {
            ChallengeItem item = taskService.getChallengeById(taskId);
            if (item == null) {
                logger.info("task taskId=" + taskId + " does not exist!");
                response.setNotFoundResponse();
                return response;
            }
            List<String> developers = developerRecommend.recommendWorkers(item);
            response.setSuccessResponse(developers);
            response.setData(developers);
        } catch (Exception e) {
            logger.error("error occurred in task recommendation,taskId=" + taskId, e);
            response.setErrorResponse();
        }
        return response;
    }

    @ResponseBody
    @RequestMapping("/team")
    public TCResponse<List<String>> teamRcommend(@RequestParam("projectId") int projectId) {
        logger.info("recommend team for a project，projectId" + projectId);
        TCResponse<List<String>> response = new TCResponse<>();
        try {
            boolean exist = taskService.projectExist(projectId);
            if (!exist) {
                logger.info("project projectId=" + projectId + " does not exist!");
                response.setNotFoundResponse();
                return response;
            }
            List<String> developers = teamRecommend.generateBestTeamUsingHeuristic(projectId);
            response.setSuccessResponse(developers);
        } catch (Exception e) {
            logger.error("error occurred in team recommendation,projectId=" + projectId, e);
            response.setErrorResponse();
        }
        return response;
    }
}
