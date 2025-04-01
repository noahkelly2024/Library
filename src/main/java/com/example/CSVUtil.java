import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.io.File;
import java.io.BufferedWriter;

public class CSVUtil {
    public static void writeToCSV(String filePath, String[][] data) {
        try (FileWriter writer = new FileWriter(filePath, true)) { // true for appending
            for (String[] row : data) {
                writer.write(String.join(",", row) + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static List<String[]> readFromCSV(String filePath) {
        List<String[]> data = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {
                data.add(line.split(","));
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return data;
    }

    public static void deleteLine(String filePath, int lineNumber) {
        File inputFile = new File(filePath);
        File tempFile = new File("temp.csv");

        try (BufferedReader reader = new BufferedReader(new FileReader(inputFile));
             BufferedWriter writer = new BufferedWriter(new FileWriter(tempFile))) {

            String currentLine;
            int lineCounter = 1;

            while ((currentLine = reader.readLine()) != null) {
                if (lineCounter != lineNumber) {
                    writer.write(currentLine + System.lineSeparator());
                }
                lineCounter++;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        inputFile.delete();
        tempFile.renameTo(inputFile);
    }
}
