package com.motofix.controller;

import com.motofix.dao.RepairTicketDAO;
import java.io.IOException;
import java.sql.SQLException;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class AdminRevenueController extends HttpServlet {
    private final RepairTicketDAO repairTicketDAO = new RepairTicketDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            request.setAttribute("revenueMonth", repairTicketDAO.getRevenueThisMonth());
            request.setAttribute("revenueToday", repairTicketDAO.getRevenueToday());
            request.setAttribute("invoiceCount", repairTicketDAO.countPaidInvoices());
        } catch (SQLException e) {
            request.setAttribute("error", "Không thể tải dữ liệu doanh thu.");
        }
        request.getRequestDispatcher("/views/admin/revenue.jsp").forward(request, response);
    }
}
