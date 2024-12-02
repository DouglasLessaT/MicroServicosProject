package com.servicemain.servicemain.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import lombok.extern.apachecommons.CommonsLog;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@Data
public class TaskRequest{

    private String id;
    private String description;
    private String urlBucketFile;
    private String typeAction ;
    @JsonIgnore
    private MultipartFile file;

    public TaskRequest() {
        this.id = UUID.randomUUID().toString();}

}
