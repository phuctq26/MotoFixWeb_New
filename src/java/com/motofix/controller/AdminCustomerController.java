package com.motofix.controller;

import com.motofix.dao.CustomerDAO;
import com.motofix.model.Customer;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class AdminCustomerController extends HttpServlet {
    private final CustomerDAO customerDAO = new CustomerDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        try {
            List<Customer> customers = customerDAO.listAll();
            request.setAttribute("customers", customers);
        } catch (SQLException e) {
            request.setAttribute("error", "Không thể tải danh sách khách hàng.");
        }
        request.getRequestDispatcher("/views/admin/customers.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        String action = request.getParameter("action");
        String idStr = request.getParameter("id");
        String username = request.getParameter("username");
        String firstName = request.getParameter("firstName");
        String lastName = request.getParameter("lastName");
        String email = request.getParameter("email");
        String address = request.getParameter("address");
        String password = request.getParameter("password");
        String activeParam = request.getParameter("isActive");
        boolean isActive = !"0".equals(activeParam);

        try {
            if ("delete".equals(action)) {
                customerDAO.deactivate(Integer.parseInt(idStr));
                request.getSession().setAttribute("message", "Đã vô hiệu hóa tài khoản khách hàng.");

            } else if ("restore".equals(action)) {
                customerDAO.activate(Integer.parseInt(idStr));
                request.getSession().setAttribute("message", "Đã khôi phục tài khoản khách hàng.");

            } else if ("edit".equals(action)) {
                customerDAO.update(Integer.parseInt(idStr), firstName, lastName, email, address, isActive);
                request.getSession().setAttribute("message", "Đã cập nhật thông tin khách hàng.");

            } else {
                // CREATE — check duplicate username
                if (customerDAO.findByUsername(username) != null) {
                    request.getSession().setAttribute("formError",
                            "Tên đăng nhập <strong>" + username + "</strong> đã tồn tại.");
                    request.getSession().setAttribute("formUsername", username);
                    request.getSession().setAttribute("formFirstName", firstName);
                    request.getSession().setAttribute("formLastName", lastName);
                    request.getSession().setAttribute("formEmail", email);
                    request.getSession().setAttribute("formAddress", address);
                    response.sendRedirect(request.getContextPath() + "/admin/customers");
                    return;
                }
                customerDAO.create(username, firstName, lastName, email, password, address);
                request.getSession().setAttribute("message", "Đã thêm khách hàng mới.");
            }
            response.sendRedirect(request.getContextPath() + "/admin/customers");

        } catch (Exception e) {
            e.printStackTrace();
            String msg = e.getMessage();
            if (msg != null && msg.contains("UNIQUE KEY")) {
                request.getSession().setAttribute("formError",
                        "Tên đăng nhập <strong>" + username + "</strong> đã tồn tại.");
                request.getSession().setAttribute("formUsername", username);
                request.getSession().setAttribute("formFirstName", firstName);
                request.getSession().setAttribute("formLastName", lastName);
                request.getSession().setAttribute("formEmail", email);
                request.getSession().setAttribute("formAddress", address);
            } else {
                request.getSession().setAttribute("formError", "Lỗi khi xử lý khách hàng: " + msg);
            }
            response.sendRedirect(request.getContextPath() + "/admin/customers");
        }
    }
}
