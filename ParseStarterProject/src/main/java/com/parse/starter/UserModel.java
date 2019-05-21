package com.parse.starter;

public class UserModel {

    String name;
    String password;
    String age;
    String type;
    String branch;
    String ID;

    // constructor
    public UserModel(String name , String password , String age , String type , String branch , String ID)
    {
        this.name = name;
        this.password = password;
        this.age = age;
        this.type = type;
        this.branch = branch;
        this.ID = ID;
    }
}
