<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page import="com.motofix.model.*" %>
<!doctype html>
<html lang="vi">
    <head>
        <title>Yêu cầu tư vấn | MotoFix Admin</title>
        <%@ include file="_head.jspf" %>
    </head>
    <body>
        <div class="layout">
            <% request.setAttribute("activeMenu","consultations"); %>
            <% request.setAttribute("pageTitle","Danh sách cần tư vấn"); %>
            <%@ include file="_sidebar.jspf" %>
            <main class="content">
                <%@ include file="_topbar.jspf" %>

                <div class="card p-4">
                    <div class="d-flex justify-content-between align-items-center mb-3">
                        <h5 class="fw-bold mb-0">Quản lý yêu cầu tư vấn</h5>
                        <div class="btn-group">
                            <a href="${pageContext.request.contextPath}/admin/ConsulationController?status=all"
                               class="btn btn-outline-secondary btn-sm">Tất cả</a>

                            <a href="${pageContext.request.contextPath}/admin/ConsulationController?status=pending"
                               class="btn btn-outline-secondary btn-sm">Chờ xử lý</a>

                            <a href="${pageContext.request.contextPath}/admin/ConsulationController?status=done"
                               class="btn btn-outline-secondary btn-sm">Đã liên hệ</a>
                        </div>
                    </div>
                    <div class="table-responsive">
                        <table class="table align-middle">
                            <thead>
                                <tr>
                                    <th>Khách hàng</th>
                                    <th>Nội dung</th>
                                    <th>Thời gian gửi</th>
                                    <th>Trạng thái</th>
                                    <th class="text-end">Thao tác</th>
                                </tr>
                            </thead>
                            <tbody>
                                <c:forEach var="item" items="${consulations}">
                                    <tr>
                                        <td>
                                            <div class="fw-semibold">${item.name}</div>
                                            <small class="text-muted">${item.phone}</small>
                                        </td>
                                        <td>${item.content}</td>
                                        <td>${item.createdAt}</td>
                                                <td><span class="badge-soft warning">
                                                        <c:if test="${!item.status}">Chờ xử lý</c:if>
                                                        <c:if test="${item.status}">Đã xử lý</c:if>
                                            </span></td>
                                <form action="${pageContext.request.contextPath}/admin/ConsulationController" method="POST">
                                    <td class="text-end">
                                        <input type="hidden" name="id" value="${item.consultationID}"/>

                                        <c:if test="${!item.status}"><button name="action" value="call" class="btn btn-sm btn-primary">Gọi lại</button></c:if>

                                        <button name="action" value="delete" class="btn btn-sm btn-outline-danger"
                                                onclick="return confirm('Bạn có chắc muốn xóa?');">
                                            <i class="bi bi-trash"></i>
                                        </button>
                                    </td>
                                </form>
                                </tr>
                            </c:forEach>

                            </tbody>
                        </table>
                    </div>
                </div>

                <%@ include file="_footer.jspf" %>
            </main>
        </div>
    </body>
</html>
