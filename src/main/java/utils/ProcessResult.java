package utils;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

/**
 * @author qcqcqc
 */
@Getter
@AllArgsConstructor
public class ProcessResult {
    private int totalFiles;
    private int totalLines;
    private List<String> processedLines;
}
