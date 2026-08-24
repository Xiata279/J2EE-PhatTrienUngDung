# J2EE-PhatTrienUngDung — Quản lý Sách (Spring Boot CRUD)

![Java](https://img.shields.io/badge/Java-21-007396?logo=openjdk&logoColor=white)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.2.2-6DB33F?logo=springboot&logoColor=white)
![Thymeleaf](https://img.shields.io/badge/Thymeleaf-005F0F?logo=thymeleaf&logoColor=white)

Ứng dụng web CRUD quản lý sách — bài thực hành môn Phát triển ứng dụng J2EE. Xây dựng theo mô hình MVC với Spring Boot và Thymeleaf.

## Tính năng
- Xem danh sách sách
- Thêm sách mới
- Chỉnh sửa thông tin sách
- Xóa sách

## Công nghệ
- Java 21, Spring Boot 3.2.2
- Spring MVC + Thymeleaf
- Maven (kèm Maven Wrapper)

## Cấu trúc chính
```
src/main/java/com/example/demo/
├─ DemoApplication.java     # Điểm khởi động
├─ Book.java                # Model sách
├─ BookController.java      # Điều hướng CRUD
├─ BookService.java         # Xử lý nghiệp vụ
└─ HomeController.java      # Trang chủ
src/main/resources/templates/   # index, books, add-book, edit-book
```

## Cách chạy
```bash
./mvnw spring-boot:run        # Windows: mvnw.cmd spring-boot:run
```
Truy cập http://localhost:8080

## Tác giả
**Nguyễn Thành Luân** — [github.com/luanem2709](https://github.com/luanem2709)
