package com.motofix.controller;

import com.motofix.dao.RepairTicketDAO;
import com.motofix.dao.TicketItemDAO;
import com.motofix.model.RepairTicket;
import com.motofix.model.User;
import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.sql.SQLException;

public class InvoicePrintController extends HttpServlet {
    private final RepairTicketDAO repairTicketDAO = new RepairTicketDAO();
    private final TicketItemDAO itemDAO = new TicketItemDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String idParam = request.getParameter("ticketId");
        if (idParam == null) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST);
            return;
        }

        boolean isAdminPath = request.getServletPath().startsWith("/admin/");
        try {
            int ticketId = Integer.parseInt(idParam);
            RepairTicket ticket;
            if (isAdminPath) {
                ticket = repairTicketDAO.findById(ticketId);
            } else {
                User user = (request.getSession(false) != null) ? (User) request.getSession(false).getAttribute("user") : null;
                if (user == null) {
                    response.sendRedirect(request.getContextPath() + "/login");
                    return;
                }
                ticket = repairTicketDAO.findByIdForCustomer(ticketId, user.getUserId());
            }

            if (ticket == null) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
                return;
            }

            request.setAttribute("ticket", ticket);
            request.setAttribute("items", itemDAO.listByTicket(ticketId));
            request.getRequestDispatcher("/views/common/invoice-print.jsp").forward(request, response);
        } catch (NumberFormatException | SQLException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST);
        }
    }
}
