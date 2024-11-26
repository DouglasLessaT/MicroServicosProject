package com.servicemain.servicemain.services;

import com.servicemain.servicemain.model.ListTask;
import com.servicemain.servicemain.model.TaskRequest;
import io.awspring.cloud.sqs.annotation.SqsListener;
import io.awspring.cloud.sqs.operations.SqsTemplate;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Slf4j
@RequiredArgsConstructor
@Service
public class SqsService {

    private final SqsTemplate sqsTemplate;
    private final ObjectMapper objectMapper;
    private final ListTask listTask;


    @Value("${sqsQueueNamePost}")
    private String filaResposta;

    //@SqsListener("${sqsQueueNameConsume}")
    public void consumeQueue(String message)  {

        log.info("Consume queue {}", message);
        Object result = convertJsonObject(message);
        listTask.add(result);
    }

    public void postQueue(TaskRequest taskRequest){

        try {
            String menssagem = objectMapper.writeValueAsString(taskRequest);
            log.info("Menssagem a ser enviada " +menssagem);
            sqsTemplate.send(filaResposta, menssagem);

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