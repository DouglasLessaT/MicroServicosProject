package com.servicemain.servicemain.model;

import lombok.Data;

import java.util.List;

@Data
public class ResponseMessageTask {
    private String message;
    private List<TaskResponse> tasks;
}
