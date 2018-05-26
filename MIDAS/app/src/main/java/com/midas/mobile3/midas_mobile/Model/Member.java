package com.midas.mobile3.midas_mobile.Model;

public class Member {

    String id, pw, name, email;

    public Member() {
        this.id = this.pw = this.name = this.email = "";
    }

    public Member(String id, String pw, String name, String email) {
        this.id = id;
        this.pw = pw;
        this.name = name;
        this.email = email;
    }

    public String getId() {
        return id;
    }

    public String getPw() {
        return pw;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }
}