package com.google.bustracker;

/**
 * Created by Gourav on 11-10-2017.
 */

public class Person {

    private String name;
    private String userName;
    private String password;
    private String busNo;

    public Person(String name, String userName, String password, String busNo) {
        this.name = name;
        this.userName = userName;
        this.password = password;
        this.busNo = busNo;
    }

    public String getName() {
        return name;
    }

    public String getUserName() {
        return userName;
    }

    public String getPassword() {
        return password;
    }

    public String getBusNo() {
        return busNo;
    }
}
