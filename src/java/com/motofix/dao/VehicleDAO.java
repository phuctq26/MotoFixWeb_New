package com.motofix.dao;

import com.motofix.controller.DBContext;
import com.motofix.model.Vehicle;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class VehicleDAO extends DBContext {

    public Integer findByOwnerAndPlate(int ownerId, String plateNumber) throws SQLException {
        String sql = "SELECT VehicleID FROM Vehicles WHERE OwnerID = ? AND PlateNumber = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, ownerId);
            stmt.setString(2, plateNumber);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("VehicleID");
                }
            }
        }
        return null;
    }

    public int create(int ownerId, String plateNumber, String brand, String model) throws SQLException {
        String sql = "INSERT INTO Vehicles (OwnerID, PlateNumber, Brand, Model) VALUES (?, ?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, ownerId);
            stmt.setString(2, plateNumber);
            stmt.setString(3, brand);
            stmt.setString(4, model);
            stmt.executeUpdate();
            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        }
        throw new SQLException("Create vehicle failed");
    }

    public List<Vehicle> listByOwner(int ownerId) throws SQLException {
        String sql = "SELECT VehicleID, OwnerID, PlateNumber, Brand, Model FROM Vehicles "
                + "WHERE OwnerID = ? ORDER BY CreatedAt DESC";
        List<Vehicle> items = new ArrayList<>();
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, ownerId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    Vehicle v = new Vehicle();
                    v.setVehicleId(rs.getInt("VehicleID"));
                    v.setOwnerId(rs.getInt("OwnerID"));
                    v.setPlateNumber(rs.getString("PlateNumber"));
                    v.setBrand(rs.getString("Brand"));
                    v.setModel(rs.getString("Model"));
                    items.add(v);
                }
            }
        }
        return items;
    }
}
