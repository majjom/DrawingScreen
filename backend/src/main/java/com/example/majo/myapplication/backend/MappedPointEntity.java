package com.example.majo.myapplication.backend;


import com.google.appengine.api.datastore.GeoPt;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;

/**
 * Created by majo on 01-Feb-15.
 */

@Entity
public class MappedPointEntity {
    @Id
    private Long id;

    private Key<SchemaMapEntity> schemaMapEntityKey;

    private float x;
    private float y;
    private float radius;

    private GeoPt geoPoint;
    private float Altitude;

    public MappedPointEntity() {
        super();
    }

    public MappedPointEntity(Long schemaMapEntityId, float x, float y, float radius, float latitude, float longitude, float altitude) {
        this.x = x;
        this.y = y;
        this.radius = radius;
        this.geoPoint = new GeoPt(latitude, longitude);
        Altitude = altitude;
        this.schemaMapEntityKey = Key.create(SchemaMapEntity.class, schemaMapEntityId);
    }

    public Long getId() {
        return id;
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public float getRadius() {
        return radius;
    }

    public float getLatitude() {
        return this.geoPoint.getLatitude();
    }

    public float getLongitude() {
        return this.geoPoint.getLatitude();
    }

    public float getAltitude() {
        return Altitude;
    }
}