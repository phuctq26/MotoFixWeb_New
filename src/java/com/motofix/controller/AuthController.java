package com.motofix.controller;

import com.motofix.dao.UserDAO;
import com.motofix.model.User;
import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

public class AuthController extends HttpServlet {

    private final UserDAO userDAO = new UserDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String path = request.getServletPath();
        if ("/logout".equals(path)) {
            HttpSession session = request.getSession(false);
            if (session != null) {
                session.invalidate();
            }
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        request.getRequestDispatcher("/views/auth/login.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setCharacterEncoding("UTF-8");
        String username = request.getParameter("username");
        String password = request.getParameter("password");

        try {
            User user = userDAO.authenticate(username, password);
            if (user != null && user.isActive()) {
                HttpSession session = request.getSession(true);
                session.setAttribute("user", user);
                if ("ADMIN".equalsIgnoreCase(user.getRole())) {
                    response.sendRedirect(request.getContextPath() + "/admin/dashboard");
                } else {
                    response.sendRedirect(request.getContextPath() + "/home");
                }
                //   return;
            } else if (user != null && !user.isActive()) {
                request.setAttribute("error", "Tài khoản của bạn đã bị vô hiệu.");
                request.getRequestDispatcher("/views/auth/login.jsp").forward(request, response);
            } else {
                request.setAttribute("error", "Sai tên đăng nhập hoặc mật khẩu.");
                request.getRequestDispatcher("/views/auth/login.jsp").forward(request, response);
            }
        } catch (Exception e) {
            // fall through to show error
        }

    }
}
