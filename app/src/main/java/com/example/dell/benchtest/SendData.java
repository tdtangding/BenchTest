package com.example.dell.benchtest;

/**
 * Created by dell on 2018/9/18.
 */

public class SendData {

    public int getCommand() {
        return Command;
    }

    public String getDevID() {
        return DevID;
    }

    public String getReserved1() {
        return Reserved1;
    }

    public String getReserved2() {
        return Reserved2;
    }



    public void setCommand(int command) {
        Command = command;
    }

    public void setDevID(String devID) {
        DevID = devID;
    }

    public void setReserved1(String reserved1) {
        Reserved1 = reserved1;
    }

    public void setReserved2(String reserved2) {
        Reserved2 = reserved2;
    }
    private int Command;
    private String DevID;
    private String Reserved1;
    private String Reserved2;
}
