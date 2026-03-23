/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package com.motofix.controller;

import com.motofix.dao.BookingDAO;
import com.motofix.model.Booking;
import com.motofix.model.User;
import java.io.IOException;
import java.io.PrintWriter;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.List;
import java.sql.SQLException;

/**
 *
 * @author ASUS
 */
public class BookingHistory extends HttpServlet {

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");
        try (PrintWriter out = response.getWriter()) {
            /* TODO output your page here. You may use following sample code. */
            out.println("<!DOCTYPE html>");
            out.println("<html>");
            out.println("<head>");
            out.println("<title>Servlet BookingHistory</title>");            
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Servlet BookingHistory at " + request.getContextPath() + "</h1>");
            out.println("</body>");
            out.println("</html>");
        }
    }

    // <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        User user = (User) request.getSession().getAttribute("user"); 
        
        if (user == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        try {
            BookingDAO dao = new BookingDAO();
            String search = request.getParameter("search");
            if (search == null) {
                search = "";
            }
            String status = request.getParameter("status");
            if (status == null || status.trim().isEmpty()) {
                status = "ALL";
            }

            int page = 1;
            int pageSize = 7;
            String pageParam = request.getParameter("page");
            if (pageParam != null && !pageParam.trim().isEmpty()) {
                try {
                    page = Integer.parseInt(pageParam);
                    if (page < 1) {
                        page = 1;
                    }
                } catch (NumberFormatException ignored) {
                    page = 1;
                }
            }

            int totalRecords = dao.countBookingsByCustomer(user.getCustomerID(), search, status);
            int totalPages = totalRecords > 0 ? (int) Math.ceil((double) totalRecords / pageSize) : 1;
            if (page > totalPages) {
                page = totalPages;
            }
            int offset = (page - 1) * pageSize;

            List<Booking> list = dao.listBookingsByCustomerPaged(user.getCustomerID(), search, status, offset, pageSize);

            request.setAttribute("listB", list);
            request.setAttribute("activeAccount", "bookings");
            request.setAttribute("currentSearch", search);
            request.setAttribute("currentStatus", status);
            request.setAttribute("currentPage", page);
            request.setAttribute("totalPages", totalPages);
            
            request.getRequestDispatcher("/views/customer/booking-history.jsp").forward(request, response);
        } catch (SQLException e) {
            request.setAttribute("error", "Lỗi kết nối dữ liệu: " + e.getMessage());
            request.getRequestDispatcher("/views/customer/booking-history.jsp").forward(request, response);
            }
        
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "Short description";
    }// </editor-fold>

}
