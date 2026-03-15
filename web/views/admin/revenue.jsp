<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!doctype html>
<html lang="vi">
    <head>
        <title>Hóa đơn & doanh thu | MotoFix Admin</title>
        <%@ include file="_head.jspf" %>
    </head>
    <body>
        <div class="layout">
            <% request.setAttribute("activeMenu","revenue"); %>
            <% request.setAttribute("pageTitle","Lịch sử hóa đơn & doanh thu"); %>
            <%@ include file="_sidebar.jspf" %>
            <main class="content">
                <%@ include file="_topbar.jspf" %>

                <% if (request.getAttribute("error") != null) { %>
                <div class="alert alert-danger"><%= request.getAttribute("error") %></div>
                <% } %>



                <div class="card p-4">
                    <div class="card-header bg-white fw-bold">Hóa đơn thanh toán gần đây</div>
                    <div class="table-responsive">
                        <table class="table table-hover align-middle mb-0">
                            <thead class="table-light">
                                <tr>
                                    <th>Mã Phiếu</th>
                                    <th>Khách hàng</th>
                                    <th>Nhân Viên Phụ Trách</th>

                                    <th>Số tiền</th>
                                    <th class="text-end">Trạng Thái</th>
                                </tr>
                            </thead>
                            <tbody>

                                <c:if test="${empty recentInvoices}">
                                    <tr>
                                        <td colspan="5" class="text-center text-muted py-4">
                                            Không có bản ghi hóa đơn nào.
                                        </td>
                                    </tr>
                                </c:if>

                                <c:forEach var="tiem" items="${recentInvoices}">
                                    <tr>
                                        <td>TK-${tiem.ticketId}</td>
                                        <td>${tiem.customerName}</td>

                                        <td>
                                            ${tiem.employeeName}
                                        </td>

                                        <td class="text-success fw-bold">
                                            <fmt:formatNumber value="${tiem.finalAmount}" type="number"/>đ
                                        </td>
                                        <td class="text-end">
                                            <form action="${pageContext.request.contextPath}/admin/revenue" method="POST">
                                                <input type="hidden" name="id" value="${tiem.ticketId}"/>

                                                <c:if test="${tiem.paymentStatus != 'PAID'}">
                                                    <button name="action" value="pay" class="btn btn-sm btn-primary">
                                                        Thanh Toán
                                                    </button>
                                                </c:if>

                                                <button name="action" value="delete" class="btn btn-sm btn-outline-danger"
                                                        onclick="return confirm('Bạn có chắc muốn quay lại?');">
                                                    <i class="bi bi-arrow-left-circle"></i>
                                                </button>
                                            </form>
                                        </td>
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
