package com.servicemain.servicemain.services;

import com.servicemain.servicemain.model.TaskRequest;
import com.servicemain.servicemain.model.TaskResponse;

public class TaskConverte {

    public static TaskResponse convertToResponse(TaskRequest taskRequest) {
        TaskResponse taskResponse = new TaskResponse();
        taskResponse.setId(taskRequest.getId());
        taskResponse.setDescription(taskRequest.getDescription());
        taskResponse.setUrlBucketFile(taskRequest.getUrlBucketFile());
        taskResponse.setTypeAction(taskRequest.getTypeAction());
        taskResponse.setFile(taskRequest.getFile());
        return taskResponse;
    }

}
