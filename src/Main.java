import java.io.*;
import java.net.URL;

public class Main {

    public static void main(String[] args) {
        try {
            // Step 1: Download the stock data file
            String url = "https://www.twse.com.tw/exchangeReport/STOCK_DAY_ALL?response=open_data";
            String fileName = "stock_data.csv";
            downloadFile(url, fileName);

            // Step 2: Create StockProcessor object and process the file
            StockProcessor processor = new StockProcessor();
            processor.process(fileName);

            // Step 3: Verify if the files are created and check their contents
            verifyFilesExistAndNotEmpty("type_a_top_20.csv");
            verifyFilesExistAndNotEmpty("type_a_bottom_20.csv");
            verifyFilesExistAndNotEmpty("type_b_top_20.csv");
            verifyFilesExistAndNotEmpty("type_b_bottom_20.csv");

            System.out.println("Processing completed successfully.");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void downloadFile(String url, String fileName) throws IOException {
        try (BufferedInputStream in = new BufferedInputStream(new URL(url).openStream());
             FileOutputStream fileOutputStream = new FileOutputStream(fileName)) {

            byte[] dataBuffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = in.read(dataBuffer, 0, 1024)) != -1) {
                fileOutputStream.write(dataBuffer, 0, bytesRead);
            }
        }
    }

    private static void verifyFilesExistAndNotEmpty(String filename) {
        File file = new File(filename);
        if (file.exists() && file.length() > 0) {
            System.out.println(filename + " created successfully.");
        } else {
            System.err.println(filename + " creation failed or is empty.");
        }
    }
}
