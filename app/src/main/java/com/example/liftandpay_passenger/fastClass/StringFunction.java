package com.example.liftandpay_passenger.fastClass;

public class StringFunction {

    private final String theString;

    public StringFunction(String theString) {
        this.theString = theString;
    }

    public String removeLastNumberOfCharacter(int numberOfCharacters) {
        return (this.theString == null || this.theString.length() == 0)
                ? null
                : (this.theString.substring(0, this.theString.length() - numberOfCharacters));
    }
}
