package com.example.majo.myapplication.backend;

import com.example.majo.myapplication.entities.MappedPointEntity;
import com.example.majo.myapplication.entities.SchemaMapEntity;
import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiMethod;
import com.google.api.server.spi.config.ApiNamespace;
import com.google.appengine.api.datastore.GeoPt;
import com.googlecode.objectify.Key;
import com.googlecode.objectify.ObjectifyService;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

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

    static {
        // Typically you would register this inside an OfyServive wrapper. See: https://code.google.com/p/objectify-appengine/wiki/BestPractices
        ObjectifyService.register(MappedPointEntity.class);
    }

    /**
     * Returns list of {@link MappedPointEntity} for given for given {@link com.example.majo.myapplication.entities.SchemaMapEntity} identified by Id .
     *
     * @param mapId the ID of the {@link com.example.majo.myapplication.entities.SchemaMapEntity} which is parent of all {@link MappedPointEntity} which will be retrieved
     * @return the list of {@link MappedPointEntity} that are children of map id.
     */
    @ApiMethod(
            name = "getAllByMapId",
            path = "mappedPointEntity/{mapId}",
            httpMethod = ApiMethod.HttpMethod.GET)
    public List<MappedPointDto> getAllByMapId(@Named("mapId") Long mapId) {
        logger.info("Getting list of MappedPointEntity with mapID: " + mapId);
        List<MappedPointDto> result = new ArrayList<>();

        for(MappedPointEntity mappedPointEntity : getAllEntitiesByMapId(mapId)){
            result.add(convertToDto(mappedPointEntity));
        }
        return result;
    }

    /**
     * Saves a {@code MappedPointEntity}. Assuming the parent {@link com.example.majo.myapplication.entities.SchemaMapEntity} is part of the specified entity.
     */
    @ApiMethod(
            name = "save",
            path = "mappedPointEntity",
            httpMethod = ApiMethod.HttpMethod.POST)
    public MappedPointDto save(MappedPointDto mappedPointDto) {
        MappedPointEntity mappedPointEntity = convertToEntity(mappedPointDto);
        ofy().save().entity(mappedPointEntity).now(); // if ID not present than new entity is created and ID is populated
        MappedPointDto result = convertToDto(mappedPointEntity);
        return result;
    }

    /**
     * Deletes all children of {@link MappedPointEntity} for given {@link com.example.majo.myapplication.entities.SchemaMapEntity} identified by Id.
     *
     * @param mapId the ID of the {@link com.example.majo.myapplication.entities.SchemaMapEntity}
     */
    @ApiMethod(
            name = "deleteAllForMapId",
            path = "mappedPointEntity/{mapId}",
            httpMethod = ApiMethod.HttpMethod.DELETE)
    public void deleteAllForMapId(@Named("mapId") Long mapId) {
        logger.info("Deleted all MappedPointEntity for mapID: " + mapId);
        ofy().delete().entities(getAllEntitiesByMapId(mapId)).now();
    }


    private List<MappedPointEntity> getAllEntitiesByMapId(Long mapId){
        SchemaMapEntity schemaMapEntity = ofy().load().type(SchemaMapEntity.class).id(mapId).now();
        return ofy().load().type(MappedPointEntity.class).filter(MappedPointEntity.SCHEMA_MAP_ENTITY_KEY_NAME, schemaMapEntity).list();
    }

    MappedPointDto convertToDto(MappedPointEntity mappedPointEntity){
        MappedPointDto result = new MappedPointDto();
        result.x = mappedPointEntity.x;
        result.y = mappedPointEntity.y;
        result.latitude = mappedPointEntity.geoPoint.getLatitude();
        result.longitude = mappedPointEntity.geoPoint.getLongitude();
        result.altitude = mappedPointEntity.altitude;
        result.radius = mappedPointEntity.radius;
        result.id = mappedPointEntity.id;
        result.schemaMapId = mappedPointEntity.schemaMapEntityKey.getId();
        return result;
    }

    MappedPointEntity convertToEntity(MappedPointDto mappedPointDto){
        MappedPointEntity result = new MappedPointEntity();
        result.x = mappedPointDto.x;
        result.y = mappedPointDto.y;
        result.geoPoint = new GeoPt(mappedPointDto.latitude, mappedPointDto.longitude);
        result.altitude = mappedPointDto.altitude;
        result.radius = mappedPointDto.radius;
        result.id = mappedPointDto.id;
        result.schemaMapEntityKey = Key.create(SchemaMapEntity.class, mappedPointDto.schemaMapId);
        return result;
    }

}