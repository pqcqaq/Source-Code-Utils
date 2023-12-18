package utils;

import com.formdev.flatlaf.FlatDarkLaf;
import com.formdev.flatlaf.FlatLightLaf;

import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.Arrays;

/**
 * @author qcqcqc
 */
public class SourceCodeProcessorFrame extends JFrame {
    private JTextArea pathTextField;
    private JTextField excludePathTextField;
    private JTextField extensionsTextField;
    private JTextField keywordsTextField;
    private JLabel resultLabel;
    private File workingDirectory;

    public SourceCodeProcessorFrame() {
        setTitle("软件著作权-源代码统计处理工具 By qcqcqc");
        setSize(550, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        initComponents();
    }

    private void initComponents() {
        workingDirectory = new File("./");
        pathTextField = new JTextArea();
        JButton selectPathButton = new JButton("选择目录");
        excludePathTextField = new JTextField(".git;.idea;node_modules");
        extensionsTextField = new JTextField("*.java;*.vue;*.ts");
        keywordsTextField = new JTextField("import;package");
        resultLabel = new JLabel("文件数: 0, 代码量: 0");

        JButton processButton = new JButton("处理源代码");
        processButton.addActionListener(e -> processSourceCode());

        setLayout(null);

        int x = 20;
        int height = 25;
        add(createLabel("选择路径:", x, 20, 120, height));
        add(pathTextField);
        add(selectPathButton);
        add(createLabel("排除路径 (以;分割):", x, 130, 250, height));
        add(excludePathTextField);
        add(createLabel("文件扩展名 (以;分割):", x, 170, 250, height));
        add(extensionsTextField);
        add(createLabel("排除开头 (以;分割):", x, 210, 250, height));
        add(keywordsTextField);
        add(createLabel("统计结果:", x, 260, 120, height));
        add(resultLabel);
        add(processButton);

        selectPathButton.setBounds(400, 20, 120, 25);
        pathTextField.setBounds(80, 20, 300, 100);
        excludePathTextField.setBounds(250, 130, 270, 25);
        extensionsTextField.setBounds(250, 170, 270, 25);
        keywordsTextField.setBounds(250, 210, 270, 25);
        resultLabel.setBounds(150, 260, 300, 25);
        processButton.setBounds(165, 300, 200, 30);

        selectPathButton.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            // 默认路径为当前工作路径
            fileChooser.setCurrentDirectory(workingDirectory);
            fileChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
            fileChooser.setMultiSelectionEnabled(true);

            int result = fileChooser.showOpenDialog(SourceCodeProcessorFrame.this);

            if (result == JFileChooser.APPROVE_OPTION) {
                File[] selectedFiles = fileChooser.getSelectedFiles();
                StringBuilder filesText = new StringBuilder();

                for (File file : selectedFiles) {
                    filesText.append(file.getPath()).append("\n");
                }

                pathTextField.setText(filesText.toString());
            }
        });
        // 在桌面中间出现
        setLocationRelativeTo(null);
    }

    private JLabel createLabel(String text, int x, int y, int width, int height) {
        JLabel label = new JLabel(text);
        label.setBounds(x, y, width, height);
        return label;
    }

    private void processSourceCode() {
        SwingUtilities.invokeLater(() -> {
            String directoryPath = pathTextField.getText().trim();
            String[] fileExtensions = extensionsTextField.getText().split(";");
            String[] excludedKeywords = keywordsTextField.getText().split(";");
            String[] split = excludePathTextField.getText().split(";");

            ProcessResult processResult = SourceCodeProcessor.processSourceCode(directoryPath, split, fileExtensions, excludedKeywords);

            if (processResult != null) {
                resultLabel.setText("文件数: " + processResult.getTotalFiles() +
                                    ", 代码量: " + processResult.getTotalLines());
                // 尝试保存文件
                try {
                    System.out.println("Saved result to result.txt");
                    File file = processResult.getProcessedFileSaved();
                    // 弹出保存框，选择保存路径
                    JFileChooser fileChooser = new JFileChooser();
                    // 默认路径为当前工作路径
                    fileChooser.setCurrentDirectory(workingDirectory);
                    fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
                    fileChooser.setMultiSelectionEnabled(false);
                    fileChooser.setSelectedFile(file);
                    int result = fileChooser.showSaveDialog(SourceCodeProcessorFrame.this);
                    if (result == JFileChooser.APPROVE_OPTION) {
                        File selectedFile = fileChooser.getSelectedFile();
                        if (selectedFile.exists()) {
                            int overwrite = JOptionPane.showConfirmDialog(SourceCodeProcessorFrame.this,
                                    "文件已存在，是否覆盖？", "文件已存在", JOptionPane.YES_NO_OPTION);
                            if (overwrite == JOptionPane.NO_OPTION) {
                                return;
                            }
                        }
                        Files.copy(file.toPath(), selectedFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
                        JOptionPane.showMessageDialog(SourceCodeProcessorFrame.this,
                                "保存成功", "保存成功", JOptionPane.INFORMATION_MESSAGE);
                    }
                } catch (IOException e) {
                    System.err.println("Failed to save result." + Arrays.toString(e.getStackTrace()));
                    // 弹出错误框
                    JOptionPane.showMessageDialog(SourceCodeProcessorFrame.this,
                            "保存失败", "保存失败", JOptionPane.ERROR_MESSAGE);
                }
            } else {
                resultLabel.setText("Error processing source code.");
                // 弹出错误框
                JOptionPane.showMessageDialog(SourceCodeProcessorFrame.this,
                        "处理源代码失败", "处理源代码失败", JOptionPane.ERROR_MESSAGE);
            }
        });
    }

    public static void main(String[] args) {
        FlatLightLaf.install();

        try {
            UIManager.setLookAndFeel(new FlatDarkLaf());
        } catch (Exception ex) {
            System.err.println("Failed to initialize LaF");
        }

        SwingUtilities.invokeLater(() -> new SourceCodeProcessorFrame().setVisible(true));
    }
}

// 测试注释用例
/*
 * 123
123
123
123
*/
// 123123
