package com.motofix.dao;

import com.motofix.model.Booking;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class BookingDAO extends DBContext {
    
    PreparedStatement st;
    ResultSet rs;
    
    public void create(int customerId, Integer vehicleId, Timestamp bookingDate, String note) throws SQLException {
        String sql = "INSERT INTO Bookings (CustomerID, VehicleID, BookingDate, Status, Note) VALUES (?, ?, ?, 'PENDING', ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, customerId);
            if (vehicleId == null) {
                stmt.setNull(2, Types.INTEGER);
            } else {
                stmt.setInt(2, vehicleId);
            }
            stmt.setTimestamp(3, bookingDate);
            stmt.setString(4, note);
            System.out.println("INSERT BOOKING: " + customerId + " - " + vehicleId + " - " + bookingDate);
            stmt.executeUpdate();
        }
    }
    
    
    // lay tat ca danh sach booking
    public List<Booking> listAll() throws SQLException {
        List<Booking> bookings = new ArrayList<>();
        try {
            String sql = """
                         select 
                         b.BookingID, CONCAT(a.lastName, ' ',a.firstName) as FullName, a.Username,
                         v.PlateNumber, b.BookingDate, b.Status, b.Note
                         from Bookings as b
                         join Customers as c on b.CustomerID = c.CustomerID
                         join Accounts as a on c.AccountID = a.AccountID
                         left join Vehicles as v on v.VehicleID = b.VehicleID
                         ORDER BY b.BookingDate DESC
                         """;
            st = connection.prepareStatement(sql);
            // truyen tham so cho cau lenh sql
            rs = st.executeQuery(); // select
            while (rs.next()) {
                int BookingID = rs.getInt("BookingID");
                String FullName = rs.getString("FullName");
                String Username = rs.getString("Username");
                String PlateNumber = rs.getString("PlateNumber");
                Timestamp bookingDate = rs.getTimestamp("BookingDate");
                String Status = rs.getString("Status");
                String Note = rs.getString("Note");
                Booking booking = new Booking(BookingID, FullName, Username, PlateNumber, bookingDate, Status, Note);
                bookings.add(booking);
            }
            return bookings;
        } catch (Exception e) {
            return null;
        }
    }
    
    public int countBookings(String filterStatus, String searchValue) throws SQLException {
        String sql = """
            SELECT COUNT(*) FROM Bookings b
            JOIN Customers c on b.CustomerID = c.CustomerID
            JOIN Accounts a on c.AccountID = a.AccountID
            LEFT JOIN Vehicles v on v.VehicleID = b.VehicleID
            WHERE 1=1
        """;
        
        if ("pending".equals(filterStatus)) {
            sql += " AND b.Status = 'PENDING'";
        } else if ("processed".equals(filterStatus)) {
            sql += " AND b.Status IN ('CONFIRMED', 'CANCELLED')";
        }
        
        if (searchValue != null && !searchValue.trim().isEmpty()) {
            sql += " AND (CONCAT(a.FirstName, ' ', a.LastName) LIKE ? OR a.Username LIKE ? OR v.PlateNumber LIKE ?)";
        }
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            if (searchValue != null && !searchValue.trim().isEmpty()) {
                String k = "%" + searchValue.trim() + "%";
                stmt.setString(1, k);
                stmt.setString(2, k);
                stmt.setString(3, k);
            }
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) return rs.getInt(1);
            }
        }
        return 0;
    }
    
    public List<Booking> listBookingsPaged(String filterStatus, String searchValue, int offset, int limit) throws SQLException {
        List<Booking> bookings = new ArrayList<>();
        String sql = """
                     select 
                     b.BookingID, CONCAT(a.lastName, ' ',a.firstName) as FullName, a.Username,
                     v.PlateNumber, b.BookingDate, b.Status, b.Note
                     from Bookings as b
                     join Customers as c on b.CustomerID = c.CustomerID
                     join Accounts as a on c.AccountID = a.AccountID
                     left join Vehicles as v on v.VehicleID = b.VehicleID
                     WHERE 1=1
                     """;
                     
        if ("pending".equals(filterStatus)) {
            sql += " AND b.Status = 'PENDING'";
        } else if ("processed".equals(filterStatus)) {
            sql += " AND b.Status IN ('CONFIRMED', 'CANCELLED')";
        }
        
        if (searchValue != null && !searchValue.trim().isEmpty()) {
            sql += " AND (CONCAT(a.FirstName, ' ', a.LastName) LIKE ? OR a.Username LIKE ? OR v.PlateNumber LIKE ?)";
        }
                     
        sql += " ORDER BY b.BookingDate DESC OFFSET ? ROWS FETCH NEXT ? ROWS ONLY";

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            int paramIdx = 1;
            if (searchValue != null && !searchValue.trim().isEmpty()) {
                String k = "%" + searchValue.trim() + "%";
                stmt.setString(paramIdx++, k);
                stmt.setString(paramIdx++, k);
                stmt.setString(paramIdx++, k);
            }
            stmt.setInt(paramIdx++, offset);
            stmt.setInt(paramIdx, limit);
            
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    int BookingID = rs.getInt("BookingID");
                    String FullName = rs.getString("FullName");
                    String Username = rs.getString("Username");
                    String PlateNumber = rs.getString("PlateNumber");
                    Timestamp bookingDate = rs.getTimestamp("BookingDate");
                    String Status = rs.getString("Status");
                    String Note = rs.getString("Note");
                    Booking booking = new Booking(BookingID, FullName, Username, PlateNumber, bookingDate, Status, Note);
                    bookings.add(booking);
                }
            }
        }
        return bookings;
    }

    public void updateStatus(int bookingId, String status) throws SQLException {
        String sql = "UPDATE Bookings SET Status = ? WHERE BookingID = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, status);
            stmt.setInt(2, bookingId);
            stmt.executeUpdate();
        }
    }
    
    // tim kiem theo bien so xe, phone, name
    public Object findALLByValue(String value) {
        List<Booking> bookings = new ArrayList<>();
        try {
            String sql = """
                         select 
                         b.BookingID, CONCAT(a.lastName, ' ',a.firstName) as FullName, a.Username,
                         v.PlateNumber, b.BookingDate, b.Status, b.Note
                         from Bookings as b
                         join Customers as c on b.CustomerID = c.CustomerID
                         join Accounts as a on c.AccountID = a.AccountID
                         left join Vehicles as v on v.VehicleID = b.VehicleID
                         WHERE 
                         CONCAT(a.FirstName, ' ', a.LastName) LIKE ? OR
                         a.Username LIKE ? OR
                         v.PlateNumber LIKE ?
                         ORDER BY b.BookingDate DESC
                         """;
            st = connection.prepareStatement(sql);
            // truyen tham so cho cau lenh sql
            String k = "%" + value + "%";
            st.setString(1, k);
            st.setString(2, k);
            st.setString(3, k);
            rs = st.executeQuery(); // select
            while (rs.next()) {
                int BookingID = rs.getInt("BookingID");
                String FullName = rs.getString("FullName");
                String Username = rs.getString("Username");
                String PlateNumber = rs.getString("PlateNumber");
                Timestamp bookingDate = rs.getTimestamp("BookingDate");
                String Status = rs.getString("Status");
                String Note = rs.getString("Note");
                Booking booking = new Booking(BookingID, FullName, Username, PlateNumber, bookingDate, Status, Note);
                bookings.add(booking);
            }
            return bookings;
        } catch (Exception e) {
            return null;
        }
    }
}
