package com.motofix.controller;

import com.motofix.dao.RepairTicketDAO;
import com.motofix.dao.TicketItemDAO;
import com.motofix.model.User;
import java.io.IOException;
import java.sql.SQLException;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class PageController extends HttpServlet {
    private final RepairTicketDAO repairTicketDAO = new RepairTicketDAO();
    private final TicketItemDAO ticketItemDAO = new TicketItemDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String path = request.getServletPath();
        switch (path) {
            case "/home":
                request.getRequestDispatcher("/views/customer/home.jsp").forward(request, response);
                break;
            case "/booking":
                request.getRequestDispatcher("/views/customer/booking.jsp").forward(request, response);
                break;
            case "/policy":
                request.getRequestDispatcher("/views/customer/policy.jsp").forward(request, response);
                break;
            case "/contact":
                request.getRequestDispatcher("/views/customer/contact.jsp").forward(request, response);
                break;
            case "/emergency":
                request.getRequestDispatcher("/views/customer/emergency.jsp").forward(request, response);
                break;
            case "/register":
                request.getRequestDispatcher("/views/customer/register.jsp").forward(request, response);
                break;
            case "/profile":
                request.getRequestDispatcher("/views/customer/profile.jsp").forward(request, response);
                break;
            case "/change-password":
                request.getRequestDispatcher("/views/customer/change-password.jsp").forward(request, response);
                break;
            case "/forgot-password":
                request.getRequestDispatcher("/views/customer/forgot-password.jsp").forward(request, response);
                break;
            case "/booking-success":
                request.getRequestDispatcher("/views/customer/booking-success.jsp").forward(request, response);
                break;
            case "/booking-detail":
                request.getRequestDispatcher("/views/customer/booking-detail.jsp").forward(request, response);
                break;
            case "/invoice-detail":
                User userInvoice = null;
                if (request.getSession(false) != null) {
                    userInvoice = (User) request.getSession(false).getAttribute("user");
                }
                if (userInvoice == null) {
                    response.sendRedirect(request.getContextPath() + "/login");
                    return;
                }
                String ticketIdParam = request.getParameter("ticketId");
                if (ticketIdParam != null) {
                    try {
                        int ticketId = Integer.parseInt(ticketIdParam);
                        request.setAttribute("ticket",
                                repairTicketDAO.findByIdForCustomer(ticketId, userInvoice.getUserId()));
                        request.setAttribute("items", ticketItemDAO.listByTicket(ticketId));
                    } catch (Exception e) {
                        request.setAttribute("error", "Không thể tải chi tiết hóa đơn.");
                    }
                }
                request.getRequestDispatcher("/views/customer/invoice-detail.jsp").forward(request, response);
                break;
            case "/vehicles":
                User user = null;
                if (request.getSession(false) != null) {
                    user = (User) request.getSession(false).getAttribute("user");
                }
                if (user == null) {
                    response.sendRedirect(request.getContextPath() + "/login");
                    return;
                }
                try {
                    request.setAttribute("tickets", repairTicketDAO.listByCustomer(user.getUserId()));
                } catch (SQLException e) {
                    request.setAttribute("error", "Không thể tải tiến độ sửa xe.");
                }
                request.getRequestDispatcher("/views/customer/vehicles.jsp").forward(request, response);
                break;
            case "/history":
                User userHistory = null;
                if (request.getSession(false) != null) {
                    userHistory = (User) request.getSession(false).getAttribute("user");
                }
                if (userHistory == null) {
                    response.sendRedirect(request.getContextPath() + "/login");
                    return;
                }
                try {
                    request.setAttribute("tickets", repairTicketDAO.listByCustomer(userHistory.getUserId()));
                } catch (SQLException e) {
                    request.setAttribute("error", "Không thể tải lịch sử sửa chữa.");
                }
                request.getRequestDispatcher("/views/customer/history.jsp").forward(request, response);
                break;
            case "/invoices":
                User userInvoices = null;
                if (request.getSession(false) != null) {
                    userInvoices = (User) request.getSession(false).getAttribute("user");
                }
                if (userInvoices == null) {
                    response.sendRedirect(request.getContextPath() + "/login");
                    return;
                }
                try {
                    request.setAttribute("tickets", repairTicketDAO.listPaidByCustomer(userInvoices.getUserId()));
                } catch (SQLException e) {
                    request.setAttribute("error", "Không thể tải hóa đơn.");
                }
                request.getRequestDispatcher("/views/customer/invoices.jsp").forward(request, response);
                break;
            default:
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
                break;
        }
    }
}
