# Session 06 - Bài 6: Chống lỗi dây chuyền và ApiResponseError

Repo có hệ thống y tế hoàn chỉnh và `MEDICAL-GATEWAY` ở cổng `8080`.

## Chạy toàn bộ hệ thống

```bash
docker compose up -d
```

Sau đó mở năm terminal tại thư mục `bai_6` và chạy lần lượt:

```bash
cd medical-discovery-server
gradle bootRun

cd patient-service
gradle bootRun

cd doctor-service
gradle bootRun

cd appointment-service
gradle bootRun

cd medical-gateway
gradle bootRun
```

Mặc định PostgreSQL dùng cổng `5432`. Nếu cổng này đang bận, chạy Compose bằng
`POSTGRES_PORT=5436 docker compose up -d`, đồng thời truyền URL tương ứng khi
chạy ba service dữ liệu, ví dụ
`PATIENT_DB_URL=jdbc:postgresql://localhost:5436/medical_db gradle bootRun`
(tương tự với `DOCTOR_DB_URL` và `APPOINTMENT_DB_URL`).

Tạo bệnh nhân:

```bash
curl -i -X POST http://localhost:8081/api/v1/patients \
  -H 'Content-Type: application/json' \
  -d '{"fullName":"Nguyễn Văn An","dateOfBirth":"1990-05-20","gender":"Nam","phoneNumber":"0901234567","address":"Hà Nội","medicalHistory":"Dị ứng Penicillin"}'
```

Endpoint `GET /api/v1/patients/{id}` được thêm để Appointment-Service có thể
kiểm tra bệnh nhân qua Eureka ở bài sau.

Tạo dữ liệu bác sĩ rồi lấy danh sách `id`, `name`, `specialization`:

```bash
curl -X POST http://localhost:8082/api/v1/doctors \
  -H 'Content-Type: application/json' \
  -d '{"name":"Trần Minh","specialization":"Nội khoa","experienceYears":10,"email":"minh@hospital.vn","status":true}'

curl http://localhost:8082/api/v1/doctors
```

Appointment-Service dùng `RestTemplate` được đánh dấu `@LoadBalanced` và gọi
đúng service name trên Eureka trước khi lưu `patientId` và `doctorId`.

```bash
curl -i -X POST http://localhost:8083/api/v1/appointments \
  -H 'Content-Type: application/json' \
  -d '{"patientId":1,"doctorId":1,"appointmentDate":"2030-10-20T09:30:00","reason":"Khám tổng quát"}'
```

Gateway định tuyến bằng `lb://` cho cả ba service. `RequestLoggingFilter` ghi IP,
HTTP method và path cho mọi request. Sau khi chạy thêm project `medical-gateway`,
mọi API được gọi tập trung qua cổng 8080, ví dụ:

```bash
curl http://localhost:8080/api/v1/doctors
```

`AppointmentService` bọc riêng từng lời gọi liên dịch vụ bằng `try-catch`. Nếu
Patient/Doctor Service bị sập hoặc Eureka không tìm thấy instance, service dừng
trước khi lưu lịch và trả HTTP 503 theo mẫu:

```json
{
  "timestamp": "2026-01-01T10:00:00Z",
  "status": 503,
  "error": "Service Unavailable",
  "message": "Doctor-Service tạm thời không phản hồi. Vui lòng thử lại sau."
}
```

## Kiểm chứng lỗi dây chuyền

1. Chạy PostgreSQL, Eureka, Patient, Doctor, Appointment và Gateway.
2. Tạo ít nhất một Patient và Doctor hợp lệ.
3. Tắt Doctor Service.
4. Gọi `POST http://localhost:8080/api/v1/appointments`.
5. Kết quả phải là JSON `ApiResponseError` với status `503`, không phải trang lỗi 500.
