package com.example.kbss_recupapp;

import android.os.Parcel;
import android.os.Parcelable;

import com.mapbox.geojson.Point;
import com.mapbox.mapboxsdk.geometry.LatLng;

import java.util.Map;

public class Marker implements Parcelable {

    public String titel, omschrijving, foto;

    public double lat, lng;

    public Map<String,Double> location;


    public Marker() {
    }

    public Marker(String titel, String omschrijving, String foto){
        this.titel = titel;
        this.omschrijving = omschrijving;
        this.foto = foto;

    }

    public Marker(String titel, String omschrijving, String foto, Map<String,Double> location){
        this.titel = titel;
        this.omschrijving = omschrijving;
        this.foto = foto;
        this.location = location;

    }

    public Marker(String titel, String omschrijving, String foto, double lat, double lng){
        this.titel = titel;
        this.omschrijving = omschrijving;
        this.foto = foto;
        this.lat = lat;
        this.lng = lng;
    }

    public String getTitel() {
        return titel;
    }

    public void setTitel(String titel) {
        this.titel = titel;
    }

    public String getOmschrijving() {
        return omschrijving;
    }

    public void setOmschrijving(String omschrijving) {
        this.omschrijving = omschrijving;
    }

    public String getFoto() {
        return foto;
    }

    public void setFoto(String foto) {
        this.foto = foto;
    }

    public double getLat() {
        return lat;
    }
    public String getLatBis() {
        String lats =String.valueOf(lat);
        return lats;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLng() {
        return lng;
    }

    public String getLngBis() {
        String lngS =String.valueOf(lng);
        return lngS;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }

    protected Marker(Parcel in) {
        titel = in.readString();
        omschrijving = in.readString();
        foto = in.readString();
        lat = in.readDouble();
        lng = in.readDouble();
    }



    public static final Creator<Marker> CREATOR = new Creator<Marker>() {
        @Override
        public Marker createFromParcel(Parcel in) {
            return new Marker(in);
        }

        @Override
        public Marker[] newArray(int size) {
            return new Marker[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(titel);
        parcel.writeString(omschrijving);
        parcel.writeString(foto);
        parcel.writeDouble(lat);
        parcel.writeDouble(lng);
    }
}
