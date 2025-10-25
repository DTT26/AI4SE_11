package com.example.backend.service;

import com.example.backend.dto.AppointmentDTO;
import com.example.backend.exception.NotFoundException;
import com.example.backend.mapper.AppointmentMapper;
import com.example.backend.model.*;
import com.example.backend.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AppointmentServiceMockitoTest {

    @Mock
    private AppointmentRepository appointmentRepository;
    @Mock
    private PatientRepository patientRepository;
    @Mock
    private DoctorRepository doctorRepository;
    @Mock
    private DoctorScheduleRepository doctorScheduleRepository;
    @Mock
    private AppointmentMapper appointmentMapper;
    @Mock
    private EmailService emailService;

    @InjectMocks
    private AppointmentService appointmentService;

    private Doctor testDoctor;
    private Patient testPatient;
    private DoctorSchedule testSchedule;
    private Appointment testAppointment;
    private AppointmentDTO.Create testCreateDTO;
    private AppointmentDTO.Response testResponseDTO;

    @BeforeEach
    void setUp() {
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
        doctorUser.setFirstName("John");
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

        // Create Appointment
        testAppointment = new Appointment();
        testAppointment.setAppointmentId(1L);
        testAppointment.setDoctor(testDoctor);
        testAppointment.setPatient(testPatient);
        testAppointment.setSchedule(testSchedule);
        testAppointment.setStartTime(LocalDateTime.of(2024, 12, 25, 9, 0));
        testAppointment.setEndTime(LocalDateTime.of(2024, 12, 25, 10, 0));
        testAppointment.setStatus("Scheduled");
        testAppointment.setNotes("Test appointment");
        testAppointment.setFee(new BigDecimal("100.00"));

        // Create DTOs
        testCreateDTO = new AppointmentDTO.Create();
        testCreateDTO.setPatientId(1L);
        testCreateDTO.setDoctorId(1L);
        testCreateDTO.setScheduleId(1L);
        testCreateDTO.setStartTime(LocalDateTime.of(2024, 12, 25, 9, 0));
        testCreateDTO.setEndTime(LocalDateTime.of(2024, 12, 25, 10, 0));
        testCreateDTO.setNotes("Test appointment");
        testCreateDTO.setFee(new BigDecimal("100.00"));

        testResponseDTO = new AppointmentDTO.Response();
        testResponseDTO.setAppointmentId(1L);
        testResponseDTO.setPatientName("Jane Smith");
        testResponseDTO.setDoctorName("Dr. John Doe");
        testResponseDTO.setStatus("Scheduled");
        testResponseDTO.setStartTime(LocalDateTime.of(2024, 12, 25, 9, 0));
        testResponseDTO.setEndTime(LocalDateTime.of(2024, 12, 25, 10, 0));
        testResponseDTO.setNotes("Test appointment");
        testResponseDTO.setFee(new BigDecimal("100.00"));
    }

    @Test
    void testCreateAppointment_Success() {
        // Given
        when(patientRepository.findById(1L)).thenReturn(Optional.of(testPatient));
        when(doctorRepository.findById(1L)).thenReturn(Optional.of(testDoctor));
        when(doctorScheduleRepository.findById(1L)).thenReturn(Optional.of(testSchedule));
        when(appointmentRepository.findByDoctor_DoctorId(1L)).thenReturn(Collections.emptyList());
        when(appointmentMapper.createDTOToEntity(any(), any(), any(), any())).thenReturn(testAppointment);
        when(appointmentRepository.save(any(Appointment.class))).thenReturn(testAppointment);
        when(appointmentMapper.entityToResponseDTO(any(Appointment.class))).thenReturn(testResponseDTO);

        // When
        AppointmentDTO.Response result = appointmentService.create(testCreateDTO);

        // Then
        assertNotNull(result);
        assertEquals(1L, result.getAppointmentId());
        assertEquals("Jane Smith", result.getPatientName());
        assertEquals("Dr. John Doe", result.getDoctorName());
        System.out.println("✅ Test passed: Create appointment - Successfully created appointment ID: " + result.getAppointmentId());

        // Verify interactions
        verify(patientRepository).findById(1L);
        verify(doctorRepository).findById(1L);
        verify(doctorScheduleRepository).findById(1L);
        verify(appointmentRepository).findByDoctor_DoctorId(1L);
        verify(appointmentMapper).createDTOToEntity(any(), any(), any(), any());
        verify(appointmentRepository).save(any(Appointment.class));
        verify(appointmentMapper).entityToResponseDTO(any(Appointment.class));
    }

    @Test
    void testCreateAppointment_PatientNotFound() {
        // Given
        testCreateDTO.setPatientId(999L);
        when(patientRepository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        NotFoundException exception = assertThrows(NotFoundException.class,
            () -> appointmentService.create(testCreateDTO),
            "Should throw NotFoundException when patient not found");

        assertTrue(exception.getMessage().contains("Không tìm thấy bệnh nhân với ID: 999"));
        System.out.println("✅ Test passed: Create appointment - Patient not found exception thrown correctly");
    }

    @Test
    void testCreateAppointment_DoctorNotFound() {
        // Given
        testCreateDTO.setDoctorId(999L);
        when(patientRepository.findById(1L)).thenReturn(Optional.of(testPatient));
        when(doctorRepository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        NotFoundException exception = assertThrows(NotFoundException.class,
            () -> appointmentService.create(testCreateDTO),
            "Should throw NotFoundException when doctor not found");

        assertTrue(exception.getMessage().contains("Không tìm thấy bác sĩ với ID: 999"));
        System.out.println("✅ Test passed: Create appointment - Doctor not found exception thrown correctly");
    }

    @Test
    void testCreateAppointment_ScheduleNotFound() {
        // Given
        testCreateDTO.setScheduleId(999L);
        when(patientRepository.findById(1L)).thenReturn(Optional.of(testPatient));
        when(doctorRepository.findById(1L)).thenReturn(Optional.of(testDoctor));
        when(doctorScheduleRepository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        NotFoundException exception = assertThrows(NotFoundException.class,
            () -> appointmentService.create(testCreateDTO),
            "Should throw NotFoundException when schedule not found");

        assertTrue(exception.getMessage().contains("Không tìm thấy lịch trình với ID: 999"));
        System.out.println("✅ Test passed: Create appointment - Schedule not found exception thrown correctly");
    }

    @Test
    void testCreateAppointment_ScheduleNotAvailable() {
        // Given
        testSchedule.setStatus("Booked");
        when(patientRepository.findById(1L)).thenReturn(Optional.of(testPatient));
        when(doctorRepository.findById(1L)).thenReturn(Optional.of(testDoctor));
        when(doctorScheduleRepository.findById(1L)).thenReturn(Optional.of(testSchedule));

        // When & Then
        IllegalStateException exception = assertThrows(IllegalStateException.class,
            () -> appointmentService.create(testCreateDTO),
            "Should throw IllegalStateException when schedule not available");

        assertTrue(exception.getMessage().contains("Lịch trình không khả dụng"));
        System.out.println("✅ Test passed: Create appointment - Schedule not available exception thrown correctly");
    }

    @Test
    void testCreateAppointment_TimeOverlap() {
        // Given
        Appointment existingAppointment = new Appointment();
        existingAppointment.setAppointmentId(2L);
        existingAppointment.setStartTime(LocalDateTime.of(2024, 12, 25, 9, 15));
        existingAppointment.setEndTime(LocalDateTime.of(2024, 12, 25, 9, 45));

        when(patientRepository.findById(1L)).thenReturn(Optional.of(testPatient));
        when(doctorRepository.findById(1L)).thenReturn(Optional.of(testDoctor));
        when(doctorScheduleRepository.findById(1L)).thenReturn(Optional.of(testSchedule));
        when(appointmentRepository.findByDoctor_DoctorId(1L)).thenReturn(List.of(existingAppointment));

        // When & Then
        IllegalStateException exception = assertThrows(IllegalStateException.class,
            () -> appointmentService.create(testCreateDTO),
            "Should throw IllegalStateException when time overlaps");

        assertTrue(exception.getMessage().contains("Khung giờ bị trùng với appointment đã tồn tại"));
        System.out.println("✅ Test passed: Create appointment - Time overlap exception thrown correctly");
    }

    @Test
    void testGetAllAppointments() {
        // Given
        when(appointmentRepository.findAll()).thenReturn(List.of(testAppointment));
        when(appointmentMapper.entityToResponseDTO(any(Appointment.class))).thenReturn(testResponseDTO);

        // When
        List<AppointmentDTO.Response> result = appointmentService.getAll();

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(1L, result.get(0).getAppointmentId());
        System.out.println("✅ Test passed: Get all appointments - Found " + result.size() + " appointments");

        // Verify interactions
        verify(appointmentRepository).findAll();
        verify(appointmentMapper).entityToResponseDTO(any(Appointment.class));
    }

    @Test
    void testGetAppointmentById_Success() {
        // Given
        when(appointmentRepository.findById(1L)).thenReturn(Optional.of(testAppointment));
        when(appointmentMapper.entityToResponseDTO(any(Appointment.class))).thenReturn(testResponseDTO);

        // When
        AppointmentDTO.Response result = appointmentService.getById(1L);

        // Then
        assertNotNull(result);
        assertEquals(1L, result.getAppointmentId());
        System.out.println("✅ Test passed: Get appointment by ID - Successfully found appointment ID: " + result.getAppointmentId());

        // Verify interactions
        verify(appointmentRepository).findById(1L);
        verify(appointmentMapper).entityToResponseDTO(any(Appointment.class));
    }

    @Test
    void testGetAppointmentById_NotFound() {
        // Given
        when(appointmentRepository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        NotFoundException exception = assertThrows(NotFoundException.class,
            () -> appointmentService.getById(999L),
            "Should throw NotFoundException when appointment not found");

        assertTrue(exception.getMessage().contains("Không tìm thấy cuộc hẹn với ID: 999"));
        System.out.println("✅ Test passed: Get appointment by ID - Appointment not found exception thrown correctly");
    }

    @Test
    void testGetAppointmentsByPatient() {
        // Given
        when(appointmentRepository.findByPatient_PatientId(1L)).thenReturn(List.of(testAppointment));
        when(appointmentMapper.entityToResponseDTO(any(Appointment.class))).thenReturn(testResponseDTO);

        // When
        List<AppointmentDTO.Response> result = appointmentService.getByPatient(1L);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        System.out.println("✅ Test passed: Get appointments by patient - Found " + result.size() + " appointments");

        // Verify interactions
        verify(appointmentRepository).findByPatient_PatientId(1L);
        verify(appointmentMapper).entityToResponseDTO(any(Appointment.class));
    }

    @Test
    void testGetAppointmentsByDoctor() {
        // Given
        when(appointmentRepository.findByDoctor_DoctorId(1L)).thenReturn(List.of(testAppointment));
        when(appointmentMapper.entityToResponseDTO(any(Appointment.class))).thenReturn(testResponseDTO);

        // When
        List<AppointmentDTO.Response> result = appointmentService.getByDoctor(1L);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        System.out.println("✅ Test passed: Get appointments by doctor - Found " + result.size() + " appointments");

        // Verify interactions
        verify(appointmentRepository).findByDoctor_DoctorId(1L);
        verify(appointmentMapper).entityToResponseDTO(any(Appointment.class));
    }

    @Test
    void testGetAvailableSlotsByDoctor() {
        // Given
        Appointment availableAppointment = new Appointment();
        availableAppointment.setPatient(null);
        availableAppointment.setStatus("Available");

        when(appointmentRepository.findByDoctor_DoctorId(1L)).thenReturn(List.of(availableAppointment));
        when(appointmentMapper.entityToResponseDTO(any(Appointment.class))).thenReturn(testResponseDTO);

        // When
        List<AppointmentDTO.Response> result = appointmentService.getAvailableSlotsByDoctor(1L);

        // Then
        assertNotNull(result);
        assertEquals(1, result.size());
        System.out.println("✅ Test passed: Get available slots by doctor - Found " + result.size() + " available slots");

        // Verify interactions
        verify(appointmentRepository).findByDoctor_DoctorId(1L);
        verify(appointmentMapper).entityToResponseDTO(any(Appointment.class));
    }

    @Test
    void testBookAppointment_Success() {
        // Given
        Appointment availableAppointment = new Appointment();
        availableAppointment.setPatient(null);
        availableAppointment.setStatus("Available");

        when(appointmentRepository.findById(1L)).thenReturn(Optional.of(availableAppointment));
        when(patientRepository.findById(1L)).thenReturn(Optional.of(testPatient));
        when(appointmentRepository.save(any(Appointment.class))).thenReturn(testAppointment);
        when(appointmentMapper.entityToResponseDTO(any(Appointment.class))).thenReturn(testResponseDTO);

        // When
        AppointmentDTO.Response result = appointmentService.bookAppointment(1L, 1L, "Booking notes");

        // Then
        assertNotNull(result);
        assertEquals(1L, result.getAppointmentId());
        System.out.println("✅ Test passed: Book appointment - Successfully booked appointment ID: " + result.getAppointmentId());

        // Verify interactions
        verify(appointmentRepository).findById(1L);
        verify(patientRepository).findById(1L);
        verify(appointmentRepository).save(any(Appointment.class));
        verify(emailService).sendSimpleEmail(anyString(), anyString(), anyString());
        verify(appointmentMapper).entityToResponseDTO(any(Appointment.class));
    }

    @Test
    void testBookAppointment_AppointmentNotFound() {
        // Given
        when(appointmentRepository.findById(999L)).thenReturn(Optional.empty());

        // When & Then
        NotFoundException exception = assertThrows(NotFoundException.class,
            () -> appointmentService.bookAppointment(999L, 1L, null),
            "Should throw NotFoundException when appointment not found");

        assertTrue(exception.getMessage().contains("Không tìm thấy cuộc hẹn với ID: 999"));
        System.out.println("✅ Test passed: Book appointment - Appointment not found exception thrown correctly");
    }

    @Test
    void testBookAppointment_AlreadyBooked() {
        // Given
        when(appointmentRepository.findById(1L)).thenReturn(Optional.of(testAppointment));

        // When & Then
        IllegalStateException exception = assertThrows(IllegalStateException.class,
            () -> appointmentService.bookAppointment(1L, 1L, null),
            "Should throw IllegalStateException when appointment already booked");

        assertTrue(exception.getMessage().contains("Khung giờ này đã được đặt"));
        System.out.println("✅ Test passed: Book appointment - Already booked exception thrown correctly");
    }

    @Test
    void testUpdateAppointment_Success() {
        // Given
        AppointmentDTO.Update updateDTO = new AppointmentDTO.Update();
        updateDTO.setStatus("Completed");
        updateDTO.setNotes("Updated notes");

        when(appointmentRepository.findById(1L)).thenReturn(Optional.of(testAppointment));
        when(appointmentRepository.save(any(Appointment.class))).thenReturn(testAppointment);
        when(appointmentMapper.entityToResponseDTO(any(Appointment.class))).thenReturn(testResponseDTO);

        // When
        AppointmentDTO.Response result = appointmentService.update(1L, updateDTO);

        // Then
        assertNotNull(result);
        assertEquals(1L, result.getAppointmentId());
        System.out.println("✅ Test passed: Update appointment - Successfully updated appointment ID: " + result.getAppointmentId());

        // Verify interactions
        verify(appointmentRepository).findById(1L);
        verify(appointmentMapper).applyUpdateToEntity(any(Appointment.class), any(AppointmentDTO.Update.class));
        verify(appointmentRepository).save(any(Appointment.class));
        verify(emailService).sendSimpleEmail(anyString(), anyString(), anyString());
        verify(appointmentMapper).entityToResponseDTO(any(Appointment.class));
    }

    @Test
    void testDeleteAppointment_Success() {
        // Given
        when(appointmentRepository.existsById(1L)).thenReturn(true);

        // When
        appointmentService.delete(1L);

        // Then
        System.out.println("✅ Test passed: Delete appointment - Successfully deleted appointment ID: 1");
        verify(appointmentRepository).existsById(1L);
        verify(appointmentRepository).deleteById(1L);
    }

    @Test
    void testDeleteAppointment_NotFound() {
        // Given
        when(appointmentRepository.existsById(999L)).thenReturn(false);

        // When & Then
        NotFoundException exception = assertThrows(NotFoundException.class,
            () -> appointmentService.delete(999L),
            "Should throw NotFoundException when appointment not found");

        assertTrue(exception.getMessage().contains("Không tìm thấy cuộc hẹn với ID: 999"));
        System.out.println("✅ Test passed: Delete appointment - Appointment not found exception thrown correctly");
    }

    @Test
    void testCancelAppointment_Success() {
        // Given
        when(appointmentRepository.findById(1L)).thenReturn(Optional.of(testAppointment));
        when(appointmentRepository.save(any(Appointment.class))).thenReturn(testAppointment);
        when(doctorScheduleRepository.save(any(DoctorSchedule.class))).thenReturn(testSchedule);
        when(appointmentMapper.entityToResponseDTO(any(Appointment.class))).thenReturn(testResponseDTO);

        // When
        AppointmentDTO.Response result = appointmentService.cancelAppointment(1L);

        // Then
        assertNotNull(result);
        assertEquals(1L, result.getAppointmentId());
        System.out.println("✅ Test passed: Cancel appointment - Successfully cancelled appointment ID: " + result.getAppointmentId());

        // Verify interactions
        verify(appointmentRepository).findById(1L);
        verify(appointmentRepository).save(any(Appointment.class));
        verify(doctorScheduleRepository).save(any(DoctorSchedule.class));
        verify(emailService).sendSimpleEmail(anyString(), anyString(), anyString());
        verify(appointmentMapper).entityToResponseDTO(any(Appointment.class));
    }
}
