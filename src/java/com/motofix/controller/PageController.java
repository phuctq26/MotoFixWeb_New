package com.motofix.controller;

import com.motofix.dao.RepairTicketDAO;
import com.motofix.dao.TicketItemDAO;
import com.motofix.dao.ServiceDAO;
import com.motofix.dao.UserDAO;
import com.motofix.dao.InvoiceDAO; 
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
    private final ServiceDAO serviceDAO = new ServiceDAO();
    private final UserDAO userDAO = new UserDAO();
    private final InvoiceDAO invoiceDAO = new InvoiceDAO(); 

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String path = request.getServletPath();

        switch (path) {

            case "/home":
                try {
                    request.setAttribute("topServices", serviceDAO.listTop4());
                } catch (SQLException e) {
                    e.printStackTrace();
                }
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

            // ================= INVOICE DETAIL =================
            case "/invoice-detail":

                User userInvoice = (User) request.getSession().getAttribute("user");

                if (userInvoice == null) {
                    response.sendRedirect(request.getContextPath() + "/login");
                    return;
                }

                String ticketIdParam = request.getParameter("ticketId");

                if (ticketIdParam != null) {
                    try {
                        int ticketId = Integer.parseInt(ticketIdParam);
                        int customerId = userDAO.getCustomerIdByAccountId(userInvoice.getUserId());

                        request.setAttribute("ticket",
                                repairTicketDAO.findByIdForCustomer(ticketId, customerId));

                        request.setAttribute("items",
                                ticketItemDAO.listByTicket(ticketId));

                    } catch (Exception e) {
                        request.setAttribute("error", "Không thể tải chi tiết hóa đơn.");
                    }
                }

                request.getRequestDispatcher("/views/customer/invoice-detail.jsp")
                        .forward(request, response);

                break;

            // ================= VEHICLES =================
            case "/vehicles":

                User user = (User) request.getSession().getAttribute("user");

                if (user == null) {
                    response.sendRedirect(request.getContextPath() + "/login");
                    return;
                }

                try {
                    int customerId = userDAO.getCustomerIdByAccountId(user.getUserId());

                    request.setAttribute("tickets",
                            repairTicketDAO.listByCustomer(customerId));

                } catch (SQLException e) {
                    request.setAttribute("error", "Không thể tải tiến độ sửa xe.");
                }

                request.getRequestDispatcher("/views/customer/vehicles.jsp")
                        .forward(request, response);

                break;

            // ================= INVOICES =================
            case "/invoices":

                User userInvoices = (User) request.getSession().getAttribute("user");

                if (userInvoices == null) {
                    response.sendRedirect(request.getContextPath() + "/login");
                    return;
                }

                try {
                    int customerId = userDAO.getCustomerIdByAccountId(userInvoices.getUserId());

                 
                    request.setAttribute("invoices",
                            invoiceDAO.getInvoicesByCustomer(customerId));

                } catch (Exception e) {
                    request.setAttribute("error", "Không thể tải hóa đơn.");
                }

                request.getRequestDispatcher("/views/customer/invoices.jsp")
                        .forward(request, response);

                break;

            default:
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
        }
    }
}