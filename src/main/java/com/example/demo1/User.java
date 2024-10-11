package com.example.demo1;

public class User {
    private String username;
    private String password;
    private Double balance;

    public User(String username, String password, Double balance) {
        this.username = username;
        this.password = password;
        this.balance = balance;
    }
    //Getters
    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public Double getBalance() {
        return balance;
    }

    //Setters
    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
            this.password = password;
    }

    public void setBalance(Double balance) {
        this.balance = balance;
    }
}
