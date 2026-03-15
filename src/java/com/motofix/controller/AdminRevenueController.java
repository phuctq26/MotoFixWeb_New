package com.motofix.controller;

import com.motofix.dao.InvoiceDAO;
import com.motofix.dao.RepairTicketDAO;
import com.motofix.model.Invoice;
import com.motofix.model.RepairTicket;
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
        int pageSize = 10;
        int page = 1;
        String pageParam = request.getParameter("page");
        if (pageParam != null) {
            try {
                page = Integer.parseInt(pageParam);
            } catch (NumberFormatException ignored) {
            }
        }

        try {
            int totalInvoices = repairTicketDAO.countRecentInvoices();
            int totalPages = totalInvoices > 0 ? (int) Math.ceil(totalInvoices / (double) pageSize) : 1;
            if (page < 1) {
                page = 1;
            } else if (page > totalPages) {
                page = totalPages;
            }

            request.setAttribute("recentInvoices", repairTicketDAO.listRecentInvoices(page, pageSize));
            request.setAttribute("currentPage", page);
            request.setAttribute("totalPages", totalPages);
            request.setAttribute("totalInvoices", totalInvoices);
            request.setAttribute("pageSize", pageSize);
        } catch (SQLException e) {
            e.printStackTrace();
            request.setAttribute("error", "Không thể tải dữ liệu doanh thu: " + e.getMessage());
        }
        request.getRequestDispatcher("/views/admin/revenue.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

        try {
            String action = request.getParameter("action");
            String id = request.getParameter("id");
            if (action.equals("pay")) {
                InvoiceDAO invoiceDao = new InvoiceDAO();
                RepairTicketDAO rpDao = new RepairTicketDAO();

                rpDao.updateStatus(Integer.parseInt(id), "DONE");
                RepairTicket repair = rpDao.findById(Integer.parseInt(id));
               

                Invoice invoiceData = rpDao.getDataForInvoice(Integer.parseInt(id));

                if (invoiceData != null) {
                    invoiceData.setDiscount(0); 
                    invoiceData.setFinalAmount(invoiceData.getTotalAmount() - invoiceData.getDiscount());
                    invoiceData.setPaymentStatus("PAID");
                    invoiceDao.createInvoice(invoiceData);
                }
                
            } else {
                RepairTicketDAO ticketDAO = new RepairTicketDAO();
                ticketDAO.updateStatus(Integer.parseInt(id), "IN_PROGRESS");
            }

            // Sau khi xử lý action, redirect về trang hiện tại với page
            int page = 1;
            String pageParam = request.getParameter("page");
            if (pageParam != null) {
                try {
                    page = Integer.parseInt(pageParam);
                } catch (NumberFormatException ignored) {
                }
            }
            response.sendRedirect(request.getContextPath() + "/admin/revenue?page=" + page);
        } catch (SQLException e) {
            e.printStackTrace();
            request.setAttribute("error", "Không thể tải dữ liệu doanh thu: " + e.getMessage());
            doGet(request, response);
        }
    }
}
