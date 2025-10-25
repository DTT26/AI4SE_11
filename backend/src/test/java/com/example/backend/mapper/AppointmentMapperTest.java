package com.example.backend.mapper;

import com.example.backend.dto.AppointmentDTO;
import com.example.backend.model.Appointment;
import com.example.backend.model.Department;
import com.example.backend.model.Doctor;
import com.example.backend.model.DoctorSchedule;
import com.example.backend.model.Patient;
import com.example.backend.model.Role;
import com.example.backend.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit Tests for AppointmentMapper - Tests mapping logic
 * This will increase coverage by testing mapper layer
 */
class AppointmentMapperTest {

    private AppointmentMapper appointmentMapper;
    private Doctor testDoctor;
    private Patient testPatient;
    private DoctorSchedule testSchedule;

    @BeforeEach
    void setUp() {
        appointmentMapper = new AppointmentMapper();
        setupTestData();
    }

    private void setupTestData() {
        // Create Role
        Role doctorRole = new Role();
        doctorRole.setId(1L);
        doctorRole.setName("DOCTOR");

        Role patientRole = new Role();
        patientRole.setId(2L);
        patientRole.setName("PATIENT");

        // Create Department
        Department department = new Department();
        department.setId(1L);
        department.setDepartmentName("Cardiology");

        // Create User for Doctor
        User doctorUser = new User();
        doctorUser.setId(1L);
        doctorUser.setFirstName("Dr. John");
        doctorUser.setLastName("Doe");
        doctorUser.setEmail("doctor@test.com");
        doctorUser.setRole(doctorRole);

        // Create Doctor
        testDoctor = new Doctor();
        testDoctor.setDoctorId(1L);
        testDoctor.setUser(doctorUser);
        testDoctor.setDepartment(department);

        // Create User for Patient
        User patientUser = new User();
        patientUser.setId(2L);
        patientUser.setFirstName("Jane");
        patientUser.setLastName("Smith");
        patientUser.setEmail("patient@test.com");
        patientUser.setRole(patientRole);

        // Create Patient
        testPatient = new Patient();
        testPatient.setPatientId(1L);
        testPatient.setUser(patientUser);

        // Create Doctor Schedule
        testSchedule = new DoctorSchedule();
        testSchedule.setScheduleId(1L);
        testSchedule.setDoctor(testDoctor);
        testSchedule.setWorkDate(LocalDate.of(2024, 12, 25));
        testSchedule.setStartTime(LocalTime.of(8, 0));
        testSchedule.setEndTime(LocalTime.of(17, 0));
        testSchedule.setStatus("Available");
    }

    @Test
    void testCreateDTOToEntity_WithPatient() {
        // Given
        AppointmentDTO.Create dto = new AppointmentDTO.Create();
        dto.setPatientId(1L);
        dto.setDoctorId(1L);
        dto.setScheduleId(1L);
        dto.setStartTime(LocalDateTime.of(2024, 12, 25, 9, 0));
        dto.setEndTime(LocalDateTime.of(2024, 12, 25, 10, 0));
        dto.setNotes("Test appointment");
        dto.setFee(new BigDecimal("100.00"));

        // When
        Appointment entity = appointmentMapper.createDTOToEntity(dto, testPatient, testDoctor, testSchedule);

        // Then
        assertNotNull(entity);
        assertEquals(testPatient, entity.getPatient());
        assertEquals(testDoctor, entity.getDoctor());
        assertEquals(testSchedule, entity.getSchedule());
        assertEquals(dto.getStartTime(), entity.getStartTime());
        assertEquals(dto.getEndTime(), entity.getEndTime());
        assertEquals(dto.getNotes(), entity.getNotes());
        assertEquals(dto.getFee(), entity.getFee());
        assertEquals("Scheduled", entity.getStatus()); // With patient = Scheduled
    }

    @Test
    void testCreateDTOToEntity_WithoutPatient() {
        // Given
        AppointmentDTO.Create dto = new AppointmentDTO.Create();
        dto.setPatientId(null);
        dto.setDoctorId(1L);
        dto.setScheduleId(1L);
        dto.setStartTime(LocalDateTime.of(2024, 12, 25, 9, 0));
        dto.setEndTime(LocalDateTime.of(2024, 12, 25, 10, 0));
        dto.setNotes("Available slot");
        dto.setFee(new BigDecimal("100.00"));

        // When
        Appointment entity = appointmentMapper.createDTOToEntity(dto, null, testDoctor, testSchedule);

        // Then
        assertNotNull(entity);
        assertNull(entity.getPatient());
        assertEquals(testDoctor, entity.getDoctor());
        assertEquals(testSchedule, entity.getSchedule());
        assertEquals("Available", entity.getStatus()); // Without patient = Available
    }

    @Test
    void testApplyUpdateToEntity_AllFields() {
        // Given
        Appointment entity = new Appointment();
        entity.setStartTime(LocalDateTime.of(2024, 12, 25, 9, 0));
        entity.setEndTime(LocalDateTime.of(2024, 12, 25, 10, 0));
        entity.setStatus("Scheduled");
        entity.setNotes("Original notes");
        entity.setFee(new BigDecimal("100.00"));

        AppointmentDTO.Update dto = new AppointmentDTO.Update();
        dto.setStartTime(LocalDateTime.of(2024, 12, 25, 10, 0));
        dto.setEndTime(LocalDateTime.of(2024, 12, 25, 11, 0));
        dto.setStatus("Completed");
        dto.setNotes("Updated notes");
        dto.setFee(new BigDecimal("120.00"));

        // When
        appointmentMapper.applyUpdateToEntity(entity, dto);

        // Then
        assertEquals(dto.getStartTime(), entity.getStartTime());
        assertEquals(dto.getEndTime(), entity.getEndTime());
        assertEquals(dto.getStatus(), entity.getStatus());
        assertEquals(dto.getNotes(), entity.getNotes());
        assertEquals(dto.getFee(), entity.getFee());
    }

    @Test
    void testApplyUpdateToEntity_PartialFields() {
        // Given
        Appointment entity = new Appointment();
        entity.setStartTime(LocalDateTime.of(2024, 12, 25, 9, 0));
        entity.setEndTime(LocalDateTime.of(2024, 12, 25, 10, 0));
        entity.setStatus("Scheduled");
        entity.setNotes("Original notes");
        entity.setFee(new BigDecimal("100.00"));

        AppointmentDTO.Update dto = new AppointmentDTO.Update();
        dto.setStatus("Completed");
        dto.setNotes("Updated notes");
        // Other fields are null

        // When
        appointmentMapper.applyUpdateToEntity(entity, dto);

        // Then
        assertEquals(LocalDateTime.of(2024, 12, 25, 9, 0), entity.getStartTime()); // Unchanged
        assertEquals(LocalDateTime.of(2024, 12, 25, 10, 0), entity.getEndTime()); // Unchanged
        assertEquals("Completed", entity.getStatus()); // Updated
        assertEquals("Updated notes", entity.getNotes()); // Updated
        assertEquals(new BigDecimal("100.00"), entity.getFee()); // Unchanged
    }

    @Test
    void testEntityToResponseDTO_WithPatient() {
        // Given
        Appointment entity = new Appointment();
        entity.setAppointmentId(1L);
        entity.setPatient(testPatient);
        entity.setDoctor(testDoctor);
        entity.setSchedule(testSchedule);
        entity.setStartTime(LocalDateTime.of(2024, 12, 25, 9, 0));
        entity.setEndTime(LocalDateTime.of(2024, 12, 25, 10, 0));
        entity.setStatus("Scheduled");
        entity.setNotes("Test appointment");
        entity.setFee(new BigDecimal("100.00"));

        // When
        AppointmentDTO.Response dto = appointmentMapper.entityToResponseDTO(entity);

        // Then
        assertNotNull(dto);
        assertEquals(1L, dto.getAppointmentId());
        assertEquals(1L, dto.getPatientId());
        assertEquals("Jane Smith", dto.getPatientName());
        assertEquals(1L, dto.getDoctorId());
        assertEquals("Dr. John Doe", dto.getDoctorName());
        assertEquals(1L, dto.getScheduleId());
        assertEquals(entity.getStartTime(), dto.getStartTime());
        assertEquals(entity.getEndTime(), dto.getEndTime());
        assertEquals("Scheduled", dto.getStatus());
        assertEquals("Test appointment", dto.getNotes());
        assertEquals(new BigDecimal("100.00"), dto.getFee());
    }

    @Test
    void testEntityToResponseDTO_WithoutPatient() {
        // Given
        Appointment entity = new Appointment();
        entity.setAppointmentId(1L);
        entity.setPatient(null);
        entity.setDoctor(testDoctor);
        entity.setSchedule(testSchedule);
        entity.setStartTime(LocalDateTime.of(2024, 12, 25, 9, 0));
        entity.setEndTime(LocalDateTime.of(2024, 12, 25, 10, 0));
        entity.setStatus("Available");
        entity.setNotes("Available slot");
        entity.setFee(new BigDecimal("100.00"));

        // When
        AppointmentDTO.Response dto = appointmentMapper.entityToResponseDTO(entity);

        // Then
        assertNotNull(dto);
        assertEquals(1L, dto.getAppointmentId());
        assertNull(dto.getPatientId());
        assertEquals("", dto.getPatientName());
        assertEquals(1L, dto.getDoctorId());
        assertEquals("Dr. John Doe", dto.getDoctorName());
        assertEquals(1L, dto.getScheduleId());
        assertEquals("Available", dto.getStatus());
    }

    @Test
    void testEntityToResponseDTO_WithNullDoctor() {
        // Given
        Appointment entity = new Appointment();
        entity.setAppointmentId(1L);
        entity.setPatient(testPatient);
        entity.setDoctor(null);
        entity.setSchedule(testSchedule);
        entity.setStartTime(LocalDateTime.of(2024, 12, 25, 9, 0));
        entity.setEndTime(LocalDateTime.of(2024, 12, 25, 10, 0));
        entity.setStatus("Scheduled");
        entity.setNotes("Test appointment");
        entity.setFee(new BigDecimal("100.00"));

        // When
        AppointmentDTO.Response dto = appointmentMapper.entityToResponseDTO(entity);

        // Then
        assertNotNull(dto);
        assertEquals(1L, dto.getAppointmentId());
        assertEquals(1L, dto.getPatientId());
        assertEquals("Jane Smith", dto.getPatientName());
        assertNull(dto.getDoctorId());
        assertEquals("", dto.getDoctorName());
        assertEquals(1L, dto.getScheduleId());
    }

    @Test
    void testEntityToResponseDTO_WithNullSchedule() {
        // Given
        Appointment entity = new Appointment();
        entity.setAppointmentId(1L);
        entity.setPatient(testPatient);
        entity.setDoctor(testDoctor);
        entity.setSchedule(null);
        entity.setStartTime(LocalDateTime.of(2024, 12, 25, 9, 0));
        entity.setEndTime(LocalDateTime.of(2024, 12, 25, 10, 0));
        entity.setStatus("Scheduled");
        entity.setNotes("Test appointment");
        entity.setFee(new BigDecimal("100.00"));

        // When
        AppointmentDTO.Response dto = appointmentMapper.entityToResponseDTO(entity);

        // Then
        assertNotNull(dto);
        assertEquals(1L, dto.getAppointmentId());
        assertEquals(1L, dto.getPatientId());
        assertEquals("Jane Smith", dto.getPatientName());
        assertEquals(1L, dto.getDoctorId());
        assertEquals("Dr. John Doe", dto.getDoctorName());
        assertNull(dto.getScheduleId());
    }
}
