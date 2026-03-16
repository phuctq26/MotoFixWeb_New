/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.motofix.dao;

import com.motofix.model.Invoice;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author TRUONG
 */
public class InvoiceDAO extends DBContext {

    PreparedStatement st;
    ResultSet rs;

    public void createInvoice(Invoice inv) {
        try {
            String sql = """
                            INSERT INTO Invoices (OrderID, CustomerID, EmployeeID, TotalAmount, Discount, FinalAmount, PaymentStatus, CreatedDate)
                                             VALUES (?, ?, ?, ?, ?, ?, ?, GETDATE())
                         """;
            st = connection.prepareStatement(sql);
            // truyen tham so cho cau lenh sql
            st.setInt(1, inv.getOrderID());
            st.setInt(2, inv.getCustomerID());
            st.setInt(3, inv.getEmployeeID());
            st.setDouble(4, inv.getTotalAmount());
            st.setDouble(5, inv.getDiscount());
            st.setDouble(6, inv.getFinalAmount());
            st.setString(7, inv.getPaymentStatus());

            st.executeUpdate();
        } catch (SQLException e) {

        }
    }

    public List<Invoice> getAllInvoices() {
        List<Invoice> accs = new ArrayList<>();
        try {
            String sql = """
                         SELECT 
                                 i.[InvoiceID],
                                 i.[OrderID],
                                 i.[CustomerID],
                                 i.[EmployeeID],
                                 i.[TotalAmount],
                                 i.[Discount],
                                 i.[FinalAmount],
                                 i.[PaymentMethod],
                                 i.[PaymentStatus],
                                 i.[CreatedDate],
                                 CONCAT(a.firstName, ' ', a.lastName) AS fullName,
                                 v.PlateNumber
                             FROM [Invoices] AS i
                             JOIN [RepairOrders] AS ro ON i.OrderID = ro.OrderID 
                             JOIN [Vehicles] AS v ON ro.VehicleID = v.VehicleID
                             JOIN [Customers] AS c ON i.CustomerID = c.CustomerID
                             JOIN [Accounts] AS a ON c.AccountID = a.AccountID
                         """;
            st = connection.prepareStatement(sql);
            // truyen tham so cho cau lenh sql
            rs = st.executeQuery(); // select
            while (rs.next()) {
                Invoice invoice = new Invoice();
                invoice.setInvoiceID(rs.getInt("InvoiceID"));
                invoice.setOrderID(rs.getInt("OrderID"));
                invoice.setCustomerID(rs.getInt("CustomerID"));
                invoice.setEmployeeID(rs.getInt("EmployeeID"));
                invoice.setTotalAmount(rs.getDouble("TotalAmount"));
                invoice.setDiscount(rs.getDouble("Discount"));
                invoice.setFinalAmount(rs.getDouble("FinalAmount"));
                invoice.setPaymentStatus(rs.getString("PaymentStatus"));
                invoice.setCreatedDate(rs.getDate("CreatedDate"));
                invoice.setFullNameCustomer(rs.getString("fullName"));
                invoice.setPlateNumber(rs.getString("PlateNumber"));
                accs.add(invoice);
            }
            return accs;
        } catch (Exception e) {
            return null;
        }
    }

    public List<String> getRevenueLabelsLast7Days() {
        List<String> labels = new ArrayList<>();
        try {
            String sql = """
                WITH Last7Days AS (
                    SELECT CAST(GETDATE() AS DATE) AS d
                    UNION ALL
                    SELECT DATEADD(day, -1, d) FROM Last7Days WHERE d > DATEADD(day, -6, CAST(GETDATE() AS DATE))
                )
                SELECT CONVERT(varchar(5), d, 103) AS Label
                FROM Last7Days
                ORDER BY d ASC
            """;
            st = connection.prepareStatement(sql);
            rs = st.executeQuery();
            while (rs.next()) {
                labels.add("'" + rs.getString("Label") + "'");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return labels;
    }

    public List<Double> getRevenueDataLast7Days() {
        List<Double> data = new ArrayList<>();
        try {
            String sql = """
                WITH Last7Days AS (
                    SELECT CAST(GETDATE() AS DATE) AS d
                    UNION ALL
                    SELECT DATEADD(day, -1, d) FROM Last7Days WHERE d > DATEADD(day, -6, CAST(GETDATE() AS DATE))
                )
                SELECT ISNULL(SUM(i.FinalAmount), 0) AS Revenue
                FROM Last7Days a
                LEFT JOIN Invoices i 
                    ON CAST(i.CreatedDate AS DATE) = a.d AND i.PaymentStatus = 'PAID'
                GROUP BY a.d
                ORDER BY a.d ASC
            """;
            st = connection.prepareStatement(sql);
            rs = st.executeQuery();
            while (rs.next()) {
                data.add(rs.getDouble("Revenue"));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return data;
    }


    public List<Invoice> getAllInvoices(String value) {
        List<Invoice> accs = new ArrayList<>();
        try {
            String sql = """
                         SELECT 
                                 i.[InvoiceID],
                                 i.[OrderID],
                                 i.[CustomerID],
                                 i.[EmployeeID],
                                 i.[TotalAmount],
                                 i.[Discount],
                                 i.[FinalAmount],
                                 i.[PaymentMethod],
                                 i.[PaymentStatus],
                                 i.[CreatedDate],
                                 CONCAT(a.firstName, ' ', a.lastName) AS fullName,
                                 v.PlateNumber
                             FROM [Invoices] AS i
                             JOIN [RepairOrders] AS ro ON i.OrderID = ro.OrderID 
                             JOIN [Vehicles] AS v ON ro.VehicleID = v.VehicleID
                             JOIN [Customers] AS c ON i.CustomerID = c.CustomerID
                             JOIN [Accounts] AS a ON c.AccountID = a.AccountID
                             WHERE 
                              CONCAT(a.FirstName, ' ', a.LastName) LIKE ? OR
                               a.Username LIKE ? OR
                               v.PlateNumber LIKE ?
                         """;
            st = connection.prepareStatement(sql);
            // truyen tham so cho cau lenh sql
            String k = "%" + value + "%";
            st.setString(1, k);
            st.setString(2, k);
            st.setString(3, k);
            rs = st.executeQuery(); // select
            while (rs.next()) {
                Invoice invoice = new Invoice();
                invoice.setInvoiceID(rs.getInt("InvoiceID"));
                invoice.setOrderID(rs.getInt("OrderID"));
                invoice.setCustomerID(rs.getInt("CustomerID"));
                invoice.setEmployeeID(rs.getInt("EmployeeID"));
                invoice.setTotalAmount(rs.getDouble("TotalAmount"));
                invoice.setDiscount(rs.getDouble("Discount"));
                invoice.setFinalAmount(rs.getDouble("FinalAmount"));
                invoice.setPaymentStatus(rs.getString("PaymentStatus"));
                invoice.setCreatedDate(rs.getDate("CreatedDate"));
                invoice.setFullNameCustomer(rs.getString("fullName"));
                invoice.setPlateNumber(rs.getString("PlateNumber"));
                accs.add(invoice);
            }
            return accs;
        } catch (Exception e) {
            return null;
        }
    }

    public List<Invoice> getInvoices(int pageIndex, int pageSize) {
        List<Invoice> accs = new ArrayList<>();
        try {
            String sql = """
                         SELECT 
                                 i.[InvoiceID],
                                 i.[OrderID],
                                 i.[CustomerID],
                                 i.[EmployeeID],
                                 i.[TotalAmount],
                                 i.[Discount],
                                 i.[FinalAmount],
                                 i.[PaymentMethod],
                                 i.[PaymentStatus],
                                 i.[CreatedDate],
                                 CONCAT(a.firstName, ' ', a.lastName) AS fullName,
                                 v.PlateNumber
                             FROM [Invoices] AS i
                             JOIN [RepairOrders] AS ro ON i.OrderID = ro.OrderID 
                             JOIN [Vehicles] AS v ON ro.VehicleID = v.VehicleID
                             JOIN [Customers] AS c ON i.CustomerID = c.CustomerID
                             JOIN [Accounts] AS a ON c.AccountID = a.AccountID
                             ORDER BY i.CreatedDate DESC
                             OFFSET ? ROWS FETCH NEXT ? ROWS ONLY
                         """;
            st = connection.prepareStatement(sql);
            st.setInt(1, (pageIndex - 1) * pageSize);
            st.setInt(2, pageSize);
            rs = st.executeQuery();
            while (rs.next()) {
                Invoice invoice = new Invoice();
                invoice.setInvoiceID(rs.getInt("InvoiceID"));
                invoice.setOrderID(rs.getInt("OrderID"));
                invoice.setCustomerID(rs.getInt("CustomerID"));
                invoice.setEmployeeID(rs.getInt("EmployeeID"));
                invoice.setTotalAmount(rs.getDouble("TotalAmount"));
                invoice.setDiscount(rs.getDouble("Discount"));
                invoice.setFinalAmount(rs.getDouble("FinalAmount"));
                invoice.setPaymentStatus(rs.getString("PaymentStatus"));
                invoice.setCreatedDate(rs.getDate("CreatedDate"));
                invoice.setFullNameCustomer(rs.getString("fullName"));
                invoice.setPlateNumber(rs.getString("PlateNumber"));
                accs.add(invoice);
            }
            return accs;
        } catch (Exception e) {
            return null;
        }
    }

    public List<Invoice> getInvoices(String value, int pageIndex, int pageSize) {
        List<Invoice> accs = new ArrayList<>();
        try {
            String sql = """
                         SELECT 
                                 i.[InvoiceID],
                                 i.[OrderID],
                                 i.[CustomerID],
                                 i.[EmployeeID],
                                 i.[TotalAmount],
                                 i.[Discount],
                                 i.[FinalAmount],
                                 i.[PaymentMethod],
                                 i.[PaymentStatus],
                                 i.[CreatedDate],
                                 CONCAT(a.firstName, ' ', a.lastName) AS fullName,
                                 v.PlateNumber
                             FROM [Invoices] AS i
                             JOIN [RepairOrders] AS ro ON i.OrderID = ro.OrderID 
                             JOIN [Vehicles] AS v ON ro.VehicleID = v.VehicleID
                             JOIN [Customers] AS c ON i.CustomerID = c.CustomerID
                             JOIN [Accounts] AS a ON c.AccountID = a.AccountID
                             WHERE 
                              CONCAT(a.FirstName, ' ', a.LastName) LIKE ? OR
                               a.Username LIKE ? OR
                               v.PlateNumber LIKE ?
                             ORDER BY i.CreatedDate DESC
                             OFFSET ? ROWS FETCH NEXT ? ROWS ONLY
                         """;
            st = connection.prepareStatement(sql);
            String k = "%" + value + "%";
            st.setString(1, k);
            st.setString(2, k);
            st.setString(3, k);
            st.setInt(4, (pageIndex - 1) * pageSize);
            st.setInt(5, pageSize);
            rs = st.executeQuery();
            while (rs.next()) {
                Invoice invoice = new Invoice();
                invoice.setInvoiceID(rs.getInt("InvoiceID"));
                invoice.setOrderID(rs.getInt("OrderID"));
                invoice.setCustomerID(rs.getInt("CustomerID"));
                invoice.setEmployeeID(rs.getInt("EmployeeID"));
                invoice.setTotalAmount(rs.getDouble("TotalAmount"));
                invoice.setDiscount(rs.getDouble("Discount"));
                invoice.setFinalAmount(rs.getDouble("FinalAmount"));
                invoice.setPaymentStatus(rs.getString("PaymentStatus"));
                invoice.setCreatedDate(rs.getDate("CreatedDate"));
                invoice.setFullNameCustomer(rs.getString("fullName"));
                invoice.setPlateNumber(rs.getString("PlateNumber"));
                accs.add(invoice);
            }
            return accs;
        } catch (Exception e) {
            return null;
        }
    }

    public int getInvoicesCount() {
        try {
            String sql = """
                            SELECT COUNT(i.InvoiceID) AS Total
                              FROM Invoices AS i
                              JOIN [RepairOrders] AS ro ON i.OrderID = ro.OrderID 
                              JOIN [Vehicles] AS v ON ro.VehicleID = v.VehicleID
                              JOIN [Customers] AS c ON i.CustomerID = c.CustomerID
                              JOIN [Accounts] AS a ON c.AccountID = a.AccountID
                         """;
            st = connection.prepareStatement(sql);
            rs = st.executeQuery();
            if (rs.next()) {
                return rs.getInt("Total");
            }
        } catch (Exception e) {
            return -1;
        }
        return -1;
    }

    public int getInvoicesCount(String value) {
        try {
            String sql = """
                            SELECT COUNT(i.InvoiceID) AS Total
                              FROM Invoices AS i
                              JOIN [RepairOrders] AS ro ON i.OrderID = ro.OrderID 
                              JOIN [Vehicles] AS v ON ro.VehicleID = v.VehicleID
                              JOIN [Customers] AS c ON i.CustomerID = c.CustomerID
                              JOIN [Accounts] AS a ON c.AccountID = a.AccountID
                             WHERE 
                              CONCAT(a.FirstName, ' ', a.LastName) LIKE ? OR
                               a.Username LIKE ? OR
                               v.PlateNumber LIKE ?
                         """;
            st = connection.prepareStatement(sql);
            String k = "%" + value + "%";
            st.setString(1, k);
            st.setString(2, k);
            st.setString(3, k);
            rs = st.executeQuery();
            if (rs.next()) {
                return rs.getInt("Total");
            }
        } catch (Exception e) {
            return -1;
        }
        return -1;
    }

    public double getRevenueMonth() {

        try {
            String sql = """
                            SELECT SUM(FinalAmount) AS TongDoanhThu
                            FROM Invoices
                            WHERE MONTH(CreatedDate) = MONTH(GETDATE()) 
                              AND YEAR(CreatedDate) = YEAR(GETDATE());
                         """;
            st = connection.prepareStatement(sql);
            // truyen tham so cho cau lenh sql

            rs = st.executeQuery();
            if (rs.next()) {
                double TongDoanhThu = rs.getDouble("TongDoanhThu");
                return TongDoanhThu;
            }
        } catch (Exception e) {
            return -1;
        }
        return -1;
    }

    public int getInvoiceCount() {
        try {
            String sql = """
                            SELECT COUNT(InvoiceID) AS SoLuongHoaDon
                              FROM Invoices
                              WHERE MONTH(CreatedDate) = MONTH(GETDATE()) 
                                AND YEAR(CreatedDate) = YEAR(GETDATE());
                         """;
            st = connection.prepareStatement(sql);
            // truyen tham so cho cau lenh sql

            rs = st.executeQuery();
            if (rs.next()) {
                int SoLuongHoaDon = rs.getInt("SoLuongHoaDon");
                return SoLuongHoaDon;
            }
        } catch (Exception e) {
            return -1;
        }
        return -1;
    }

    public double getRevenueToday() {
        try {
            String sql = """
                            SELECT SUM(FinalAmount) AS DoanhThuHomNay
                            FROM Invoices
                            WHERE CAST(CreatedDate AS DATE) = CAST(GETDATE() AS DATE);
                         """;
            st = connection.prepareStatement(sql);
            // truyen tham so cho cau lenh sql

            rs = st.executeQuery();
            if (rs.next()) {
                double TongDoanhThu = rs.getDouble("DoanhThuHomNay");
                return TongDoanhThu;
            }
        } catch (Exception e) {
            return -1;
        }
        return -1;
    }

}
