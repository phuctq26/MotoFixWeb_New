package com.motofix.dao;

import com.motofix.model.Vehicle;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class VehicleDAO extends DBContext {
    PreparedStatement st;
    ResultSet rs;
    public VehicleDAO() {
        super();
    }

    public VehicleDAO(Connection conn) {
        this.connection = conn;
    }

    // Tìm xe theo CustomerID + biển số
    public Integer findByOwnerAndPlate(int ownerId, String plateNumber) throws SQLException {

        String sql = "SELECT VehicleID FROM Vehicles WHERE CustomerID = ? AND PlateNumber = ?";

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

    // Tạo xe mới
    public int create(int ownerId, String plateNumber, String brand, String model) throws SQLException {

        int vehicleID = -1;
        try {
            String sql = """
                INSERT INTO Vehicles
                    (CustomerID, PlateNumber, Brand, Model, CreatedAt)
                VALUES
                    (?, ?, ?, ?, GETDATE());
                """;
            st = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
            st.setInt(1, ownerId);
            st.setString(2, plateNumber);
            st.setString(3, brand);
            st.setString(4, model);
            st.executeUpdate();
            ResultSet rs = st.getGeneratedKeys();
            if (rs.next()) {
                vehicleID = rs.getInt(1);  
            }
            return vehicleID;
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return -1;
    }

    // Lấy danh sách xe của 1 user theo AccountID
    public List<Vehicle> listByOwner(int accountId) throws SQLException {

        List<Vehicle> vehicles = new ArrayList<>();
        try {
            String sql = """
                         select distinct
                             v.VehicleID,
                             a.AccountID,
                             v.PlateNumber,
                             v.Brand,
                             v.Model
                         from Vehicles v
                         join Customers c on v.CustomerID = c.CustomerID
                         join Accounts a on a.AccountID = c.AccountID
                         where a.AccountID = ?
                         """;
            st = connection.prepareStatement(sql);
            st.setInt(1, accountId);
            rs = st.executeQuery();
            while (rs.next()) {

                int VehicleID = rs.getInt("VehicleID");
                int AccountID = rs.getInt("AccountID");
                String PlateNumber = rs.getString("PlateNumber");
                String Brand = rs.getString("Brand");
                String Model = rs.getString("Model");

                Vehicle acc = new Vehicle(VehicleID, AccountID, PlateNumber, Brand, Model);
                vehicles.add(acc);
            }
            return vehicles;
        } catch (Exception e) {
            return null;
        }
    }
    
    // Lấy danh sách xe của 1 user theo USerID
    public List<Vehicle> listByOwner1(int userId) throws SQLException {

        List<Vehicle> vehicles = new ArrayList<>();
        try {
            String sql = """
                            select distinct
                                v.VehicleID,
                                c.CustomerID,
                                v.PlateNumber,
                                v.Brand,
                                v.Model
                            from Vehicles v
                            join Customers c on v.CustomerID = c.CustomerID
                            join Accounts a on a.AccountID = c.AccountID
                            where c.CustomerID = ?
                         """;
            st = connection.prepareStatement(sql);
            st.setInt(1, userId);
            rs = st.executeQuery();
            while (rs.next()) {

                int VehicleID = rs.getInt("VehicleID");
                int CustomerID = rs.getInt("CustomerID");
                String PlateNumber = rs.getString("PlateNumber");
                String Brand = rs.getString("Brand");
                String Model = rs.getString("Model");

                Vehicle acc = new Vehicle(VehicleID, CustomerID, PlateNumber, Brand, Model);
                vehicles.add(acc);
            }
            return vehicles;
        } catch (Exception e) {
            return null;
        }
    }

    public Integer getVehicleFromBooking(int customerId) {

    String sql = """
        SELECT VehicleID
        FROM Bookings
        WHERE CustomerID = ?
        ORDER BY BookingID DESC
    """;

    try {
        st = connection.prepareStatement(sql);

        st.setInt(1, customerId);

        ResultSet rs = st.executeQuery();

        if (rs.next()) {
            int v = rs.getInt("VehicleID");
            return rs.wasNull() ? null : v;
        }

    } catch (Exception e) {
        e.printStackTrace();
    }

    return null;
 }
}
