package com.servicemain.servicemain.controllers;

import com.servicemain.servicemain.model.ListTask;
import com.servicemain.servicemain.model.TaskRequest;
import com.servicemain.servicemain.services.S3Service;
import com.servicemain.servicemain.services.SqsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.Collections;
import java.util.List;

@Controller
@Slf4j
@RequiredArgsConstructor
public class MainController {

    private final ListTask listTask;

    @GetMapping
    public String index(Model model) {

        var result = listTask.getTasks();
        log.info("Teste tela");
        model.addAttribute("tasks", result);
        return "index";

    }

}
