package com.parse.starter;

public class TransactionModel {

    // declare attributes
     String branch;
     String code;
     String start;
     String end;
     String amount;
     String day;
     String user;

    // constructor
    public TransactionModel(String branch , String code , String start , String end , String amount , String day ,  String user)
    {
        this.branch = branch;
        this.code = code;
        this.start = start;
        this.end = end;
        this.amount = amount;
        this.day = day;
        this.user = user;
    }

}
