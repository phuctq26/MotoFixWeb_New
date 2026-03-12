package com.motofix.dao;

import com.motofix.model.Part;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * DAO for the Parts table in MotoFixDBNew.
 */
public class PartDAO extends DBContext {
    
    PreparedStatement st;
    ResultSet rs;
    
    private Part map(ResultSet rs) throws SQLException {
        Part p = new Part();
        p.setPartId(rs.getInt("PartID"));
        p.setPartName(rs.getString("PartName"));
        p.setDescription(rs.getString("Description"));
        p.setImportPrice(rs.getDouble("ImportPrice"));
        p.setSellingPrice(rs.getDouble("SellingPrice"));
        p.setStockQuantity(rs.getInt("StockQuantity"));
        p.setImageUrl(rs.getString("ImageURL"));
        p.setActive(rs.getBoolean("IsActive"));
        return p;
    }

    public List<Part> listAll() throws SQLException {
        List<Part> parts = new ArrayList<>();
        try {
            String sql = """
                         SELECT PartID, PartName, Description, ImportPrice, SellingPrice,
                         StockQuantity, ImageURL, IsActive FROM Parts ORDER BY PartID DESC
                         """;
            st = connection.prepareStatement(sql);
            // truyen tham so cho cau lenh sql
            rs = st.executeQuery(); // select
            while (rs.next()) {
                int PartID = rs.getInt("PartID");
                String PartName = rs.getString("PartName");
                String Description = rs.getString("Description");
                double ImportPrice = rs.getDouble("ImportPrice");
                double SellingPrice = rs.getDouble("SellingPrice");
                int StockQuantity = rs.getInt("StockQuantity");
                String ImageURL = rs.getString("ImageURL");
                boolean IsActive = rs.getBoolean("IsActive");
                Part part = new Part(PartID, PartName, Description, ImportPrice, SellingPrice, StockQuantity, ImageURL, IsActive);
                parts.add(part);
            }
            return parts;
        } catch (Exception e) {
            return null;
        }
    }

    public Part findById(int id) throws SQLException {
        String sql = "SELECT PartID, PartName, Description, ImportPrice, SellingPrice, "
                + "StockQuantity, ImageURL, IsActive FROM Parts WHERE PartID=?";
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

    public void create(String name, String description, double importPrice, double sellingPrice,
            int stock, String imageUrl, boolean isActive) throws SQLException {
        String sql = "INSERT INTO Parts (PartName, Description, ImportPrice, SellingPrice, "
                + "StockQuantity, ImageURL, IsActive) VALUES (?,?,?,?,?,?,?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, name);
            stmt.setString(2, description);
            stmt.setDouble(3, importPrice);
            stmt.setDouble(4, sellingPrice);
            stmt.setInt(5, stock);
            stmt.setString(6, imageUrl != null ? imageUrl : "");
            stmt.setBoolean(7, isActive);
            stmt.executeUpdate();
        }
    }

    public void update(int id, String name, String description, double importPrice, double sellingPrice,
            int stock, String imageUrl, boolean isActive) throws SQLException {
        String sql = "UPDATE Parts SET PartName=?, Description=?, ImportPrice=?, SellingPrice=?, "
                + "StockQuantity=?, ImageURL=?, IsActive=? WHERE PartID=?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, name);
            stmt.setString(2, description);
            stmt.setDouble(3, importPrice);
            stmt.setDouble(4, sellingPrice);
            stmt.setInt(5, stock);
            stmt.setString(6, imageUrl != null ? imageUrl : "");
            stmt.setBoolean(7, isActive);
            stmt.setInt(8, id);
            stmt.executeUpdate();
        }
    }

    /** Soft delete: set IsActive = 0 */
    public void deactivate(int id) throws SQLException {
        String sql = "UPDATE Parts SET IsActive=0 WHERE PartID=?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        }
    }
}
