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
        String sql = "SELECT * FROM Parts ORDER BY PartID DESC";
        try (PreparedStatement stmt = connection.prepareStatement(sql); ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                parts.add(map(rs));
            }
        }
        return parts;
    }
    public List<Part> listAll1() throws SQLException {
        List<Part> parts = new ArrayList<>();
        String sql = "SELECT * FROM Parts where IsActive=1 ORDER BY PartID DESC";
        try (PreparedStatement stmt = connection.prepareStatement(sql); ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                parts.add(map(rs));
            }
        }
        return parts;
    }

    public int countAll(String searchValue) throws SQLException {
        String sql = "SELECT COUNT(*) FROM Parts";
        if (searchValue != null && !searchValue.trim().isEmpty()) {
            sql += " WHERE PartName LIKE ? OR Description LIKE ?";
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

    public List<Part> listPaged(String searchValue, int offset, int limit) throws SQLException {
        List<Part> list = new ArrayList<>();
        String sql = "SELECT * FROM Parts";
        if (searchValue != null && !searchValue.trim().isEmpty()) {
            sql += " WHERE PartName LIKE ? OR Description LIKE ?";
        }
        sql += " ORDER BY PartID DESC OFFSET ? ROWS FETCH NEXT ? ROWS ONLY";
        
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

    /**
     * Soft delete: set IsActive = 0
     */
    public void deactivate(int id) throws SQLException {
        String sql = "UPDATE Parts SET IsActive=0 WHERE PartID=?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, id);
            stmt.executeUpdate();
        }
    }

    public void updateStock(int partId, int newQuantity) {
        try{
            String sql = """
                           UPDATE Parts SET StockQuantity = ? WHERE PartID = ?
                         """;
            st = connection.prepareStatement(sql);
            // truyen tham so cho cau lenh sql
            
            st.setInt(1, newQuantity);
            st.setInt(2, partId);
            
            st.executeUpdate();           
        }catch(SQLException e){
            
        }
    }
}
