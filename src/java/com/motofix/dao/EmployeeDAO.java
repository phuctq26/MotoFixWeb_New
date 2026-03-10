package com.motofix.dao;

import com.motofix.controller.DBContext;
import com.motofix.model.Employee;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO for Employees table in MotoFixDBNew.
 * FullName and Phone are optional columns — add them with:
 * ALTER TABLE Employees ADD FullName NVARCHAR(100), Phone VARCHAR(20);
 */
public class EmployeeDAO extends DBContext {

    /** Try to detect if FullName column exists (cached after first check) */
    private static Boolean hasExtraColumns = null;

    private boolean checkExtraColumns() {
        if (hasExtraColumns != null)
            return hasExtraColumns;
        try (PreparedStatement st = connection.prepareStatement(
                "SELECT TOP 1 FullName FROM Employees")) {
            st.executeQuery();
            hasExtraColumns = true;
        } catch (SQLException e) {
            hasExtraColumns = false;
        }
        return hasExtraColumns;
    }

    private Employee map(ResultSet rs, boolean hasExtra) throws SQLException {
        Employee e = new Employee();
        e.setEmployeeId(rs.getInt("EmployeeID"));
        e.setPosition(rs.getString("Position"));
        try {
            e.setSalary(rs.getLong("Salary"));
        } catch (SQLException ignored) {
        }
        Date hireDate = rs.getDate("HireDate");
        e.setHireDate(hireDate != null ? hireDate.toString() : "");
        e.setActive(rs.getBoolean("Status"));
        if (hasExtra) {
            try {
                e.setFullName(rs.getString("FullName"));
            } catch (SQLException ignored) {
            }
            try {
                e.setPhone(rs.getString("Phone"));
            } catch (SQLException ignored) {
            }
        }
        return e;
    }

    public List<Employee> listAll() throws SQLException {
        List<Employee> list = new ArrayList<>();
        boolean extra = checkExtraColumns();
        String sql = extra
                ? "SELECT EmployeeID, FullName, Phone, Position, Salary, HireDate, Status FROM Employees ORDER BY EmployeeID DESC"
                : "SELECT EmployeeID, Position, Salary, HireDate, Status FROM Employees ORDER BY EmployeeID DESC";
        try (PreparedStatement stmt = connection.prepareStatement(sql);
                ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                list.add(map(rs, extra));
            }
        }
        return list;
    }

    /**
     * Finds by phone for duplicate check. Returns null when Phone column missing.
     */
    public Employee findByPhone(String phone) throws SQLException {
        if (phone == null || phone.isEmpty())
            return null;
        try {
            if (!checkExtraColumns())
                return null; // column missing, skip check
            String sql = "SELECT EmployeeID, FullName, Phone, Position, Salary, HireDate, Status "
                    + "FROM Employees WHERE Phone=?";
            try (PreparedStatement stmt = connection.prepareStatement(sql)) {
                stmt.setString(1, phone);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next())
                        return map(rs, true);
                }
            }
        } catch (SQLException ignored) {
        }
        return null;
    }

    public void create(String fullName, String phone, String position,
            long salary, String hireDate, boolean status) throws SQLException {
        boolean extra = checkExtraColumns();
        if (extra) {
            String sql = "INSERT INTO Employees (FullName, Phone, Position, Salary, HireDate, Status) VALUES (?,?,?,?,?,?)";
            try (PreparedStatement stmt = connection.prepareStatement(sql)) {
                stmt.setString(1, fullName != null ? fullName : "");
                stmt.setString(2, phone != null ? phone : "");
                stmt.setString(3, position);
                stmt.setLong(4, salary);
                setDateOrNow(stmt, 5, hireDate);
                stmt.setBoolean(6, status);
                stmt.executeUpdate();
            }
        } else {
            String sql = "INSERT INTO Employees (Position, Salary, HireDate, Status) VALUES (?,?,?,?)";
            try (PreparedStatement stmt = connection.prepareStatement(sql)) {
                stmt.setString(1, position);
                stmt.setLong(2, salary);
                setDateOrNow(stmt, 3, hireDate);
                stmt.setBoolean(4, status);
                stmt.executeUpdate();
            }
        }
    }

    public void update(int id, String fullName, String phone, String position,
            long salary, String hireDate, boolean status) throws SQLException {
        boolean extra = checkExtraColumns();
        if (extra) {
            String sql = "UPDATE Employees SET FullName=?, Phone=?, Position=?, Salary=?, HireDate=?, Status=? WHERE EmployeeID=?";
            try (PreparedStatement stmt = connection.prepareStatement(sql)) {
                stmt.setString(1, fullName != null ? fullName : "");
                stmt.setString(2, phone != null ? phone : "");
                stmt.setString(3, position);
                stmt.setLong(4, salary);
                setDateOrNull(stmt, 5, hireDate);
                stmt.setBoolean(6, status);
                stmt.setInt(7, id);
                stmt.executeUpdate();
            }
        } else {
            String sql = "UPDATE Employees SET Position=?, Salary=?, HireDate=?, Status=? WHERE EmployeeID=?";
            try (PreparedStatement stmt = connection.prepareStatement(sql)) {
                stmt.setString(1, position);
                stmt.setLong(2, salary);
                setDateOrNull(stmt, 3, hireDate);
                stmt.setBoolean(4, status);
                stmt.setInt(5, id);
                stmt.executeUpdate();
            }
        }
    }

    /** Soft delete: set Status = 0 */
    public void deactivate(int id) throws SQLException {
        String sql = "UPDATE Employees SET Status=0 WHERE EmployeeID=?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        }
    }

    // ── helpers ────────────────────────────────────────────────────────────────
    private void setDateOrNow(PreparedStatement stmt, int idx, String dateStr) throws SQLException {
        if (dateStr != null && !dateStr.isEmpty()) {
            stmt.setDate(idx, Date.valueOf(dateStr));
        } else {
            stmt.setDate(idx, new Date(System.currentTimeMillis()));
        }
    }

    private void setDateOrNull(PreparedStatement stmt, int idx, String dateStr) throws SQLException {
        if (dateStr != null && !dateStr.isEmpty()) {
            stmt.setDate(idx, Date.valueOf(dateStr));
        } else {
            stmt.setNull(idx, Types.DATE);
        }
    }
}
