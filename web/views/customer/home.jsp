<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!doctype html>
<html lang="vi">
    <head>
        <title>Trang chủ | MotoFix</title>
        <%@ include file="_head.jspf" %>
        <style>
            .hero {
                background:url("https://images.unsplash.com/photo-1489515217757-5fd1be406fef?q=80&w=1400&auto=format&fit=crop") center/cover;
            }
            .stats-badge {
                position:absolute;
                left:20px;
                bottom:-20px;
                background:var(--brand);
                color:#fff;
                border-radius:14px;
                padding:10px 16px;
                box-shadow:0 12px 20px rgba(30,120,255,.25);
                font-weight:700;
            }
        </style>
    </head>
    <body>
        <% request.setAttribute("activeMenu","home"); %>
        <%@ include file="_header.jspf" %>

        <section class="hero">
            <div class="container hero-content">
                <div class="col-lg-7">
                    <h1>Chăm sóc xe yêu của bạn <span class="text-primary">Chuyên nghiệp &amp; tận tâm</span></h1>
                    <p class="text-light mt-3">Hệ thống sửa chữa xe máy hiện đại với đội ngũ kỹ thuật viên tay nghề cao. Đặt lịch ngay hôm nay để trải nghiệm dịch vụ tốt nhất.</p>
                    <div class="d-flex gap-3 mt-4">
                        <a class="btn btn-primary" href="${pageContext.request.contextPath}/booking">Đặt lịch ngay</a>
                        <a class="btn btn-outline-light" href="#services">Xem dịch vụ</a>
                    </div>
                </div>
            </div>
        </section>

        <section id="services" class="py-5">
            <div class="container">
                <div class="text-center mb-4">
                    <h3 class="section-title">Dịch vụ phổ biến</h3>
                    <p class="text-muted">Các gói dịch vụ được khách hàng tin dùng nhất tại MotoFix</p>
                </div>
                <div class="row g-4">
                    <c:forEach var="s" items="${topServices}" varStatus="loop">
                        <div class="col-md-3">
                            <div class="service-card">
                                <div class="icon">
                                    <c:choose>
                                        <c:when test="${loop.index == 0}"><i class="bi bi-droplet"></i></c:when>
                                        <c:when test="${loop.index == 1}"><i class="bi bi-circle"></i></c:when>
                                        <c:when test="${loop.index == 2}"><i class="bi bi-gear"></i></c:when>
                                        <c:otherwise><i class="bi bi-battery-charging"></i></c:otherwise>
                                    </c:choose>
                                </div>
                                <h6 class="fw-bold"><c:out value="${s.serviceName}"/></h6>
                                <p class="text-muted small"><c:out value="${s.description}"/></p>
                                <div class="badge-price">
                                    <fmt:formatNumber value="${s.price}" type="number"/>đ
                                </div>
                            </div>
                        </div>
                    </c:forEach>
                </div>
            </div>
        </section>

        <section class="py-5">
            <div class="container">
                <div class="row g-4 align-items-center">
                    <div class="col-lg-6">
                        <h3 class="section-title">Tại sao chọn MotoFix?</h3>
                        <ul class="list-unstyled mt-3">
                            <li class="mb-2"><i class="bi bi-check-circle text-success"></i> Phụ tùng chính hãng 100%</li>
                            <li class="mb-2"><i class="bi bi-check-circle text-success"></i> Minh bạch về giá</li>
                            <li class="mb-2"><i class="bi bi-check-circle text-success"></i> Đặt lịch dễ dàng</li>
                        </ul>
                    </div>
                    <div class="col-lg-6">
                        <div class="position-relative">
                            <img class="img-fluid rounded-4" src="https://images.unsplash.com/photo-1500530855697-b586d89ba3ee?q=80&w=1200&auto=format&fit=crop" alt="MotoFix" />
                            <div class="stats-badge">10+ năm kinh nghiệm</div>
                        </div>
                    </div>
                </div>
            </div>
        </section>

        <section class="py-5 bg-light">
            <div class="container">
                <div class="row g-4 align-items-center"> <div class="col-lg-5">
                        <div class="pe-lg-4"> <h3 class="section-title mb-3">Tìm chúng tôi trên bản đồ</h3>
                            <p class="text-muted mb-4">
                                Khu Giáo dục và Đào tạo - Khu Công nghệ cao Hòa Lạc, <br>
                                Km29 Đại lộ Thăng Long, Thạch Thất, Hà Nội
                            </p>
                            <div class="contact-details">
                                <p class="mb-2"><strong><i class="bi bi-clock me-2"></i> Giờ làm việc:</strong> 08:00 - 18:00</p>
                                <p class="mb-2"><strong><i class="bi bi-telephone me-2"></i> Hotline:</strong> 0123 456 789</p>
                            </div>
                            <a href="https://www.google.com/maps?q=FPT+University+Hanoi" target="_blank" class="btn btn-primary mt-3">
                                <i class="bi bi-geo-alt me-2"></i>Chỉ đường trên Google Maps
                            </a>
                        </div>
                    </div>

                    <div class="col-lg-7">
                        <div class="card border-0 shadow-sm overflow-hidden" style="border-radius: 1rem;">
                            <iframe 
                                src="https://www.google.com/maps/embed?pb=!1m18!1m12!1m3!1d458114.27999944973!2d104.9155479890625!3d21.012416700000003!2m3!1f0!2f0!3f0!3m2!1i1024!2i768!4f13.1!3m3!1m2!1s0x3135abc60e7d3f19%3A0x2be9d7d0b5abcbf4!2zVHLGsOG7nW5nIMSQ4bqhaSBo4buNYyBGUFQgSMOgIE7hu5lp!5e1!3m2!1svi!2s!4v1773641182817!5m2!1svi!2s" 
                                width="100%" 
                                height="400" 
                                style="border:0; display:block;" 
                                allowfullscreen="" 
                                loading="lazy" 
                                referrerpolicy="no-referrer-when-downgrade">
                            </iframe>
                        </div>
                    </div>

                </div>
            </div>
        </section>
        <%@ include file="_footer.jspf" %>
    </body>
</html>
