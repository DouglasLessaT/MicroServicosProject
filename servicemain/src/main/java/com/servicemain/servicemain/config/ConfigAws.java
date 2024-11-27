package com.servicemain.servicemain.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import java.net.URI;

import software.amazon.awssdk.auth.credentials.AnonymousCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sqs.SqsAsyncClient;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.SqsClientBuilder;

@Configuration
public class ConfigAws {

    @Bean
    public SqsAsyncClient sqsAsyncClientI() {
        return SqsAsyncClient.builder()
                .endpointOverride(URI.create("https://sqs.us-east-2.amazonaws.com/891377294367/"))// -> config no host
                .region(Region.SA_EAST_1)
                .build();
    }

}
