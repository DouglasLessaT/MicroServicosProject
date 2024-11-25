package com.servicemain.servicemain.config;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import java.net.URI;

//import org.springframework.security.config.annotation.web.builders.HttpSecurity;
//import org.springframework.security.web.SecurityFilterChain;
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
                .endpointOverride(URI.create("http://localhost:4566"))// -> config no host
                .region(Region.SA_EAST_1)
                .build();
    }

    /*
    * "/error**",
        "/index**",
        "/doc**",
        "/auth/**",
        "/auth/login",
        "/swagger-ui/**",
        "/v3/api-docs/**"
      */
//    @Bean
//    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
//        http
//                .authorizeRequests()
//                .antMatchers("/swagger-ui/**", "/v3/api-docs/**", "/swagger-ui.html")
//                .permitAll()
//                .anyRequest()
//                .authenticated()
//                .and()
//                .formLogin().disable()  // Desativa a tela de login padrão
//                .csrf().disable();
//        return http.build();
//    }

}
