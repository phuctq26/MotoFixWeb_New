<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<!doctype html>
<html lang="vi">
    <head>
        <title>Liên hệ | MotoFix</title>
        <%@ include file="_head.jspf" %>
    </head>
    <body>
        <% request.setAttribute("activeMenu","contact"); %>
        <%@ include file="_header.jspf" %>

        <section class="page-hero">
            <div class="container text-center">
                <h2>Liên hệ &amp; hỗ trợ</h2>
                <p class="text-light">Đội ngũ MotoFix luôn sẵn sàng lắng nghe và giải đáp thắc mắc của bạn.</p>
            </div>
        </section>

        <section class="py-5">
            <div class="container">
                <div class="row g-4">
                    <div class="col-lg-4">
                        <div class="form-card">
                            <h5 class="fw-bold">Thông tin liên hệ</h5>
                            <div class="mt-3">
                                <p><i class="bi bi-geo-alt"></i> Khu Giáo dục và Đào tạo - Khu Công nghệ cao Hòa Lạc, Km29 Đại lộ Thăng Long, Thạch Thất, Hà Nội</p>
                                <p><i class="bi bi-telephone"></i> 0123 456 789</p>
                                <p><i class="bi bi-envelope"></i> contact@motofix.vn</p>
                                <p><i class="bi bi-clock"></i> Thứ 2 - CN: 08:00 - 20:00</p>
                            </div>
                        </div>
                        <div class="form-card mt-4 text-center">
                            <div class="text-muted">Google Map Embed Here</div>
                        </div>
                    </div>
                    <div class="col-lg-8">
                        <div class="form-card">
                            <h5 class="fw-bold">Đăng ký tư vấn miễn phí</h5>
                            <c:if test="${not empty message}">
                                <div class="alert alert-success alert-dismissible fade show mt-2" role="alert">
                                    ${message}
                                    <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
                                </div>
                            </c:if>
                            <p class="text-muted">Để lại thông tin, kỹ thuật viên sẽ liên hệ tư vấn phù hợp.</p>
                            <form class="mt-3" action="ConsulationController" method="post">

                                <div class="row g-3">
                                    <div class="col-md-6">
                                        <label class="form-label">Họ và tên</label>
                                        <input class="form-control" name="fullName" placeholder="Nguyễn Văn A" value="${fullName}"/>
                                    </div>
                                    <div class="col-md-6">
                                        <label class="form-label">Số điện thoại</label>
                                        <input class="form-control" name="phoneNumber" placeholder="090 xxx xxxx" value="${phoneNumber}"/>
                                    </div>
                                </div>
                                <div class="mt-3">
                                    <label class="form-label">Nội dung cần tư vấn</label>
                                    <textarea class="form-control" rows="4" name="content"  placeholder="Ví dụ: Xe bị kêu lạ ở động cơ, cần tư vấn thay nhớt...">${content}</textarea>
                                </div>
                                <button class="btn btn-primary mt-3">Gửi yêu cầu</button>
                            </form>
                        </div>
                    </div>
                </div>

                <div class="mt-5">
                    <h4 class="section-title text-center">Câu hỏi thường gặp</h4>
                    <div class="accordion mt-3" id="faq">
                        <div class="accordion-item">
                            <h2 class="accordion-header">
                                <button class="accordion-button" type="button" data-bs-toggle="collapse" data-bs-target="#faq1">Thời gian bảo dưỡng toàn bộ xe mất bao lâu?</button>
                            </h2>
                            <div id="faq1" class="accordion-collapse collapse show" data-bs-parent="#faq">
                                <div class="accordion-body">Khoảng 2-3 giờ tùy tình trạng xe.</div>
                            </div>
                        </div>
                        <div class="accordion-item">
                            <h2 class="accordion-header">
                                <button class="accordion-button collapsed" type="button" data-bs-toggle="collapse" data-bs-target="#faq2">MotoFix có dịch vụ cứu hộ xe máy không?</button>
                            </h2>
                            <div id="faq2" class="accordion-collapse collapse" data-bs-parent="#faq">
                                <div class="accordion-body">Có, liên hệ hotline để được hỗ trợ nhanh.</div>
                            </div>
                        </div>
                    </div>
                </div>
            </div>
        </section>

        <%@ include file="_footer.jspf" %>
    </body>
</html>
