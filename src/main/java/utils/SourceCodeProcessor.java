package utils;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Stream;

/**
 * @author qcqcqc
 */
public class SourceCodeProcessor {

    public static ProcessResult processSourceCode(String directoryPath, String[] fileExtensions, String[] excludedKeywords) {
        int totalFiles = 0;
        int totalLines = 0;
        System.out.println("directoryPath: " + directoryPath);
        System.out.println("fileExtensions: " + Arrays.toString(fileExtensions));
        System.out.println("excludedKeywords: " + Arrays.toString(excludedKeywords));
        try {
            List<Path> files = Files.walk(Paths.get(directoryPath))
                    .filter(Files::isRegularFile)
                    .filter(path -> isFileWithExtensions(path, fileExtensions))
                    .toList();
            // 新建输出文件
            Path processed = Paths.get("processed-result.txt");
            System.out.println("processed: " + processed);
            // 以utf-8编码写入文件，如果有则覆盖
            FileWriter fileWriter = new FileWriter(processed.toFile(), StandardCharsets.UTF_8, false);

            for (Path file : files) {
                List<String> lines = Files.readAllLines(file, StandardCharsets.UTF_8);
                List<String> processedLines = processLines(lines, excludedKeywords);
                totalFiles++;
                totalLines += processedLines.size();
                // 写入文件
                processedLines.forEach(line -> {
                    try {
                        fileWriter.write(line + "\n");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
            }
            fileWriter.close();
            return new ProcessResult(totalFiles, totalLines, processed.toFile());
        } catch (IOException e) {
            // 弹出错误框
            e.printStackTrace();
            return null;
        }
    }

    private static boolean isFileWithExtensions(Path path, String[] extensions) {
        //extensions : [*.java, *.vue, *.ts]
        System.out.println("path: " + path);
        for (String extension : extensions) {
            if (path.toString().toLowerCase()
                    .endsWith(extension.trim().toLowerCase()
                            .split("\\*")[1].replace(".", "")
                    )) {
                return true;
            }
        }
        return false;
    }

    private static List<String> processLines(List<String> lines, String[] excludedKeywords) {
        // 处理每一行源代码，删除空行，删除注释，删除指定开头的行
        // 如果excludedKeywords为空，则不删除指定开头的行
        Stream<String> stringStream = lines.stream()
                .filter(line -> !line.trim().isEmpty())
                // 删除单行注释
                .filter(line -> !line.trim().startsWith("//"))
                // 删除多行注释(以/*开头，以*/结尾，以及中间的所有行，使用正则表达式)
                .filter(line -> !line.trim().matches("/\\*.*\\*/"))
                // 删除文档注释(以/**开头，以*/结尾，以及中间的所有行，使用正则表达式)
                .filter(line -> !line.trim().matches("/\\*\\*.*\\*/"));
        if (!"".equals(excludedKeywords[0])) {
            stringStream = stringStream.filter(line -> !startsWithAny(line, excludedKeywords));
        }
        return stringStream.toList();

    }

    private static boolean startsWithAny(String line, String[] prefixes) {
        for (String prefix : prefixes) {
            if (line.startsWith(prefix.trim())) {
                return true;
            }
        }
        return false;
    }
}
