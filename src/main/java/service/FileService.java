package service;

import domain.Student;
import java.io.*;
import java.time.LocalDate;
import java.util.*;

public class FileService {

    // Rubric 5.4: Must be saved inside a project controlled folder named data
    private static final String DATA_DIR = "data/";

    public FileService() {
        File dir = new File(DATA_DIR);
        if (!dir.exists()) {
            dir.mkdirs(); // Auto-create the folder if it doesn't exist
        }
    }

    // --- EXPORT LOGIC ---
    public String exportToCSV(List<Student> students, String filename) {
        String filepath = DATA_DIR + filename + ".csv";

        try (PrintWriter writer = new PrintWriter(new FileWriter(filepath))) {
            // Write CSV Header
            writer.println("Student ID,Full Name,Programme,Level,GPA,Email,Phone,Date Added,Status");

            // Write Data
            for (Student s : students) {
                writer.printf("%s,%s,%s,%d,%.2f,%s,%s,%s,%s\n",
                        s.getStudentId(), s.getFullName(), s.getProgramme(),
                        s.getLevel(), s.getGpa(), s.getEmail(),
                        s.getPhoneNumber(), s.getDateAdded().toString(), s.getStatus());
            }
            return "Success! File exported to: " + filepath;
        } catch (IOException e) {
            return "Export failed: " + e.getMessage();
        }
    }

    // --- IMPORT LOGIC ---
    public List<String> importFromCSV(File file, StudentService studentService) {
        List<String> importReport = new ArrayList<>();
        int successCount = 0;

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            boolean isFirstLine = true;
            int rowNumber = 1;

            while ((line = br.readLine()) != null) {
                if (isFirstLine) {
                    isFirstLine = false; // Skip the header row
                    rowNumber++;
                    continue;
                }

                String[] data = line.split(",");
                try {
                    if (data.length < 9) {
                        throw new IllegalArgumentException("Missing columns. Expected 9, got " + data.length);
                    }

                    // Parse the data
                    Student s = new Student(
                            data[0].trim(), data[1].trim(), data[2].trim(),
                            Integer.parseInt(data[3].trim()), Double.parseDouble(data[4].trim()),
                            data[5].trim(), data[6].trim(), LocalDate.parse(data[7].trim()), data[8].trim()
                    );

                    // Add to DB (studentService will automatically validate formatting & duplicate IDs!)
                    studentService.addStudent(s);
                    successCount++;

                } catch (Exception e) {
                    // Rubric 5.5: Invalid rows must be skipped and recorded!
                    importReport.add("Error on Row " + rowNumber + " (ID: " + data[0] + "): " + e.getMessage());
                }
                rowNumber++;
            }
        } catch (Exception e) {
            importReport.add("CRITICAL ERROR: Could not read file - " + e.getMessage());
        }

        importReport.add(0, "Import Complete! Successfully added " + successCount + " students.");
        return importReport;
    }
}
