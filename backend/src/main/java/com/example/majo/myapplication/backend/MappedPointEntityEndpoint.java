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

import static com.googlecode.objectify.ObjectifyService.ofy;

/**
 * WARNING: This generated code is intended as a sample or starting point for using a
 * Google Cloud Endpoints RESTful API with an Objectify entity. It provides no data access
 * restrictions and no data validation.
 * <p/>
 * DO NOT deploy this code unchanged as part of a real application to real users.
 */
@Api(
        name = "mappedPointEntityApi",
        version = "v1",
        resource = "mappedPointEntity",
        namespace = @ApiNamespace(
                ownerDomain = "backend.myapplication.majo.example.com",
                ownerName = "backend.myapplication.majo.example.com",
                packagePath = ""
        )
)
public class MappedPointEntityEndpoint {

    private static final Logger logger = Logger.getLogger(MappedPointEntityEndpoint.class.getName());

    private static final int DEFAULT_LIST_LIMIT = 20;

    static {
        // Typically you would register this inside an OfyServive wrapper. See: https://code.google.com/p/objectify-appengine/wiki/BestPractices
        ObjectifyService.register(MappedPointEntity.class);
    }

    /**
     * Returns the {@link MappedPointEntity} with the corresponding ID.
     *
     * @param id the ID of the entity to be retrieved
     * @return the entity with the corresponding ID
     * @throws NotFoundException if there is no {@code MappedPointEntity} with the provided ID.
     */
    @ApiMethod(
            name = "get",
            path = "mappedPointEntity/{id}",
            httpMethod = ApiMethod.HttpMethod.GET)
    public MappedPointEntity get(@Named("id") Long id) throws NotFoundException {
        logger.info("Getting MappedPointEntity with ID: " + id);
        MappedPointEntity mappedPointEntity = ofy().load().type(MappedPointEntity.class).id(id).now();
        if (mappedPointEntity == null) {
            throw new NotFoundException("Could not find MappedPointEntity with ID: " + id);
        }
        return mappedPointEntity;
    }

    /**
     * Inserts a new {@code MappedPointEntity}.
     */
    @ApiMethod(
            name = "insert",
            path = "mappedPointEntity",
            httpMethod = ApiMethod.HttpMethod.POST)
    public MappedPointEntity insert(MappedPointEntity mappedPointEntity) {
        // Typically in a RESTful API a POST does not have a known ID (assuming the ID is used in the resource path).
        // You should validate that mappedPointEntity.id has not been set. If the ID type is not supported by the
        // Objectify ID generator, e.g. long or String, then you should generate the unique ID yourself prior to saving.
        //
        // If your client provides the ID then you should probably use PUT instead.
        ofy().save().entity(mappedPointEntity).now();
        logger.info("Created MappedPointEntity with ID: " + mappedPointEntity.getId());

        return ofy().load().entity(mappedPointEntity).now();
    }

    /**
     * Updates an existing {@code MappedPointEntity}.
     *
     * @param id                the ID of the entity to be updated
     * @param mappedPointEntity the desired state of the entity
     * @return the updated version of the entity
     * @throws NotFoundException if the {@code id} does not correspond to an existing
     *                           {@code MappedPointEntity}
     */
    @ApiMethod(
            name = "update",
            path = "mappedPointEntity/{id}",
            httpMethod = ApiMethod.HttpMethod.PUT)
    public MappedPointEntity update(@Named("id") Long id, MappedPointEntity mappedPointEntity) throws NotFoundException {
        // TODO: You should validate your ID parameter against your resource's ID here.
        checkExists(id);
        ofy().save().entity(mappedPointEntity).now();
        logger.info("Updated MappedPointEntity: " + mappedPointEntity);
        return ofy().load().entity(mappedPointEntity).now();
    }

    /**
     * Deletes the specified {@code MappedPointEntity}.
     *
     * @param id the ID of the entity to delete
     * @throws NotFoundException if the {@code id} does not correspond to an existing
     *                           {@code MappedPointEntity}
     */
    @ApiMethod(
            name = "remove",
            path = "mappedPointEntity/{id}",
            httpMethod = ApiMethod.HttpMethod.DELETE)
    public void remove(@Named("id") Long id) throws NotFoundException {
        checkExists(id);
        ofy().delete().type(MappedPointEntity.class).id(id).now();
        logger.info("Deleted MappedPointEntity with ID: " + id);
    }

    /**
     * List all entities.
     *
     * @param cursor used for pagination to determine which page to return
     * @param limit  the maximum number of entries to return
     * @return a response that encapsulates the result list and the next page token/cursor
     */
    @ApiMethod(
            name = "list",
            path = "mappedPointEntity",
            httpMethod = ApiMethod.HttpMethod.GET)
    public CollectionResponse<MappedPointEntity> list(@Nullable @Named("cursor") String cursor, @Nullable @Named("limit") Integer limit) {
        limit = limit == null ? DEFAULT_LIST_LIMIT : limit;
        Query<MappedPointEntity> query = ofy().load().type(MappedPointEntity.class).limit(limit);
        if (cursor != null) {
            query = query.startAt(Cursor.fromWebSafeString(cursor));
        }
        QueryResultIterator<MappedPointEntity> queryIterator = query.iterator();
        List<MappedPointEntity> mappedPointEntityList = new ArrayList<MappedPointEntity>(limit);
        while (queryIterator.hasNext()) {
            mappedPointEntityList.add(queryIterator.next());
        }
        return CollectionResponse.<MappedPointEntity>builder().setItems(mappedPointEntityList).setNextPageToken(queryIterator.getCursor().toWebSafeString()).build();
    }

    private void checkExists(Long id) throws NotFoundException {
        try {
            ofy().load().type(MappedPointEntity.class).id(id).safe();
        } catch (com.googlecode.objectify.NotFoundException e) {
            throw new NotFoundException("Could not find MappedPointEntity with ID: " + id);
        }
    }
}