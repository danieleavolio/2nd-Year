package com.example;

public class MonthIncome {

    private String month;
    private Double totalAmount;

    public MonthIncome(String month, Double totalAmount) {
        this.month = month;
        this.totalAmount = totalAmount;
    }

    public MonthIncome() {
    }

    public String getMonth() {
        return month;
    }

    public Double getTotalAmount() {
        return totalAmount;
    }

    public void setMonth(String month) {
        this.month = month;
    }

    public void setTotalAmount(Double totalAmount) {
        this.totalAmount = totalAmount;
    }

    @Override
    public String toString() {
        return "MonthIncome{" +
                "month='" + month + '\'' +
                ", totalAmount=" + totalAmount +
                '}';
    }

}
