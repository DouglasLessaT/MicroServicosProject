package com.servicemain.servicemain.services;

import com.amazonaws.services.s3.model.*;
import com.servicemain.servicemain.FileListenerException;
import io.swagger.v3.oas.annotations.servers.Server;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import com.amazonaws.services.s3.AmazonS3;

import io.awspring.cloud.core.io.s3.PathMatchingSimpleStorageResourcePatternResolver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;

import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.io.WritableResource;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Value;


import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class S3Service {

    @Value("${s3Bucket}")
    private String dir;
    private final ResourceLoader resourceLoader;
    private final AmazonS3 amazonS3;

    public boolean isFileExists(String file) {
        log.info("Arquivo do S3 existe ?");
        try {

            Boolean exists = amazonS3.doesObjectExist(dir, file);
            if (exists) {
                log.info("O arquivo '{}' existe no bucket '{}'", file, dir);
            } else {
                log.info("O arquivo '{}' NÃO existe no bucket '{}'", file, dir);
            }
            return exists;
        } catch (Exception ex) {
            return false;
        }
    }

    public List<Resource> searchFile(String name, boolean exact) {
        log.info("Arquivo do S3 busca ");
       try{
           ListObjectsV2Request request = new ListObjectsV2Request()
                   .withBucketName(dir)
                   .withPrefix(exact? name : name);
           ListObjectsV2Result result = amazonS3.listObjectsV2(request);

           return result.getObjectSummaries().stream()
                   .map(S3ObjectSummary::getKey) // Obtém o nome completo do arquivo
                   .filter(key -> exact ? key.equals(name) : key.contains(name)) // Filtra pelo nome
                   .sorted() // Ordena os resultados
                   .map(key -> resourceLoader.getResource(String.format("s3://%s/%s", dir, key))) // Transforma em Resource
                   .collect(Collectors.toList());

       } catch (Exception ex) {
           log.error("Erro ao buscar arquivos no S3", ex);
           return List.of();
       }

    }

    public synchronized String saveFile(InputStream from, String to) throws FileListenerException {

        log.info("Bucket do S3: " + dir);
        log.info("Arquivo para salvar: " + to);

        try {

            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentLength(from.available());
            amazonS3.putObject(dir, to, from, metadata);
            log.info("Arquivo salvo com sucesso no S3: " + to);
            String url = String.format("https://%s.s3.us-east-2.amazonaws.com/%s", dir, to);
            log.info("Arquivo salvo no S3 e disponível permanentemente em: " + url);

            return url;
        } catch (Exception ex) {
            log.error("Erro ao salvar arquivo no S3", ex);
            throw new FileListenerException("Erro ao salvar arquivo no S3", ex);
        }

    }

    public String contentFile(String file) {
        try {

            S3Object s3Object = amazonS3.getObject(dir, file);
            try (InputStream inputStream = s3Object.getObjectContent();
                 BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.ISO_8859_1))) {
                // Lê o conteúdo linha por linha e junta as linhas com "\n"
                return reader.lines().collect(Collectors.joining("\n"));
            }
        } catch (IOException ex) {
            log.error("Erro ao processar o conteúdo do arquivo", ex);
            return null;
        }
    }

    @PostConstruct
    public void testS3Connection() {
        try {

            log.info("Buckets: " + amazonS3.listBuckets());
            log.info("Buckets Região: " + amazonS3.getRegion());
            var exists = amazonS3.doesBucketExistV2("projetomicroservicos");
            log.info("BUcket exsts "+ exists);
            if(exists){
                log.info("AmazonS3 bucket existe " );

            }else{
                log.info("Bucket não existe");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
