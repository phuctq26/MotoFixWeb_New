package com.motofix.controller;

import com.motofix.dao.EmployeeDAO;
import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import com.motofix.model.Employee;

public class AdminStaffController extends HttpServlet {
    private final EmployeeDAO employeeDAO = new EmployeeDAO();

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

        try {
            int totalEmployees = employeeDAO.countAll();
            int totalPages = (int) Math.ceil((double) totalEmployees / pageSize);
            int offset = (page - 1) * pageSize;
            
            List<Employee> staffs = employeeDAO.listPaged(offset, pageSize);
            
            request.setAttribute("staffs", staffs);
            request.setAttribute("currentPage", page);
            request.setAttribute("totalPages", totalPages);
        } catch (SQLException e) {
            request.setAttribute("error", "Không thể tải danh sách nhân viên.");
        }
        request.getRequestDispatcher("/views/admin/staff.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        String action = request.getParameter("action");
        String idStr = request.getParameter("id");
        String fullName = request.getParameter("fullName");
        String phone = request.getParameter("phone");
        String position = request.getParameter("position");
        String salaryStr = request.getParameter("salary");
        String hireDate = request.getParameter("hireDate");
        String statusParam = request.getParameter("status");

        long salary = 0;
        try {
            salary = Long.parseLong(salaryStr);
        } catch (Exception ignored) {
        }
        boolean isActive = !"INACTIVE".equals(statusParam);

        try {
            if ("delete".equals(action)) {
                employeeDAO.deactivate(Integer.parseInt(idStr));
                request.getSession().setAttribute("message", "Đã chuyển nhân viên sang trạng thái nghỉ việc.");

            } else if ("edit".equals(action)) {
                employeeDAO.update(Integer.parseInt(idStr), fullName, phone, position,
                        salary, hireDate, isActive);
                request.getSession().setAttribute("message", "Đã cập nhật thông tin nhân viên.");

            } else {
                // CREATE — check duplicate phone first
                if (phone != null && !phone.isEmpty() && employeeDAO.findByPhone(phone) != null) {
                    request.getSession().setAttribute("formError",
                            "Số điện thoại <strong>" + phone + "</strong> đã tồn tại trong hệ thống.");
                    request.getSession().setAttribute("formPhone", phone);
                    request.getSession().setAttribute("formFullName", fullName);
                    request.getSession().setAttribute("formPosition", position);
                    request.getSession().setAttribute("formSalary", salaryStr);
                    request.getSession().setAttribute("formHireDate", hireDate);
                    response.sendRedirect(request.getContextPath() + "/admin/staff");
                    return;
                }
                employeeDAO.create(fullName, phone, position, salary, hireDate, isActive);
                request.getSession().setAttribute("message", "Đã thêm nhân viên mới.");
            }
            response.sendRedirect(request.getContextPath() + "/admin/staff");

        } catch (Exception e) {
            e.printStackTrace();
            String msg = e.getMessage();
            if (msg != null && msg.contains("UNIQUE KEY")) {
                request.getSession().setAttribute("formError",
                        "Số điện thoại <strong>" + phone + "</strong> đã tồn tại.");
                request.getSession().setAttribute("formPhone", phone);
                request.getSession().setAttribute("formFullName", fullName);
                request.getSession().setAttribute("formPosition", position);
                request.getSession().setAttribute("formSalary", salaryStr);
                request.getSession().setAttribute("formHireDate", hireDate);
            } else {
                request.getSession().setAttribute("formError", "Lỗi: " + msg);
            }
            response.sendRedirect(request.getContextPath() + "/admin/staff");
        }
    }
}
