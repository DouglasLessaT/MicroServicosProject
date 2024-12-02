package com.servicemain.servicemain.model;

import lombok.Data;

import java.util.List;

@Data
public class ResponseMessage {

    private String message;
    private List<TaskRequest> tasks;

}
