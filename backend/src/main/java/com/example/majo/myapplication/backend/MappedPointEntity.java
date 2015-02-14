package com.example.majo.myapplication.backend;


import com.google.appengine.api.datastore.GeoPt;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Ignore;
import com.googlecode.objectify.annotation.Index;

/**
 * Created by majo on 01-Feb-15.
 */

@Entity
public class MappedPointEntity {
    public final static String SCHEMA_MAP_ENTITY_KEY_NAME = "schemaMapEntityKey";

    @Id
    public Long id;
    @Index
    public Key<SchemaMapEntity> schemaMapEntityKey;

    public float x;
    public float y;
    public float radius;

    public GeoPt geoPoint;
    public float altitude;

    public MappedPointEntity() {
        super();
    }
}