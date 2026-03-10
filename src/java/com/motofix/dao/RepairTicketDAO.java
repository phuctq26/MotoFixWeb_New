package com.motofix.dao;

import com.motofix.controller.DBContext;
import com.motofix.model.RepairTicket;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RepairTicketDAO extends DBContext {

    // ── helper mapper ─────────────────────────────────────────────────────────
    private RepairTicket mapTicket(ResultSet rs) throws SQLException {
        RepairTicket t = new RepairTicket();
        t.setTicketId(rs.getInt("TicketID"));
        t.setTicketCode(rs.getString("TicketCode"));
        t.setStatus(rs.getString("Status"));
        t.setPaymentStatus(rs.getString("PaymentStatus"));
        t.setFinalAmount(rs.getDouble("FinalAmount"));
        t.setTotalAmount(rs.getDouble("TotalAmount"));
        t.setDiscount(rs.getDouble("Discount"));
        t.setCustomerName(rs.getString("FullName"));
        t.setPhone(rs.getString("Phone"));
        t.setPlateNumber(rs.getString("PlateNumber"));
        t.setCreatedAt(rs.getTimestamp("CreatedAt"));
        return t;
    }

    private static final String BASE_SELECT = "SELECT t.TicketID, t.TicketCode, t.Status, t.PaymentStatus, "
            + "t.FinalAmount, t.TotalAmount, t.Discount, "
            + "u.FullName, u.Phone, v.PlateNumber, t.CreatedAt "
            + "FROM RepairTickets t "
            + "JOIN Users u ON t.CustomerID = u.UserID "
            + "JOIN Vehicles v ON t.VehicleID = v.VehicleID ";

    // ── CRUD ──────────────────────────────────────────────────────────────────
    public int create(int customerId, int vehicleId, String diagnosis) throws SQLException {
        String sql = "INSERT INTO RepairTickets (CustomerID, VehicleID, Diagnosis, Status) "
                + "VALUES (?, ?, ?, 'OPEN')";
        try (PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, customerId);
            stmt.setInt(2, vehicleId);
            stmt.setString(3, diagnosis);
            stmt.executeUpdate();
            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    int ticketId = rs.getInt(1);
                    String code = "TK-" + ticketId;
                    try (PreparedStatement up = connection.prepareStatement(
                            "UPDATE RepairTickets SET TicketCode = ? WHERE TicketID = ?")) {
                        up.setString(1, code);
                        up.setInt(2, ticketId);
                        up.executeUpdate();
                    }
                    return ticketId;
                }
            }
        }
        throw new SQLException("Create ticket failed");
    }

    public List<RepairTicket> listForRepair() throws SQLException {
        String sql = BASE_SELECT
                + "WHERE t.Status IN ('OPEN','IN_PROGRESS','TESTING','COMPLETED') "
                + "ORDER BY t.CreatedAt DESC";
        List<RepairTicket> items = new ArrayList<>();
        try (PreparedStatement stmt = connection.prepareStatement(sql);
                ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                items.add(mapTicket(rs));
            }
        }
        return items;
    }

    public List<RepairTicket> listByCustomer(int customerId) throws SQLException {
        String sql = BASE_SELECT + "WHERE t.CustomerID = ? ORDER BY t.CreatedAt DESC";
        List<RepairTicket> items = new ArrayList<>();
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, customerId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    items.add(mapTicket(rs));
                }
            }
        }
        return items;
    }

    public List<RepairTicket> listPaidByCustomer(int customerId) throws SQLException {
        String sql = BASE_SELECT
                + "WHERE t.CustomerID = ? AND t.PaymentStatus = 'PAID' ORDER BY t.CreatedAt DESC";
        List<RepairTicket> items = new ArrayList<>();
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, customerId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    items.add(mapTicket(rs));
                }
            }
        }
        return items;
    }

    public List<RepairTicket> listPaidAll() throws SQLException {
        String sql = BASE_SELECT
                + "WHERE t.PaymentStatus = 'PAID' ORDER BY t.CreatedAt DESC";
        List<RepairTicket> items = new ArrayList<>();
        try (PreparedStatement stmt = connection.prepareStatement(sql);
                ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                items.add(mapTicket(rs));
            }
        }
        return items;
    }

    public List<RepairTicket> listForCheckout() throws SQLException {
        String sql = BASE_SELECT
                + "WHERE t.PaymentStatus = 'UNPAID' ORDER BY t.CreatedAt DESC";
        List<RepairTicket> items = new ArrayList<>();
        try (PreparedStatement stmt = connection.prepareStatement(sql);
                ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                items.add(mapTicket(rs));
            }
        }
        return items;
    }

    public RepairTicket findById(int ticketId) throws SQLException {
        String sql = BASE_SELECT + "WHERE t.TicketID = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, ticketId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next())
                    return mapTicket(rs);
            }
        }
        return null;
    }

    public RepairTicket findByIdForCustomer(int ticketId, int customerId) throws SQLException {
        String sql = BASE_SELECT + "WHERE t.TicketID = ? AND t.CustomerID = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, ticketId);
            stmt.setInt(2, customerId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next())
                    return mapTicket(rs);
            }
        }
        return null;
    }

    public void updateStatus(int ticketId, String status) throws SQLException {
        String sql = "UPDATE RepairTickets SET Status = ? WHERE TicketID = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, status);
            stmt.setInt(2, ticketId);
            stmt.executeUpdate();
        }
    }

    public void updateTotals(int ticketId) throws SQLException {
        String sql = "UPDATE RepairTickets "
                + "SET TotalAmount = (SELECT ISNULL(SUM(TotalPrice),0) FROM TicketItems WHERE TicketID = ?), "
                + "FinalAmount = (SELECT ISNULL(SUM(TotalPrice),0) FROM TicketItems WHERE TicketID = ?) - Discount "
                + "WHERE TicketID = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, ticketId);
            stmt.setInt(2, ticketId);
            stmt.setInt(3, ticketId);
            stmt.executeUpdate();
        }
    }

    public void markPaid(int ticketId, String method) throws SQLException {
        String sql = "UPDATE RepairTickets SET PaymentStatus = 'PAID', PaymentMethod = ?, "
                + "PaidAt = GETDATE(), Status = 'COMPLETED' WHERE TicketID = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, method);
            stmt.setInt(2, ticketId);
            stmt.executeUpdate();
        }
    }

    // ── Revenue stats ─────────────────────────────────────────────────────────
    public double getRevenueThisMonth() throws SQLException {
        String sql = "SELECT ISNULL(SUM(FinalAmount),0) FROM RepairTickets "
                + "WHERE PaymentStatus='PAID' AND MONTH(PaidAt)=MONTH(GETDATE()) AND YEAR(PaidAt)=YEAR(GETDATE())";
        try (PreparedStatement stmt = connection.prepareStatement(sql);
                ResultSet rs = stmt.executeQuery()) {
            if (rs.next())
                return rs.getDouble(1);
        }
        return 0;
    }

    public double getRevenueToday() throws SQLException {
        String sql = "SELECT ISNULL(SUM(FinalAmount),0) FROM RepairTickets "
                + "WHERE PaymentStatus='PAID' AND CAST(PaidAt AS DATE)=CAST(GETDATE() AS DATE)";
        try (PreparedStatement stmt = connection.prepareStatement(sql);
                ResultSet rs = stmt.executeQuery()) {
            if (rs.next())
                return rs.getDouble(1);
        }
        return 0;
    }

    public int countPaidInvoices() throws SQLException {
        String sql = "SELECT COUNT(*) FROM RepairTickets WHERE PaymentStatus='PAID'";
        try (PreparedStatement stmt = connection.prepareStatement(sql);
                ResultSet rs = stmt.executeQuery()) {
            if (rs.next())
                return rs.getInt(1);
        }
        return 0;
    }
}
