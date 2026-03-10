package com.motofix.dao;

import com.motofix.controller.DBContext;
import com.motofix.model.TicketItem;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TicketItemDAO extends DBContext {

    public List<TicketItem> listByTicket(int ticketId) throws SQLException {
        String sql = "SELECT ID, TicketID, ItemName, Quantity, UnitPrice, TotalPrice, Type "
                + "FROM TicketItems WHERE TicketID = ?";
        List<TicketItem> items = new ArrayList<>();
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, ticketId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    TicketItem item = new TicketItem();
                    item.setId(rs.getInt("ID"));
                    item.setTicketId(rs.getInt("TicketID"));
                    item.setItemName(rs.getString("ItemName"));
                    item.setQuantity(rs.getInt("Quantity"));
                    item.setUnitPrice(rs.getDouble("UnitPrice"));
                    item.setTotalPrice(rs.getDouble("TotalPrice"));
                    item.setType(rs.getString("Type"));
                    items.add(item);
                }
            }
        }
        return items;
    }

    public void addItem(int ticketId, Integer itemId, String itemName, int quantity,
            double unitPrice, String type) throws SQLException {
        String sql = "INSERT INTO TicketItems (TicketID, ItemID, ItemName, Quantity, UnitPrice, Type) "
                + "VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, ticketId);
            if (itemId == null) {
                stmt.setNull(2, Types.INTEGER);
            } else {
                stmt.setInt(2, itemId);
            }
            stmt.setString(3, itemName);
            stmt.setInt(4, quantity);
            stmt.setDouble(5, unitPrice);
            stmt.setString(6, type);
            stmt.executeUpdate();
        }
    }
}
