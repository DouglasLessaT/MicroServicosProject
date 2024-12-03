package com.servicemain.servicemain.services;

import com.amazonaws.services.sqs.AmazonSQSAsync;
import com.servicemain.servicemain.model.ListTask;
import com.servicemain.servicemain.model.ModelTaskConsumed;
import com.servicemain.servicemain.model.TaskRequest;

//import io.awspring.cloud.sqs.annotation.SqsListener;
//import io.awspring.cloud.sqs.operations.SqsTemplate;

import io.awspring.cloud.messaging.core.QueueMessagingTemplate;
import io.awspring.cloud.messaging.listener.annotation.SqsListener;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class SqsService {

    private final QueueMessagingTemplate queueMessagingTemplate;
    private final ObjectMapper objectMapper;
    private final ListTask listTask;

    public SqsService(QueueMessagingTemplate queueMessagingTemplate, ObjectMapper objectMapper, ListTask listTask) {
        this.queueMessagingTemplate = queueMessagingTemplate;
        this.objectMapper = objectMapper;
        this.listTask = listTask;
    }

    //private final SqsTemplate sqsTemplate;

    @Value("${sqsQueueNamePost}")
    private String filaResposta;

    @SqsListener("toListNotification")
    public void consumeQueue(String message)  {

        log.info("Consume queue " + message.toString());
        Object result = convertJsonObject(message);
        listTask.add(result);
    }

    public synchronized void consumeManually(AmazonSQSAsync amazonSQSAsync) {
        String queueUrl = amazonSQSAsync.getQueueUrl("toListNotification").getQueueUrl();
        amazonSQSAsync.receiveMessage(queueUrl).getMessages().forEach(message -> {
            //TaskRequest result = convertJsonTask(message.getBody());
            Object result = convertJsonObject(message.getBody());

            log.info("Consume manually " + result.toString());
            listTask.add(result);
            log.info("Mensagem consumida manualmente: {}", message.getBody());
            amazonSQSAsync.deleteMessage(queueUrl, message.getReceiptHandle());
        });
    }

    public synchronized void postQueue(TaskRequest taskRequest){

        try {
            String menssagem = objectMapper.writeValueAsString(taskRequest);
            log.info("Menssagem a ser enviada " +menssagem);
            this.queueMessagingTemplate.send(filaResposta, MessageBuilder.withPayload(menssagem).build());

        } catch (JsonProcessingException  e) {
            e.printStackTrace();
        }

    }

    private Object convertJsonObject(String taskJson){
        try {
            Object task = objectMapper.readValue(taskJson, Object.class);
            return task;
        } catch (Exception e) {
            log.error("Erro ao converter JSON para Pagamento: " + e.getMessage());
            return null;
        }
    }

    private TaskRequest convertJsonTask(String taskJson){
        try {
            ModelTaskConsumed task = objectMapper.readValue(taskJson, ModelTaskConsumed.class);
            TaskRequest taskRequest = new TaskRequest();

            taskRequest.setUrlBucketFile(task.getUrlBucketAws());
            taskRequest.setDescription(task.getDescription());
            taskRequest.setId(task.getId());

            return taskRequest;
        } catch (Exception e) {
            log.error("Erro ao converter JSON para Pagamento: " + e.getMessage());
            return null;
        }
    }

}