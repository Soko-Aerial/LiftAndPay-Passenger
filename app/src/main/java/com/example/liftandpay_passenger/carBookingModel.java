package com.example.liftandpay_passenger;

public class carBookingModel {

    public int image;
    private String carName;
    private String costPerKilometer;

    public carBookingModel(int image, String carName, String costPerKilometer) {
        this.image = image;
        this.carName = carName;
        this.costPerKilometer = costPerKilometer;
    }

    public String getCarName() {
        return carName;
    }

    public void setCarName(String carName) {
        this.carName = carName;
    }

    public String getCostPerKilometer() {
        return costPerKilometer;
    }

    public void setCostPerKilometer(String costPerKilometer) {
        this.costPerKilometer = costPerKilometer;
    }

    public int getImage() {
        return image;
    }

    public void setImage(int image) {
        this.image = image;
    }
}
