package com.motofix.dao;

import com.motofix.model.TicketItem;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class TicketItemDAO extends DBContext {
    PreparedStatement st;
    ResultSet rs;
    public List<TicketItem> listByTicket(int ticketId) throws SQLException {
        List<TicketItem> ticketitems = new ArrayList<>();
        try {
            String sql = """
                         SELECT * FROM (
                             SELECT 
                                 rsd.DetailID AS ID,
                                 rsd.OrderID AS TicketID,
                                 s.ServiceName AS ItemName,
                                 rsd.Quantity,
                                 rsd.UnitPrice,
                                 rsd.TotalPrice,
                                 'SERVICE' AS Type
                             FROM RepairServiceDetails rsd
                             JOIN Services s ON rsd.ServiceID = s.ServiceID
                         
                             UNION ALL
                         
                             SELECT
                                 rpd.DetailID AS ID,
                                 rpd.OrderID AS TicketID,
                                 p.PartName AS ItemName,
                                 rpd.Quantity,
                                 rpd.UnitPrice,
                                 rpd.TotalPrice,
                                 'PART' AS Type
                             FROM RepairPartDetails rpd
                             JOIN Parts p ON rpd.PartID = p.PartID
                         ) AS items
                         WHERE TicketID = ?
                         ORDER BY ID;
                         """;
            st = connection.prepareStatement(sql);
            // truyen tham so cho cau lenh sql
            st.setInt(1, ticketId);
            rs = st.executeQuery(); // select
            while (rs.next()) {
                int id = rs.getInt("ID");
                int TicketID = rs.getInt("TicketID");
                String ItemName = rs.getString("ItemName");
                int Quantity = rs.getInt("Quantity");
                double UnitPrice = rs.getDouble("UnitPrice");
                double TotalPrice = rs.getDouble("TotalPrice");
                String Type = rs.getString("Type");
                TicketItem ticket = new TicketItem(id, TicketID, ItemName, Quantity, UnitPrice, TotalPrice, Type);
                ticketitems.add(ticket);
            }
            return ticketitems;
        } catch (Exception e) {
            return null;
        }
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
