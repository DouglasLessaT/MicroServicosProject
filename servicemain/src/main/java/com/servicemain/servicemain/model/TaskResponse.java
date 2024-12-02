package com.servicemain.servicemain.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class TaskResponse {

    private String id;
    private String description;
    private String urlBucketFile;
    private String typeAction ;
    @JsonIgnore
    private MultipartFile file;

}
