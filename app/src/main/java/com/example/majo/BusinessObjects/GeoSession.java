package com.example.majo.BusinessObjects;

import java.util.Date;

/**
 * Created by moravekm on 17-Dec-14.
 */
public class GeoSession extends PersistedObject {
    public String name;
    public Date dateCreated;

    public GeoSession(int id, String name, Date dateCreated){
        this.id = id;
        this.name = name;
        this.dateCreated = dateCreated;
    }

}
