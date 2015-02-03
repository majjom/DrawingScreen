package com.example.majo.myapplication.backend;

import com.google.appengine.api.datastore.Blob;
import com.googlecode.objectify.annotation.Entity;
import com.googlecode.objectify.annotation.Id;

import java.util.Date;
import java.util.List;

import static com.googlecode.objectify.ObjectifyService.ofy;

/**
 * Created by majo on 01-Feb-15.
 */

@Entity
public class SchemaMapEntity {
    @Id
    private Long id;
    private String name;
    private Date dateCreated;

    public byte[] getThumbnailImage() {
        return thumbnailImage;
    }

    private byte[] thumbnailImage;

    public Long getId() {
        return id;
    }

    public void clearId() {
        this.id = null;
    }

    public String getName() {
        return name;
    }

    public Date getDateCreated() {
        return dateCreated;
    }

    public SchemaMapEntity(){
        super();
    }

    public SchemaMapEntity(String name, Date dateCreated, byte[] thumbnailImage){
        this(null, name, dateCreated, thumbnailImage);
    }

    public SchemaMapEntity(Long id, String name, Date dateCreated, byte[] thumbnailImage){
        super();
        this.id = id;
        this.name = name;
        this.dateCreated = dateCreated;
        this.thumbnailImage = thumbnailImage;
    }
}
