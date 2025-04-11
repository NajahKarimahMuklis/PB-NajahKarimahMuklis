package com.example.pb_nkm3.expense;

public class ExpenseItem {
    private String category;
    private String amount;
    private String date;
    private String key;

    // Constructor kosong diperlukan untuk Firebase


    public ExpenseItem(String category, String amount, String date) {
        this.category = category;
        this.amount = amount;
        this.date = date;
    }

    // Getter
    public String getCategory() {
        return category;
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
        this.category = newSource;
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
