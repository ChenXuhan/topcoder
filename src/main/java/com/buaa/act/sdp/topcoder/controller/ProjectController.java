package com.buaa.act.sdp.topcoder.controller;

import com.buaa.act.sdp.topcoder.common.TCResponse;
import com.buaa.act.sdp.topcoder.model.task.TaskItem;
import com.buaa.act.sdp.topcoder.service.basic.TaskService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

/**
 * Created by yang on 2017/11/24.
 */
@Controller
@RequestMapping("/project")
public class ProjectController {

    private static final Logger logger = LoggerFactory.getLogger(ProjectController.class);

    @Autowired
    private TaskService taskService;

    @ResponseBody
    @RequestMapping("/tasks")
    public TCResponse<List<TaskItem>> getProjectTasks(@RequestParam("projectId") int projectId) {
        logger.info("get tasks in a project,projectId=" + projectId);
        TCResponse<List<TaskItem>> response = new TCResponse<>();
        try {
            List<TaskItem> tasks = taskService.getProjectTasks(projectId);
            if (tasks.size() == 0) {
                response.setNotFoundResponse();
                return response;
            }
            response.setSuccessResponse(tasks);
        } catch (Exception e) {
            logger.error("error occurred when get tasks in a project, projectId=" + projectId, e);
            response.setErrorResponse();
        }
        return response;
    }

    @ResponseBody
    @RequestMapping("/maxId")
    public TCResponse<Integer> getMaxProjectId() {
        logger.info("get max projectId");
        TCResponse<Integer> response = new TCResponse<>();
        try {
            int projectId = taskService.getMaxProjectId();
            response.setSuccessResponse(projectId);
        } catch (Exception e) {
            logger.error("error occurred when getting max projectId", e);
            response.setErrorResponse();
        }
        return response;
    }

    @ResponseBody
    @RequestMapping("/all")
    public TCResponse<List<Integer>> getAllProjects(@RequestParam(value = "pageNum", defaultValue = "1") int pageNum, @RequestParam(value = "pageSize", defaultValue = "10") int pageSize) {
        logger.info("get projects list, pageNum=" + pageNum + ", pageSize=" + pageSize);
        TCResponse<List<Integer>> response = new TCResponse<>();
        try {
            List<Integer> projectIds = taskService.getAllProjectIds(pageNum,pageSize);
            response.setSuccessResponse(projectIds);
        } catch (Exception e) {
            logger.error("error occurred when getting all projectIds", e);
            response.setErrorResponse();
        }
        return response;
    }
}
