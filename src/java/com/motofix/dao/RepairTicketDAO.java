package com.motofix.dao;

import com.motofix.model.RepairTicket;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RepairTicketDAO extends DBContext {
    
    PreparedStatement st;
    ResultSet rs;
    
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
        int ticketID = -1;

        try {
            String sql = """
                insert into RepairOrders 
                    (BookingID, CustomerID, VehicleID, EmployeeID, CurrentOdometer, Diagnosis, Note, Status, CreatedAt, CompletedAt)
                    VALUES (NULL, ?, ?, NULL, NULL, ?, NULL, 'RECEIVED', GETDATE(), NULL)
                """;
            st = connection.prepareStatement(sql);
            st.setInt(1, customerId);
            st.setInt(2, vehicleId);
            st.setString(3, diagnosis);

            st.executeUpdate();
            ResultSet rs = st.getGeneratedKeys();
            if (rs.next()) {
                ticketID = rs.getInt(1);
            }

            return ticketID;

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return -1;
    }

    public List<RepairTicket> listForRepair() throws SQLException {
        String sql = """
        SELECT 
            r.OrderID, 
            r.Status, 
            a.firstName, 
            a.lastName, 
            a.Email, 
            v.PlateNumber, 
            r.CreatedAt
        FROM RepairOrders r
        JOIN Customers c ON r.CustomerID = c.CustomerID
        JOIN Accounts a ON c.AccountID = a.AccountID
        JOIN Vehicles v ON r.VehicleID = v.VehicleID
        WHERE r.Status IN ('RECEIVED', 'IN_PROGRESS')
        ORDER BY r.CreatedAt DESC
    """;

        List<RepairTicket> items = new ArrayList<>();

        try {
            st = connection.prepareStatement(sql);
            rs = st.executeQuery();

            while (rs.next()) {
                RepairTicket t = new RepairTicket();

                t.setTicketId(rs.getInt("OrderID"));
                t.setTicketCode("RO-" + rs.getInt("OrderID"));
                t.setStatus(rs.getString("Status"));
                t.setCustomerName(rs.getString("lastName") + " " + rs.getString("firstName"));
                t.setPlateNumber(rs.getString("PlateNumber"));
                t.setCreatedAt(rs.getTimestamp("CreatedAt"));

                items.add(t);
            }

        } catch (Exception e) {
            e.printStackTrace();
            return null;
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
        String sql = """
        SELECT ro.*, a.firstName + ' ' + a.lastName as CustomerName, a.Username, v.PlateNumber,
               (SELECT ISNULL(SUM(TotalPrice), 0) FROM (
                    SELECT TotalPrice FROM RepairServiceDetails WHERE OrderID = ro.OrderID
                    UNION ALL
                    SELECT TotalPrice FROM RepairPartDetails WHERE OrderID = ro.OrderID
               ) as t) as ComputedTotal,
               ISNULL(i.Discount, 0) as InvoiceDiscount
        FROM RepairOrders ro
        JOIN Customers c ON ro.CustomerID = c.CustomerID
        JOIN Accounts a ON c.AccountID = a.AccountID
        JOIN Vehicles v ON ro.VehicleID = v.VehicleID
        LEFT JOIN Invoices i ON ro.OrderID = i.OrderID
        WHERE ro.OrderID = ?
    """;

        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, ticketId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                RepairTicket t = new RepairTicket();
                t.setTicketId(rs.getInt("OrderID"));
                t.setCustomerName(rs.getString("CustomerName"));
                t.setPlateNumber(rs.getString("PlateNumber"));
                t.setStatus(rs.getString("Status"));
                t.setPhone(rs.getString("Username"));
                // Gán giá trị tính toán vào Model
                double total = rs.getDouble("ComputedTotal");
                double disc = rs.getDouble("InvoiceDiscount");
                t.setTotalAmount(total);
                t.setDiscount(disc);
                t.setFinalAmount(total - disc);
                return t;
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
        String sql = "UPDATE RepairOrders SET Status = ? WHERE OrderID = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, status);
            stmt.setInt(2, ticketId);
            stmt.executeUpdate();
        }
    }

    public void updateTotals(int ticketId) throws SQLException {
        String sql = """
        UPDATE Invoices
        SET TotalAmount = sub.ComputedTotal,
            FinalAmount = sub.ComputedTotal - ISNULL(Invoices.Discount, 0)
        FROM Invoices
        INNER JOIN (
            SELECT OrderID, SUM(TotalPrice) AS ComputedTotal
            FROM (
                SELECT OrderID, TotalPrice FROM RepairServiceDetails WHERE OrderID = ?
                UNION ALL
                SELECT OrderID, TotalPrice FROM RepairPartDetails WHERE OrderID = ?
            ) AS x
            GROUP BY OrderID
        ) AS sub ON Invoices.OrderID = sub.OrderID
        WHERE Invoices.OrderID = ?
    """;

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
