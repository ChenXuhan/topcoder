package com.buaa.act.sdp.topcoder.controller;

import com.buaa.act.sdp.topcoder.common.Constant;
import com.buaa.act.sdp.topcoder.common.TCResponse;
import com.buaa.act.sdp.topcoder.model.task.TaskItem;
import com.buaa.act.sdp.topcoder.service.basic.TaskService;
import com.buaa.act.sdp.topcoder.service.recommend.result.DeveloperRecommend;
import com.buaa.act.sdp.topcoder.service.recommend.result.TeamRecommend;
import com.buaa.act.sdp.topcoder.service.statistics.MsgFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;
import java.util.Map;

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
    @Autowired
    private MsgFilter msgFilter;

    @ResponseBody
    @RequestMapping("/task")
    public TCResponse<List<String>> recommendDevelopersForTask(@RequestParam("taskId") int taskId) {
        logger.info("recommend developers for a single task，taskId=" + taskId);
        TCResponse<List<String>> response = new TCResponse<>();
        try {
            TaskItem item = taskService.getTaskById(taskId);
            if (item == null) {
                logger.info("task taskId=" + taskId + " does not exist!");
                response.setNotFoundResponse();
                return response;
            }
            if (!Constant.TASK_TYPE.contains(item.getChallengeType())) {
                logger.info("task taskId=" + taskId + " type not support!");
                response.setNotSupport();
                return response;
            }
            if (!msgFilter.filterTask(item)) {
                logger.info("task taskId=" + taskId + " msg incomplete!");
                response.setMsgMiss();
                return response;
            }
            List<String> developers = developerRecommend.recommendDevelopers(item);
            if (developers == null || developers.size() == 0) {
                response.setNotEnoughTrainningSet();
            } else {
                response.setSuccessResponse(developers);
            }
        } catch (Exception e) {
            logger.error("error occurred in task recommendation,taskId=" + taskId, e);
            response.setErrorResponse();
        }
        return response;
    }

    @ResponseBody
    @RequestMapping("/team")
    public TCResponse<Map<Integer, String>> recommendTeamForProject(@RequestParam("projectId") int projectId) {
        logger.info("recommend team for a project，projectId" + projectId);
        TCResponse<Map<Integer, String>> response = new TCResponse<>();
        try {
            if (!taskService.projectExist(projectId)) {
                logger.info("project projectId=" + projectId + " does not exist!");
                response.setNotFoundResponse();
                return response;
            }
            Map<Integer, String> developers = teamRecommend.generateBestTeamUsingHeuristic(projectId);
            if (developers == null) {
                response.setNotSupport();

            } else if (developers.size() == 0) {
                response.setNotEnoughTrainningSet();
            } else {
                response.setSuccessResponse(developers);
            }
        } catch (Exception e) {
            logger.error("error occurred in team recommendation,projectId=" + projectId, e);
            response.setErrorResponse();
        }
        return response;
    }
}
