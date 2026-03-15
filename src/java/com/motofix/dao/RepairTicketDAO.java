package com.motofix.dao;

import com.motofix.model.Invoice;
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
        try (PreparedStatement stmt = connection.prepareStatement(sql); ResultSet rs = stmt.executeQuery()) {
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
        try (PreparedStatement stmt = connection.prepareStatement(sql); ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                items.add(mapTicket(rs));
            }
        }
        return items;
    }

    public RepairTicket findById(int ticketId) throws SQLException {
        String sql = """
        SELECT ro.*, a.firstName + ' ' + a.lastName as CustomerName, a.Username, e.EmployeeID,v.PlateNumber, 
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
        left join Employees as e on e.EmployeeID = ro.EmployeeID
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
                t.setEmployeeID(rs.getString("EmployeeID"));
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
                if (rs.next()) {
                    return mapTicket(rs);
                }
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
        } catch (Exception e) {
            e.printStackTrace();
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
        try (PreparedStatement stmt = connection.prepareStatement(sql); ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                return rs.getDouble(1);
            }
        }
        return 0;
    }

    public double getRevenueToday() throws SQLException {
        String sql = "SELECT ISNULL(SUM(FinalAmount),0) FROM RepairTickets "
                + "WHERE PaymentStatus='PAID' AND CAST(PaidAt AS DATE)=CAST(GETDATE() AS DATE)";
        try (PreparedStatement stmt = connection.prepareStatement(sql); ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                return rs.getDouble(1);
            }
        }
        return 0;
    }

    public int countPaidInvoices() throws SQLException {
        String sql = "SELECT COUNT(*) FROM RepairTickets WHERE PaymentStatus='PAID'";
        try (PreparedStatement stmt = connection.prepareStatement(sql); ResultSet rs = stmt.executeQuery()) {
            if (rs.next()) {
                return rs.getInt(1);
            }
        }
        return 0;
    }

    public List<RepairTicket> listRecentInvoices() throws SQLException {
        List<RepairTicket> lists = new ArrayList<>();
        // Sử dụng Text Block (Java 15+)
        String sql = """
            WITH OrderCosts AS (
                SELECT 
                    OrderID, 
                    SUM(TotalPrice) AS ComputedTotal
                FROM (
                    SELECT OrderID, TotalPrice FROM RepairServiceDetails
                    UNION ALL
                    SELECT OrderID, TotalPrice FROM RepairPartDetails
                ) AS CombinedDetails
                GROUP BY OrderID
            )
            SELECT 
                r.OrderID,
                r.Status,
                CONCAT(a.firstName, ' ', a.lastName) AS CustomerName,
                v.PlateNumber,
                e.FullName AS EmployeeName,
                i.PaymentStatus,
                ISNULL(i.Discount, 0) AS Discount,
                i.CreatedDate,
                -- Quan trọng: Đặt tên bí danh rõ ràng để Java gọi đúng
                ISNULL(oc.ComputedTotal, 0) AS ComputedTotalAmount, 
                (ISNULL(oc.ComputedTotal, 0) - ISNULL(i.Discount, 0)) AS CalculatedFinalAmount
            FROM RepairOrders r
            JOIN Customers c ON r.CustomerID = c.CustomerID
            JOIN Accounts a ON c.AccountID = a.AccountID
            JOIN Vehicles v ON r.VehicleID = v.VehicleID
            LEFT JOIN Employees e ON r.EmployeeID = e.EmployeeID
            LEFT JOIN Invoices i ON r.OrderID = i.OrderID
            LEFT JOIN OrderCosts oc ON r.OrderID = oc.OrderID
            WHERE r.Status = 'COMPLETED'
            ORDER BY r.CompletedAt DESC;
            """;

        try (PreparedStatement stmt = connection.prepareStatement(sql); ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                RepairTicket ticket = new RepairTicket();

                ticket.setTicketId(rs.getInt("OrderID"));
                ticket.setCustomerName(rs.getString("CustomerName"));
                ticket.setPlateNumber(rs.getString("PlateNumber"));
                ticket.setStatus(rs.getString("Status"));
                ticket.setPaymentStatus(rs.getString("PaymentStatus"));

                ticket.setTotalAmount(rs.getDouble("ComputedTotalAmount"));
                ticket.setDiscount(rs.getDouble("Discount"));

                ticket.setFinalAmount(rs.getDouble("CalculatedFinalAmount"));

                ticket.setCreatedAt(rs.getTimestamp("CreatedDate"));
                ticket.setEmployeeName(rs.getString("EmployeeName"));

                lists.add(ticket);
            }
        } catch (SQLException e) {

            System.err.println("Lỗi SQL: " + e.getMessage());
            throw e;
        }
        return lists;
    }

    public Invoice getDataForInvoice(int orderId) throws SQLException {
        String sql = """
            WITH OrderCosts AS (
                SELECT OrderID, SUM(TotalPrice) AS ComputedTotal
                FROM (
                    SELECT OrderID, TotalPrice FROM RepairServiceDetails
                    UNION ALL
                    SELECT OrderID, TotalPrice FROM RepairPartDetails
                ) AS CombinedDetails
                GROUP BY OrderID
            )
            SELECT r.OrderID, r.CustomerID, r.EmployeeID, ISNULL(oc.ComputedTotal, 0) as ComputedTotal
            FROM RepairOrders r
            LEFT JOIN OrderCosts oc ON r.OrderID = oc.OrderID
            WHERE r.OrderID = ?
            """;
        try (PreparedStatement st = connection.prepareStatement(sql)) {
            st.setInt(1, orderId);
            ResultSet rs = st.executeQuery();
            if (rs.next()) {
                Invoice inv = new Invoice();
                inv.setOrderID(rs.getInt("OrderID"));
                inv.setCustomerID(rs.getInt("CustomerID"));
                inv.setEmployeeID(rs.getInt("EmployeeID"));
                inv.setTotalAmount(rs.getDouble("ComputedTotal"));
                return inv;
            }
        }
        return null;
    }

    public Object getRecentInvoices1() {
        List<RepairTicket> repairTickets = new ArrayList<>();
        String sql = """
                 SELECT 
                    ro.OrderID AS ticketId,
                    'RO-' + CAST(ro.OrderID AS VARCHAR) AS ticketCode,
                    a.firstName + ' ' + a.lastName AS customerName,
                    a.Email AS phone, -- Hoặc thay bằng cột Phone nếu bạn đã thêm vào Accounts
                    v.PlateNumber AS plateNumber,
                    ro.Status AS status,
                    'PAID' AS paymentStatus, -- Vì đã COMPLETED nên mặc định là PAID để hiển thị
                    (ISNULL((SELECT SUM(TotalPrice) FROM RepairServiceDetails WHERE OrderID = ro.OrderID), 0) + 
                     ISNULL((SELECT SUM(TotalPrice) FROM RepairPartDetails WHERE OrderID = ro.OrderID), 0)) AS totalAmount,
                    0 AS discount, -- Bạn có thể join thêm bảng Invoices nếu muốn lấy chiết khấu thực
                    (ISNULL((SELECT SUM(TotalPrice) FROM RepairServiceDetails WHERE OrderID = ro.OrderID), 0) + 
                     ISNULL((SELECT SUM(TotalPrice) FROM RepairPartDetails WHERE OrderID = ro.OrderID), 0)) AS finalAmount,
                    N'Tiền mặt' AS paymentMethod,
                    ro.CompletedAt AS createdAt
                 FROM RepairOrders ro
                 JOIN Customers c ON ro.CustomerID = c.CustomerID
                 JOIN Accounts a ON c.AccountID = a.AccountID
                 JOIN Vehicles v ON ro.VehicleID = v.VehicleID
                 WHERE ro.Status = 'COMPLETED'
                 ORDER BY ro.CompletedAt DESC;
                 """;
        try {
            // Mở kết nối và chuẩn bị query (Giả sử bạn đã có biến connection)
            st = connection.prepareStatement(sql);
            rs = st.executeQuery();

            while (rs.next()) {
                RepairTicket ticket = new RepairTicket();

                ticket.setTicketId(rs.getInt("ticketId"));
                ticket.setTicketCode(rs.getString("ticketCode"));
                ticket.setCustomerName(rs.getString("customerName"));
                ticket.setPhone(rs.getString("phone"));
                ticket.setPlateNumber(rs.getString("plateNumber"));
                ticket.setStatus(rs.getString("status"));
                ticket.setPaymentStatus(rs.getString("paymentStatus"));
                ticket.setTotalAmount(rs.getDouble("totalAmount"));
                ticket.setDiscount(rs.getDouble("discount"));
                ticket.setFinalAmount(rs.getDouble("finalAmount"));
                ticket.setPaymentMethod(rs.getString("paymentMethod"));
                ticket.setCreatedAt(rs.getTimestamp("createdAt"));

                repairTickets.add(ticket);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        return repairTickets;
    }

    public Object listByTicket(int ticketId) {
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

    public int findByIdForEmployee(int ticketId) {
        int id;
        try {
            String sql = """
                            select EmployeeID from RepairOrders
                                                        where OrderID = 1
                         """;
            st = connection.prepareStatement(sql);
            // truyen tham so cho cau lenh sql
            st.setInt(1, ticketId);
            rs = st.executeQuery();
            if (rs.next()) {
                id = rs.getInt("EmployeeID");
                return id;
            }
        } catch (Exception e) {
            return -1;
        }
        return -1;
    }

    public int getEmployeeIdByTicketId(int ticketId) {

        try {
            String sql = """
                            SELECT EmployeeID FROM RepairOrders WHERE OrderID = ?
                         """;
            st = connection.prepareStatement(sql);
            // truyen tham so cho cau lenh sql
            st.setInt(1, ticketId);
            rs = st.executeQuery();
            if (rs.next()) {
                return rs.getInt("EmployeeID");
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1;
    }

    public void assignEmployee(int ticketId, int empId) {
        try {
            String sql = """
                            UPDATE RepairOrders SET EmployeeID = ? WHERE OrderID = ?
                         """;
            st = connection.prepareStatement(sql);
            // truyen tham so cho cau lenh sql

            st.setInt(1, empId);
            st.setInt(2, ticketId);
            st.executeUpdate();
        } catch (SQLException e) {

        }
    }
}
