/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package com.motofix.controller;

import com.motofix.dao.ConsulationDAO;
import com.motofix.model.consulation;
import java.io.IOException;
import java.io.PrintWriter;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.List;

/**
 *
 * @author TRUONG
 */
@WebServlet(name = "AdminConsulationController", urlPatterns = {"/admin/ConsulationController"})
public class AdminConsulationController extends HttpServlet {

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
            out.println("<title>Servlet AdminConsulationController</title>");
            out.println("</head>");
            out.println("<body>");
            out.println("<h1>Servlet AdminConsulationController at " + request.getContextPath() + "</h1>");
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
        ConsulationDAO consulDAO = new ConsulationDAO();

        String status = request.getParameter("status");

        List<consulation> consulations;

        if (status == null) {
            consulations = consulDAO.getAllConsulations();
        } else if (status.equals("pending")) {
            consulations = consulDAO.getByStatus(false);
        } else if (status.equals("done")) {
            consulations = consulDAO.getByStatus(true);
        } else {
            consulations = consulDAO.getAllConsulations();
        }

        request.setAttribute("consulations", consulations);
        request.getRequestDispatcher("/views/admin/consultations.jsp").forward(request, response);
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
        String action = request.getParameter("action");
        int id = Integer.parseInt(request.getParameter("id"));
        ConsulationDAO consulDAO = new ConsulationDAO();
        switch (action) {
            case "call":
                // cập nhật Status = 1
                consulDAO.updateStatus(id, true);
                break;
            case "delete":
                // xóa bản ghi
                consulDAO.delete(id);
                break;

            default:
                break;
        }
        response.sendRedirect(request.getContextPath() + "/admin/ConsulationController");
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
