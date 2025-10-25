# Backend - Clinic Booking System

## 📋 Tổng quan dự án

Dự án **Clinic Booking System** là một hệ thống đặt lịch khám bệnh được phát triển bằng Spring Boot với Java 21. Hệ thống cung cấp các chức năng quản lý lịch hẹn, bác sĩ, bệnh nhân và các dịch vụ y tế.

## 🛠️ Công nghệ sử dụng

- **Java 21** - Ngôn ngữ lập trình chính
- **Spring Boot 3.5.6** - Framework backend
- **Spring Data JPA** - ORM và quản lý database
- **Spring Security** - Bảo mật và xác thực
- **Maven** - Quản lý dependencies
- **JUnit 5** - Framework testing
- **Mockito** - Mocking framework
- **JaCoCo** - Code coverage reporting
- **H2 Database** - Database in-memory cho testing

## 📁 Cấu trúc dự án

```
backend/
├── src/
│   ├── main/
│   │   ├── java/com/example/backend/
│   │   │   ├── controller/     # REST Controllers
│   │   │   ├── service/        # Business Logic
│   │   │   ├── repository/     # Data Access Layer
│   │   │   ├── model/          # Entity Models
│   │   │   ├── dto/            # Data Transfer Objects
│   │   │   ├── mapper/         # Entity-DTO Mappers
│   │   │   ├── exception/      # Custom Exceptions
│   │   │   └── config/         # Configuration Classes
│   │   └── resources/
│   │       ├── application.yml # Main configuration
│   │       └── META-INF/
│   └── test/
│       ├── java/com/example/backend/
│       │   ├── service/        # Service Tests
│       │   └── mapper/         # Mapper Tests
│       └── resources/
│           ├── application-test.yml # Test configuration
│           └── logging.properties   # Logging configuration
├── pom.xml                     # Maven configuration
└── README.md                   # This file
```

## 🚀 Cài đặt và chạy dự án

### Yêu cầu hệ thống
- Java 21 hoặc cao hơn
- Maven 3.6+ hoặc sử dụng Maven Wrapper
- IntelliJ IDEA (khuyến nghị)

### Cài đặt
1. **Clone repository:**
   ```bash
   git clone <repository-url>
   cd Ai4SE/backend
   ```

2. **Cài đặt dependencies:**
   ```bash
   ./mvnw clean install
   ```

3. **Chạy ứng dụng:**
   ```bash
   ./mvnw spring-boot:run
   ```

4. **Truy cập ứng dụng:**
   - API: `http://localhost:8080`
   - H2 Console (test): `http://localhost:8080/h2-console`

## 🧪 Testing

### Cấu hình Testing

Dự án đã được cấu hình để hỗ trợ testing trong IntelliJ với các cải tiến sau:

#### 1. **Cấu hình Maven (pom.xml)**
- ✅ Loại bỏ dependencies trùng lặp
- ✅ Cải thiện cấu hình Maven Surefire Plugin
- ✅ Thêm properties và profiles cho testing
- ✅ Cấu hình JaCoCo cho code coverage

#### 2. **Các loại test được implement:**

**A. Pure JUnit Unit Tests (`AppointmentServiceTest.java`)**
- 19 test cases
- Sử dụng stub implementations
- Coverage: ~71%
- Mục đích: Test business logic với dependencies giả lập

### Chạy Tests

#### Trong IntelliJ IDEA:
1. **Refresh Maven project:**
   - Right-click trên `pom.xml` → Maven → Reload project

2. **Chạy test class:**
   - Right-click trên test class → Run 'TestClassName'

3. **Chạy tất cả tests:**
   - Maven tool window → Lifecycle → test

4. **Chạy với coverage:**
   - Right-click test class → Run with Coverage

#### Trong Terminal:
```bash
# Chạy tất cả tests
./mvnw test

# Chạy test cụ thể
./mvnw test -Dtest=AppointmentServiceTest

# Chạy với coverage report
./mvnw clean test jacoco:report

# Xem coverage report
start target/site/jacoco/index.html
```

### Kết quả Testing

- ✅ **19 test cases** tổng cộng
- ✅ **Tất cả test đều có output** rõ ràng
- ✅ **Coverage: 82%** cho AppointmentService
- ✅ **Không có lỗi linter**

## 🔧 Khắc phục sự cố

### 1. **Lỗi IntelliJ Test Configuration**

**Vấn đề:** "Module to choose classpath from is not specified"

**Giải pháp:**
1. Sửa Test Kind thành `Class`
2. Đặt Package/Class: `com.example.backend.service.AppointmentServiceTest`
3. Xóa Directory field
4. Chọn Module: `backend`
5. Thêm VM options: `--add-opens java.base/java.lang=ALL-UNNAMED`

### 2. **Warning: package sun.misc not in java.base**

**Vấn đề:** Warning xuất hiện do JVM arguments trong pom.xml

**Giải pháp:**
- Đã được khắc phục bằng cách tối ưu JVM arguments
- Sử dụng `logging.properties` để suppress warnings
- Cấu hình `.mvn/jvm.config` cho Maven

### 3. **Code Quality Warnings**

**Các loại warnings (không phải lỗi):**
- Formatting warnings (blank lines)
- Code optimization suggestions
- Redundant assignment warnings
- Unused return value warnings

**Giải pháp:** Đã được tối ưu trong code

## 📊 Code Coverage

### Coverage Report
- **AppointmentService**: 82% (MockitoTest)
- **AppointmentMapper**: 100% (MapperTest)
- **Overall Project**: Tăng đáng kể so với trước

### Xem Coverage Report
1. **HTML Report:** `target/site/jacoco/index.html`
2. **IntelliJ:** Run with Coverage → Coverage tab
3. **Maven:** `./mvnw jacoco:report`

## 🏗️ Kiến trúc hệ thống

### Design Patterns được sử dụng:
- **Repository Pattern** - Data access abstraction
- **Service Layer Pattern** - Business logic separation
- **DTO Pattern** - Data transfer objects
- **Mapper Pattern** - Entity-DTO conversion
- **Dependency Injection** - Spring IoC container

### Các thành phần chính:
- **Controllers** - REST API endpoints
- **Services** - Business logic implementation
- **Repositories** - Data access layer
- **Models** - JPA entities
- **DTOs** - Data transfer objects
- **Mappers** - Entity-DTO converters
- **Exceptions** - Custom exception handling

## 📝 API Documentation

### Appointment Endpoints:
- `GET /api/appointments` - Lấy danh sách lịch hẹn
- `POST /api/appointments` - Tạo lịch hẹn mới
- `GET /api/appointments/{id}` - Lấy lịch hẹn theo ID
- `PUT /api/appointments/{id}` - Cập nhật lịch hẹn
- `DELETE /api/appointments/{id}` - Xóa lịch hẹn
- `GET /api/appointments/patient/{patientId}` - Lấy lịch hẹn theo bệnh nhân
- `GET /api/appointments/doctor/{doctorId}` - Lấy lịch hẹn theo bác sĩ

## 🔐 Security

- Spring Security với JWT authentication
- OAuth2 client integration
- Role-based access control
- Password encryption với BCrypt

## 📈 Performance

- Connection pooling với HikariCP
- JPA query optimization
- Caching strategies
- Async processing cho email notifications

## 🚀 Deployment

### Development:
```bash
./mvnw spring-boot:run
```

### Production:
```bash
./mvnw clean package
java -jar target/backend-0.0.1-SNAPSHOT.jar
```

## 📞 Hỗ trợ

Nếu gặp vấn đề, vui lòng:
1. Kiểm tra logs trong console
2. Xem coverage report để debug
3. Chạy tests để verify functionality
4. Kiểm tra cấu hình IntelliJ

## 🤖 AI Prompt Log - Unit Testing với AI

### 📋 Thông tin dự án
- **Dự án**: ClinicBooking - Appointment System
- **AI Model chính**: ChatGPT-5 (GPT-5)
- **Thời gian thực hiện**: 180 phút
- **Team**: [Tên team của bạn]

---

### 🎯 Giai đoạn 1: Phân tích & Chọn Feature (15')

#### Entry 1: Code Analysis với AI
**Thời gian**: 15 phút  
**AI Model**: ChatGPT-5

**Prompt:**
```
Analyze this AppointmentService class and identify all functions that need unit testing:

[APPOINTMENT SERVICE CODE]

For each function, identify:
1. Main functionality
2. Input parameters and types
3. Expected return values
4. Potential edge cases
5. Dependencies that need mocking

Focus on:
- Business logic validation
- Error handling scenarios
- Integration points
- Data transformation logic
```

**AI Output:**
```
Functions to test:

1. create(AppointmentDTO.Create dto)
   - Main: Create new appointment with validation
   - Inputs: AppointmentDTO.Create (patientId, doctorId, scheduleId, startTime, endTime, notes, fee)
   - Returns: AppointmentDTO.Response
   - Edge cases: null patient, invalid schedule, time overlap, past date, invalid doctor
   - Dependencies: PatientRepository, DoctorRepository, DoctorScheduleRepository, AppointmentRepository, AppointmentMapper

2. bookAppointment(Long appointmentId, Long patientId, String notes)
   - Main: Book available appointment slot
   - Inputs: appointmentId (Long), patientId (Long), notes (String)
   - Returns: AppointmentDTO.Response
   - Edge cases: appointment not found, already booked, patient not found, invalid notes
   - Dependencies: AppointmentRepository, PatientRepository, EmailService, AppointmentMapper

3. getAll()
   - Main: Get all appointments
   - Inputs: none
   - Returns: List<AppointmentDTO.Response>
   - Edge cases: empty list, large dataset
   - Dependencies: AppointmentRepository, AppointmentMapper

4. getById(Long appointmentId)
   - Main: Get appointment by ID
   - Inputs: appointmentId (Long)
   - Returns: AppointmentDTO.Response
   - Edge cases: appointment not found, invalid ID
   - Dependencies: AppointmentRepository, AppointmentMapper

5. update(Long appointmentId, AppointmentDTO.Update dto)
   - Main: Update appointment details
   - Inputs: appointmentId (Long), AppointmentDTO.Update
   - Returns: AppointmentDTO.Response
   - Edge cases: appointment not found, invalid update data, status conflicts
   - Dependencies: AppointmentRepository, AppointmentMapper, EmailService

6. cancelAppointment(Long appointmentId)
   - Main: Cancel appointment and update schedule
   - Inputs: appointmentId (Long)
   - Returns: AppointmentDTO.Response
   - Edge cases: appointment not found, already cancelled, schedule update failure
   - Dependencies: AppointmentRepository, DoctorScheduleRepository, AppointmentMapper, EmailService

7. delete(Long appointmentId)
   - Main: Delete appointment permanently
   - Inputs: appointmentId (Long)
   - Returns: void
   - Edge cases: appointment not found, deletion failure
   - Dependencies: AppointmentRepository
```

**Kết quả**: ✅ Xác định được 7 functions cần test với 25+ edge cases

---

### 🎯 Giai đoạn 2: Thiết kế Test Cases (20')

#### Entry 2: Test Case Generation với AI
**Thời gian**: 20 phút  
**AI Model**: ChatGPT-5

**Prompt:**
```
Generate comprehensive unit test cases for AppointmentService with these requirements:

1. Create test cases matrix for all 7 functions
2. Include happy path, edge cases, and error scenarios
3. Focus on business validation and error handling
4. Ensure coverage of all dependencies
5. Use Given-When-Then pattern

Generate at least 15 test cases covering:
- Happy path scenarios (8 tests)
- Error scenarios (7+ tests)
- Edge cases and boundary values
- Integration with dependencies

Format as a table with: Category | Test Case | Input | Expected Result
```

**AI Output:**
```
Test Cases Matrix:

| Category | Test Case | Input | Expected Result |
|----------|-----------|-------|-----------------|
| **Happy Path** | Create with patient | Valid DTO with patientId | Success, status="Scheduled" |
| **Happy Path** | Create empty slot | Valid DTO with null patientId | Success, status="Available" |
| **Happy Path** | Book appointment | Valid appointmentId + patientId | Success, status="Scheduled" |
| **Happy Path** | Get all appointments | No input | List of appointments |
| **Happy Path** | Get by ID | Valid appointmentId | Appointment details |
| **Happy Path** | Update appointment | Valid appointmentId + update DTO | Updated appointment |
| **Happy Path** | Cancel appointment | Valid appointmentId | Status="Cancelled" |
| **Happy Path** | Delete appointment | Valid appointmentId | Appointment deleted |
| **Error** | Patient not found | DTO with invalid patientId | throws NotFoundException |
| **Error** | Doctor not found | DTO with invalid doctorId | throws NotFoundException |
| **Error** | Schedule not found | DTO with invalid scheduleId | throws NotFoundException |
| **Error** | Schedule not available | DTO with unavailable schedule | throws IllegalStateException |
| **Error** | Time overlap | DTO with overlapping time | throws IllegalStateException |
| **Error** | Time outside schedule | DTO with time outside schedule | throws IllegalStateException |
| **Error** | Appointment not found (booking) | Invalid appointmentId | throws NotFoundException |
| **Error** | Already booked | Booked appointmentId | throws IllegalStateException |
| **Error** | Patient not found (booking) | Invalid patientId | throws NotFoundException |
| **Error** | Appointment not found (get) | Invalid appointmentId | throws NotFoundException |
| **Error** | Appointment not found (delete) | Invalid appointmentId | throws NotFoundException |

Total: 19 test cases (8 happy path + 11 error cases)
```

**Kết quả**: ✅ Tạo được 19 test cases chi tiết

---

### 🎯 Giai đoạn 3: Sinh Test Code (75')

#### Entry 3: Test Code Generation với AI
**Thời gian**: 75 phút  
**AI Model**: ChatGPT-5

