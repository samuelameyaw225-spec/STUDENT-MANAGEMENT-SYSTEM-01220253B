package service;

import domain.Student;
import java.util.*;
import java.util.stream.Collectors;

public class ReportService {

    private final StudentService studentService;

    public ReportService(StudentService studentService) {
        this.studentService = studentService;
    }


    public List<Student> getTopPerformers(String programme, Integer level) throws Exception {
        return studentService.getAllStudents().stream()
                .filter(s -> programme == null || programme.equals("All") || s.getProgramme().equalsIgnoreCase(programme))
                .filter(s -> level == null || s.getLevel() == level)
                .sorted(Comparator.comparing(Student::getGpa).reversed())
                .limit(10)
                .collect(Collectors.toList());
    }


    public List<Student> getAtRiskStudents(double threshold) throws Exception {
        return studentService.getAllStudents().stream()
                .filter(s -> s.getGpa() < threshold)
                .sorted(Comparator.comparing(Student::getGpa))
                .collect(Collectors.toList());
    }


    public Map<String, Long> getGpaDistribution() throws Exception {
        List<Student> students = studentService.getAllStudents();
        Map<String, Long> distribution = new LinkedHashMap<>(); // Preserves insertion order

        distribution.put("3.5 - 4.0 (First Class)", students.stream().filter(s -> s.getGpa() >= 3.5).count());
        distribution.put("3.0 - 3.49 (Second Upper)", students.stream().filter(s -> s.getGpa() >= 3.0 && s.getGpa() < 3.5).count());
        distribution.put("2.0 - 2.99 (Second Lower)", students.stream().filter(s -> s.getGpa() >= 2.0 && s.getGpa() < 3.0).count());
        distribution.put("0.0 - 1.99 (Fail/Risk)", students.stream().filter(s -> s.getGpa() < 2.0).count());

        return distribution;
    }


    public List<String> getProgrammeSummary() throws Exception {
        Map<String, List<Student>> byProgramme = studentService.getAllStudents().stream()
                .collect(Collectors.groupingBy(Student::getProgramme));

        return byProgramme.entrySet().stream().map(entry -> {
            String prog = entry.getKey();
            int total = entry.getValue().size();
            double avgGpa = entry.getValue().stream().mapToDouble(Student::getGpa).average().orElse(0.0);
            return String.format("%s: %d Students | Average GPA: %.2f", prog, total, avgGpa);
        }).collect(Collectors.toList());
    }
}