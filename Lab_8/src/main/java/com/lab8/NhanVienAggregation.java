package com.lab8;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class NhanVienAggregation {

    public static void main(String[] args) {
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;

        try {
            // B1. Chuẩn bị môi trường và thiết lập kết nối
            Class.forName("org.sqlite.JDBC");
            String url = "jdbc:sqlite:nhanvien_aggregation.db";
            String username = "";
            String passwd = "";
            conn = DriverManager.getConnection(url, username, passwd);
            System.out.println("Kết nối database thành công!");

            // B2. Tạo đối tượng Statement từ Connection
            stmt = conn.createStatement();

            // B3.1. Tạo bảng NhanVien với cột lương
            String sql_create_table = "CREATE TABLE IF NOT EXISTS NhanVien (" +
                    "id INTEGER PRIMARY KEY, " +
                    "ten TEXT NOT NULL, " +
                    "chuc_vu TEXT, " +
                    "luong REAL)";
            stmt.executeUpdate(sql_create_table);
            System.out.println("Tạo bảng NhanVien thành công!");

            // B3.2. Thêm nhiều bản ghi với cột lương
            conn.setAutoCommit(false);
            try {
                String sql_insert1 = "INSERT INTO NhanVien (id, ten, chuc_vu, luong) VALUES (1, 'Nguyễn Văn A', 'Lập trình viên', 15000000)";
                String sql_insert2 = "INSERT INTO NhanVien (id, ten, chuc_vu, luong) VALUES (2, 'Trần Thị B', 'Thư ký', 8000000)";
                String sql_insert3 = "INSERT INTO NhanVien (id, ten, chuc_vu, luong) VALUES (3, 'Lê Văn C', 'Quản lý', 25000000)";
                String sql_insert4 = "INSERT INTO NhanVien (id, ten, chuc_vu, luong) VALUES (4, 'Phạm Văn D', 'Lập trình viên', 12000000)";
                String sql_insert5 = "INSERT INTO NhanVien (id, ten, chuc_vu, luong) VALUES (5, 'Hoàng Thị E', 'Thiết kế', 10000000)";
                String sql_insert6 = "INSERT INTO NhanVien (id, ten, chuc_vu, luong) VALUES (6, 'Đặng Văn F', 'Lập trình viên', 18000000)";
                String sql_insert7 = "INSERT INTO NhanVien (id, ten, chuc_vu, luong) VALUES (7, 'Vũ Thị G', 'Thư ký', 7500000)";
                String sql_insert8 = "INSERT INTO NhanVien (id, ten, chuc_vu, luong) VALUES (8, 'Ngô Văn H', 'Quản lý', 30000000)";

                stmt.executeUpdate(sql_insert1);
                stmt.executeUpdate(sql_insert2);
                stmt.executeUpdate(sql_insert3);
                stmt.executeUpdate(sql_insert4);
                stmt.executeUpdate(sql_insert5);
                stmt.executeUpdate(sql_insert6);
                stmt.executeUpdate(sql_insert7);
                stmt.executeUpdate(sql_insert8);

                conn.commit();
                System.out.println("Thêm dữ liệu thành công!");
            } catch (SQLException e) {
                conn.rollback();
                System.out.println("Lỗi khi thêm dữ liệu, rollback: " + e.getMessage());
            }
            conn.setAutoCommit(true);

            // Hiển thị danh sách nhân viên
            System.out.println("\n=== Danh sách nhân viên ===");
            String sql_select_all = "SELECT id, ten, chuc_vu, luong FROM NhanVien";
            rs = stmt.executeQuery(sql_select_all);
            while (rs.next()) {
                int id = rs.getInt("id");
                String ten = rs.getString("ten");
                String chucVu = rs.getString("chuc_vu");
                double luong = rs.getDouble("luong");
                System.out.println("ID: " + id + ", Tên: " + ten + ", Chức vụ: " + chucVu + ", Lương: " + luong);
            }
            rs.close();

            // B5. Truy vấn nâng cao với các hàm Aggregation

            // SUM(luong) - Tính tổng lương
            System.out.println("\n=== Tính tổng lương của tất cả nhân viên (SUM) ===");
            String sql_sum = "SELECT SUM(luong) as tong_luong FROM NhanVien";
            rs = stmt.executeQuery(sql_sum);
            if (rs.next()) {
                double tongLuong = rs.getDouble("tong_luong");
                System.out.println("Tổng lương: " + tongLuong);
            }
            rs.close();

            // AVG(luong) - Tìm mức lương trung bình
            System.out.println("\n=== Tìm mức lương trung bình (AVG) ===");
            String sql_avg = "SELECT AVG(luong) as luong_trung_binh FROM NhanVien";
            rs = stmt.executeQuery(sql_avg);
            if (rs.next()) {
                double luongTrungBinh = rs.getDouble("luong_trung_binh");
                System.out.println("Lương trung bình: " + luongTrungBinh);
            }
            rs.close();

            // MAX(luong) - Tìm nhân viên có mức lương cao nhất
            System.out.println("\n=== Tìm nhân viên có mức lương cao nhất (MAX) ===");
            String sql_max = "SELECT id, ten, chuc_vu, luong FROM NhanVien WHERE luong = (SELECT MAX(luong) FROM NhanVien)";
            rs = stmt.executeQuery(sql_max);
            while (rs.next()) {
                int id = rs.getInt("id");
                String ten = rs.getString("ten");
                String chucVu = rs.getString("chuc_vu");
                double luong = rs.getDouble("luong");
                System.out.println("ID: " + id + ", Tên: " + ten + ", Chức vụ: " + chucVu + ", Lương cao nhất: " + luong);
            }
            rs.close();

            // MIN(luong) - Tìm nhân viên có mức lương thấp nhất
            System.out.println("\n=== Tìm nhân viên có mức lương thấp nhất (MIN) ===");
            String sql_min = "SELECT id, ten, chuc_vu, luong FROM NhanVien WHERE luong = (SELECT MIN(luong) FROM NhanVien)";
            rs = stmt.executeQuery(sql_min);
            while (rs.next()) {
                int id = rs.getInt("id");
                String ten = rs.getString("ten");
                String chucVu = rs.getString("chuc_vu");
                double luong = rs.getDouble("luong");
                System.out.println("ID: " + id + ", Tên: " + ten + ", Chức vụ: " + chucVu + ", Lương thấp nhất: " + luong);
            }
            rs.close();

            // COUNT và GROUP BY - Đếm số lượng nhân viên theo chức vụ
            System.out.println("\n=== Đếm số lượng nhân viên theo chức vụ (COUNT và GROUP BY) ===");
            String sql_count_group = "SELECT chuc_vu, COUNT(*) as so_luong FROM NhanVien GROUP BY chuc_vu";
            rs = stmt.executeQuery(sql_count_group);
            while (rs.next()) {
                String chucVu = rs.getString("chuc_vu");
                int soLuong = rs.getInt("so_luong");
                System.out.println("Chức vụ: " + chucVu + ", Số lượng: " + soLuong);
            }
            rs.close();

            // COUNT - Đếm tổng số nhân viên
            System.out.println("\n=== Đếm tổng số nhân viên (COUNT) ===");
            String sql_count = "SELECT COUNT(*) as tong_nhan_vien FROM NhanVien";
            rs = stmt.executeQuery(sql_count);
            if (rs.next()) {
                int tongNhanVien = rs.getInt("tong_nhan_vien");
                System.out.println("Tổng số nhân viên: " + tongNhanVien);
            }
            rs.close();

            // AVG theo chức vụ - Tính lương trung bình theo chức vụ
            System.out.println("\n=== Tính lương trung bình theo chức vụ (AVG và GROUP BY) ===");
            String sql_avg_group = "SELECT chuc_vu, AVG(luong) as luong_trung_binh FROM NhanVien GROUP BY chuc_vu";
            rs = stmt.executeQuery(sql_avg_group);
            while (rs.next()) {
                String chucVu = rs.getString("chuc_vu");
                double luongTrungBinh = rs.getDouble("luong_trung_binh");
                System.out.println("Chức vụ: " + chucVu + ", Lương trung bình: " + luongTrungBinh);
            }
            rs.close();

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
