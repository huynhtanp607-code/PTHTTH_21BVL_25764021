package com.lab8;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class SanPhamPreparedStatement {

    public static void main(String[] args) {
        Connection conn = null;
        Statement stmt = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            // B1. Chuẩn bị môi trường và thiết lập kết nối
            Class.forName("org.sqlite.JDBC");
            String url = "jdbc:sqlite:sanpham_prepared.db";
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

            // B3.2. CREATE - Thêm dữ liệu sử dụng PreparedStatement
            conn.setAutoCommit(false);
            try {
                // Sử dụng PreparedStatement để tránh SQL injection
                String sql_insert = "INSERT INTO SanPham (maSP, tenSP, gia, soluong) VALUES (?, ?, ?, ?)";
                pstmt = conn.prepareStatement(sql_insert);

                // Thêm sản phẩm 1
                pstmt.setInt(1, 1);
                pstmt.setString(2, "Laptop Dell XPS");
                pstmt.setDouble(3, 25000000);
                pstmt.setInt(4, 10);
                pstmt.executeUpdate();

                // Thêm sản phẩm 2
                pstmt.setInt(1, 2);
                pstmt.setString(2, "iPhone 15 Pro");
                pstmt.setDouble(3, 30000000);
                pstmt.setInt(4, 15);
                pstmt.executeUpdate();

                // Thêm sản phẩm 3
                pstmt.setInt(1, 3);
                pstmt.setString(2, "Samsung Galaxy S24");
                pstmt.setDouble(3, 20000000);
                pstmt.setInt(4, 20);
                pstmt.executeUpdate();

                // Thêm sản phẩm 4
                pstmt.setInt(1, 4);
                pstmt.setString(2, "MacBook Air M2");
                pstmt.setDouble(3, 28000000);
                pstmt.setInt(4, 8);
                pstmt.executeUpdate();

                conn.commit();
                System.out.println("Thêm dữ liệu thành công bằng PreparedStatement!");
            } catch (SQLException e) {
                conn.rollback();
                System.out.println("Lỗi khi thêm dữ liệu, rollback: " + e.getMessage());
            }
            conn.setAutoCommit(true);
            pstmt.close();

            // B4. READ - Tìm kiếm sản phẩm theo tenSP sử dụng PreparedStatement
            System.out.println("\n=== Tìm kiếm sản phẩm theo tên ===");
            String sql_search = "SELECT maSP, tenSP, gia, soluong FROM SanPham WHERE tenSP LIKE ?";
            pstmt = conn.prepareStatement(sql_search);
            pstmt.setString(1, "%iPhone%");
            rs = pstmt.executeQuery();

            while (rs.next()) {
                int maSP = rs.getInt("maSP");
                String tenSP = rs.getString("tenSP");
                double gia = rs.getDouble("gia");
                int soluong = rs.getInt("soluong");
                System.out.println("Mã SP: " + maSP + ", Tên SP: " + tenSP + ", Giá: " + gia + ", Số lượng: " + soluong);
            }
            rs.close();
            pstmt.close();

            // UPDATE - Cập nhật sản phẩm sử dụng PreparedStatement
            System.out.println("\n=== Cập nhật sản phẩm sử dụng PreparedStatement ===");
            conn.setAutoCommit(false);
            try {
                String sql_update = "UPDATE SanPham SET gia = ?, soluong = ? WHERE maSP = ?";
                pstmt = conn.prepareStatement(sql_update);
                pstmt.setDouble(1, 29000000);
                pstmt.setInt(2, 18);
                pstmt.setInt(3, 2);
                int rowsAffected = pstmt.executeUpdate();
                conn.commit();
                System.out.println("Đã cập nhật " + rowsAffected + " sản phẩm!");
            } catch (SQLException e) {
                conn.rollback();
                System.out.println("Lỗi khi cập nhật, rollback: " + e.getMessage());
            }
            conn.setAutoCommit(true);
            pstmt.close();

            // Hiển thị sản phẩm sau khi cập nhật
            System.out.println("\n=== Sản phẩm sau khi cập nhật ===");
            String sql_select = "SELECT maSP, tenSP, gia, soluong FROM SanPham WHERE maSP = ?";
            pstmt = conn.prepareStatement(sql_select);
            pstmt.setInt(1, 2);
            rs = pstmt.executeQuery();

            while (rs.next()) {
                int maSP = rs.getInt("maSP");
                String tenSP = rs.getString("tenSP");
                double gia = rs.getDouble("gia");
                int soluong = rs.getInt("soluong");
                System.out.println("Mã SP: " + maSP + ", Tên SP: " + tenSP + ", Giá: " + gia + ", Số lượng: " + soluong);
            }
            rs.close();
            pstmt.close();

            // DELETE - Xóa sản phẩm sử dụng PreparedStatement
            System.out.println("\n=== Xóa sản phẩm sử dụng PreparedStatement ===");
            conn.setAutoCommit(false);
            try {
                String sql_delete = "DELETE FROM SanPham WHERE maSP = ?";
                pstmt = conn.prepareStatement(sql_delete);
                pstmt.setInt(1, 4);
                int rowsAffected = pstmt.executeUpdate();
                conn.commit();
                System.out.println("Đã xóa " + rowsAffected + " sản phẩm!");
            } catch (SQLException e) {
                conn.rollback();
                System.out.println("Lỗi khi xóa, rollback: " + e.getMessage());
            }
            conn.setAutoCommit(true);
            pstmt.close();

            // Hiển thị lại danh sách sau khi xóa
            System.out.println("\n=== Danh sách sản phẩm sau khi xóa ===");
            rs = stmt.executeQuery("SELECT maSP, tenSP, gia, soluong FROM SanPham");
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
                if (pstmt != null) {
                    pstmt.close();
                }
            } catch (SQLException e) {
                System.out.println("Lỗi khi đóng PreparedStatement: " + e.getMessage());
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
