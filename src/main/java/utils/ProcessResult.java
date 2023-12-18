package utils;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.File;

/**
 * @author qcqcqc
 */
@Getter
@AllArgsConstructor
public class ProcessResult {
    private Long totalFiles;
    private Long totalLines;
    private File processedFileSaved;
}
