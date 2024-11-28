package com.servicemain.servicemain.config;

import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.regions.Regions;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;
import com.amazonaws.services.sqs.AmazonSQSAsync;
import com.amazonaws.services.sqs.AmazonSQSAsyncClientBuilder;
import io.awspring.cloud.messaging.core.QueueMessagingTemplate;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import java.net.URI;

import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sqs.SqsAsyncClient;
import software.amazon.awssdk.services.sqs.SqsClient;

@Configuration
public class ConfigAws {

    @Value("${aws.acessKey}")
    private String acessKey;

    @Value("${aws.secretKey}")
    private String secretkey;

    @Bean
    public AWSCredentials awsCredentials() {
        return new BasicAWSCredentials(acessKey, secretkey);
    }

    @Bean
    public SqsAsyncClient sqsAsyncClient() {
        return SqsAsyncClient.builder()
                .endpointOverride(URI.create("https://sqs.us-east-2.amazonaws.com/891377294367/"))// -> config no host
                .region(Region.US_EAST_2)
                .build();
    }

    @Bean
    public AmazonSQSAsync amazonSQSAsync() {
        return AmazonSQSAsyncClientBuilder.standard()
                .withRegion(Regions.US_EAST_1)
                .withCredentials(new AWSStaticCredentialsProvider(awsCredentials()))
                .build();
    }


    @Bean
    public QueueMessagingTemplate queueMessagingTemplate(AmazonSQSAsync amazonSQSAsync) {
        return new QueueMessagingTemplate(amazonSQSAsync);
    }

    @Bean
    public AmazonS3 amazonS3() {
        return AmazonS3ClientBuilder.standard()
                .withCredentials(new AWSStaticCredentialsProvider(awsCredentials()))
                .withRegion(Regions.US_EAST_2).build();
    }

}
