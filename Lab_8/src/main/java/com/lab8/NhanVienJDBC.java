package com.lab8;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class NhanVienJDBC {

    public static void main(String[] args) {
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;

        try {
            // B1. Chuẩn bị môi trường và thiết lập kết nối
            Class.forName("org.sqlite.JDBC");
            String url = "jdbc:sqlite:nhanvien.db";
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
                    "chuc_vu TEXT)";
            stmt.executeUpdate(sql_create_table);
            System.out.println("Tạo bảng NhanVien thành công!");

            // B3.2. Thêm dữ liệu
            String sql_insert1 = "INSERT INTO NhanVien (id, ten, chuc_vu) VALUES (1, 'Nguyễn Văn A', 'Lập trình viên')";
            String sql_insert2 = "INSERT INTO NhanVien (id, ten, chuc_vu) VALUES (2, 'Trần Thị B', 'Thư ký')";
            String sql_insert3 = "INSERT INTO NhanVien (id, ten, chuc_vu) VALUES (3, 'Lê Văn C', 'Quản lý')";

            stmt.executeUpdate(sql_insert1);
            stmt.executeUpdate(sql_insert2);
            stmt.executeUpdate(sql_insert3);
            System.out.println("Thêm dữ liệu thành công!");

            // B4. Truy vấn và hiển thị dữ liệu
            String sql_select = "SELECT id, ten, chuc_vu FROM NhanVien";
            rs = stmt.executeQuery(sql_select);

            System.out.println("\n=== Danh sách nhân viên ===");
            while (rs.next()) {
                int id = rs.getInt("id");
                String ten = rs.getString("ten");
                String chucVu = rs.getString("chuc_vu");
                System.out.println("ID: " + id + ", Tên: " + ten + ", Chức vụ: " + chucVu);
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
