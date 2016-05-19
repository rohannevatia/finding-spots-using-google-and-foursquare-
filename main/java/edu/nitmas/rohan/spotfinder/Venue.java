package edu.nitmas.rohan.spotfinder;

import com.google.android.gms.maps.model.LatLng;

public class Venue {
    private String id, name, address;
    private LatLng ltlg;
    Venue(String id, String name, String address, LatLng ltlg){
        this.id=id;
        this.name=name;
        this.address=address;
        this.ltlg=ltlg;
    }
    String getid(){
        return id;
    }
    String getname(){
        return name;
    }
    String getAddress(){
        return address;
    }
    double getLat(){
        return ltlg.latitude;
    }
    double getLng(){
        return ltlg.longitude;
    }
}
