package com.madapps.madtaxi_rider.model;

/**
 * Created by shehan_k on 11/7/2017.
 */

public class Rider {

    private String name,email,password,phone;

    public Rider(String name, String email, String password, String phone) {
        this.name = name;
        this.email = email;
        this.password = password;
        this.phone = phone;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public String getPhone() {
        return phone;
    }
}
