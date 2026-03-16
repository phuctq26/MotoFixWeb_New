<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!doctype html>
<html lang="vi">
    <head>
        <title>Dashboard | MotoFix Admin</title>
        <%@ include file="_head.jspf" %>
    </head>
    <body>
        <div class="layout">
            <% request.setAttribute("activeMenu","dashboard"); %>
            <% request.setAttribute("pageTitle","Dashboard Tổng Quan"); %>
            <%@ include file="_sidebar.jspf" %>
            <main class="content">
                <%@ include file="_topbar.jspf" %>

                <div class="row g-3">
                    <div class="col-md-3">
                        <div class="card stat-card">
                            <div class="d-flex align-items-center gap-3">
                                <div class="stat-icon"><i class="bi bi-currency-dollar"></i></div>
                                <div>
                                    <div class="text-muted">Doanh thu hôm nay</div>
                                    <div class="fw-bold"><%= String.format("%,.0f", request.getAttribute("revenueToday") != null ? (Double) request.getAttribute("revenueToday") : 0.0) %>đ</div>
                                </div>
                            </div>
                        </div>
                    </div>
                    <div class="col-md-3">
                        <div class="card stat-card">
                            <div class="d-flex align-items-center gap-3">
                                <div class="stat-icon"><i class="bi bi-tools"></i></div>
                                <div>
                                    <div class="text-muted">Xe đang sửa</div>
                                    <div class="fw-bold">${vehicleInprogress}</div>
                                </div>
                            </div>
                        </div>
                    </div>
                    <div class="col-md-3">
                        <div class="card stat-card">
                            <div class="d-flex align-items-center gap-3">
                                <div class="stat-icon"><i class="bi bi-calendar-event"></i></div>
                                <div>
                                    <div class="text-muted">Đặt lịch chờ duyệt</div>
                                    <div class="fw-bold">${vehiclePending}</div>
                                </div>
                            </div>
                        </div>
                    </div>
                    <div class="col-md-3">
                        <div class="card stat-card">
                            <div class="d-flex align-items-center gap-3">
                                <div class="stat-icon"><i class="bi bi-people"></i></div>
                                <div>
                                    <div class="text-muted">Khách hàng mới</div>
                                    <div class="fw-bold">${newUser}</div>
                                </div>
                            </div>
                        </div>
                    </div>
                </div>

                <div class="row g-4 mt-1">
                    <div class="col-lg-7">
                        <div class="card p-4">
                            <h6 class="fw-bold mb-3">Biểu đồ doanh thu tuần</h6>
                            <div class="chart-placeholder"></div>
                        </div>
                    </div>
                    <div class="col-lg-5">
                        <div class="card p-4 border-0 shadow-sm" style="border-radius: 1rem;">
                            <div class="d-flex justify-content-between align-items-center mb-4">
                                <h6 class="fw-bold mb-0">Hoạt động gần đây</h6>
                            </div>

                            <div class="activity-list">
                                <c:forEach var="act" items="${recentActivities}" varStatus="status">
                                    <div class="d-flex gap-3 ${!status.last ? 'mb-4' : ''}">
                                        <span class="badge-soft ${act.category}" style="min-width: 100px; text-align: center;">
                                            ${act.type}
                                        </span>

                                        <div>
                                            <div class="fw-semibold text-dark">${act.mainInfo}</div>
                                            <small class="text-muted">
                                                ${act.timeAgo} &bull; ${act.subInfo}
                                            </small>
                                        </div>
                                    </div>
                                </c:forEach>

                                <c:if test="${empty recentActivities}">
                                    <p class="text-muted small text-center">Chưa có hoạt động nào hôm nay.</p>
                                </c:if>
                            </div>
                        </div>
                    </div>
                </div>

                <%@ include file="_footer.jspf" %>
            </main>
        </div>
    </body>
</html>
