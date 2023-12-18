package utils;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Stream;

/**
 * @author qcqcqc
 */
public class SourceCodeProcessor {

    public static ProcessResult processSourceCode(String directoryPath, String[] fileExtensions, String[] excludedKeywords) {
        long totalFiles = 0;
        long totalLines = 0;
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
                String content = new String(Files.readAllBytes(file), StandardCharsets.UTF_8);
                String processedContent = processContent(content, excludedKeywords);

                totalFiles++;
                totalLines += processedContent.lines().count();

                // 写入文件
                fileWriter.write(processedContent);
            }

            fileWriter.close();
            return new ProcessResult(totalFiles, totalLines, processed.toFile());
        } catch (IOException e) {
            // 弹出错误框
            e.printStackTrace();
            return null;
        }
    }

    private static String processContent(String content, String[] excludedKeywords) {
        // 包含多行注释和javaDoc注释的正则表达式
        String mutiLineCommentRegex = "/\\*([^*]|[\\r\\n]|(\\*+([^*/]|[\\r\\n])))*\\*+/";
        // 包含单行注释的正则表达式
        String singleLineCommentRegex = "//.*";

        content = content.replaceAll(mutiLineCommentRegex, "");
        content = content.replaceAll(singleLineCommentRegex, "");

        // 将content转为以行的流
        List<String> lines = content.lines().toList();
        List<String> list = lines.stream().filter(SourceCodeProcessor::filterBlankLines)
                .filter(line -> filterLine(line, excludedKeywords)).toList();
        content = String.join("\n", list);

        return content += "\n";
    }

    private static boolean filterBlankLines(String line) {
        return !line.trim().isEmpty();
    }

    private static boolean filterLine(String line, String[] excludedKeywords) {
        // 如果excludedKeywords为空，则不删除指定开头的行
        if ("".equals(excludedKeywords[0])) {
            return true;
        }
        for (String prefix : excludedKeywords) {
            if (line.startsWith(prefix.trim())) {
                return false;
            }
        }
        return true;
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
