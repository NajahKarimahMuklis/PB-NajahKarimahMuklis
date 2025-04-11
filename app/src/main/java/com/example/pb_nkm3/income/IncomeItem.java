package com.example.pb_nkm3.income;

public class IncomeItem {
    private String source;
    private String amount;
    private String date;
    private String key;

    // Constructor kosong diperlukan untuk Firebase
    public IncomeItem() {}

    public IncomeItem(String source, String amount, String date) {
        this.source = source;
        this.amount = amount;
        this.date = date;
    }

    // Getter
    public String getSource() {
        return source;
    }

    public String getAmount() {
        return amount;
    }

    public String getDate() {
        return date;
    }

    public String getKey() {
        return key;
    }

    // Setter
    public void setSource(String newSource) {
        this.source = newSource;
    }

    public void setAmount(String newAmount) {
        this.amount = newAmount;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public void setKey(String key) {
        this.key = key;
    }
}
