package com.example.shakegraph;

public class CustomUser {
    private String email;
    private String username;
    private String password;

    public CustomUser(String email, String username, String password) {
        this.email = email;
        this.username = username;
        this.password = password;
    }
    public CustomUser(){    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
