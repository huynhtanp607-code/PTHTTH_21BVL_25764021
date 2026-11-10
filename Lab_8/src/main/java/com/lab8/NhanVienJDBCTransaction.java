package com.lab8;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class NhanVienJDBCTransaction {

    public static void main(String[] args) {
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;

        try {
            // B1. Chuẩn bị môi trường và thiết lập kết nối
            Class.forName("org.sqlite.JDBC");
            String url = "jdbc:sqlite:nhanvien_transaction.db";
            String username = "";
            String passwd = "";
            conn = DriverManager.getConnection(url, username, passwd);
            System.out.println("Kết nối database thành công!");

            // B2. Tạo đối tượng Statement từ Connection
            stmt = conn.createStatement();

            // B3.1. Tạo bảng
            String sql_create_table = "CREATE TABLE IF NOT EXISTS NhanVien (" +
                    "id INTEGER PRIMARY KEY, " +
                    "ten TEXT NOT NULL, " +
                    "chuc_vu TEXT, " +
                    "luong REAL)";
            stmt.executeUpdate(sql_create_table);
            System.out.println("Tạo bảng NhanVien thành công!");

            // Sử dụng giao dịch (Transaction)
            conn.setAutoCommit(false); // Tắt chế độ auto-commit

            try {
                // B3.2. Thêm dữ liệu trong giao dịch
                String sql_insert1 = "INSERT INTO NhanVien (id, ten, chuc_vu, luong) VALUES (1, 'Nguyễn Văn A', 'Lập trình viên', 15000000)";
                String sql_insert2 = "INSERT INTO NhanVien (id, ten, chuc_vu, luong) VALUES (2, 'Trần Thị B', 'Thư ký', 8000000)";
                String sql_insert3 = "INSERT INTO NhanVien (id, ten, chuc_vu, luong) VALUES (3, 'Lê Văn C', 'Quản lý', 25000000)";
                String sql_insert4 = "INSERT INTO NhanVien (id, ten, chuc_vu, luong) VALUES (4, 'Phạm Văn D', 'Lập trình viên', 12000000)";
                String sql_insert5 = "INSERT INTO NhanVien (id, ten, chuc_vu, luong) VALUES (5, 'Hoàng Thị E', 'Thiết kế', 10000000)";

                stmt.executeUpdate(sql_insert1);
                stmt.executeUpdate(sql_insert2);
                stmt.executeUpdate(sql_insert3);
                stmt.executeUpdate(sql_insert4);
                stmt.executeUpdate(sql_insert5);

                conn.commit(); // Commit giao dịch khi tất cả thao tác thành công
                System.out.println("Thêm dữ liệu thành công (Transaction committed)!");

            } catch (SQLException e) {
                conn.rollback(); // Rollback nếu có lỗi
                System.out.println("Lỗi khi thêm dữ liệu, thực hiện rollback: " + e.getMessage());
            }

            conn.setAutoCommit(true); // Bật lại auto-commit

            // B4. Truy vấn và hiển thị dữ liệu
            String sql_select = "SELECT id, ten, chuc_vu, luong FROM NhanVien";
            rs = stmt.executeQuery(sql_select);

            System.out.println("\n=== Danh sách nhân viên ===");
            while (rs.next()) {
                int id = rs.getInt("id");
                String ten = rs.getString("ten");
                String chucVu = rs.getString("chuc_vu");
                double luong = rs.getDouble("luong");
                System.out.println("ID: " + id + ", Tên: " + ten + ", Chức vụ: " + chucVu + ", Lương: " + luong);
            }

        } catch (ClassNotFoundException e) {
            System.out.println("Không tìm thấy driver JDBC: " + e.getMessage());
        } catch (SQLException e) {
            System.out.println("Lỗi SQL: " + e.getMessage());
        } finally {
            // B5. Đóng kết nối và giải phóng tài nguyên
            try {
                if (rs != null) {
                    rs.close();
                }
            } catch (SQLException e) {
                System.out.println("Lỗi khi đóng ResultSet: " + e.getMessage());
            }
            try {
                if (stmt != null) {
                    stmt.close();
                }
            } catch (SQLException e) {
                System.out.println("Lỗi khi đóng Statement: " + e.getMessage());
            }
            try {
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException e) {
                System.out.println("Lỗi khi đóng Connection: " + e.getMessage());
            }
            System.out.println("Đã đóng tất cả các kết nối!");
        }
    }
}
