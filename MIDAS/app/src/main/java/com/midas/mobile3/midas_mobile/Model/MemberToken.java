package com.midas.mobile3.midas_mobile.Model;

public class MemberToken {
    private static Member token = null;

    public static Boolean hasLoginToken(){
        return token != null ;
    }

    public static Member getToken() {
        return token;
    }

    public static void setToken(Member token) {
        MemberToken.token = token;
    }

    public static void removeToken(){
        MemberToken.token = null;
    }
}