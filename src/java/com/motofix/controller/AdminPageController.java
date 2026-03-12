package com.motofix.controller;

import com.motofix.dao.ConsulationDAO;
import com.motofix.model.consulation;
import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.List;

public class AdminPageController extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String path = request.getServletPath();
        switch (path) {
            case "/admin/dashboard":
                request.getRequestDispatcher("/views/admin/dashboard.jsp").forward(request, response);
                break;
            case "/admin/consultations":
                ConsulationDAO consulDAO = new ConsulationDAO();
                List<consulation> consulations = consulDAO.getAllConsulations();
                request.setAttribute("consulations", consulations);
                request.getRequestDispatcher("/views/admin/consultations.jsp").forward(request, response);
                break;
            case "/admin/reception":
                request.getRequestDispatcher("/views/admin/reception.jsp").forward(request, response);
                break;

            default:
                response.sendError(HttpServletResponse.SC_NOT_FOUND);
                break;
        }
    }
}
