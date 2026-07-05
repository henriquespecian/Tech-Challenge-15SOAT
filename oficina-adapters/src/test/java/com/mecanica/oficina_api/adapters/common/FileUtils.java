package com.mecanica.oficina_api.adapters.common;

import java.io.IOException;
import java.nio.file.Files;
import org.springframework.core.io.DefaultResourceLoader;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;

@Component
public class FileUtils {
  private final ResourceLoader resourceLoader = new DefaultResourceLoader();

  public String readResourceFile(String fileName) throws IOException {
    var file = resourceLoader.getResource("classpath:%s" .formatted(fileName)).getFile();
    return new String(Files.readAllBytes(file.toPath()));
  }
}
