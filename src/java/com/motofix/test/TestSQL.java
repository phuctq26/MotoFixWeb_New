package com.motofix.test;

import com.motofix.dao.InvoiceDAO;

public class TestSQL {
    public static void main(String[] args) {
        InvoiceDAO dao = new InvoiceDAO();
        System.out.println("Labels: " + dao.getRevenueLabelsLast7Days());
        System.out.println("Data: " + dao.getRevenueDataLast7Days());
    }
}
