package com.servicemain.servicemain.services;

import com.amazonaws.services.sqs.AmazonSQSAsync;
import com.servicemain.servicemain.model.ListTask;
import com.servicemain.servicemain.model.TaskRequest;

//import io.awspring.cloud.sqs.annotation.SqsListener;
//import io.awspring.cloud.sqs.operations.SqsTemplate;

import io.awspring.cloud.messaging.core.QueueMessagingTemplate;
import io.awspring.cloud.messaging.listener.annotation.SqsListener;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
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

    @SqsListener("${sqsQueueNameConsume}")
    public void consumeQueue(String message)  {

        log.info("Consume queue {}", message);
        Object result = convertJsonObject(message);
        listTask.add(result);
    }

    public void postQueue(TaskRequest taskRequest){

        try {
            String menssagem = objectMapper.writeValueAsString(taskRequest);
            log.info("Menssagem a ser enviada " +menssagem);
//            sqsTemplate.send(filaResposta, menssagem);
            this.queueMessagingTemplate.send(filaResposta, MessageBuilder.withPayload(menssagem).build());

        } catch (JsonProcessingException  e) {
            e.printStackTrace();
        }

    }

    private Object convertJsonObject(String pagamentoJson){
        try {
            Object task = objectMapper.readValue(pagamentoJson, Object.class);
            return task;
        } catch (Exception e) {
            log.error("Erro ao converter JSON para Pagamento: " + e.getMessage());
            return null;
        }
    }

}