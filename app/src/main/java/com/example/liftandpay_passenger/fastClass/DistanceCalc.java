package com.example.liftandpay_passenger.fastClass;

public class DistanceCalc {

    private double earthRadiusInKm = 6371;
    private double degreesToRadians(double degrees){
        return degrees * Math.PI/180;
    }

    public double distanceBtnCoordinates(double lat1,double lon1, double lat2,double lon2){
        double dlat = degreesToRadians(lat1-lat2);
        double dlon = degreesToRadians(lon1-lon2);

        lat1 = degreesToRadians(lat1);
        lat2 = degreesToRadians(lat2);

        double a = Math.sin(dlat/2) * Math.sin(dlat/2) +
                Math.sin(dlon/2) * Math.sin(dlon/2) *
                        Math.cos(lat1) * Math.cos(lat2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));

        return earthRadiusInKm * c;

    }
}
