package com.servicemain.servicemain.controllers.api;


import com.servicemain.servicemain.FileListenerException;
import com.servicemain.servicemain.model.*;
import com.servicemain.servicemain.services.S3Service;
import com.servicemain.servicemain.services.SqsService;
import com.servicemain.servicemain.services.TaskConverte;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springdoc.core.converters.ResponseSupportConverter;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/v1/main")
@RequiredArgsConstructor
public class MainApiController {

    private final S3Service s3Service;
    private final SqsService sqsService;
    private final ListTask listTask;
    private final ResponseSupportConverter responseSupportConverter;

    @PostMapping("/save")
    public ResponseEntity<Object> saveTask(@ModelAttribute TaskRequest task) throws IOException {

        if(task.getDescription() == null || task.getDescription().isEmpty() || task.getFile() == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Tarefa invalida.");
        }

        log.info("Objeto " + task.toString());
        var taskRequest = new TaskRequest();

        taskRequest.setDescription(task.getDescription());
        taskRequest.setFile(task.getFile());
        taskRequest.setTypeAction("POST");

        try {

            InputStream inputStream = task.getFile().getInputStream();
            String fileName = task.getFile().getOriginalFilename();
            String urlFile = s3Service.saveFile(inputStream, fileName);
            log.info("Arquivo salvo no S3 "+ fileName);
            taskRequest.setUrlBucketFile(urlFile);

        } catch (Exception e) {
            log.error("Erro ao salvar o arquivo no S3: " + e.getMessage(), e);
            throw new RuntimeException(e);
        }

        listTask.add(taskRequest);
        log.info("Objeto TaskRequest: "+ taskRequest.toString());

        sqsService.postQueue(taskRequest);

        ResponseMessage responseMessage = new ResponseMessage();
        responseMessage.setMessage("Salvo com secesso!");
        responseMessage.setTasks(List.of(taskRequest));

        return ResponseEntity.ok().body(responseMessage);

    }

    @PutMapping("/edit/{id}")
    public ResponseEntity<Object> editTask(
            @PathVariable String id,
            @ModelAttribute TaskRequest taskRequest) throws IOException, FileListenerException {

        var taskFinded = listTask.getTasks()
                .stream()
                .filter(task -> {
                    if (task instanceof TaskRequest) {
                        return ((TaskRequest) task).getId().equals(id);
                    }
                    return false;
                })
                .findFirst();

        if (taskFinded.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Tarefa não encontrada.");
        }

        var task = (TaskRequest) taskFinded.get();

        log.info("Objeto ",  taskFinded.get());
        log.info("id "+ id);
        log.info("Teste rota de edição " + task.toString());

        task.setId(id);
        task.setDescription(taskRequest.getDescription());
        log.info("Teste upload " + taskRequest.getUrlBucketFile());
        if(taskRequest.getUrlBucketFile() != null || taskRequest.getFile() != null) {

            InputStream inputStream = taskRequest.getFile().getInputStream();
            String fileName = taskRequest.getFile().getOriginalFilename();
            String urlFile = s3Service.saveFile(inputStream, fileName);
            log.info("Arquivo salvo no S3 "+ fileName);
            task.setUrlBucketFile(urlFile);

        }

        task.setTypeAction("PUT");

        sqsService.postQueue(task);

        var responseMessage = new ResponseMessage();
        responseMessage.setMessage("Tarefa atualizada com sucesso!");
        responseMessage.setTasks(List.of(task));

        return ResponseEntity.ok().body(responseMessage);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Object> deleteTask(@PathVariable String id) {

        var taskFinded = listTask.getTasks()
                .stream()
                .filter(task -> {
                    if (task instanceof TaskRequest) {
                        return ((TaskRequest) task).getId().equals(id);
                    }
                    return false;
                }).findFirst();

        if (taskFinded.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Tarefa não encontrada.");
        }

        var taskRequest = (TaskRequest) taskFinded.get();
        listTask.remove(taskRequest);
        taskRequest.setTypeAction("DELETE");
        sqsService.postQueue(taskRequest);

        return ResponseEntity.ok().body("Teste editar");
    }


    @GetMapping("/buscar/{id}")
    public ResponseEntity<Object> getTaskById(@PathVariable String id) {

        var taskFinded = listTask.getTasks()
                .stream()
                .filter(task -> {
                    if (task instanceof TaskRequest) {
                        return ((TaskRequest) task).getId().equals(id);
                    }
                    return false;
                })
                .findFirst();

        if (taskFinded.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Tarefa não encontrada.");
        }

        var taskRequest = (TaskRequest) taskFinded.get();
        taskRequest.setTypeAction("PUT");
        log.info("Objeto encontrado "+taskRequest.toString());

        TaskResponse taskResponse = TaskConverte.convertToResponse(taskRequest);

        var responseMessage = new ResponseMessageTask();
        responseMessage.setMessage("Teste buscar");
        responseMessage.setTasks(List.of(taskResponse));

        return ResponseEntity.ok().body(responseMessage);
    }
}
