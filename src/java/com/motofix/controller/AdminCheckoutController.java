package com.motofix.controller;

import com.motofix.dao.RepairTicketDAO;
import com.motofix.dao.TicketItemDAO;
import com.motofix.model.RepairTicket;
import com.motofix.util.VietQrUtil;
import java.io.IOException;
import java.sql.SQLException;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class AdminCheckoutController extends HttpServlet {
    private final RepairTicketDAO ticketDAO = new RepairTicketDAO();
    private final TicketItemDAO itemDAO = new TicketItemDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String idParam = request.getParameter("ticketId");
        try {
            request.setAttribute("tickets", ticketDAO.listForCheckout());
            if (idParam != null) {
                int ticketId = Integer.parseInt(idParam);
                RepairTicket ticket = ticketDAO.findById(ticketId);
                request.setAttribute("ticket", ticket);
                request.setAttribute("items", itemDAO.listByTicket(ticketId));
                if (ticket != null) {
                    String orderInfo = "MotoFix " + ticket.getTicketCode();
                    long amount = Math.round(ticket.getFinalAmount());
                    request.setAttribute("qrUrl", VietQrUtil.generateQrLink(amount, orderInfo));
                }
            }
        } catch (SQLException e) {
            request.setAttribute("error", "Không thể tải dữ liệu checkout.");
        }
        request.getRequestDispatcher("/views/admin/checkout.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String idParam = request.getParameter("ticketId");
        String method = request.getParameter("paymentMethod");
        if (idParam != null) {
            try {
                int ticketId = Integer.parseInt(idParam);
                ticketDAO.markPaid(ticketId, method != null ? method : "CASH");
                response.sendRedirect(request.getContextPath() + "/admin/checkout?ticketId=" + ticketId + "&paid=1");
                return;
            } catch (SQLException e) {
                request.setAttribute("error", "Không thể cập nhật thanh toán.");
            }
        }
        response.sendRedirect(request.getContextPath() + "/admin/checkout");
    }
}
