package com.hospital.main;

import com.hospital.enums.Gender;
import com.hospital.enums.PatientStatus;
import com.hospital.model.*;
import com.hospital.service.AdmissionService;
import com.hospital.service.StaffService;
import com.hospital.service.ReportService;
import com.hospital.exception.*;

import java.util.List;
import java.util.Scanner;

/**
 * Main entry point for the Hospital Management System.
 *
 * <p>This class bootstraps the application by creating a {@link Hospital} instance,
 * pre-populating it with sample departments and wards, and presenting an
 * interactive console menu that delegates to the three core services:
 * {@link AdmissionService}, {@link StaffService}, and {@link ReportService}.</p>
 *
 * <h2>Design Decisions</h2>
 * <ul>
 *   <li>Instance-based rather than static-method-only — keeps state in fields,
 *       making the class testable and extensible.</li>
 *   <li>Every user-facing operation is wrapped in its own {@code handle*()} helper
 *       so the main loop stays small and readable.</li>
 *   <li>All input is read with {@link Scanner#nextLine()} to avoid the classic
 *       {@code nextInt() / nextLine()} newline-consumption bug.</li>
 *   <li>Success messages use ✓ and errors use ✗ for quick visual scanning.</li>
 * </ul>
 *
 * @author Hospital Management System Team
 * @version 1.0
 */
public class Main {

    // ───────────────────────────── Fields ──────────────────────────────

    /** The hospital instance managed by this application. */
    private final Hospital hospital;

    /** Service responsible for patient registration, admission, treatment, and discharge. */
    private final AdmissionService admissionService;

    /** Service responsible for adding and assigning staff members. */
    private final StaffService staffService;

    /** Service responsible for generating reports and statistics. */
    private final ReportService reportService;

    // ─────────────────────────── Constructor ───────────────────────────

    /**
     * Constructs the application, wiring up the hospital and all services,
     * then populating the system with sample data for demonstration purposes.
     */
    public Main() {
        this.hospital = new Hospital("City General Hospital");
        this.admissionService = new AdmissionService(hospital);
        this.staffService = new StaffService(hospital);
        this.reportService = new ReportService(hospital);
        initializeSampleData();
    }

    // ────────────────────── Sample-Data Bootstrap ─────────────────────

    /**
     * Pre-populates the hospital with three departments and three wards so
     * the user can start experimenting immediately without manual setup.
     */
    private void initializeSampleData() {
        try {
            // Departments
            hospital.addDepartment(new Department("Cardiology"));
            hospital.addDepartment(new Department("Neurology"));
            hospital.addDepartment(new Department("General Medicine"));

            // Wards
            hospital.addWard(new Ward("General Ward", 20));
            hospital.addWard(new Ward("ICU", 10));
            hospital.addWard(new Ward("Pediatric Ward", 15));

            System.out.println("  ✓ Sample data loaded: 3 departments, 3 wards.");
        } catch (HospitalException e) {
            System.err.println("  ✗ Failed to initialize sample data: " + e.getMessage());
        }
    }

    // ═══════════════════════════ Main Loop ═════════════════════════════

    /**
     * Runs the interactive console loop.
     *
     * <p>Displays the menu, reads the user's choice, and dispatches to the
     * appropriate handler.  The loop continues until the user selects
     * option&nbsp;0 (Exit).</p>
     */
    public void run() {
        Scanner scanner = new Scanner(System.in);
        boolean running = true;

        System.out.println("\n  🏥 Welcome to the Hospital Management System!\n");

        while (running) {
            displayMenu();
            System.out.print("  ➤ Enter your choice: ");
            String input = scanner.nextLine().trim();

            // Parse the choice safely
            int choice;
            try {
                choice = Integer.parseInt(input);
            } catch (NumberFormatException e) {
                System.out.println("\n  ✗ Invalid input. Please enter a number between 0 and 17.\n");
                continue;
            }

            System.out.println(); // visual breathing room

            switch (choice) {
                // ── Patient Management ──
                case 1:  handleRegisterPatient(scanner);   break;
                case 2:  handleAdmitPatient(scanner);      break;
                case 3:  handleStartTreatment(scanner);    break;
                case 4:  handleDischargePatient(scanner);   break;
                case 5:  handleAssignDoctor(scanner);       break;
                case 6:  handleAssignWard(scanner);         break;
                case 7:  handleViewPatient(scanner);        break;
                case 8:  handleViewAuditTrail(scanner);     break;

                // ── Staff Management ──
                case 9:  handleAddDoctor(scanner);          break;
                case 10: handleAddNurse(scanner);           break;
                case 11: handleAssignDepartment(scanner);   break;
                case 12: handleViewStaff(scanner);          break;

                // ── Reports ──
                case 13: handleGenerateReport();            break;
                case 14: handleViewAllPatients();           break;
                case 15: handleViewAllStaff();              break;
                case 16: handleViewAllWards();              break;
                case 17: handleViewAllDepartments();        break;

                // ── Exit ──
                case 0:
                    running = false;
                    System.out.println("  👋 Thank you for using the Hospital Management System.");
                    System.out.println("     Goodbye!\n");
                    break;

                default:
                    System.out.println("  ✗ Invalid choice. Please select 0–17.\n");
            }
        }

        scanner.close();
    }

    // ══════════════════════════ Menu Display ═══════════════════════════

    /**
     * Prints the main menu to the console with box-drawing characters for
     * a professional, easy-to-scan layout.
     */
    private void displayMenu() {
        System.out.println("╔══════════════════════════════════════════════════════════╗");
        System.out.println("║          HOSPITAL MANAGEMENT SYSTEM                      ║");
        System.out.println("║          City General Hospital                           ║");
        System.out.println("╠══════════════════════════════════════════════════════════╣");
        System.out.println("║                                                          ║");
        System.out.println("║   ── Patient Management ──                               ║");
        System.out.println("║    1.  Register Patient                                  ║");
        System.out.println("║    2.  Admit Patient                                     ║");
        System.out.println("║    3.  Start Treatment                                   ║");
        System.out.println("║    4.  Discharge Patient                                 ║");
        System.out.println("║    5.  Assign Doctor to Patient                          ║");
        System.out.println("║    6.  Assign Ward to Patient                            ║");
        System.out.println("║    7.  View Patient Details                              ║");
        System.out.println("║    8.  View Patient Audit Trail                          ║");
        System.out.println("║                                                          ║");
        System.out.println("║   ── Staff Management ──                                 ║");
        System.out.println("║    9.  Add Doctor                                        ║");
        System.out.println("║   10.  Add Nurse                                         ║");
        System.out.println("║   11.  Assign Staff to Department                        ║");
        System.out.println("║   12.  View Staff Details                                ║");
        System.out.println("║                                                          ║");
        System.out.println("║   ── Reports ──                                          ║");
        System.out.println("║   13.  Generate Full Report                              ║");
        System.out.println("║   14.  View All Patients                                 ║");
        System.out.println("║   15.  View All Staff                                    ║");
        System.out.println("║   16.  View All Wards                                    ║");
        System.out.println("║   17.  View All Departments                              ║");
        System.out.println("║                                                          ║");
        System.out.println("║    0.  Exit                                              ║");
        System.out.println("║                                                          ║");
        System.out.println("╚══════════════════════════════════════════════════════════╝");
    }

    // ══════════════════════ Input-Helper Methods ══════════════════════

    /**
     * Prompts the user to select a gender and returns the corresponding
     * {@link Gender} enum constant.
     *
     * @param scanner the active {@link Scanner} for console input
     * @return the selected {@link Gender}, defaulting to {@code OTHER} on invalid input
     */
    private Gender readGender(Scanner scanner) {
        System.out.println("  Gender:");
        System.out.println("    1. Male");
        System.out.println("    2. Female");
        System.out.println("    3. Other");
        System.out.print("  ➤ Select gender (1-3): ");
        String genderInput = scanner.nextLine().trim();

        switch (genderInput) {
            case "1": return Gender.MALE;
            case "2": return Gender.FEMALE;
            case "3": return Gender.OTHER;
            default:
                System.out.println("  ⚠ Invalid selection — defaulting to OTHER.");
                return Gender.OTHER;
        }
    }

    // ════════════════ Patient-Management Handlers ═════════════════════

    /**
     * Guides the user through registering a new patient by collecting
     * name, age, gender, contact number, and address.
     *
     * @param scanner the active {@link Scanner} for console input
     */
    private void handleRegisterPatient(Scanner scanner) {
        System.out.println("  ── Register New Patient ──\n");
        try {
            System.out.print("  Name    : ");
            String name = scanner.nextLine().trim();

            System.out.print("  Age     : ");
            int age = Integer.parseInt(scanner.nextLine().trim());

            Gender gender = readGender(scanner);

            System.out.print("  Contact : ");
            String contact = scanner.nextLine().trim();

            System.out.print("  Address : ");
            String address = scanner.nextLine().trim();

            Patient patient = admissionService.registerPatient(name, age, gender, contact, address);
            System.out.println("\n  ✓ Patient registered successfully!");
            System.out.println("    ID   : " + patient.getPatientId());
            System.out.println("    Name : " + patient.getName());
            System.out.println("    Status: " + patient.getStatus());
        } catch (NumberFormatException e) {
            System.out.println("\n  ✗ Invalid age. Please enter a valid number.");
        } catch (HospitalException e) {
            System.out.println("\n  ✗ Registration failed: " + e.getMessage());
        }
        System.out.println();
    }

    /**
     * Admits a previously registered patient by their ID.
     *
     * @param scanner the active {@link Scanner} for console input
     */
    private void handleAdmitPatient(Scanner scanner) {
        System.out.println("  ── Admit Patient ──\n");
        try {
            System.out.print("  Patient ID: ");
            String patientId = scanner.nextLine().trim();

            admissionService.admitPatient(patientId);
            System.out.println("\n  ✓ Patient " + patientId + " admitted successfully!");
        } catch (PatientNotFoundException e) {
            System.out.println("\n  ✗ Patient not found: " + e.getMessage());
        } catch (AlreadyAdmittedException e) {
            System.out.println("\n  ✗ Already admitted: " + e.getMessage());
        } catch (InvalidPatientStateException e) {
            System.out.println("\n  ✗ Invalid state transition: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("\n  ✗ Error: " + e.getMessage());
        }
        System.out.println();
    }

    /**
     * Transitions an admitted patient to the {@code UNDER_TREATMENT} state.
     *
     * @param scanner the active {@link Scanner} for console input
     */
    private void handleStartTreatment(Scanner scanner) {
        System.out.println("  ── Start Treatment ──\n");
        try {
            System.out.print("  Patient ID: ");
            String patientId = scanner.nextLine().trim();

            admissionService.startTreatment(patientId);
            System.out.println("\n  ✓ Treatment started for patient " + patientId + ".");
        } catch (PatientNotFoundException e) {
            System.out.println("\n  ✗ Patient not found: " + e.getMessage());
        } catch (InvalidPatientStateException e) {
            System.out.println("\n  ✗ Invalid state transition: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("\n  ✗ Error: " + e.getMessage());
        }
        System.out.println();
    }

    /**
     * Discharges a patient who is currently admitted or under treatment.
     *
     * @param scanner the active {@link Scanner} for console input
     */
    private void handleDischargePatient(Scanner scanner) {
        System.out.println("  ── Discharge Patient ──\n");
        try {
            System.out.print("  Patient ID: ");
            String patientId = scanner.nextLine().trim();

            admissionService.dischargePatient(patientId);
            System.out.println("\n  ✓ Patient " + patientId + " discharged successfully.");
        } catch (PatientNotFoundException e) {
            System.out.println("\n  ✗ Patient not found: " + e.getMessage());
        } catch (InvalidPatientStateException e) {
            System.out.println("\n  ✗ Invalid state transition: " + e.getMessage());
        } catch (HospitalException e) {
            System.out.println("\n  ✗ Discharge failed: " + e.getMessage());
        }
        System.out.println();
    }

    /**
     * Assigns a doctor to a patient using both IDs.
     *
     * @param scanner the active {@link Scanner} for console input
     */
    private void handleAssignDoctor(Scanner scanner) {
        System.out.println("  ── Assign Doctor to Patient ──\n");
        try {
            System.out.print("  Patient ID: ");
            String patientId = scanner.nextLine().trim();

            System.out.print("  Doctor ID : ");
            String doctorId = scanner.nextLine().trim();

            admissionService.assignDoctor(patientId, doctorId);
            System.out.println("\n  ✓ Doctor " + doctorId + " assigned to patient " + patientId + ".");
        } catch (PatientNotFoundException e) {
            System.out.println("\n  ✗ Patient not found: " + e.getMessage());
        } catch (StaffAssignmentException e) {
            System.out.println("\n  ✗ Assignment failed: " + e.getMessage());
        } catch (HospitalException e) {
            System.out.println("\n  ✗ Error: " + e.getMessage());
        }
        System.out.println();
    }

    /**
     * Assigns a ward to a patient using both IDs.
     *
     * @param scanner the active {@link Scanner} for console input
     */
    private void handleAssignWard(Scanner scanner) {
        System.out.println("  ── Assign Ward to Patient ──\n");
        try {
            System.out.print("  Patient ID: ");
            String patientId = scanner.nextLine().trim();

            System.out.print("  Ward ID   : ");
            String wardId = scanner.nextLine().trim();

            admissionService.assignWard(patientId, wardId);
            System.out.println("\n  ✓ Ward " + wardId + " assigned to patient " + patientId + ".");
        } catch (PatientNotFoundException e) {
            System.out.println("\n  ✗ Patient not found: " + e.getMessage());
        } catch (HospitalException e) {
            System.out.println("\n  ✗ Ward assignment failed: " + e.getMessage());
        }
        System.out.println();
    }

    /**
     * Displays the details of a single patient by their ID.
     *
     * @param scanner the active {@link Scanner} for console input
     */
    private void handleViewPatient(Scanner scanner) {
        System.out.println("  ── View Patient Details ──\n");
        try {
            System.out.print("  Patient ID: ");
            String patientId = scanner.nextLine().trim();

            Patient patient = admissionService.getPatient(patientId);
            System.out.println("\n  " + patient);
        } catch (PatientNotFoundException e) {
            System.out.println("\n  ✗ Patient not found: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("\n  ✗ Error: " + e.getMessage());
        }
        System.out.println();
    }

    /**
     * Displays the audit trail (state-change history) for a patient.
     *
     * @param scanner the active {@link Scanner} for console input
     */
    private void handleViewAuditTrail(Scanner scanner) {
        System.out.println("  ── View Patient Audit Trail ──\n");
        try {
            System.out.print("  Patient ID: ");
            String patientId = scanner.nextLine().trim();

            List<AuditEntry> history = reportService.getPatientAdmissionHistory(patientId);
            if (history == null || history.isEmpty()) {
                System.out.println("\n  ⚠ No audit entries found for patient " + patientId + ".");
            } else {
                System.out.println("\n  Audit Trail for Patient " + patientId + ":");
                System.out.println("  ─────────────────────────────────────────────");
                for (AuditEntry entry : history) {
                    System.out.println("    • " + entry);
                }
            }
        } catch (PatientNotFoundException e) {
            System.out.println("\n  ✗ Patient not found: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("\n  ✗ Error: " + e.getMessage());
        }
        System.out.println();
    }

    // ═════════════════ Staff-Management Handlers ══════════════════════

    /**
     * Guides the user through adding a new doctor to the hospital.
     *
     * @param scanner the active {@link Scanner} for console input
     */
    private void handleAddDoctor(Scanner scanner) {
        System.out.println("  ── Add New Doctor ──\n");
        try {
            System.out.print("  Name           : ");
            String name = scanner.nextLine().trim();

            System.out.print("  Age            : ");
            int age = Integer.parseInt(scanner.nextLine().trim());

            Gender gender = readGender(scanner);

            System.out.print("  Contact        : ");
            String contact = scanner.nextLine().trim();

            System.out.print("  Specialization : ");
            String specialization = scanner.nextLine().trim();

            Doctor doctor = staffService.addDoctor(name, age, gender, contact, specialization);
            System.out.println("\n  ✓ Doctor added successfully!");
            System.out.println("    ID             : " + doctor.getStaffId());
            System.out.println("    Name           : " + doctor.getName());
            System.out.println("    Specialization : " + doctor.getSpecialization());
        } catch (NumberFormatException e) {
            System.out.println("\n  ✗ Invalid age. Please enter a valid number.");
        } catch (HospitalException e) {
            System.out.println("\n  ✗ Failed to add doctor: " + e.getMessage());
        }
        System.out.println();
    }

    /**
     * Guides the user through adding a new nurse to the hospital.
     *
     * @param scanner the active {@link Scanner} for console input
     */
    private void handleAddNurse(Scanner scanner) {
        System.out.println("  ── Add New Nurse ──\n");
        try {
            System.out.print("  Name       : ");
            String name = scanner.nextLine().trim();

            System.out.print("  Age        : ");
            int age = Integer.parseInt(scanner.nextLine().trim());

            Gender gender = readGender(scanner);

            System.out.print("  Contact    : ");
            String contact = scanner.nextLine().trim();

            System.out.print("  Shift Type (DAY/NIGHT/ROTATING): ");
            String shiftType = scanner.nextLine().trim();

            Nurse nurse = staffService.addNurse(name, age, gender, contact, shiftType);
            System.out.println("\n  ✓ Nurse added successfully!");
            System.out.println("    ID        : " + nurse.getStaffId());
            System.out.println("    Name      : " + nurse.getName());
            System.out.println("    Shift     : " + nurse.getShiftType());
        } catch (NumberFormatException e) {
            System.out.println("\n  ✗ Invalid age. Please enter a valid number.");
        } catch (HospitalException e) {
            System.out.println("\n  ✗ Failed to add nurse: " + e.getMessage());
        }
        System.out.println();
    }

    /**
     * Assigns an existing staff member to a department.
     *
     * @param scanner the active {@link Scanner} for console input
     */
    private void handleAssignDepartment(Scanner scanner) {
        System.out.println("  ── Assign Staff to Department ──\n");
        try {
            System.out.print("  Staff ID      : ");
            String staffId = scanner.nextLine().trim();

            System.out.print("  Department ID : ");
            String deptId = scanner.nextLine().trim();

            staffService.assignDepartment(staffId, deptId);
            System.out.println("\n  ✓ Staff " + staffId + " assigned to department " + deptId + ".");
        } catch (StaffAssignmentException e) {
            System.out.println("\n  ✗ Assignment failed: " + e.getMessage());
        } catch (DepartmentException e) {
            System.out.println("\n  ✗ Department error: " + e.getMessage());
        } catch (HospitalException e) {
            System.out.println("\n  ✗ Error: " + e.getMessage());
        }
        System.out.println();
    }

    /**
     * Displays the details of a single staff member by their ID.
     *
     * @param scanner the active {@link Scanner} for console input
     */
    private void handleViewStaff(Scanner scanner) {
        System.out.println("  ── View Staff Details ──\n");
        try {
            System.out.print("  Staff ID: ");
            String staffId = scanner.nextLine().trim();

            Staff staff = staffService.getStaff(staffId);
            System.out.println("\n  " + staff);
        } catch (HospitalException e) {
            System.out.println("\n  ✗ Staff not found: " + e.getMessage());
        }
        System.out.println();
    }

    // ═══════════════════════ Report Handlers ══════════════════════════

    /**
     * Generates and prints the full hospital report produced by
     * {@link ReportService#generateFullReport()}.
     */
    private void handleGenerateReport() {
        System.out.println("  ── Full Hospital Report ──\n");
        try {
            String report = reportService.generateFullReport();
            System.out.println(report);
        } catch (Exception e) {
            System.out.println("  ✗ Report generation failed: " + e.getMessage());
        }
        System.out.println();
    }

    /**
     * Lists all patients currently tracked by the hospital.
     */
    private void handleViewAllPatients() {
        System.out.println("  ── All Patients ──\n");
        try {
            int total = reportService.getTotalPatientCount();
            List<Patient> admitted = reportService.getAdmittedPatients();
            List<Patient> discharged = reportService.getDischargedPatients();

            System.out.println("  Total patients    : " + total);
            System.out.println("  Currently admitted: " + admitted.size());
            System.out.println("  Discharged        : " + discharged.size());

            if (!admitted.isEmpty()) {
                System.out.println("\n  ── Admitted Patients ──");
                for (Patient p : admitted) {
                    System.out.println("    • " + p);
                }
            }

            if (!discharged.isEmpty()) {
                System.out.println("\n  ── Discharged Patients ──");
                for (Patient p : discharged) {
                    System.out.println("    • " + p);
                }
            }

            if (total == 0) {
                System.out.println("\n  ⚠ No patients registered yet.");
            }
        } catch (Exception e) {
            System.out.println("  ✗ Error: " + e.getMessage());
        }
        System.out.println();
    }

    /**
     * Lists all staff members (doctors and nurses) in the hospital.
     */
    private void handleViewAllStaff() {
        System.out.println("  ── All Staff ──\n");
        try {
            List<Doctor> doctors = reportService.getAllDoctors();
            List<Nurse> nurses = reportService.getAllNurses();

            System.out.println("  Doctors : " + doctors.size());
            System.out.println("  Nurses  : " + nurses.size());

            if (!doctors.isEmpty()) {
                System.out.println("\n  ── Doctors ──");
                for (Doctor d : doctors) {
                    System.out.println("    • " + d);
                }
            }

            if (!nurses.isEmpty()) {
                System.out.println("\n  ── Nurses ──");
                for (Nurse n : nurses) {
                    System.out.println("    • " + n);
                }
            }

            if (doctors.isEmpty() && nurses.isEmpty()) {
                System.out.println("\n  ⚠ No staff members added yet.");
            }
        } catch (Exception e) {
            System.out.println("  ✗ Error: " + e.getMessage());
        }
        System.out.println();
    }

    /**
     * Displays all wards with their bed capacities and availability.
     */
    private void handleViewAllWards() {
        System.out.println("  ── All Wards ──\n");
        try {
            int totalBeds = reportService.getTotalAvailableBeds();
            List<Ward> wards = hospital.getAllWards();

            System.out.println("  Total available beds: " + totalBeds);
            System.out.println();

            if (wards == null || wards.isEmpty()) {
                System.out.println("  ⚠ No wards configured.");
            } else {
                System.out.println(String.format("  %-5s %-20s %-10s %-10s",
                        "ID", "Name", "Capacity", "Available"));
                System.out.println("  " + "─".repeat(50));
                for (Ward w : wards) {
                    System.out.println(String.format("  %-5s %-20s %-10d %-10d",
                            w.getWardId(), w.getWardName(), w.getTotalBeds(), w.getAvailableBeds()));
                }
            }
        } catch (Exception e) {
            System.out.println("  ✗ Error: " + e.getMessage());
        }
        System.out.println();
    }

    /**
     * Displays all departments along with their staff counts.
     */
    private void handleViewAllDepartments() {
        System.out.println("  ── All Departments ──\n");
        try {
            List<Department> departments = hospital.getAllDepartments();

            if (departments == null || departments.isEmpty()) {
                System.out.println("  ⚠ No departments configured.");
            } else {
                System.out.println(String.format("  %-5s %-25s %-12s",
                        "ID", "Name", "Staff Count"));
                System.out.println("  " + "─".repeat(45));
                for (Department dept : departments) {
                    System.out.println(String.format("  %-5s %-25s %-12d",
                            dept.getDepartmentId(), dept.getDepartmentName(), dept.getStaffCount()));
                }
            }

            // Department-wise staff count from ReportService
            java.util.Map<String, Long> deptCounts = reportService.getDepartmentWiseStaffCount();
            if (deptCounts != null && !deptCounts.isEmpty()) {
                System.out.println("\n  ── Department-Wise Staff Breakdown ──");
                deptCounts.forEach((name, count) ->
                    System.out.println(String.format("    %-25s : %d staff", name, count)));
            }
        } catch (Exception e) {
            System.out.println("  ✗ Error: " + e.getMessage());
        }
        System.out.println();
    }

    // ═══════════════════════ Entry Point ═══════════════════════════════

    /**
     * Application entry point.
     *
     * @param args command-line arguments (not used)
     */
    public static void main(String[] args) {
        new Main().run();
    }
}
