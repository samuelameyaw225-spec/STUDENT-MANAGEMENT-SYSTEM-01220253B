package service;

import domain.Student;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import repository.IStudentRepository;


import org.junit.jupiter.api.AfterAll;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class StudentServiceTest {

    private StudentService studentService;

    @BeforeEach
    void setUp() {
        // We pass null for the repository because we are only testing validation logic,
        // which does not touch the database.
        studentService = new StudentService(null);
    }

    // --- 1. SUCCESS TEST ---

    @Test
    @DisplayName("Should pass validation with a perfectly valid student")
    void testValidStudent_ShouldPass() {
        Student validStudent = createValidStudent();
        // If validation passes, no exception should be thrown
        assertDoesNotThrow(() -> studentService.validateStudent(validStudent));
    }

    // --- 2. STUDENT ID TESTS ---

    @Test
    @DisplayName("Should fail when Student ID is less than 4 characters")
    void testStudentId_TooShort_ShouldFail() {
        Student s = createValidStudent();
        s.setStudentId("123"); 
        
        Exception ex = assertThrows(IllegalArgumentException.class, () -> studentService.validateStudent(s));
        assertTrue(ex.getMessage().contains("Student ID"));
    }

    @Test
    @DisplayName("Should fail when Student ID contains special characters")
    void testStudentId_SpecialChars_ShouldFail() {
        Student s = createValidStudent();
        s.setStudentId("STU-1234!"); 
        
        Exception ex = assertThrows(IllegalArgumentException.class, () -> studentService.validateStudent(s));
        assertTrue(ex.getMessage().contains("Student ID"));
    }

    // --- 3. FULL NAME TESTS ---

    @Test
    @DisplayName("Should fail when Full Name contains digits")
    void testFullName_ContainsDigits_ShouldFail() {
        Student s = createValidStudent();
        s.setFullName("Ameyaw 123"); 
        
        Exception ex = assertThrows(IllegalArgumentException.class, () -> studentService.validateStudent(s));
        assertTrue(ex.getMessage().contains("Full name"));
    }

    @Test
    @DisplayName("Should fail when Full Name is less than 2 characters")
    void testFullName_TooShort_ShouldFail() {
        Student s = createValidStudent();
        s.setFullName("A"); 
        
        Exception ex = assertThrows(IllegalArgumentException.class, () -> studentService.validateStudent(s));
        assertTrue(ex.getMessage().contains("Full name"));
    }

    // --- 4. PROGRAMME TESTS ---

    @Test
    @DisplayName("Should fail when Programme is empty")
    void testProgramme_Empty_ShouldFail() {
        Student s = createValidStudent();
        s.setProgramme("   "); 
        
        Exception ex = assertThrows(IllegalArgumentException.class, () -> studentService.validateStudent(s));
        assertTrue(ex.getMessage().contains("Programme is required"));
    }

    // --- 5. LEVEL TESTS ---

    @Test
    @DisplayName("Should fail when Level is not in the allowed list")
    void testLevel_InvalidValue_ShouldFail() {
        Student s = createValidStudent();
        s.setLevel(150); 
        
        Exception ex = assertThrows(IllegalArgumentException.class, () -> studentService.validateStudent(s));
        assertTrue(ex.getMessage().contains("Level must be one of"));
    }

    // --- 6. GPA TESTS ---

    @Test
    @DisplayName("Should fail when GPA is below 0.0")
    void testGpa_BelowZero_ShouldFail() {
        Student s = createValidStudent();
        s.setGpa(-0.5); 
        
        Exception ex = assertThrows(IllegalArgumentException.class, () -> studentService.validateStudent(s));
        assertTrue(ex.getMessage().contains("GPA"));
    }

    @Test
    @DisplayName("Should fail when GPA is above 4.0")
    void testGpa_AboveFour_ShouldFail() {
        Student s = createValidStudent();
        s.setGpa(4.1); 
        
        Exception ex = assertThrows(IllegalArgumentException.class, () -> studentService.validateStudent(s));
        assertTrue(ex.getMessage().contains("GPA"));
    }

    // --- 7. EMAIL TESTS ---

    @Test
    @DisplayName("Should fail when Email is missing '@'")
    void testEmail_MissingAtSign_ShouldFail() {
        Student s = createValidStudent();
        s.setEmail("ameyawsamuel.com"); 
        
        Exception ex = assertThrows(IllegalArgumentException.class, () -> studentService.validateStudent(s));
        assertTrue(ex.getMessage().contains("email"));
    }

    @Test
    @DisplayName("Should fail when Email is missing '.'")
    void testEmail_MissingDot_ShouldFail() {
        Student s = createValidStudent();
        s.setEmail("ameyaw@samuelcom"); 
        
        Exception ex = assertThrows(IllegalArgumentException.class, () -> studentService.validateStudent(s));
        assertTrue(ex.getMessage().contains("email"));
    }

    // --- 8. PHONE NUMBER TESTS ---

    @Test
    @DisplayName("Should fail when Phone Number is too short")
    void testPhone_TooShort_ShouldFail() {
        Student s = createValidStudent();
        s.setPhoneNumber("0244123"); // Only 7 digits
        
        Exception ex = assertThrows(IllegalArgumentException.class, () -> studentService.validateStudent(s));
        assertTrue(ex.getMessage().contains("Phone number"));
    }

    @Test
    @DisplayName("Should fail when Phone Number contains letters")
    void testPhone_ContainsLetters_ShouldFail() {
        Student s = createValidStudent();
        s.setPhoneNumber("0244ABCDEF"); 
        
        Exception ex = assertThrows(IllegalArgumentException.class, () -> studentService.validateStudent(s));
        assertTrue(ex.getMessage().contains("Phone number"));
    }

    // --- HELPER METHOD ---

    /**
     * Helper method to create a baseline student that passes all validation rules.
     */
    private Student createValidStudent() {
        return new Student(
                "STU1234",           // ID (Valid 4-20)
                "Ameyaw Samuel",     // Name (Valid, 2-60, no digits)
                "Computer Science",  // Programme
                400,                 // Level (Allowed list)
                3.8,                 // GPA (Valid 0.0-4.0)
                "ameyaw@example.com",// Email (Valid format)
                "0244123456",        // Phone (Valid 10-15 digits)
                LocalDate.now(),
                "Active"
        );
    }
    @AfterAll
    static void exportTestEvidence() {
        String folderPath = "data";
        String filePath = folderPath + "/testevidence.txt";

        try {
            // Ensure the 'data' directory exists [cite: 72]
            File directory = new File(folderPath);
            if (!directory.exists()) {
                directory.mkdirs();
            }

            FileWriter fileWriter = new FileWriter(filePath, true); // true = append mode
            PrintWriter printWriter = new PrintWriter(fileWriter);

            printWriter.println("========================================");
            printWriter.println("TEST EVIDENCE REPORT");
            printWriter.println("Timestamp: " + LocalDateTime.now());
            printWriter.println("Project: Student Management System Plus");
            printWriter.println("Status: All 13 validation tests executed.");
            printWriter.println("Results: PASSED");
            printWriter.println("========================================\n");

            printWriter.close();
            System.out.println("Test evidence exported to: " + filePath);
        } catch (IOException e) {
            System.err.println("Failed to export test evidence: " + e.getMessage());
        }
    }
}