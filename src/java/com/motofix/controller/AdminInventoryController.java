package com.motofix.controller;

import com.motofix.dao.PartDAO;
import com.motofix.model.Part;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class AdminInventoryController extends HttpServlet {
    private final PartDAO partDAO = new PartDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String pageStr = request.getParameter("page");
        int page = 1;
        int pageSize = 10;
        if (pageStr != null && !pageStr.isEmpty()) {
            try {
                page = Integer.parseInt(pageStr);
            } catch (NumberFormatException ignored) {}
        }

        String searchValue = request.getParameter("search");
        if (searchValue == null) searchValue = "";

        try {
            int totalParts = partDAO.countAll(searchValue);
            int totalPages = (int) Math.ceil((double) totalParts / pageSize);
            int offset = (page - 1) * pageSize;

            List<Part> parts = partDAO.listPaged(searchValue, offset, pageSize);

            request.setAttribute("parts", parts);
            request.setAttribute("currentPage", page);
            request.setAttribute("totalPages", totalPages);
            request.setAttribute("currentSearch", searchValue);
        } catch (SQLException e) {
            request.setAttribute("error", "Không thể tải danh sách phụ tùng.");
        }
        request.getRequestDispatcher("/views/admin/inventory.jsp").forward(request, response);
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
        String importPriceStr = request.getParameter("importPrice");
        String stockStr = request.getParameter("stock");
        String imageUrl = request.getParameter("imageUrl");
        String activeParam = request.getParameter("isActive");

        double sellingPrice = (priceStr != null && !priceStr.isEmpty()) ? Double.parseDouble(priceStr) : 0;
        double importPrice = (importPriceStr != null && !importPriceStr.isEmpty())
                ? Double.parseDouble(importPriceStr)
                : 0;
        int stock = (stockStr != null && !stockStr.isEmpty()) ? Integer.parseInt(stockStr) : 0;
        boolean isActive = "1".equals(activeParam) || "on".equals(activeParam) || "true".equals(activeParam);

        try {
            if ("delete".equals(action)) {
                partDAO.deactivate(Integer.parseInt(idStr));
                request.getSession().setAttribute("message", "Đã ngừng bán phụ tùng.");

            } else if ("edit".equals(action)) {
                partDAO.update(Integer.parseInt(idStr), name, description, importPrice, sellingPrice,
                        stock, imageUrl, isActive);
                request.getSession().setAttribute("message", "Đã cập nhật phụ tùng.");

            } else {
                partDAO.create(name, description, importPrice, sellingPrice, stock, imageUrl, isActive);
                request.getSession().setAttribute("message", "Đã thêm phụ tùng mới.");
            }
            response.sendRedirect(request.getContextPath() + "/admin/inventory");
        } catch (Exception e) {
            e.printStackTrace();
            request.getSession().setAttribute("error", "Lỗi xử lý phụ tùng: " + e.getMessage());
            response.sendRedirect(request.getContextPath() + "/admin/inventory");
        }
    }
}
