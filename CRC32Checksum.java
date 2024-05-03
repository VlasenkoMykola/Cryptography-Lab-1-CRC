import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.util.zip.CRC32;

// Made CRC algorithm using the IEEE 802.3 standard CRC32 polynomial
// The polynomial used is 0xEDB88320.

public class CRC32Checksum extends JFrame {

    private JTextField filePathField;
    private JTextArea resultArea;

    // CRC32 polynomial
    private static final int CRC32_POLYNOMIAL = 0xEDB88320;
    private static int[] crcTable;

    // Generate CRC32 lookup table
    private static void generateCRCTable() {
        crcTable = new int[256];
        for (int i = 0; i < 256; i++) {
            int crc = i;
            for (int j = 0; j < 8; j++) {
                if ((crc & 1) == 1) {
                    crc = (crc >>> 1) ^ CRC32_POLYNOMIAL;
                } else {
                    crc >>>= 1;
                }
            }
            crcTable[i] = crc;
        }
    }

    // Calculate CRC32 checksum
    private static long calculateCRC32Checksum(byte[] bytes) {
        int crc = 0xFFFFFFFF;
        for (byte b : bytes) {
            crc = (crc >>> 8) ^ crcTable[(crc ^ b) & 0xFF];
        }
        return crc ^ 0xFFFFFFFF;
    }

    public CRC32Checksum() {
        super("CRC32 Checksum Calculator");
        setSize(600, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        JPanel inputPanel = new JPanel(new FlowLayout());
        JLabel filePathLabel = new JLabel("File Path:");
        filePathField = new JTextField(50);
        inputPanel.add(filePathLabel);
        inputPanel.add(filePathField);

        JButton calculateButton = new JButton("Calculate");
        resultArea = new JTextArea(5, 50);
        resultArea.setEditable(false);

        calculateButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String filePath = filePathField.getText().trim();
                if (!filePath.isEmpty()) {
                    File file = new File(filePath);
                    if (file.exists()) {
                        try (FileInputStream fis = new FileInputStream(file)) {
                            generateCRCTable();
                            byte[] buffer = new byte[1024];
                            CRC32 crc32 = new CRC32();
                            int bytesRead;
                            while ((bytesRead = fis.read(buffer)) != -1) {
                                crc32.update(buffer, 0, bytesRead);
                            }
                            long checksum = crc32.getValue();
                            resultArea.setText("CRC32 checksum of " + filePath + ": " + checksum);
                        } catch (IOException ex) {
                            resultArea.setText("Error reading file: " + ex.getMessage());
                        }
                    } else {
                        resultArea.setText("File not found: " + filePath);
                    }
                } else {
                    resultArea.setText("Please enter a file path.");
                }
            }
        });

        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.add(calculateButton);

        JScrollPane scrollPane = new JScrollPane(resultArea);

        add(inputPanel, BorderLayout.NORTH);
        add(buttonPanel, BorderLayout.CENTER);
        add(scrollPane, BorderLayout.SOUTH);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new CRC32Checksum().setVisible(true);
        });
    }
}