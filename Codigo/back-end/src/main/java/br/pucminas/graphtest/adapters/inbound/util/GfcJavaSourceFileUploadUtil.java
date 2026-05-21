package br.pucminas.graphtest.adapters.inbound.util;

import br.pucminas.graphtest.application.exception.JavaSourceFileException;
import lombok.experimental.UtilityClass;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.charset.StandardCharsets;

/**
 * Utilitario inbound para validacao e leitura de uploads de arquivos Java da feature GFC.
 */
@UtilityClass
public class GfcJavaSourceFileUploadUtil {

    public static String readJavaSourceFile(MultipartFile file) {
        validateJavaSourceFile(file);
        try {
            return new String(file.getBytes(), StandardCharsets.UTF_8);
        } catch (IOException exception) {
            throw new JavaSourceFileException("Nao foi possivel ler o arquivo Java enviado.");
        }
    }

    public static void validateJavaSourceFile(MultipartFile file) {
        if (file == null) {
            throw new JavaSourceFileException("O arquivo Java e obrigatorio.");
        }
        if (file.isEmpty()) {
            throw new JavaSourceFileException("O arquivo Java enviado esta vazio.");
        }

        String filename = file.getOriginalFilename();
        if (filename == null || !filename.toLowerCase().endsWith(".java")) {
            throw new JavaSourceFileException("O arquivo enviado deve possuir extensao .java.");
        }
    }
}
