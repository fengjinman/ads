package com.powerwin.entity;

/**
 * Created by fengjinman Administrator on 2018/7/10.
 */
public class User {

    int id;

    String username;

    String password;

    int number;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
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

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public User(int id, String username, String password, int number) {
        this.id = id;
        this.username = username;
        this.password = password;
        this.number = number;
    }

    public User() {
    }
}
