package com.servicemain.servicemain.services;

import io.swagger.v3.oas.annotations.servers.Server;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Value;

@Slf4j
@Service
@RequiredArgsConstructor
public class S3Service {

    @Value("${s3Bucket}")
    private String dir;
    
    @Autowired
    private final ResourceLoader resourceLoader;

    private ResourcePatternResolver resourcePatternResolver;

    @Autowired //Resolve padroes locais e recursos do S3 como o s3:
    public void setupResolver(ApplicationContext applicationContext, AmazonS3 amazonS3) {
        this.resourcePatternResolver = new PathMatchingSimpleStorageResourcePatternResolver(amazonS3,
                applicationContext);
    }

    public boolean isFileExists(String file) {
        log.info("Arquivo do S3 existe ?");
        try {
            Resource resource = this.resourceLoader
                    .getResource(String.format("s3://%s/%s", dir, file));
            return resource.contentLength() > 0;
        } catch (Exception ex) {
            return false;
        }
    }

    public List<Resource> searchFile(String name, boolean exact) {
        log.info("Arquivo do S3 busca ");
        Resource[] resources = null;
        try {
            if (exact)
                resources = this.resourcePatternResolver.getResources(String.format("s3://%s/%s", dir, name)); // Busca exatamento o arquivo com o nome e extensao igual
            else
                resources = this.resourcePatternResolver.getResources(String.format("s3://%s/%s*.*", dir, name)); // Busca arquivos com nomes parecidos e com qualquer extensao
        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
        }

        return Arrays.asList(resources).stream().sorted(Comparator.comparing(Resource::getFilename)).collect(Collectors.toList());
    }

    public void saveFile(InputStream from, String to) throws FileListenerException {
        Resource resource = this.resourceLoader.getResource(String.format("s3://%s/%s", dir, to));
        WritableResource writableResource = (WritableResource) resource;

        //O OutputStream é fechado automaticamente 
        try (OutputStream outputStream = writableResource.getOutputStream()) {
            from.transferTo(outputStream);
        } catch (Exception ex) {
            log.error(ex.getMessage(), ex);
            throw new FileListenerException(ex.getMessage(), ex);
        }
    }

    public String contentFile(String file) {
        try {
            Resource resource = resourceLoader.getResource(String.format("s3://%s/%s", dir, file));
            return new BufferedReader(
                    new InputStreamReader(resource.getInputStream(), StandardCharsets.ISO_8859_1))
                    .lines()
                    .collect(Collectors.joining("\n"));
        } catch (Exception ex) {
            return null;
        }
    }

}
