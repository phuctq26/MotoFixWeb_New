package com.motofix.controller;

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

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String idParam = request.getParameter("ticketId");
        try {
            request.setAttribute("tickets", repairTicketDAO.listPaidAll());
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
}
