package com.motofix.dao;

import com.motofix.model.Service;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO for the Services table in MotoFixDBNew.
 */
public class ServiceDAO extends DBContext {
    PreparedStatement st;
    ResultSet rs;
    private Service map(ResultSet rs) throws SQLException {
        Service s = new Service();
        s.setServiceId(rs.getInt("ServiceID"));
        s.setServiceName(rs.getString("ServiceName"));
        s.setDescription(rs.getString("Description"));
        s.setPrice(rs.getDouble("Price"));
        s.setActive(rs.getBoolean("IsActive"));
        return s;
    }

    public List<Service> listAll() throws SQLException {
        List<Service> services = new ArrayList<>();
        String sql = "SELECT * FROM Services ORDER BY ServiceID DESC";
        try (PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                services.add(map(rs));
            }
        }
        return services;
    }
    public List<Service> listAll1() throws SQLException {
        List<Service> services = new ArrayList<>();
        String sql = "SELECT * FROM Services where isActive = 1 ORDER BY ServiceID DESC ";
        try (PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                services.add(map(rs));
            }
        }
        return services;
    }

    public List<Service> listTop4() throws SQLException {
        List<Service> services = new ArrayList<>();
        String sql = "SELECT TOP 4 * FROM Services WHERE IsActive = 1 ORDER BY ServiceID ASC";
        try (PreparedStatement stmt = connection.prepareStatement(sql);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                services.add(map(rs));
            }
        }
        return services;
    }

    public int countAll(String searchValue) throws SQLException {
        String sql = "SELECT COUNT(*) FROM Services";
        if (searchValue != null && !searchValue.trim().isEmpty()) {
            sql += " WHERE ServiceName LIKE ? OR Description LIKE ?";
        }
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            if (searchValue != null && !searchValue.trim().isEmpty()) {
                String k = "%" + searchValue.trim() + "%";
                stmt.setString(1, k);
                stmt.setString(2, k);
            }
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) return rs.getInt(1);
            }
        }
        return 0;
    }

    public List<Service> listPaged(String searchValue, int offset, int limit) throws SQLException {
        List<Service> list = new ArrayList<>();
        String sql = "SELECT * FROM Services";
        if (searchValue != null && !searchValue.trim().isEmpty()) {
            sql += " WHERE ServiceName LIKE ? OR Description LIKE ?";
        }
        sql += " ORDER BY ServiceID DESC OFFSET ? ROWS FETCH NEXT ? ROWS ONLY";
        
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            int paramIdx = 1;
            if (searchValue != null && !searchValue.trim().isEmpty()) {
                String k = "%" + searchValue.trim() + "%";
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

    public Service findById(int id) throws SQLException {
        String sql = "SELECT ServiceID, ServiceName, Description, Price, IsActive "
                + "FROM Services WHERE ServiceID=?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return map(rs);
                }
            }
        }
        return null;
    }

    public void create(String name, String description, double price, boolean isActive) throws SQLException {
        String sql = "INSERT INTO Services (ServiceName, Description, Price, IsActive) VALUES (?,?,?,?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, name);
            stmt.setString(2, description);
            stmt.setDouble(3, price);
            stmt.setBoolean(4, isActive);
            stmt.executeUpdate();
        }
    }

    public void update(int id, String name, String description, double price, boolean isActive) throws SQLException {
        String sql = "UPDATE Services SET ServiceName=?, Description=?, Price=?, IsActive=? WHERE ServiceID=?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, name);
            stmt.setString(2, description);
            stmt.setDouble(3, price);
            stmt.setBoolean(4, isActive);
            stmt.setInt(5, id);
            stmt.executeUpdate();
        }
    }

    /** Soft delete: set IsActive = 0 */
    public void deactivate(int id) throws SQLException {
        String sql = "UPDATE Services SET IsActive=0 WHERE ServiceID=?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        }
    }
}
