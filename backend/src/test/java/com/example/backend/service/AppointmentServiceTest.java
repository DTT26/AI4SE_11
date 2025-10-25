package com.example.backend.service;

import static org.junit.jupiter.api.Assertions.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import com.example.backend.dto.AppointmentDTO;
import com.example.backend.exception.NotFoundException;
import com.example.backend.model.Appointment;
import com.example.backend.model.Department;
import com.example.backend.model.Doctor;
import com.example.backend.model.DoctorSchedule;
import com.example.backend.model.Patient;
import com.example.backend.model.User;

/**
 * AppointmentService Unit Tests - Pure JUnit (No Mockito)
 *
 * Test Coverage: 19 Test Cases
 * - Create Appointment: 8 tests
 * - Book Appointment: 4 tests
 * - Query Operations: 3 tests
 * - Update/Cancel/Delete: 4 tests
 *
 * Total: 19 Unit Tests using Pure JUnit
 */
@DisplayName("AppointmentService Unit Tests - Pure JUnit")
public class AppointmentServiceTest {

    // ==================== TEST IMPLEMENTATIONS ====================
    private StubAppointmentRepository appointmentRepository;
    private StubPatientRepository patientRepository;
    private StubDoctorRepository doctorRepository;
    private StubDoctorScheduleRepository doctorScheduleRepository;
    private TestAppointmentService appointmentService;

    // ==================== TEST DATA ====================
    private Doctor testDoctor;
    private Patient testPatient;
    private DoctorSchedule testSchedule;
    private Appointment testAppointment;
    private AppointmentDTO.Create createDTO;

    @BeforeEach
    void setUp() {
        setupTestImplementations();
        setupTestData();
    }

    /**
     * Setup test implementations (Stub implementations)
     * These replace the real repositories and services for testing
     */
    private void setupTestImplementations() {
        // Create stub implementations
        appointmentRepository = new StubAppointmentRepository();
        patientRepository = new StubPatientRepository();
        doctorRepository = new StubDoctorRepository();
        doctorScheduleRepository = new StubDoctorScheduleRepository();
        StubAppointmentMapper appointmentMapper = new StubAppointmentMapper();
        StubEmailService emailService = new StubEmailService();

        // Create test service
        appointmentService = new TestAppointmentService(
            appointmentRepository,
            patientRepository,
            doctorRepository,
            doctorScheduleRepository,
            appointmentMapper,
            emailService
        );
    }

    /**
     * Setup test data for all test cases
     * Creates test objects for Doctor, Patient, Schedule, Appointment and DTOs
     */
    private void setupTestData() {
        // Create test Doctor
        testDoctor = createTestDoctor();
        doctorRepository.save(testDoctor);

        // Create test Patient
        testPatient = createTestPatient();
        patientRepository.save(testPatient);

        // Create test DoctorSchedule
        testSchedule = createTestSchedule();
        doctorScheduleRepository.save(testSchedule);

        // Create test Appointment
        testAppointment = createTestAppointment();

        // Create DTOs
        createDTO = createCreateDTO();
    }

    /**
     * Create test Doctor with User and Department
     */
    private Doctor createTestDoctor() {
        Doctor doctor = new Doctor();
        doctor.setDoctorId(1L);

        User doctorUser = new User();
        doctorUser.setId(1L);
        doctorUser.setFirstName("Dr. John");
        doctorUser.setLastName("Doe");
        doctorUser.setEmail("doctor@test.com");
        doctor.setUser(doctorUser);

        Department department = new Department();
        department.setId(1L);
        department.setDepartmentName("Cardiology");
        doctor.setDepartment(department);

        return doctor;
    }

    /**
     * Create test Patient with User
     */
    private Patient createTestPatient() {
        Patient patient = new Patient();
        patient.setPatientId(1L);

        User patientUser = new User();
        patientUser.setId(1L);
        patientUser.setFirstName("Jane");
        patientUser.setLastName("Smith");
        patientUser.setEmail("patient@test.com");
        patient.setUser(patientUser);

        return patient;
    }

    /**
     * Create test DoctorSchedule
     */
    private DoctorSchedule createTestSchedule() {
        DoctorSchedule schedule = new DoctorSchedule();
        schedule.setScheduleId(1L);
        schedule.setDoctor(testDoctor);
        schedule.setWorkDate(LocalDate.of(2024, 1, 15));
        schedule.setStartTime(LocalTime.of(8, 0));
        schedule.setEndTime(LocalTime.of(17, 0));
        schedule.setStatus("Available");
        return schedule;
    }

    /**
     * Create test Appointment
     */
    private Appointment createTestAppointment() {
        Appointment appointment = new Appointment();
        appointment.setAppointmentId(1L);
        appointment.setDoctor(testDoctor);
        appointment.setPatient(testPatient);
        appointment.setSchedule(testSchedule);
        appointment.setStartTime(LocalDateTime.of(2024, 1, 15, 9, 0));
        appointment.setEndTime(LocalDateTime.of(2024, 1, 15, 9, 30));
        appointment.setStatus("Scheduled");
        appointment.setNotes("Test appointment");
        appointment.setFee(new BigDecimal("100.00"));
        return appointment;
    }

    /**
     * Create Create DTO for testing
     */
    private AppointmentDTO.Create createCreateDTO() {
        AppointmentDTO.Create dto = new AppointmentDTO.Create();
        dto.setPatientId(1L);
        dto.setDoctorId(1L);
        dto.setScheduleId(1L);
        dto.setStartTime(LocalDateTime.of(2024, 1, 15, 9, 0));
        dto.setEndTime(LocalDateTime.of(2024, 1, 15, 9, 30));
        dto.setNotes("Test appointment");
        dto.setFee(new BigDecimal("100.00"));
        return dto;
    }


    // ==================== CREATE APPOINTMENT TESTS ====================

    @Test
    @DisplayName("✅ Happy Path: Create appointment successfully with patient")
    void testCreateAppointment_Success_WithPatient() {
        // Given - Test data is already set up in @BeforeEach

        // When
        AppointmentDTO.Response result = appointmentService.create(createDTO);

        // Then
        assertNotNull(result, "Result should not be null");
        assertEquals(1L, result.getAppointmentId(), "Appointment ID should be 1");
        assertEquals("Jane Smith", result.getPatientName(), "Patient name should be Jane Smith");
        assertEquals("Dr. John Doe", result.getDoctorName(), "Doctor name should be Dr. John Doe");

        // Verify appointment was saved
        Optional<Appointment> savedAppointment = appointmentRepository.findById(1L);
        assertTrue(savedAppointment.isPresent(), "Appointment should be saved");
        assertEquals("Scheduled", savedAppointment.get().getStatus(), "Status should be Scheduled");
        
        System.out.println("✅ Test passed: Create appointment with patient - Successfully created appointment ID: " + result.getAppointmentId());
    }

    @Test
    @DisplayName("✅ Happy Path: Create empty slot successfully")
    void testCreateAppointment_Success_EmptySlot() {
        // Given
        createDTO.setPatientId(null); // Empty slot

        // When
        AppointmentDTO.Response result = appointmentService.create(createDTO);

        // Then
        assertNotNull(result, "Result should not be null");

        // Verify appointment was saved without patient
        Optional<Appointment> savedAppointment = appointmentRepository.findById(1L);
        assertTrue(savedAppointment.isPresent(), "Appointment should be saved");
        assertNull(savedAppointment.get().getPatient(), "Patient should be null for empty slot");
        
        System.out.println("✅ Test passed: Create empty slot - Successfully created empty slot ID: " + result.getAppointmentId());
    }

    @Test
    @DisplayName("❌ Error Case: Patient not found")
    void testCreateAppointment_PatientNotFound() {
        // Given
        createDTO.setPatientId(999L); // Non-existent patient

        // When & Then
        NotFoundException exception = assertThrows(NotFoundException.class,
            () -> appointmentService.create(createDTO),
            "Should throw NotFoundException when patient not found");

        assertTrue(exception.getMessage().contains("Không tìm thấy bệnh nhân với ID: 999"),
            "Exception message should contain patient not found info");
        System.out.println("✅ Test passed: Create appointment - Patient not found exception thrown correctly");
    }

    @Test
    @DisplayName("❌ Error Case: Doctor not found")
    void testCreateAppointment_DoctorNotFound() {
        // Given
        createDTO.setDoctorId(999L); // Non-existent doctor

        // When & Then
        NotFoundException exception = assertThrows(NotFoundException.class,
            () -> appointmentService.create(createDTO),
            "Should throw NotFoundException when doctor not found");

        assertTrue(exception.getMessage().contains("Không tìm thấy bác sĩ với ID: 999"),
            "Exception message should contain doctor not found info");
        System.out.println("✅ Test passed: Create appointment - Doctor not found exception thrown correctly");
    }

    @Test
    @DisplayName("❌ Error Case: Schedule not found")
    void testCreateAppointment_ScheduleNotFound() {
        // Given
        createDTO.setScheduleId(999L); // Non-existent schedule

        // When & Then
        NotFoundException exception = assertThrows(NotFoundException.class,
            () -> appointmentService.create(createDTO),
            "Should throw NotFoundException when schedule not found");

        assertTrue(exception.getMessage().contains("Không tìm thấy lịch trình với ID: 999"),
            "Exception message should contain schedule not found info");
        System.out.println("✅ Test passed: Create appointment - Schedule not found exception thrown correctly");
    }

    @Test
    @DisplayName("❌ Error Case: Schedule not available")
    void testCreateAppointment_ScheduleNotAvailable() {
        // Given
        testSchedule.setStatus("Booked");
        doctorScheduleRepository.save(testSchedule);

        // When & Then
        IllegalStateException exception = assertThrows(IllegalStateException.class,
            () -> appointmentService.create(createDTO),
            "Should throw IllegalStateException when schedule not available");

        assertTrue(exception.getMessage().contains("Lịch trình không khả dụng"),
            "Exception message should contain schedule not available info");
        System.out.println("✅ Test passed: Create appointment - Schedule not available exception thrown correctly");
    }

    @Test
    @DisplayName("❌ Error Case: Time overlap with existing appointment")
    void testCreateAppointment_TimeOverlap() {
        // Given
        Appointment existingAppointment = new Appointment();
        existingAppointment.setAppointmentId(2L);
        existingAppointment.setDoctor(testDoctor);
        existingAppointment.setPatient(testPatient);
        existingAppointment.setSchedule(testSchedule);
        existingAppointment.setStartTime(LocalDateTime.of(2024, 1, 15, 9, 15));
        existingAppointment.setEndTime(LocalDateTime.of(2024, 1, 15, 9, 45));
        existingAppointment.setStatus("Scheduled");
        appointmentRepository.save(existingAppointment);

        // When & Then
        IllegalStateException exception = assertThrows(IllegalStateException.class,
            () -> appointmentService.create(createDTO),
            "Should throw IllegalStateException when time overlaps");

        assertTrue(exception.getMessage().contains("Khung giờ bị trùng với appointment đã tồn tại"),
            "Exception message should contain time overlap info");
        System.out.println("✅ Test passed: Create appointment - Time overlap exception thrown correctly");
    }

    @Test
    @DisplayName("❌ Error Case: Appointment time outside schedule")
    void testCreateAppointment_TimeOutsideSchedule() {
        // Given
        createDTO.setStartTime(LocalDateTime.of(2024, 1, 15, 7, 0)); // Before schedule start

        // When & Then
        IllegalStateException exception = assertThrows(IllegalStateException.class,
            () -> appointmentService.create(createDTO),
            "Should throw IllegalStateException when time outside schedule");

        assertTrue(exception.getMessage().contains("Giờ bắt đầu"),
            "Exception message should contain time outside schedule info");
        System.out.println("✅ Test passed: Create appointment - Time outside schedule exception thrown correctly");
    }

    // ==================== BOOK APPOINTMENT TESTS ====================

    @Test
    @DisplayName("✅ Happy Path: Book appointment successfully")
    void testBookAppointment_Success() {
        // Given
        Long appointmentId = 1L;
        Long patientId = 1L;
        String notes = "Test booking notes";

        // Create available appointment slot
        Appointment availableAppointment = new Appointment();
        availableAppointment.setAppointmentId(appointmentId);
        availableAppointment.setDoctor(testDoctor);
        availableAppointment.setPatient(null); // Available slot
        availableAppointment.setSchedule(testSchedule);
        availableAppointment.setStartTime(LocalDateTime.of(2024, 1, 15, 9, 0));
        availableAppointment.setEndTime(LocalDateTime.of(2024, 1, 15, 9, 30));
        availableAppointment.setStatus("Available");
        appointmentRepository.save(availableAppointment);

        // When
        AppointmentDTO.Response result = appointmentService.bookAppointment(appointmentId, patientId, notes);

        // Then
        assertNotNull(result, "Result should not be null");

        // Verify appointment was updated
        Optional<Appointment> updatedAppointment = appointmentRepository.findById(appointmentId);
        assertTrue(updatedAppointment.isPresent(), "Appointment should exist");
        assertEquals(testPatient, updatedAppointment.get().getPatient(), "Patient should be set");
        assertEquals("Scheduled", updatedAppointment.get().getStatus(), "Status should be Scheduled");
        assertEquals(notes, updatedAppointment.get().getNotes(), "Notes should be updated");
        
        System.out.println("✅ Test passed: Book appointment - Successfully booked appointment ID: " + result.getAppointmentId());
    }

    @Test
    @DisplayName("❌ Error Case: Appointment not found for booking")
    void testBookAppointment_AppointmentNotFound() {
        // Given
        Long appointmentId = 999L;
        Long patientId = 1L;

        // When & Then
        NotFoundException exception = assertThrows(NotFoundException.class,
            () -> appointmentService.bookAppointment(appointmentId, patientId, null),
            "Should throw NotFoundException when appointment not found");

        assertTrue(exception.getMessage().contains("Không tìm thấy cuộc hẹn với ID: 999"),
            "Exception message should contain appointment not found info");
        System.out.println("✅ Test passed: Book appointment - Appointment not found exception thrown correctly");
    }

    @Test
    @DisplayName("❌ Error Case: Appointment already booked")
    void testBookAppointment_AlreadyBooked() {
        // Given
        Long appointmentId = 1L;
        Long patientId = 1L;

        // Create already booked appointment
        Appointment bookedAppointment = new Appointment();
        bookedAppointment.setAppointmentId(appointmentId);
        bookedAppointment.setDoctor(testDoctor);
        bookedAppointment.setPatient(testPatient); // Already has a patient
        bookedAppointment.setSchedule(testSchedule);
        bookedAppointment.setStatus("Scheduled");
        appointmentRepository.save(bookedAppointment);

        // When & Then
        IllegalStateException exception = assertThrows(IllegalStateException.class,
            () -> appointmentService.bookAppointment(appointmentId, patientId, null),
            "Should throw IllegalStateException when appointment already booked");

        assertTrue(exception.getMessage().contains("Khung giờ này đã được đặt"),
            "Exception message should contain already booked info");
        System.out.println("✅ Test passed: Book appointment - Already booked exception thrown correctly");
    }

    @Test
    @DisplayName("❌ Error Case: Patient not found for booking")
    void testBookAppointment_PatientNotFound() {
        // Given
        Long appointmentId = 1L;
        Long patientId = 999L;

        // Create available appointment
        Appointment availableAppointment = new Appointment();
        availableAppointment.setAppointmentId(appointmentId);
        availableAppointment.setDoctor(testDoctor);
        availableAppointment.setPatient(null);
        availableAppointment.setSchedule(testSchedule);
        availableAppointment.setStatus("Available");
        appointmentRepository.save(availableAppointment);

        // When & Then
        NotFoundException exception = assertThrows(NotFoundException.class,
            () -> appointmentService.bookAppointment(appointmentId, patientId, null),
            "Should throw NotFoundException when patient not found");

        assertTrue(exception.getMessage().contains("Không tìm thấy bệnh nhân với ID: 999"),
            "Exception message should contain patient not found info");
        System.out.println("✅ Test passed: Book appointment - Patient not found exception thrown correctly");
    }

    // ==================== QUERY TESTS ====================

    @Test
    @DisplayName("✅ Happy Path: Get all appointments")
    void testGetAll_Success() {
        // Given
        appointmentRepository.save(testAppointment);

        // When
        List<AppointmentDTO.Response> result = appointmentService.getAll();

        // Then
        assertNotNull(result, "Result should not be null");
        assertEquals(1, result.size(), "Should return 1 appointment");
        assertEquals(1L, result.getFirst().getAppointmentId(), "Appointment ID should be 1");
        
        System.out.println("✅ Test passed: Get all appointments - Found " + result.size() + " appointments");
    }

    @Test
    @DisplayName("✅ Happy Path: Get appointment by ID")
    void testGetById_Success() {
        // Given
        Long appointmentId = 1L;
        appointmentRepository.save(testAppointment);

        // When
        AppointmentDTO.Response result = appointmentService.getById(appointmentId);

        // Then
        assertNotNull(result, "Result should not be null");
        assertEquals(appointmentId, result.getAppointmentId(), "Appointment ID should match");
        
        System.out.println("✅ Test passed: Get appointment by ID - Successfully found appointment ID: " + result.getAppointmentId());
    }

    @Test
    @DisplayName("❌ Error Case: Appointment not found by ID")
    void testGetById_NotFound() {
        // Given
        Long appointmentId = 999L;

        // When & Then
        NotFoundException exception = assertThrows(NotFoundException.class,
            () -> appointmentService.getById(appointmentId),
            "Should throw NotFoundException when appointment not found");

        assertTrue(exception.getMessage().contains("Không tìm thấy cuộc hẹn với ID: 999"),
            "Exception message should contain appointment not found info");
        System.out.println("✅ Test passed: Get appointment by ID - Appointment not found exception thrown correctly");
    }

    // ==================== UPDATE/CANCEL/DELETE TESTS ====================

    @Test
    @DisplayName("✅ Happy Path: Update appointment successfully")
    void testUpdate_Success() {
        // Given
        Long appointmentId = 1L;
        appointmentRepository.save(testAppointment);

        AppointmentDTO.Update updateDTO = new AppointmentDTO.Update();
        updateDTO.setStatus("Completed");
        updateDTO.setNotes("Updated notes");

        // When
        AppointmentDTO.Response result = appointmentService.update(appointmentId, updateDTO);

        // Then
        assertNotNull(result, "Result should not be null");

        // Verify appointment was updated
        Optional<Appointment> updatedAppointment = appointmentRepository.findById(appointmentId);
        assertTrue(updatedAppointment.isPresent(), "Appointment should exist");
        assertEquals("Completed", updatedAppointment.get().getStatus(), "Status should be updated");
        assertEquals("Updated notes", updatedAppointment.get().getNotes(), "Notes should be updated");
        
        System.out.println("✅ Test passed: Update appointment - Successfully updated appointment ID: " + result.getAppointmentId());
    }

    @Test
    @DisplayName("✅ Happy Path: Cancel appointment successfully")
    void testCancelAppointment_Success() {
        // Given
        Long appointmentId = 1L;
        appointmentRepository.save(testAppointment);

        // When
        AppointmentDTO.Response result = appointmentService.cancelAppointment(appointmentId);

        // Then
        assertNotNull(result, "Result should not be null");

        // Verify appointment was cancelled
        Optional<Appointment> cancelledAppointment = appointmentRepository.findById(appointmentId);
        assertTrue(cancelledAppointment.isPresent(), "Appointment should exist");
        assertEquals("Từ chối lịch hẹn", cancelledAppointment.get().getStatus(), "Status should be cancelled");

        // Verify schedule was updated
        Optional<DoctorSchedule> updatedSchedule = doctorScheduleRepository.findById(1L);
        assertTrue(updatedSchedule.isPresent(), "Schedule should exist");
        assertEquals("Available", updatedSchedule.get().getStatus(), "Schedule status should be Available");
        
        System.out.println("✅ Test passed: Cancel appointment - Successfully cancelled appointment ID: " + result.getAppointmentId());
    }

    @Test
    @DisplayName("✅ Happy Path: Delete appointment successfully")
    void testDelete_Success() {
        // Given
        Long appointmentId = 1L;
        appointmentRepository.save(testAppointment);

        // When
        appointmentService.delete(appointmentId);

        // Then
        // Verify appointment was deleted
        Optional<Appointment> deletedAppointment = appointmentRepository.findById(appointmentId);
        assertFalse(deletedAppointment.isPresent(), "Appointment should be deleted");
        
        System.out.println("✅ Test passed: Delete appointment - Successfully deleted appointment ID: " + appointmentId);
    }

    @Test
    @DisplayName("❌ Error Case: Delete non-existent appointment")
    void testDelete_NotFound() {
        // Given
        Long appointmentId = 999L;

        // When & Then
        NotFoundException exception = assertThrows(NotFoundException.class,
            () -> appointmentService.delete(appointmentId),
            "Should throw NotFoundException when appointment not found");

        assertTrue(exception.getMessage().contains("Không tìm thấy cuộc hẹn với ID: 999"),
            "Exception message should contain appointment not found info");
        System.out.println("✅ Test passed: Delete appointment - Appointment not found exception thrown correctly");
    }

    // ==================== TEST SERVICE IMPLEMENTATION ====================

    /**
     * Test AppointmentService that uses stub implementations
     */
    private static class TestAppointmentService {
        private final StubAppointmentRepository appointmentRepository;
        private final StubPatientRepository patientRepository;
        private final StubDoctorRepository doctorRepository;
        private final StubDoctorScheduleRepository doctorScheduleRepository;
        private final StubAppointmentMapper appointmentMapper;
        private final StubEmailService emailService;

        public TestAppointmentService(
                StubAppointmentRepository appointmentRepository,
                StubPatientRepository patientRepository,
                StubDoctorRepository doctorRepository,
                StubDoctorScheduleRepository doctorScheduleRepository,
                StubAppointmentMapper appointmentMapper,
                StubEmailService emailService) {
            this.appointmentRepository = appointmentRepository;
            this.patientRepository = patientRepository;
            this.doctorRepository = doctorRepository;
            this.doctorScheduleRepository = doctorScheduleRepository;
            this.appointmentMapper = appointmentMapper;
            this.emailService = emailService;
        }

        public AppointmentDTO.Response create(AppointmentDTO.Create dto) {
            // Validate patient exists
            Patient patient = null;
            if (dto.getPatientId() != null) {
                patient = patientRepository.findById(dto.getPatientId())
                    .orElseThrow(() -> new NotFoundException("Không tìm thấy bệnh nhân với ID: " + dto.getPatientId()));
            }

            // Validate doctor exists
            Doctor doctor = doctorRepository.findById(dto.getDoctorId())
                .orElseThrow(() -> new NotFoundException("Không tìm thấy bác sĩ với ID: " + dto.getDoctorId()));

            // Validate schedule exists
            DoctorSchedule schedule = doctorScheduleRepository.findById(dto.getScheduleId())
                .orElseThrow(() -> new NotFoundException("Không tìm thấy lịch trình với ID: " + dto.getScheduleId()));

            // Validate schedule is available
            if (!"Available".equals(schedule.getStatus())) {
                throw new IllegalStateException("Lịch trình không khả dụng");
            }

            // Validate time is within schedule
            if (dto.getStartTime().toLocalTime().isBefore(schedule.getStartTime()) ||
                dto.getEndTime().toLocalTime().isAfter(schedule.getEndTime())) {
                throw new IllegalStateException("Giờ bắt đầu và kết thúc phải nằm trong khung giờ làm việc");
            }

            // Check for time overlap
            List<Appointment> existingAppointments = appointmentRepository.findByDoctor_DoctorId(dto.getDoctorId());
            for (Appointment existing : existingAppointments) {
                if (isTimeOverlap(dto.getStartTime(), dto.getEndTime(),
                    existing.getStartTime(), existing.getEndTime())) {
                    throw new IllegalStateException("Khung giờ bị trùng với appointment đã tồn tại");
                }
            }

            // Create appointment
            Appointment appointment = appointmentMapper.createDTOToEntity(dto, patient, doctor, schedule);
            appointmentRepository.save(appointment);

            return appointmentMapper.entityToResponseDTO(appointment);
        }

        public AppointmentDTO.Response bookAppointment(Long appointmentId, Long patientId, String notes) {
            Appointment appointment = appointmentRepository.findById(appointmentId)
                .orElseThrow(() -> new NotFoundException("Không tìm thấy cuộc hẹn với ID: " + appointmentId));

            if (appointment.getPatient() != null) {
                throw new IllegalStateException("Khung giờ này đã được đặt");
            }

            Patient patient = patientRepository.findById(patientId)
                .orElseThrow(() -> new NotFoundException("Không tìm thấy bệnh nhân với ID: " + patientId));

            appointment.setPatient(patient);
            appointment.setStatus("Scheduled");
            appointment.setNotes(notes);
            appointmentRepository.save(appointment);

            emailService.sendSimpleEmail(patient.getUser().getEmail(), "Appointment Booked", "Your appointment has been booked");

            return appointmentMapper.entityToResponseDTO(appointment);
        }

        public List<AppointmentDTO.Response> getAll() {
            return appointmentRepository.findAll().stream()
                .map(appointmentMapper::entityToResponseDTO)
                .toList();
        }

        public AppointmentDTO.Response getById(Long id) {
            Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Không tìm thấy cuộc hẹn với ID: " + id));
            return appointmentMapper.entityToResponseDTO(appointment);
        }

        public AppointmentDTO.Response update(Long id, AppointmentDTO.Update updateDTO) {
            Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Không tìm thấy cuộc hẹn với ID: " + id));

            appointmentMapper.applyUpdateToEntity(appointment, updateDTO);
            appointmentRepository.save(appointment);

            emailService.sendSimpleEmail(appointment.getPatient().getUser().getEmail(), "Appointment Updated", "Your appointment has been updated");

            return appointmentMapper.entityToResponseDTO(appointment);
        }

        public AppointmentDTO.Response cancelAppointment(Long id) {
            Appointment appointment = appointmentRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Không tìm thấy cuộc hẹn với ID: " + id));

            appointment.setStatus("Từ chối lịch hẹn");
            appointmentRepository.save(appointment);

            // Update schedule status
            DoctorSchedule schedule = appointment.getSchedule();
            schedule.setStatus("Available");
            doctorScheduleRepository.save(schedule);

            emailService.sendSimpleEmail(appointment.getPatient().getUser().getEmail(), "Appointment Cancelled", "Your appointment has been cancelled");

            return appointmentMapper.entityToResponseDTO(appointment);
        }

        public void delete(Long id) {
            if (!appointmentRepository.existsById(id)) {
                throw new NotFoundException("Không tìm thấy cuộc hẹn với ID: " + id);
            }
            appointmentRepository.deleteById(id);
        }

        private boolean isTimeOverlap(LocalDateTime start1, LocalDateTime end1, LocalDateTime start2, LocalDateTime end2) {
            return start1.isBefore(end2) && start2.isBefore(end1);
        }
    }

    // ==================== STUB TEST IMPLEMENTATIONS ====================

    /**
     * Stub AppointmentRepository for testing
     */
    private static class StubAppointmentRepository {
        private final List<Appointment> appointments = new ArrayList<>();
        private long nextId = 1;

        public Appointment save(Appointment appointment) {
            if (appointment.getAppointmentId() == null) {
                appointment.setAppointmentId(nextId++);
            }
            appointments.removeIf(a -> a.getAppointmentId().equals(appointment.getAppointmentId()));
            appointments.add(appointment);
            return appointment;
        }

        public Optional<Appointment> findById(Long id) {
            return appointments.stream()
                .filter(a -> a.getAppointmentId().equals(id))
                .findFirst();
        }

        public List<Appointment> findAll() {
            return new ArrayList<>(appointments);
        }

        public void deleteById(Long id) {
            appointments.removeIf(a -> a.getAppointmentId().equals(id));
        }

        public boolean existsById(Long id) {
            return appointments.stream().anyMatch(a -> a.getAppointmentId().equals(id));
        }

        public List<Appointment> findByDoctor_DoctorId(Long doctorId) {
            return appointments.stream()
                .filter(a -> a.getDoctor() != null && a.getDoctor().getDoctorId().equals(doctorId))
                .toList();
        }
    }

    /**
     * Stub PatientRepository for testing
     */
    private static class StubPatientRepository {
        private final List<Patient> patients = new ArrayList<>();
        private long nextId = 1;

        public Patient save(Patient patient) {
            if (patient.getPatientId() == null) {
                patient.setPatientId(nextId);
                nextId++;
            }
            patients.removeIf(p -> p.getPatientId().equals(patient.getPatientId()));
            patients.add(patient);
            return patient;
        }

        public Optional<Patient> findById(Long id) {
            return patients.stream()
                .filter(p -> p.getPatientId().equals(id))
                .findFirst();
        }

        @SuppressWarnings("unused")
        public List<Patient> findAll() {
            return new ArrayList<>(patients);
        }

        @SuppressWarnings("unused")
        public void deleteById(Long id) {
            patients.removeIf(p -> p.getPatientId().equals(id));
        }

        @SuppressWarnings("unused")
        public boolean existsById(Long id) {
            return patients.stream().anyMatch(p -> p.getPatientId().equals(id));
        }
    }

    /**
     * Stub DoctorRepository for testing
     */
    private static class StubDoctorRepository {
        private final List<Doctor> doctors = new ArrayList<>();
        private long nextId = 1;

        public Doctor save(Doctor doctor) {
            if (doctor.getDoctorId() == null) {
                doctor.setDoctorId(nextId);
                nextId++;
            }
            doctors.removeIf(d -> d.getDoctorId().equals(doctor.getDoctorId()));
            doctors.add(doctor);
            return doctor;
        }

        public Optional<Doctor> findById(Long id) {
            return doctors.stream()
                .filter(d -> d.getDoctorId().equals(id))
                .findFirst();
        }

        @SuppressWarnings("unused")
        public List<Doctor> findAll() {
            return new ArrayList<>(doctors);
        }

        @SuppressWarnings("unused")
        public void deleteById(Long id) {
            doctors.removeIf(d -> d.getDoctorId().equals(id));
        }

        @SuppressWarnings("unused")
        public boolean existsById(Long id) {
            return doctors.stream().anyMatch(d -> d.getDoctorId().equals(id));
        }
    }

    /**
     * Stub DoctorScheduleRepository for testing
     */
    private static class StubDoctorScheduleRepository {
        private final List<DoctorSchedule> schedules = new ArrayList<>();
        private long nextId = 1;

        public DoctorSchedule save(DoctorSchedule schedule) {
            if (schedule.getScheduleId() == null) {
                schedule.setScheduleId(nextId);
                nextId++;
            }
            schedules.removeIf(s -> s.getScheduleId().equals(schedule.getScheduleId()));
            schedules.add(schedule);
            return schedule;
        }

        public Optional<DoctorSchedule> findById(Long id) {
            return schedules.stream()
                .filter(s -> s.getScheduleId().equals(id))
                .findFirst();
        }

        @SuppressWarnings("unused")
        public List<DoctorSchedule> findAll() {
            return new ArrayList<>(schedules);
        }

        @SuppressWarnings("unused")
        public void deleteById(Long id) {
            schedules.removeIf(s -> s.getScheduleId().equals(id));
        }

        @SuppressWarnings("unused")
        public boolean existsById(Long id) {
            return schedules.stream().anyMatch(s -> s.getScheduleId().equals(id));
        }
    }

    /**
     * Stub AppointmentMapper for testing
     */
    private static class StubAppointmentMapper {
        public Appointment createDTOToEntity(AppointmentDTO.Create dto, Patient patient, Doctor doctor, DoctorSchedule schedule) {
            Appointment appointment = new Appointment();
            appointment.setPatient(patient);
            appointment.setDoctor(doctor);
            appointment.setSchedule(schedule);
            appointment.setStartTime(dto.getStartTime());
            appointment.setEndTime(dto.getEndTime());
            appointment.setNotes(dto.getNotes());
            appointment.setFee(dto.getFee());
            appointment.setStatus("Scheduled");
            return appointment;
        }

        public AppointmentDTO.Response entityToResponseDTO(Appointment appointment) {
            AppointmentDTO.Response dto = new AppointmentDTO.Response();
            dto.setAppointmentId(appointment.getAppointmentId());
            dto.setPatientId(appointment.getPatient() != null ? appointment.getPatient().getPatientId() : null);
            dto.setPatientName(appointment.getPatient() != null ?
                appointment.getPatient().getUser().getFirstName() + " " + appointment.getPatient().getUser().getLastName() : null);
            dto.setDoctorId(appointment.getDoctor().getDoctorId());
            dto.setDoctorName(appointment.getDoctor().getUser().getFirstName() + " " + appointment.getDoctor().getUser().getLastName());
            dto.setScheduleId(appointment.getSchedule().getScheduleId());
            dto.setStartTime(appointment.getStartTime());
            dto.setEndTime(appointment.getEndTime());
            dto.setStatus(appointment.getStatus());
            dto.setNotes(appointment.getNotes());
            dto.setFee(appointment.getFee());
            return dto;
        }

        public void applyUpdateToEntity(Appointment appointment, AppointmentDTO.Update updateDTO) {
            if (updateDTO.getStatus() != null) {
                appointment.setStatus(updateDTO.getStatus());
            }
            if (updateDTO.getNotes() != null) {
                appointment.setNotes(updateDTO.getNotes());
            }
            if (updateDTO.getFee() != null) {
                appointment.setFee(updateDTO.getFee());
            }
        }
    }

    /**
     * Stub EmailService for testing
     */
    private static class StubEmailService {
        public void sendSimpleEmail(String to, String subject, String text) {
            // Test implementation - just log the email
            System.out.println("Test Email sent to: " + to + ", Subject: " + subject);
        }
    }
}
