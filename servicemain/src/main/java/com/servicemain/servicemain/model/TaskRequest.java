package com.servicemain.servicemain.model;

import lombok.Data;
import lombok.extern.apachecommons.CommonsLog;
import org.springframework.stereotype.Component;

@Data
public class TaskRequest{

    private Long id;
    private String description;
    private String urlBucket;
    private String typeAction ;
    private MultipartFile file;
}
