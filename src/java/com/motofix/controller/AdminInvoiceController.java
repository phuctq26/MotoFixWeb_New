package com.motofix.controller;

import com.motofix.dao.InvoiceDAO;
import com.motofix.dao.RepairTicketDAO;
import com.motofix.dao.TicketItemDAO;
import com.motofix.model.RepairTicket;
import java.io.IOException;
import java.sql.SQLException;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class AdminInvoiceController extends HttpServlet {

    private final RepairTicketDAO repairTicketDAO = new RepairTicketDAO();
    private final TicketItemDAO itemDAO = new TicketItemDAO();
    private final InvoiceDAO invoiceDao = new InvoiceDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String idParam = request.getParameter("ticketId");
        String value = request.getParameter("value");

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
            int totalInvoices = (value != null && !value.isBlank())
                    ? invoiceDao.getInvoicesCount(value)
                    : invoiceDao.getInvoicesCount();
            int totalPages = totalInvoices > 0 ? (int) Math.ceil(totalInvoices / (double) pageSize) : 1;
            if (page < 1) {
                page = 1;
            } else if (page > totalPages) {
                page = totalPages;
            }

            request.setAttribute("invoices", (value != null && !value.isBlank())
                    ? invoiceDao.getInvoices(value, page, pageSize)
                    : invoiceDao.getInvoices(page, pageSize));
            request.setAttribute("currentPage", page);
            request.setAttribute("totalPages", totalPages);
            request.setAttribute("pageSize", pageSize);
            request.setAttribute("totalInvoices", totalInvoices);
            request.setAttribute("value", value);
            request.setAttribute("revenueMonth", invoiceDao.getRevenueMonth());
            request.setAttribute("invoiceCount", invoiceDao.getInvoiceCount());
            request.setAttribute("revenueToday", invoiceDao.getRevenueToday());

            if (idParam != null) {
                int ticketId = Integer.parseInt(idParam);
                RepairTicket ticket = repairTicketDAO.findById(ticketId);
                request.setAttribute("ticket", ticket);
                request.setAttribute("items", itemDAO.listByTicket(ticketId));
            }
        } catch (SQLException e) {
            request.setAttribute("error", "Không thể tải danh sách hóa đơn.");
        }
        request.getRequestDispatcher("/views/admin/invoices.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        doGet(request, response);
    }
}
