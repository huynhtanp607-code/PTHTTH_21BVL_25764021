package com.lab8;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class SanPhamJDBC {

    public static void main(String[] args) {
        Connection conn = null;
        Statement stmt = null;
        ResultSet rs = null;

        try {
            // B1. Chuẩn bị môi trường và thiết lập kết nối
            Class.forName("org.sqlite.JDBC");
            String url = "jdbc:sqlite:sanpham.db";
            String username = "";
            String passwd = "";
            conn = DriverManager.getConnection(url, username, passwd);
            System.out.println("Kết nối database thành công!");

            // B2. Tạo đối tượng Statement từ Connection
            stmt = conn.createStatement();

            // B3.1. Tạo bảng SanPham
            String sql_create_table = "CREATE TABLE IF NOT EXISTS SanPham (" +
                    "maSP INTEGER PRIMARY KEY, " +
                    "tenSP TEXT NOT NULL, " +
                    "gia REAL, " +
                    "soluong INTEGER)";
            stmt.executeUpdate(sql_create_table);
            System.out.println("Tạo bảng SanPham thành công!");

            // B3.2. CREATE - Thêm dữ liệu (ít nhất 3 sản phẩm)
            conn.setAutoCommit(false);
            try {
                String sql_insert1 = "INSERT INTO SanPham (maSP, tenSP, gia, soluong) VALUES (1, 'Laptop Dell XPS', 25000000, 10)";
                String sql_insert2 = "INSERT INTO SanPham (maSP, tenSP, gia, soluong) VALUES (2, 'iPhone 15 Pro', 30000000, 15)";
                String sql_insert3 = "INSERT INTO SanPham (maSP, tenSP, gia, soluong) VALUES (3, 'Samsung Galaxy S24', 20000000, 20)";
                String sql_insert4 = "INSERT INTO SanPham (maSP, tenSP, gia, soluong) VALUES (4, 'MacBook Air M2', 28000000, 8)";
                String sql_insert5 = "INSERT INTO SanPham (maSP, tenSP, gia, soluong) VALUES (5, 'iPad Pro', 18000000, 12)";

                stmt.executeUpdate(sql_insert1);
                stmt.executeUpdate(sql_insert2);
                stmt.executeUpdate(sql_insert3);
                stmt.executeUpdate(sql_insert4);
                stmt.executeUpdate(sql_insert5);

                conn.commit();
                System.out.println("Thêm dữ liệu thành công!");
            } catch (SQLException e) {
                conn.rollback();
                System.out.println("Lỗi khi thêm dữ liệu, rollback: " + e.getMessage());
            }
            conn.setAutoCommit(true);

            // B4. READ - Truy vấn tất cả sản phẩm
            System.out.println("\n=== Danh sách tất cả sản phẩm ===");
            String sql_select_all = "SELECT maSP, tenSP, gia, soluong FROM SanPham";
            rs = stmt.executeQuery(sql_select_all);
            while (rs.next()) {
                int maSP = rs.getInt("maSP");
                String tenSP = rs.getString("tenSP");
                double gia = rs.getDouble("gia");
                int soluong = rs.getInt("soluong");
                System.out.println("Mã SP: " + maSP + ", Tên SP: " + tenSP + ", Giá: " + gia + ", Số lượng: " + soluong);
            }
            rs.close();

            // READ - Tìm kiếm sản phẩm theo tenSP
            System.out.println("\n=== Tìm kiếm sản phẩm có tên chứa 'iPhone' ===");
            String sql_search = "SELECT maSP, tenSP, gia, soluong FROM SanPham WHERE tenSP LIKE '%iPhone%'";
            rs = stmt.executeQuery(sql_search);
            while (rs.next()) {
                int maSP = rs.getInt("maSP");
                String tenSP = rs.getString("tenSP");
                double gia = rs.getDouble("gia");
                int soluong = rs.getInt("soluong");
                System.out.println("Mã SP: " + maSP + ", Tên SP: " + tenSP + ", Giá: " + gia + ", Số lượng: " + soluong);
            }
            rs.close();

            // UPDATE - Cập nhật giá hoặc số lượng của sản phẩm theo maSP
            System.out.println("\n=== Cập nhật sản phẩm có maSP = 2 ===");
            conn.setAutoCommit(false);
            try {
                String sql_update_gia = "UPDATE SanPham SET gia = 29000000 WHERE maSP = 2";
                String sql_update_soluong = "UPDATE SanPham SET soluong = 18 WHERE maSP = 2";
                stmt.executeUpdate(sql_update_gia);
                stmt.executeUpdate(sql_update_soluong);
                conn.commit();
                System.out.println("Cập nhật thành công!");
            } catch (SQLException e) {
                conn.rollback();
                System.out.println("Lỗi khi cập nhật, rollback: " + e.getMessage());
            }
            conn.setAutoCommit(true);

            // Hiển thị lại sản phẩm sau khi cập nhật
            System.out.println("\n=== Sản phẩm sau khi cập nhật ===");
            String sql_select_updated = "SELECT maSP, tenSP, gia, soluong FROM SanPham WHERE maSP = 2";
            rs = stmt.executeQuery(sql_select_updated);
            while (rs.next()) {
                int maSP = rs.getInt("maSP");
                String tenSP = rs.getString("tenSP");
                double gia = rs.getDouble("gia");
                int soluong = rs.getInt("soluong");
                System.out.println("Mã SP: " + maSP + ", Tên SP: " + tenSP + ", Giá: " + gia + ", Số lượng: " + soluong);
            }
            rs.close();

            // DELETE - Xóa sản phẩm theo maSP
            System.out.println("\n=== Xóa sản phẩm có maSP = 5 ===");
            conn.setAutoCommit(false);
            try {
                String sql_delete = "DELETE FROM SanPham WHERE maSP = 5";
                int rowsAffected = stmt.executeUpdate(sql_delete);
                conn.commit();
                System.out.println("Đã xóa " + rowsAffected + " sản phẩm!");
            } catch (SQLException e) {
                conn.rollback();
                System.out.println("Lỗi khi xóa, rollback: " + e.getMessage());
            }
            conn.setAutoCommit(true);

            // Hiển thị lại danh sách sau khi xóa
            System.out.println("\n=== Danh sách sản phẩm sau khi xóa ===");
            rs = stmt.executeQuery(sql_select_all);
            while (rs.next()) {
                int maSP = rs.getInt("maSP");
                String tenSP = rs.getString("tenSP");
                double gia = rs.getDouble("gia");
                int soluong = rs.getInt("soluong");
                System.out.println("Mã SP: " + maSP + ", Tên SP: " + tenSP + ", Giá: " + gia + ", Số lượng: " + soluong);
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
