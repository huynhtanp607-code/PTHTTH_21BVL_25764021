# LAB 6 - JAVA RMI EXERCISES

## Danh sách bài tập
1. **Bai1_HelloWorld** - Máy chủ RMI trả về chuỗi "Hello, World!"
2. **Bai2_AddNumbers** - Tính tổng hai số
3. **Bai3_PrimeCheck** - Kiểm tra số nguyên tố
4. **Bai4_ContactManager** - Quản lý danh bạ
5. **Bai5_SimpleChat** - Chat đơn giản
6. **Bai6_BankAccount** - Quản lý tài khoản ngân hàng
7. **Bai7_GeometryArea** - Tính diện tích hình học
8. **Bai8_FlightBooking** - Đặt vé máy bay
9. **Bai9_OnlineAuction** - Hệ thống đấu giá trực tuyến

## Cấu trúc mỗi bài tập
Mỗi bài tập bao gồm 3 file:
- `*Interface.java` - Interface RMI
- `*Server.java` - Server RMI
- `*Client.java` - Client RMI

## Cách chạy

### Bước 1: Biên dịch các file
Di chuyển vào thư mục bài tập và biên dịch:
```bash
cd Bai1_HelloWorld
javac *.java
```

### Bước 2: Khởi động RMI Registry (nếu chưa chạy)
```bash
rmiregistry
```
Hoặc server sẽ tự động tạo registry trên port 1099.

### Bước 3: Chạy Server
Mở terminal mới và chạy:
```bash
java *Server
```
Ví dụ:
```bash
java HelloServer
```

### Bước 4: Chạy Client
Mở terminal mới và chạy:
```bash
java *Client
```
Ví dụ:
```bash
java HelloClient
```

## Lưu ý
- Mỗi bài tập sử dụng port 1099 cho RMI Registry
- Chỉ chạy một bài tập tại một thời điểm để tránh xung đột port
- Đảm bảo đóng các terminal đang chạy server khi chuyển sang bài khác
