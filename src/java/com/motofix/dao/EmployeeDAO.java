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
     PreparedStatement st;
    ResultSet rs;
    
    private Employee map(ResultSet rs) throws SQLException {
        Employee e = new Employee();
        e.setEmployeeId(rs.getInt("EmployeeID"));
        e.setFullName(rs.getString("FullName"));
        e.setPhone(rs.getString("Phone"));
        e.setPosition(rs.getString("Position"));
        e.setSalary(rs.getLong("Salary"));
        Date hireDate = rs.getDate("HireDate");
        e.setHireDate(hireDate != null ? hireDate.toString() : "");
        e.setStatus(rs.getInt("Status"));
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
    
    public List<Employee> listAllBySatus1() throws SQLException {
        List<Employee> list = new ArrayList<>();
        String sql = """
                     SELECT * FROM Employees e
                       where e.Position=N'Kỹ thuật viên' and e.Status in (1,2)
                     ORDER BY status DESC
                     """;
        try (PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                list.add(map(rs));
            }
        }
        return list;
    }
    public List<Employee> listAllByStatus1() throws SQLException {
        List<Employee> list = new ArrayList<>();
        String sql = """
                     SELECT * FROM Employees
                     
                     ORDER BY status DESC
                     """;
        try (PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                list.add(map(rs));
            }
        }
        return list;
    }
    
    public List<Employee> listAllBySatus(int id) throws SQLException {
        List<Employee> list = new ArrayList<>();
        String sql = """
                     SELECT * FROM Employees WHERE Status = 2 OR EmployeeID = ?
                     ORDER BY status DESC
                     """;
        try (PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            stmt.setInt(1, id);
            while (rs.next()) {
                list.add(map(rs));
            }
        }
        return list;
    }

    public int countAll(String searchValue) throws SQLException {
        String sql = "SELECT COUNT(*) FROM Employees";
        if (searchValue != null && !searchValue.trim().isEmpty()) {
            sql += " WHERE FullName LIKE ? OR Position LIKE ? OR Phone LIKE ?";
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

    public List<Employee> listPaged(String searchValue, int offset, int limit) throws SQLException {
        List<Employee> list = new ArrayList<>();
        String sql = "SELECT * FROM Employees ";
        if (searchValue != null && !searchValue.trim().isEmpty()) {
            sql += " WHERE FullName LIKE ? OR Position LIKE ? OR Phone LIKE ?";
        }
        sql += " ORDER BY EmployeeID DESC OFFSET ? ROWS FETCH NEXT ? ROWS ONLY";
        
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
                       long salary, String hireDate, int status) throws SQLException {
        String sql = "INSERT INTO Employees (FullName, Phone, Position, Salary, HireDate, Status) VALUES (?,?,?,?,?,?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, fullName);
            stmt.setString(2, phone);
            stmt.setString(3, position);
            stmt.setLong(4, salary);
            stmt.setDate(5, (hireDate == null || hireDate.isEmpty()) ? new Date(System.currentTimeMillis()) : Date.valueOf(hireDate));
            stmt.setInt(6, status);
            stmt.executeUpdate();
        }
    }

    public void update(int id, String fullName, String phone, String position,
                       long salary, String hireDate, int status) throws SQLException {
        String sql = "UPDATE Employees SET FullName=?, Phone=?, Position=?, Salary=?, HireDate=?, Status=? WHERE EmployeeID=?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, fullName);
            stmt.setString(2, phone);
            stmt.setString(3, position);
            stmt.setLong(4, salary);
            if (hireDate == null || hireDate.isEmpty()) stmt.setNull(5, Types.DATE);
            else stmt.setDate(5, Date.valueOf(hireDate));
            stmt.setInt(6, status);
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

    public void updateStatus(int parseInt, int status) {
        try{
            String sql = """
                            UPDATE Employees
                            SET Status = ?
                            WHERE EmployeeID = ?;
                         """;
            st = connection.prepareStatement(sql);
            // truyen tham so cho cau lenh sql
            st.setInt(1, status);
            st.setInt(2, parseInt);
            st.executeUpdate();           
        }catch(SQLException e){
            
        }
    }

    public Employee findByID(int parseInt) {
        Employee acc; 
        try{
            String sql = """
                            SELECT  [EmployeeID]
                                  ,[Position]
                                  ,[Salary]
                                  ,[HireDate]
                                  ,[FullName]
                                  ,[Phone]
                                  ,[Status]
                              FROM [[Employees]
                              where EmployeeID = ?
                         """;
            st = connection.prepareStatement(sql);
            // truyen tham so cho cau lenh sql
            st.setInt(1, parseInt);
            rs = st.executeQuery();
            if(rs.next()){
                acc = new Employee();
                acc.setFullName(rs.getString("FullName"));
                acc.setPhone(rs.getString("Phone"));
                acc.setPosition(rs.getString("Position"));
                acc.setSalary(rs.getLong("Salary"));
                acc.setStatus(rs.getInt("Status"));
                return acc;
            }
            else{
                return null;
            }
        }catch(Exception e){
            return null;
        }
    }
}
