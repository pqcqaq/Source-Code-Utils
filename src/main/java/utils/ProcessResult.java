package utils;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.io.File;
import java.util.List;

/**
 * @author qcqcqc
 */
@Getter
@AllArgsConstructor
public class ProcessResult {
    private int totalFiles;
    private int totalLines;
    private File processedFileSaved;
}
