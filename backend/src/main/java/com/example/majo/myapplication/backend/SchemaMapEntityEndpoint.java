package com.example.majo.myapplication.backend;

import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiMethod;
import com.google.api.server.spi.config.ApiNamespace;
import com.google.api.server.spi.response.CollectionResponse;
import com.google.api.server.spi.response.NotFoundException;
import com.google.appengine.api.datastore.Cursor;
import com.google.appengine.api.datastore.QueryResultIterator;
import com.googlecode.objectify.ObjectifyService;
import com.googlecode.objectify.cmd.Query;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

import javax.annotation.Nullable;
import javax.inject.Named;

import contract.SchemaMapDto;

import static com.googlecode.objectify.ObjectifyService.ofy;

/**
 * WARNING: This generated code is intended as a sample or starting point for using a
 * Google Cloud Endpoints RESTful API with an Objectify entity. It provides no data access
 * restrictions and no data validation.
 * <p/>
 * DO NOT deploy this code unchanged as part of a real application to real users.
 */
@Api(
        name = "schemaMapEntityApi",
        version = "v1",
        resource = "schemaMapEntity",
        namespace = @ApiNamespace(
                ownerDomain = "backend.myapplication.majo.example.com",
                ownerName = "backend.myapplication.majo.example.com",
                packagePath = ""
        )
)
public class SchemaMapEntityEndpoint {

    private static final Logger logger = Logger.getLogger(SchemaMapEntityEndpoint.class.getName());

    private static final int DEFAULT_LIST_LIMIT = 20;

    static {
        // Typically you would register this inside an OfyServive wrapper. See: https://code.google.com/p/objectify-appengine/wiki/BestPractices
        ObjectifyService.register(SchemaMapEntity.class);
    }

    /**
     * Returns the {@link SchemaMapEntity} with the corresponding ID.
     *
     * @param id the ID of the entity to be retrieved
     * @return the entity with the corresponding ID
     */
    @ApiMethod(
            name = "getById",
            path = "schemaMapEntity/{id}",
            httpMethod = ApiMethod.HttpMethod.GET)
    public SchemaMapDto getById(@Named("id") Long id)  {
        logger.info("Getting SchemaMapEntity with ID: " + id);
        SchemaMapDto schemaMapDto = convertToDto(ofy().load().type(SchemaMapEntity.class).id(id).now());
        return schemaMapDto;
    }

    /**
     * Returns the list of {@link SchemaMapEntity} with the corresponding name.
     *
     * @param name the name of the entities to be retrieved
     * @return the entity with the corresponding ID
     */
    @ApiMethod(
            name = "getByName")
    public List<SchemaMapDto> getByName(@Named("name") String name)  {
        logger.info("Getting SchemaMapEntity with name: " + name);
        List<SchemaMapDto> result = new ArrayList<>();
        for(SchemaMapEntity schemaMapEntity : ofy().load().type(SchemaMapEntity.class).filter(SchemaMapEntity.PROPERTY_NAME, name).list()){
            result.add(convertToDto(schemaMapEntity));
        }
        return result;
    }

    /**
     * Inserts a new {@code SchemaMapEntity}.
     */
    @ApiMethod(
            name = "save",
            path = "schemaMapEntity",
            httpMethod = ApiMethod.HttpMethod.POST)
    public SchemaMapDto save(SchemaMapDto schemaMapDto) {
        SchemaMapEntity schemaMapEntity = convertToEntity(schemaMapDto);
        ofy().save().entity(schemaMapEntity).now(); // here we get key if we have none
        logger.info("Saved SchemaMapEntity with ID: " + schemaMapEntity.id);
        return convertToDto(schemaMapEntity);
    }

    /**
     * Deletes the specified {@code SchemaMapEntity}.
     *
     * @param id the ID of the entity to delete
     * @throws NotFoundException if the {@code id} does not correspond to an existing
     *                           {@code SchemaMapEntity}
     */
    @ApiMethod(
            name = "delete",
            path = "schemaMapEntity/{id}",
            httpMethod = ApiMethod.HttpMethod.DELETE)
    public void delete(@Named("id") Long id)  {
        ofy().delete().type(SchemaMapEntity.class).id(id).now();
        logger.info("Deleted SchemaMapEntity with ID: " + id);
    }

    SchemaMapDto convertToDto(SchemaMapEntity schemaMapEntity){
        SchemaMapDto result = new SchemaMapDto();
        result.id = schemaMapEntity.id;
        result.dateCreated = schemaMapEntity.dateCreated;
        result.name = schemaMapEntity.name;
        result.thumbnailImage = schemaMapEntity.thumbnailImage;
        result.version = schemaMapEntity.version;
        return result;
    }

    SchemaMapEntity convertToEntity(SchemaMapDto schemaMapDto){
        SchemaMapEntity result = new SchemaMapEntity();
        result.id = schemaMapDto.id;
        result.dateCreated = schemaMapDto.dateCreated;
        result.name = schemaMapDto.name;
        result.thumbnailImage = schemaMapDto.thumbnailImage;
        result.version = schemaMapDto.version;
        return result;
    }
}