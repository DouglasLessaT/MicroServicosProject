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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.Collections;
import java.util.List;

@Controller
@Slf4j
@RequiredArgsConstructor
public class MainController {

    private final S3Service s3Service;
    private final SqsService sqsService;
    private final ListTask listTask;

    @GetMapping
    public String index(Model model) {

        var result = listTask.getTasks();

        //Buscar arquivo do S3 

        log.info("Teste tela");
        model.addAttribute("tasks", result);
        model.addAttribute("taskRequest", new TaskRequest());
        return "index";
    }

    @PostMapping("/save")
    public String saveTask(@ModelAttribute TaskRequest taskRequest, BindingResult bindingResult) {
        sqsService.postQueue(taskRequest);
        //Salvar arquivo do S3 
        return "redirect:/index";
    }

    @GetMapping("/edit/{id}")
    public String editTask(@ModelAttribute TaskRequest taskRequest, BindingResult bindingResult) {
        sqsService.postQueue(taskRequest);
        //Atualizar arquivo do S3
        return "index"; 
    }

    // Método para excluir uma tarefa
    @PostMapping("/delete/{id}")
    public String deleteTask(@ModelAttribute TaskRequest taskRequest) {
        sqsService.postQueue(taskRequest);
        //dletar arquivo do S3 
        return "redirect:/index";
    }


}
