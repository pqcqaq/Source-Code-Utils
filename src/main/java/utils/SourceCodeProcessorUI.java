package utils;

import com.formdev.flatlaf.FlatDarkLaf;
import com.formdev.flatlaf.FlatLightLaf;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

/**
 * @author qcqcqc
 */
public class SourceCodeProcessorUI extends JFrame {
    private JTextArea pathTextField;
    private JTextField extensionsTextField;
    private JTextField keywordsTextField;
    private JLabel resultLabel;

    public SourceCodeProcessorUI() {
        setTitle("软件著作权-源代码统计处理工具 By qcqcqc");
        setSize(550, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        initComponents();
    }

    private void initComponents() {
        pathTextField = new JTextArea();
        JButton selectPathButton = new JButton("选择目录");
        extensionsTextField = new JTextField("*.java;*.vue;*.ts");
        keywordsTextField = new JTextField("import;package");
        resultLabel = new JLabel("文件数: 0, 代码量: 0");

        JButton processButton = new JButton("处理源代码");
        processButton.addActionListener(e -> processSourceCode());

        setLayout(null);

        add(createLabel("选择路径:", 20, 20, 120, 25));
        add(pathTextField);
        add(selectPathButton);
        add(createLabel("文件扩展名 (以;分割):", 20, 145, 250, 25));
        add(extensionsTextField);
        add(createLabel("排除开头 (以;分割):", 20, 195, 250, 25));
        add(keywordsTextField);
        add(createLabel("统计结果:", 20, 245, 120, 25));
        add(resultLabel);
        add(processButton);

        selectPathButton.setBounds(400, 20, 120, 25);
        pathTextField.setBounds(100, 20, 280, 100);
        extensionsTextField.setBounds(250, 145, 270, 25);
        keywordsTextField.setBounds(250, 195, 270, 25);
        resultLabel.setBounds(150, 245, 300, 25);
        processButton.setBounds(150, 295, 200, 30);

        selectPathButton.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            fileChooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
            fileChooser.setMultiSelectionEnabled(true);

            int result = fileChooser.showOpenDialog(SourceCodeProcessorUI.this);

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

            ProcessResult processResult = SourceCodeProcessor.processSourceCode(directoryPath, fileExtensions, excludedKeywords);

            if (processResult != null) {
                resultLabel.setText("文件数: " + processResult.getTotalFiles() +
                                    ", 代码量: " + processResult.getTotalLines());
                // 尝试保存文件
                try {
                    File file = new File("./result.txt");
                    if (!file.exists()) {
                        boolean newFile = file.createNewFile();
                        if (!newFile) {
                            throw new RuntimeException("Cannot create file: result.txt");
                        }
                    }
                    FileWriter fileWriter = new FileWriter(file);
                    for (String line : processResult.getProcessedLines()) {
                        fileWriter.write(line + "\n");
                    }
                    fileWriter.flush();
                    fileWriter.close();
                    System.out.println("Saved result to result.txt");
                    // 弹出保存框，选择保存路径
                    JFileChooser fileChooser = new JFileChooser();
                    fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
                    fileChooser.setMultiSelectionEnabled(false);
                    fileChooser.setSelectedFile(new File("result.txt"));
                    int result = fileChooser.showSaveDialog(SourceCodeProcessorUI.this);
                    if (result == JFileChooser.APPROVE_OPTION) {
                        File selectedFile = fileChooser.getSelectedFile();
                        if (selectedFile.exists()) {
                            int overwrite = JOptionPane.showConfirmDialog(SourceCodeProcessorUI.this,
                                    "文件已存在，是否覆盖？", "文件已存在", JOptionPane.YES_NO_OPTION);
                            if (overwrite == JOptionPane.NO_OPTION) {
                                return;
                            }
                        }
                        Files.copy(file.toPath(), selectedFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
                        JOptionPane.showMessageDialog(SourceCodeProcessorUI.this,
                                "保存成功", "保存成功", JOptionPane.INFORMATION_MESSAGE);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                resultLabel.setText("Error processing source code.");
            }
        });
    }

    public static void main(String[] args) {
        FlatLightLaf.install();

        try {
            UIManager.setLookAndFeel( new FlatDarkLaf());
        } catch( Exception ex ) {
            System.err.println( "Failed to initialize LaF" );
        }

        SwingUtilities.invokeLater(() -> new SourceCodeProcessorUI().setVisible(true));
    }
}
