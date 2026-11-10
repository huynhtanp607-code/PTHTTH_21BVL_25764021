# Lab 8 - JDBC (Java Database Connection)

## Mô tả
Dự án này thực hiện các bài tập về JDBC với SQLite theo hướng dẫn thực hành Lab 8.

## Cấu trúc dự án
```
Lab_8/
├── pom.xml
├── src/
│   └── main/
│       └── java/
│           └── com/
│               └── lab8/
│                   ├── NhanVienJDBC.java
│                   ├── NhanVienJDBCTransaction.java
│                   ├── SanPhamJDBC.java
│                   ├── SanPhamPreparedStatement.java
│                   └── NhanVienAggregation.java
```

## Các lớp Java

### 1. NhanVienJDBC.java
- Thực hiện các bước B1-B5 theo hướng dẫn
- Tạo bảng NhanVien với các cột: id, ten, chuc_vu
- Thêm 3 nhân viên mẫu
- Truy vấn và hiển thị danh sách nhân viên
- Áp dụng try-catch-finally để xử lý ngoại lệ

### 2. NhanVienJDBCTransaction.java
- Mở rộng từ NhanVienJDBC
- Thêm cột lương vào bảng NhanVien
- Sử dụng giao dịch (Transaction) để thêm dữ liệu
- Tắt auto-commit, sử dụng commit() và rollback()
- Áp dụng try-catch-finally nghiêm ngặt

### 3. SanPhamJDBC.java
- Quản lý danh mục sản phẩm (Bài tập gợi ý 1)
- Bảng SanPham với các cột: maSP, tenSP, gia, soluong
- Thực hiện CRUD (Create, Read, Update, Delete)
- Create: Thêm 5 sản phẩm
- Read: Liệt kê tất cả sản phẩm, tìm kiếm theo tên
- Update: Cập nhật giá và số lượng theo maSP
- Delete: Xóa sản phẩm theo maSP
- Sử dụng Transaction cho các thao tác

### 4. SanPhamPreparedStatement.java
- Áp dụng PreparedStatement thay vì Statement (Bài tập gợi ý 4)
- Tăng cường bảo mật và hiệu năng
- Tránh SQL injection
- Sử dụng tham số (?) thay vì chuỗi trực tiếp
- Thực hiện CRUD với PreparedStatement

### 5. NhanVienAggregation.java
- Truy vấn dữ liệu nâng cao với các hàm Aggregation (Bài tập gợi ý 5)
- Thêm nhiều bản ghi nhân viên với cột lương
- SUM(luong): Tính tổng lương
- AVG(luong): Tìm mức lương trung bình
- MAX(luong): Tìm nhân viên có lương cao nhất
- MIN(luong): Tìm nhân viên có lương thấp nhất
- COUNT: Đếm số lượng nhân viên
- GROUP BY: Đếm số lượng nhân viên theo chức vụ
- AVG với GROUP BY: Tính lương trung bình theo chức vụ

## Cách chạy

### Biên dịch dự án
```bash
mvn clean compile
```

### Chạy từng chương trình

1. Chạy NhanVienJDBC:
```bash
mvn exec:java -Dexec.mainClass="com.lab8.NhanVienJDBC"
```

2. Chạy NhanVienJDBCTransaction:
```bash
mvn exec:java -Dexec.mainClass="com.lab8.NhanVienJDBCTransaction"
```

3. Chạy SanPhamJDBC:
```bash
mvn exec:java -Dexec.mainClass="com.lab8.SanPhamJDBC"
```

4. Chạy SanPhamPreparedStatement:
```bash
mvn exec:java -Dexec.mainClass="com.lab8.SanPhamPreparedStatement"
```

5. Chạy NhanVienAggregation:
```bash
mvn exec:java -Dexec.mainClass="com.lab8.NhanVienAggregation"
```

## Các file database được tạo
- nhanvien.db (từ NhanVienJDBC)
- nhanvien_transaction.db (từ NhanVienJDBCTransaction)
- sanpham.db (từ SanPhamJDBC)
- sanpham_prepared.db (từ SanPhamPreparedStatement)
- nhanvien_aggregation.db (từ NhanVienAggregation)

## Yêu cầu hệ thống
- Java 17 hoặc cao hơn
- Maven 3.6+
- SQLite JDBC Driver (được tự động tải qua Maven)
