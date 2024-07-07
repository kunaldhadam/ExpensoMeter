package com.example.expense2.model;

public class transaction {
    private int id;
    private String transaction_name;
    private String transaction_type;
    private double transaction_amount;
    private String date;

    public transaction(int id, String transaction_name, String transaction_type, double transaction_amount) {
        this.id = id;
        this.transaction_name = transaction_name;
        this.transaction_type = transaction_type;
        this.transaction_amount = transaction_amount;
    }

    public transaction(String transaction_name, String transaction_type, double transaction_amount) {
        this.transaction_name = transaction_name;
        this.transaction_type = transaction_type;
        this.transaction_amount = transaction_amount;
    }

    public transaction(){}

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTransaction_name() {
        return transaction_name;
    }

    public void setTransaction_name(String transaction_name) {
        this.transaction_name = transaction_name;
    }

    public String getTransaction_type() {
        return transaction_type;
    }

    public void setTransaction_type(String transaction_type) {
        this.transaction_type = transaction_type;
    }

    public double getTransaction_amount() {
        return transaction_amount;
    }

    public void setTransaction_amount(double transaction_amount) {
        this.transaction_amount = transaction_amount;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
