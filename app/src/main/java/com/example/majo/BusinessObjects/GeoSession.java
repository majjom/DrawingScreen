package com.example.majo.BusinessObjects;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by moravekm on 17-Dec-14.
 */
public class GeoSession extends PersistedObject implements Serializable {
    public String name;
    public Date dateCreated;
    public boolean isBeingTracked = false;
    protected List<GeoLocation> geoLocations;

    public GeoSession(String name){
        this(name, new Date());
    }

    public GeoSession(String name, Date dateCreated){
        this.name = name;
        this.dateCreated = dateCreated;
        this.geoLocations = new ArrayList<>();
    }

    public List<GeoLocation> getGeoLocations(){
        return this.geoLocations;
    }

    public void addGeoLocations(ArrayList<GeoLocation> geoLocations){
        this.geoLocations.addAll(geoLocations);
    }

    public void addGeoLocation(GeoLocation geoLocation){
        ArrayList<GeoLocation> tmp = new ArrayList<>();
        tmp.add(geoLocation);
        addGeoLocations(tmp);
    }
}
