package com.example;

public class YearIncome {

    private String year;
    private Double totalAmount;

    public YearIncome(String year, Double totalAmount) {
        this.year = year;
        this.totalAmount = totalAmount;
    }

    public YearIncome() {
    }

    public String getYear() {
        return year;
    }

    public Double getTotalAmount() {
        return totalAmount;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public void setTotalAmount(Double totalAmount) {
        this.totalAmount = totalAmount;
    }

    @Override
    public String toString() {
        return "YearIncome{" +
                "year='" + year + '\'' +
                ", totalAmount=" + totalAmount +
                '}';
    }

}
