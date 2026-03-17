<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<%@ page import="com.motofix.util.VietQrUtil" %>
<%@ page import="com.motofix.model.RepairTicket" %>
<!doctype html>
<html lang="vi">
    <head>
        <title>Hóa đơn & doanh thu | MotoFix Admin</title>
        <%@ include file="_head.jspf" %>
    </head>
    <body>
        <div class="layout">
            <% request.setAttribute("activeMenu","revenue"); %>
            <% request.setAttribute("pageTitle","Danh sách thanh toán"); %>
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
                                    <c:if test="${tiem.paymentStatus != 'PAID'}">
                                        <%
                                            RepairTicket currentTicket = (RepairTicket) pageContext.getAttribute("tiem");
                                            long qrAmount = (long) currentTicket.getFinalAmount();
                                            String qrInfo = "Thanh toan hoa don TK " + currentTicket.getTicketId();
                                            String qrLink = VietQrUtil.generateQrLink(qrAmount, qrInfo);
                                            pageContext.setAttribute("qrLinkCode", qrLink);
                                        %>
                                    </c:if>
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
                                            <form action="${pageContext.request.contextPath}/admin/revenue" method="POST" style="display:inline-block;">
                                                <input type="hidden" name="id" value="${tiem.ticketId}"/>

                                                <c:if test="${tiem.paymentStatus != 'PAID'}">
                                                    <button type="button" class="btn btn-sm btn-outline-primary me-1" data-bs-toggle="modal" data-bs-target="#qrModal${tiem.ticketId}">
                                                        Mã QR
                                                    </button>

                                                    <button name="action" value="pay" class="btn btn-sm btn-primary">
                                                        Thanh Toán
                                                    </button>
                                                </c:if>

                                                <button name="action" value="delete" class="btn btn-sm btn-outline-danger ms-1"
                                                        onclick="return confirm('Bạn có chắc muốn quay lại?');">
                                                    <i class="bi bi-arrow-left-circle"></i>
                                                </button>
                                            </form>

                                            <c:if test="${tiem.paymentStatus != 'PAID'}">
                                                <!-- Modal QR Code -->
                                                <div class="modal fade text-start" id="qrModal${tiem.ticketId}" tabindex="-1">
                                                    <div class="modal-dialog modal-dialog-centered modal-sm">
                                                        <div class="modal-content">
                                                            <div class="modal-header">
                                                                <h5 class="modal-title">Thanh toán VNPay QR</h5>
                                                                <button type="button" class="btn-close" data-bs-dismiss="modal"></button>
                                                            </div>
                                                            <div class="modal-body text-center">
                                                                <p class="mb-2">Quét mã để thanh toán 
                                                                    <strong class="text-success"><fmt:formatNumber value="${tiem.finalAmount}" type="number"/>đ</strong>
                                                                </p>
                                                                <img src="${qrLinkCode}" alt="QR Code" class="img-fluid border rounded p-2 mb-3" style="max-width: 250px;">
                                                                <p class="small text-muted mb-0">Hóa đơn: <strong>TK-${tiem.ticketId}</strong></p>
                                                            </div>
                                                            <div class="modal-footer justify-content-center p-2">
                                                                <form action="${pageContext.request.contextPath}/admin/revenue" method="POST">
                                                                    <input type="hidden" name="id" value="${tiem.ticketId}"/>
                                                                    <button type="submit" name="action" value="pay" class="btn btn-success">
                                                                        <i class="bi bi-check-circle me-1"></i>Xác nhận đã thanh toán
                                                                    </button>
                                                                </form>
                                                            </div>
                                                        </div>
                                                    </div>
                                                </div>
                                            </c:if>
                                        </td>
                                    </tr>
                                </c:forEach>

                            </tbody>
                        </table>
                    </div>
                    <c:if test="${totalPages > 1}">
                        <nav aria-label="Page navigation" class="mt-4">
                            <ul class="pagination justify-content-center">
                                <li class="page-item ${currentPage == 1 ? 'disabled' : ''}">
                                    <a class="page-link" href="?page=${currentPage - 1}">Trước</a>
                                </li>

                                <c:forEach begin="1" end="${totalPages}" var="i">
                                    <li class="page-item ${currentPage == i ? 'active' : ''}">
                                        <a class="page-link" href="?page=${i}">${i}</a>
                                    </li>
                                </c:forEach>

                                <li class="page-item ${currentPage == totalPages ? 'disabled' : ''}">
                                    <a class="page-link" href="?page=${currentPage + 1}">Sau</a>
                                </li>
                            </ul>
                        </nav>
                    </c:if>
                </div>

                <%@ include file="_footer.jspf" %>
            </main>
        </div>
    </body>
</html>
