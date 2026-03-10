package com.motofix.dao;

import com.motofix.controller.DBContext;
import com.motofix.model.Service;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO for the Services table in MotoFixDBNew.
 */
public class ServiceDAO extends DBContext {

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
        String sql = "SELECT ServiceID, ServiceName, Description, Price, IsActive "
                + "FROM Services ORDER BY ServiceID DESC";
        List<Service> list = new ArrayList<>();
        try (PreparedStatement stmt = connection.prepareStatement(sql);
                ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                list.add(map(rs));
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
