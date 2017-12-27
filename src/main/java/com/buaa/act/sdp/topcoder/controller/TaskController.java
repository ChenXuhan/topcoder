package com.buaa.act.sdp.topcoder.controller;

import com.buaa.act.sdp.topcoder.common.Constant;
import com.buaa.act.sdp.topcoder.common.TCData;
import com.buaa.act.sdp.topcoder.common.TCResponse;
import com.buaa.act.sdp.topcoder.model.developer.Registrant;
import com.buaa.act.sdp.topcoder.model.task.TaskItem;
import com.buaa.act.sdp.topcoder.service.basic.TaskService;
import com.buaa.act.sdp.topcoder.service.statistics.MsgFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Created by yang on 2017/11/17.
 */
@Controller
@RequestMapping("/task")
public class TaskController {

    private static final Logger logger = LoggerFactory.getLogger(TaskController.class);

    @Autowired
    private TaskService taskService;
    @Autowired
    private MsgFilter msgFilter;

    @ResponseBody
    @RequestMapping("/detail")
    public TCResponse<TaskItem> getTaskById(@RequestParam("taskId") int taskId) {
        logger.info("get task detail info,taskId=" + taskId);
        TCResponse<TaskItem> response = new TCResponse<>();
        try {
            TaskItem item = taskService.getTaskById(taskId);
            if (item == null) {
                response.setNotFoundResponse();
                return response;
            }
            response.setSuccessResponse(item);
        } catch (Exception e) {
            logger.error("error occurred when getting task,taskId=" + taskId, e);
            response.setErrorResponse();
        }
        return response;
    }

    @ResponseBody
    @RequestMapping("/all")
    public TCResponse<TCData<TaskItem>> getAllTasks(@RequestParam(value = "pageNum", defaultValue = "1") int pageNum, @RequestParam(value = "pageSize", defaultValue = "10") int pageSize) {
        logger.info("get tasks list, pageNum=" + pageNum + ", pageSize=" + pageSize);
        TCResponse<TCData<TaskItem>> response = new TCResponse<>();
        try {
            TCData<TaskItem> data = taskService.getAllTasks(pageNum, pageSize);
            response.setSuccessResponse(data);
        } catch (Exception e) {
            response.setErrorResponse();
            logger.error("error occurred in getting all tasks", e);
        }
        return response;
    }

    @RequestMapping("/register")
    @ResponseBody
    public TCResponse<List<Registrant>> getTaskRegistrant(@RequestParam("taskId") int taskId) {
        logger.info("get a task registrants info");
        TCResponse<List<Registrant>> response = new TCResponse<>();
        try {
            List<Registrant> data = taskService.getTaskRegistrants(taskId);
            if (data == null) {
                response.setNotFoundResponse();
                return response;
            }
            response.setSuccessResponse(data);
        } catch (Exception e) {
            response.setErrorResponse();
            logger.error("error occurred in getting a task registrants", e);
        }
        return response;
    }

    @RequestMapping(value = "/upload", method = RequestMethod.POST)
    @ResponseBody
    public TCResponse<Boolean> uploadTask(@RequestBody TaskItem item) {
        TCResponse<Boolean> response = new TCResponse<>();
        try {
            if (!Constant.TASK_TYPE.contains(item.getChallengeType())) {
                response.setNotSupport();
                return response;
            }
            if (!msgFilter.filterTask(item)) {
                response.setMsgMiss();
                return response;
            }
            logger.info("upload task,type=" + item.getChallengeType());
            taskService.uploadTask(item);
            response.setSuccessResponse(true);
        } catch (Exception e) {
            logger.error("error occurred in uploading new task...", e);
            response.setErrorResponse();
        }
        return response;
    }

    @RequestMapping("/similar")
    @ResponseBody
    public TCResponse<List<TaskItem>> getSimilarTasks(@RequestParam("taskId") int taskId) {
        logger.info("get similar tasks,taskId=" + taskId);
        TCResponse<List<TaskItem>> response = new TCResponse<>();
        try {
            TaskItem item = taskService.getTaskById(taskId);
            if (item == null) {
                response.setNotFoundResponse();
                return response;
            }
            if (!Constant.TASK_TYPE.contains(item.getChallengeType())) {
                response.setNotSupport();
                return response;
            }
            response.setSuccessResponse(taskService.getSimilerTask(item));
        } catch (Exception e) {
            logger.error("error occurred in getting similar tasks...", e);
            response.setErrorResponse();
        }
        return response;
    }

    @ResponseBody
    @RequestMapping("/type")
    public TCResponse<String[]> getTaskType() {
        logger.info("get tasks type...");
        TCResponse<String[]> response = new TCResponse<>();
        try {
            response.setSuccessResponse(taskService.getTaskTypes());
        } catch (Exception e) {
            logger.error("error occurred in getting task type...", e);
            response.setErrorResponse();
        }
        return response;
    }

    @ResponseBody
    @RequestMapping("/technology")
    public TCResponse<String[]> getTaskTechnologies() {
        logger.info("get tasks technologies...");
        TCResponse<String[]> response = new TCResponse<>();
        try {
            response.setSuccessResponse(taskService.getTaskTechnologies());
        } catch (Exception e) {
            logger.error("error occurred in getting task technologies...", e);
            response.setErrorResponse();
        }
        return response;
    }

    @ResponseBody
    @RequestMapping("/platform")
    public TCResponse<String[]> getTaskPlatforms() {
        logger.info("get tasks platforms...");
        TCResponse<String[]> response = new TCResponse<>();
        try {
            response.setSuccessResponse(taskService.getTaskPlatforms());
        } catch (Exception e) {
            logger.error("error occurred in getting task platforms...", e);
            response.setErrorResponse();
        }
        return response;
    }

    @ResponseBody
    @RequestMapping("/language")
    public TCResponse<String[]> getTaskLanguages() {
        logger.info("get tasks languages...");
        TCResponse<String[]> response = new TCResponse<>();
        try {
            response.setSuccessResponse(taskService.getTaskLanguages());
        } catch (Exception e) {
            logger.error("error occurred in getting task languages...", e);
            response.setErrorResponse();
        }
        return response;
    }
}
