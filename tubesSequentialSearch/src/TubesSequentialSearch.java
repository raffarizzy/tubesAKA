import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.data.category.DefaultCategoryDataset;

import javax.swing.*;
import java.awt.*;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class TubesSequentialSearch {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(TubesSequentialSearch::createAndShowGUI);
    }

    private static void createAndShowGUI() {
        JFrame frame = new JFrame("Sequential Search");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(400, 400);

        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(8, 1));

        JButton importCSVButton = new JButton("Import CSV File");
        JButton showArrayButton = new JButton("Tunjukan Isi Array");
        JButton showGraphButton = new JButton("Tampilkan Grafik Waktu");
        JLabel targetLabel = new JLabel("Masukkan elemen yang ingin dicari:");
        JTextField targetInput = new JTextField();

        JButton iterativeButton = new JButton("Cari (Iteratif)");
        JButton recursiveButton = new JButton("Cari (Rekursif)");

        JLabel resultLabelR = new JLabel("Hasil pencarian rekursif: ");
        JLabel resultLabelI = new JLabel("Hasil pencarian iteratif: ");

        List<Long> iterativeTimes = new ArrayList<>();
        List<Long> recursiveTimes = new ArrayList<>();
        List<Integer> inputSizes = new ArrayList<>();

        final String[][][] csvData = {null};

        importCSVButton.addActionListener(e -> {
            csvData[0] = importCSVManually();
            if (csvData[0] == null || csvData[0].length == 0) {
                JOptionPane.showMessageDialog(null, "File CSV tidak valid atau kosong.", "Error", JOptionPane.ERROR_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(null, "File CSV berhasil diimpor.", "Informasi", JOptionPane.INFORMATION_MESSAGE);
            }
        });

        showArrayButton.addActionListener(e -> {
            if (csvData[0] == null) {
                JOptionPane.showMessageDialog(null, "File CSV belum diimpor.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            showArrayGUI(csvData[0]);
        });

        showGraphButton.addActionListener(e -> {
            if (iterativeTimes.isEmpty() || recursiveTimes.isEmpty() || inputSizes.isEmpty()) {
                JOptionPane.showMessageDialog(null, "Tidak ada data untuk ditampilkan di grafik.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            showGraph(iterativeTimes, recursiveTimes, inputSizes);
        });

        iterativeButton.addActionListener(e -> {
            int index = -1;
            long duration = 0; 
            
            if (csvData[0] == null) {
                JOptionPane.showMessageDialog(null, "File CSV belum diimpor.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            String[] array = flattenArray(csvData[0]);
            String target = targetInput.getText();
            
            
            for (int warmup = 0; warmup < 5; warmup++) {
                iterativeSearch(array, target);
            }
            
            for (int i=0; i < 10; i++) {
                long startTime = System.nanoTime();
                index = iterativeSearch(array, target);
                long endTime = System.nanoTime();
                duration += endTime - startTime;
            }
            
            resultLabelI.setText("Hasil pencarian (Iteratif): " +
                    (index >= 0 ? "Ditemukan di indeks " + index : "Tidak ditemukan") +
                    " | Waktu: " + duration/10 + " ns atau " + String.format("%.3f", (double)(duration/10) / 1000000) + " ms");

            iterativeTimes.add(duration/10);
            inputSizes.add(index+1);
        });

        recursiveButton.addActionListener(e -> {
            int index = -1;
            long duration = 0; 
            
            if (csvData[0] == null) {
                JOptionPane.showMessageDialog(null, "File CSV belum diimpor.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
            String[] array = flattenArray(csvData[0]);
            String target = targetInput.getText();
                     
            for (int warmup = 0; warmup < 5; warmup++) {
                recursiveSearch(array, target, 0);
            }
            
            for (int i =0; i < 10; i++) {
                long startTime = System.nanoTime();
                index = recursiveSearch(array, target, 0);
                long endTime = System.nanoTime();
                duration += endTime - startTime;
            }
            
            resultLabelR.setText("Hasil pencarian (Rekursif): " +
                    (index >= 0 ? "Ditemukan di indeks " + index : "Tidak ditemukan") +
                    " | Waktu: " + duration/10 + " ns atau " + String.format("%.3f", (double)(duration/10) / 1000000) + " ms");

            recursiveTimes.add(duration/10);
        });

        panel.add(targetLabel);
        panel.add(targetInput);
        panel.add(iterativeButton);
        panel.add(recursiveButton);
        panel.add(resultLabelI);
        panel.add(resultLabelR);
        panel.add(showArrayButton);
        panel.add(showGraphButton);
        panel.add(importCSVButton);

        frame.getContentPane().add(panel);
        frame.setVisible(true);
    }

    private static void showGraph(List<Long> iterativeTimes, List<Long> recursiveTimes, List<Integer> inputSizes) {
        DefaultCategoryDataset dataset = new DefaultCategoryDataset();

        for (int i = 0; i < inputSizes.size(); i++) {
            dataset.addValue(iterativeTimes.get(i), "Iterative", inputSizes.get(i));
            dataset.addValue(recursiveTimes.get(i), "Recursive", inputSizes.get(i));
        }

        JFreeChart chart = ChartFactory.createLineChart(
                "Running Time Comparison",
                "Input Size",
                "Time (ns)",
                dataset
        );

        JFrame chartFrame = new JFrame("Running Time Graph");
        chartFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        chartFrame.setSize(600, 400);

        ChartPanel chartPanel = new ChartPanel(chart);
        chartFrame.add(chartPanel);
        chartFrame.setVisible(true);
    }

    private static void showArrayGUI(String[][] csvData) {
        JFrame arrayFrame = new JFrame("Isi Array");
        arrayFrame.setSize(400, 400);
        JTextArea arrayText = new JTextArea();

        StringBuilder arrayContent = new StringBuilder();
        for (String[] row : csvData) {
            for (String value : row) {
                arrayContent.append(value).append(" ");
            }
            arrayContent.append("\n");
        }
        arrayText.setText(arrayContent.toString());
        arrayText.setEditable(false);

        arrayFrame.add(new JScrollPane(arrayText));
        arrayFrame.setVisible(true);
    }

    private static int iterativeSearch(String[] array, String target) {
        for (int i = 0; i < array.length; i++) {
            if (array[i].trim().equals(target)) {
                return i;
            }
        }
        return -1;
    }

    private static int recursiveSearch(String[] array, String target, int index) {
        if (index >= array.length) {
            return -1;
        }
        if (array[index].trim().equals(target)) {
            return index;
        }
        return recursiveSearch(array, target, index + 1);
    }

    private static String[] flattenArray(String[][] csvData) {
        List<String> flatList = new ArrayList<>();
        for (String[] row : csvData) {
            for (String value : row) {
                flatList.add(value.trim());
            }
        }
        return flatList.toArray(new String[0]);
    }

    private static String[][] importCSVManually() {
        JFileChooser fileChooser = new JFileChooser();
        int result = fileChooser.showOpenDialog(null);
        if (result == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            List<String[]> data = new ArrayList<>();
            try (BufferedReader br = new BufferedReader(new FileReader(file))) {
                String line;
                while ((line = br.readLine()) != null) {
                    String[] values = line.split(",");
                    data.add(values);
                }
            } catch (IOException e) {
                JOptionPane.showMessageDialog(null, "Error membaca file: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                return null;
            }
            return data.toArray(new String[0][]);
        }
        return null;
    }
}
