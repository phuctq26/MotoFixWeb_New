package com.motofix.dao;

import com.motofix.model.Employee;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO cho bảng Employees.
 * Các cột FullName, Phone, Position, Salary, HireDate, Status (BIT) đã được xác nhận tồn tại.
 */
public class EmployeeDAO extends DBContext {

    private Employee map(ResultSet rs) throws SQLException {
        Employee e = new Employee();
        e.setEmployeeId(rs.getInt("EmployeeID"));
        e.setFullName(rs.getString("FullName"));
        e.setPhone(rs.getString("Phone"));
        e.setPosition(rs.getString("Position"));
        e.setSalary(rs.getLong("Salary"));
        Date hireDate = rs.getDate("HireDate");
        e.setHireDate(hireDate != null ? hireDate.toString() : "");
        e.setActive(rs.getBoolean("Status"));
        return e;
    }

    public List<Employee> listAll() throws SQLException {
        List<Employee> list = new ArrayList<>();
        String sql = "SELECT * FROM Employees ORDER BY EmployeeID DESC";
        try (PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                list.add(map(rs));
            }
        }
        return list;
    }

    public int countAll() throws SQLException {
        String sql = "SELECT COUNT(*) FROM Employees";
        try (PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) return rs.getInt(1);
        }
        return 0;
    }

    public List<Employee> listPaged(int offset, int limit) throws SQLException {
        List<Employee> list = new ArrayList<>();
        String sql = "SELECT * FROM Employees ORDER BY EmployeeID DESC "
                   + "OFFSET ? ROWS FETCH NEXT ? ROWS ONLY";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, offset);
            stmt.setInt(2, limit);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    list.add(map(rs));
                }
            }
        }
        return list;
    }

    public Employee findByPhone(String phone) throws SQLException {
        if (phone == null || phone.isEmpty()) return null;
        String sql = "SELECT * FROM Employees WHERE Phone = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, phone);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next() ? map(rs) : null;
            }
        }
    }

    public void create(String fullName, String phone, String position,
                       long salary, String hireDate, boolean isActive) throws SQLException {
        String sql = "INSERT INTO Employees (FullName, Phone, Position, Salary, HireDate, Status) VALUES (?,?,?,?,?,?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, fullName);
            stmt.setString(2, phone);
            stmt.setString(3, position);
            stmt.setLong(4, salary);
            stmt.setDate(5, (hireDate == null || hireDate.isEmpty()) ? new Date(System.currentTimeMillis()) : Date.valueOf(hireDate));
            stmt.setInt(6, isActive ? 1 : 0);
            stmt.executeUpdate();
        }
    }

    public void update(int id, String fullName, String phone, String position,
                       long salary, String hireDate, boolean isActive) throws SQLException {
        String sql = "UPDATE Employees SET FullName=?, Phone=?, Position=?, Salary=?, HireDate=?, Status=? WHERE EmployeeID=?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, fullName);
            stmt.setString(2, phone);
            stmt.setString(3, position);
            stmt.setLong(4, salary);
            if (hireDate == null || hireDate.isEmpty()) stmt.setNull(5, Types.DATE);
            else stmt.setDate(5, Date.valueOf(hireDate));
            stmt.setInt(6, isActive ? 1 : 0);
            stmt.setInt(7, id);
            stmt.executeUpdate();
        }
    }

    public void deactivate(int id) throws SQLException {
        String sql = "UPDATE Employees SET Status = 0 WHERE EmployeeID = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        }
    }
}
