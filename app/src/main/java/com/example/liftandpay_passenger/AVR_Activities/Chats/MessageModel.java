package com.example.liftandpay_passenger.AVR_Activities.Chats;

public class MessageModel {

    private String message;
    private int vType;
    private int time;
    private int image;

    MessageModel(String message, int vType){
        this.message = message;
        this.vType =vType;
    }


    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getvType() {
        return vType;
    }

    public void setvType(int vType) {
        this.vType = vType;
    }
}
