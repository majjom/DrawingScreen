package com.example.majo.myapplication.entities;

import com.google.appengine.api.datastore.Blob;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;
import com.googlecode.objectify.annotation.Index;

import java.util.Date;
import java.util.List;

import static com.googlecode.objectify.ObjectifyService.ofy;

/**
 * Created by majo on 01-Feb-15.
 */

@Entity
public class SchemaMapEntity {
    public static final String PROPERTY_NAME = "name";

    @Id
    public Long id;
    @Index
    public String name;
    public Date dateCreated;
    public byte[] thumbnailImage;
    public int version;
    public String imageBlobKey;
    public String imageBlobServingUrl;

    public SchemaMapEntity(){
        super();
    }
}
