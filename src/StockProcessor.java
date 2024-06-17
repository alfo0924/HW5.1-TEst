import java.io.*;
import java.util.*;

public class StockProcessor {

    public List<String> process(String filename) {
        List<String> resultFiles = new ArrayList<>();

        try {
            File inputFile = new File(filename);
            BufferedReader reader = new BufferedReader(new FileReader(inputFile));

            // Skip the header line
            String header = reader.readLine();

            // Prepare output files
            String type_a_top_20 = "type_a_top_20.csv";
            String type_a_bottom_20 = "type_a_bottom_20.csv";
            String type_b_top_20 = "type_b_top_20.csv";
            String type_b_bottom_20 = "type_b_bottom_20.csv";

            FileWriter writerA_top = new FileWriter(type_a_top_20);
            FileWriter writerA_bottom = new FileWriter(type_a_bottom_20);
            FileWriter writerB_top = new FileWriter(type_b_top_20);
            FileWriter writerB_bottom = new FileWriter(type_b_bottom_20);

            // Write headers for each file
            writerA_top.write(header + ",漲跌幅\n");
            writerA_bottom.write(header + ",漲跌幅\n");
            writerB_top.write(header + ",漲跌幅\n");
            writerB_bottom.write(header + ",漲跌幅\n");

            // Read each line and process
            String line;
            List<String[]> records = new ArrayList<>();
            while ((line = reader.readLine()) != null) {
                String[] fields = line.split(",");

                // Clean up fields: remove extra quotes and trim whitespace
                for (int i = 0; i < fields.length; i++) {
                    fields[i] = fields[i].replaceAll("^\"|\"$", "").trim();
                }

                // Ensure fields[7] and fields[8] are not empty before parsing
                if (!fields[7].isEmpty() && !fields[8].isEmpty()) {
                    try {
                        double closePrice = Double.parseDouble(fields[7]);
                        double previousClosePrice = Double.parseDouble(fields[8]);

                        // Calculate 漲跌幅
                        double changePercentage = ((closePrice - previousClosePrice) / previousClosePrice) * 100;

                        // Append changePercentage to fields array
                        String[] newFields = Arrays.copyOf(fields, fields.length + 1);
                        newFields[fields.length] = String.format("%.2f", changePercentage);

                        // Sort and write to appropriate file
                        if (fields[0].matches("\\d{4}")) { // 證券代碼為4位數
                            records.add(newFields);
                        } else if (fields[0].matches("00\\d{3}|01\\d{3}|02\\d{3}")) { // 證券代碼為00, 01, 02開頭
                            records.add(newFields);
                        }
                    } catch (NumberFormatException e) {
                        // Handle parsing errors (e.g., invalid numeric format)
                        System.err.println("Error parsing numeric values: " + e.getMessage());
                    }
                } else {
                    System.err.println("Empty or invalid fields encountered: " + Arrays.toString(fields));
                }
            }

            // Close input file
            reader.close();

            // Sort records
            Collections.sort(records, new Comparator<String[]>() {
                @Override
                public int compare(String[] o1, String[] o2) {
                    // Compare based on 漲跌幅 (last element)
                    double changePercentage1 = Double.parseDouble(o1[o1.length - 1]);
                    double changePercentage2 = Double.parseDouble(o2[o2.length - 1]);
                    return Double.compare(changePercentage2, changePercentage1); // Descending order
                }
            });

            // Write sorted records to respective files
            int countA_top = 0, countA_bottom = 0, countB_top = 0, countB_bottom = 0;
            for (String[] record : records) {
                if (record[0].matches("\\d{4}")) { // 證券代碼為4位數
                    if (Double.parseDouble(record[record.length - 1]) >= 0 && countA_top < 20) {
                        writeToFile(writerA_top, record);
                        countA_top++;
                    } else if (Double.parseDouble(record[record.length - 1]) < 0 && countA_bottom < 20) {
                        writeToFile(writerA_bottom, record);
                        countA_bottom++;
                    }
                } else if (record[0].matches("00\\d{3}|01\\d{3}|02\\d{3}")) { // 證券代碼為00, 01, 02開頭
                    if (Double.parseDouble(record[record.length - 1]) >= 0 && countB_top < 10) {
                        writeToFile(writerB_top, record);
                        countB_top++;
                    } else if (Double.parseDouble(record[record.length - 1]) < 0 && countB_bottom < 10) {
                        writeToFile(writerB_bottom, record);
                        countB_bottom++;
                    }
                }
            }

            // Close all writers
            writerA_top.close();
            writerA_bottom.close();
            writerB_top.close();
            writerB_bottom.close();

            // Add filenames to result list
            resultFiles.add(type_a_top_20);
            resultFiles.add(type_a_bottom_20);
            resultFiles.add(type_b_top_20);
            resultFiles.add(type_b_bottom_20);

        } catch (IOException e) {
            e.printStackTrace();
        }

        return resultFiles;
    }

    private void writeToFile(FileWriter writer, String[] record) throws IOException {
        // Write the record to the file
        for (int i = 0; i < record.length - 1; i++) {
            writer.append(record[i]).append(",");
        }
        writer.append(record[record.length - 1]).append("\n");
    }
}
