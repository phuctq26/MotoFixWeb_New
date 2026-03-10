package com.motofix.controller;

import com.motofix.dao.ServiceDAO;
import com.motofix.model.Service;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class AdminServiceController extends HttpServlet {
    private final ServiceDAO serviceDAO = new ServiceDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            List<Service> services = serviceDAO.listAll();
            request.setAttribute("services", services);
        } catch (SQLException e) {
            request.setAttribute("error", "Không thể tải danh sách dịch vụ.");
        }
        request.getRequestDispatcher("/views/admin/services.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        String action = request.getParameter("action");
        String idStr = request.getParameter("id");
        String name = request.getParameter("name");
        String description = request.getParameter("description");
        String priceStr = request.getParameter("price");
        String activeParam = request.getParameter("isActive");

        double price = (priceStr != null && !priceStr.isEmpty()) ? Double.parseDouble(priceStr) : 0;
        boolean isActive = "1".equals(activeParam) || "on".equals(activeParam) || "true".equals(activeParam);

        try {
            if ("delete".equals(action)) {
                serviceDAO.deactivate(Integer.parseInt(idStr));
                request.getSession().setAttribute("message", "Đã ngừng hoạt động dịch vụ.");

            } else if ("edit".equals(action)) {
                serviceDAO.update(Integer.parseInt(idStr), name, description, price, isActive);
                request.getSession().setAttribute("message", "Đã cập nhật dịch vụ.");

            } else {
                serviceDAO.create(name, description, price, isActive);
                request.getSession().setAttribute("message", "Đã thêm dịch vụ mới.");
            }
            response.sendRedirect(request.getContextPath() + "/admin/services");
        } catch (Exception e) {
            e.printStackTrace();
            request.getSession().setAttribute("error", "Lỗi xử lý dịch vụ: " + e.getMessage());
            response.sendRedirect(request.getContextPath() + "/admin/services");
        }
    }
}
