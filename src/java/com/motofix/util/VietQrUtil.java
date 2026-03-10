package com.motofix.util;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

public class VietQrUtil {
    private VietQrUtil() {
    }

    public static String generateQrLink(long totalAmount, String orderInfo) {
        try {
            String bankId = "970422";
            String accountNo = "3398135906";
            String template = "compact2";

            String content = URLEncoder.encode(orderInfo, StandardCharsets.UTF_8.toString());
            return "https://img.vietqr.io/image/" + bankId + "-" + accountNo + "-" + template + ".png"
                    + "?amount=" + totalAmount
                    + "&addInfo=" + content;
        } catch (Exception e) {
            return "";
        }
    }
}
