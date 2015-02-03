package com.example.majo.myapplication.backend;

import contract.ISchemaMapStorage;
import contract.MappedPointDto;
import contract.SchemaMapDto;
import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiMethod;
import com.google.api.server.spi.config.ApiNamespace;
import com.google.api.server.spi.response.NotFoundException;
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
        name = "schemaMapStorageApi",
        version = "v1",
        resource = "schemaMapEntity",
        namespace = @ApiNamespace(
                ownerDomain = "backend.myapplication.majo.example.com",
                ownerName = "backend.myapplication.majo.example.com",
                packagePath = ""
        )
)
public class SchemaMapStorageEndpoint implements ISchemaMapStorage {

    private static final Logger logger = Logger.getLogger(SchemaMapStorageEndpoint.class.getName());

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
     * @throws NotFoundException if there is no {@code SchemaMapEntity} with the provided ID.
     */
    @ApiMethod(
            name = "getById",
            path = "schemaMapEntity/{id}",
            httpMethod = ApiMethod.HttpMethod.GET)
    public SchemaMapDto getById(@Named("id") Long id)  {
        logger.info("Getting SchemaMapEntity with ID: " + id);

        // load map
        SchemaMapEntity schemaMapEntity = ofy().load().type(SchemaMapEntity.class).id(id).now();

        // load coordinates
        List<MappedPointEntity> mappedPointEntities = ofy().load().type(MappedPointEntity.class).filter("schemaMapEntityKey", id).list();

        SchemaMapDto result = assembleSchemaMap(schemaMapEntity, mappedPointEntities);

        return result;
    }

    @ApiMethod(
            name = "getByName"
    )
    public List<SchemaMapDto> getByName(@Named("name") String name)  {
        if (name == null || name.equals("ee")){
            logger.info("Getting SchemaMapEntity with name: NULL or empty");
            return assembleSchemaMaps(ofy().load().type(SchemaMapEntity.class).list());
        }
        logger.info("Getting SchemaMapEntity with name: " + name);
        return assembleSchemaMaps(ofy().load().type(SchemaMapEntity.class).filter("name", name).list());
    }

    /**
     * Inserts a new {@code SchemaMapEntity}.
     */
    @ApiMethod(
            name = "save",
            path = "schemaMapEntity",
            httpMethod = ApiMethod.HttpMethod.POST)
    public SchemaMapDto save(SchemaMapDto schemaMap) {
        if (schemaMap.id == null){
            // store Map and get key
            SchemaMapEntity schemaMapEntity = getSchemaMapEntity(schemaMap);
            ofy().save().entity(schemaMapEntity).now(); // this is sync save, will populate the ID (no need to load from DB)
            schemaMap.id = schemaMapEntity.getId();

            //store coordinates
            List<MappedPointEntity> mappedPointEntityList = getMappedPointEntityList(schemaMap);
            ofy().save().entities(mappedPointEntityList).now();

            // return DTO with generated ID
            logger.info("Inserting SchemaMapEntity the new ID: " + schemaMap.id);
            return schemaMap;
        } else {
            // store Map, The kye is specified. If entity with this key exists in DB, than it is updated else inserted (with given key).
            SchemaMapEntity schemaMapEntity = getSchemaMapEntity(schemaMap);
            ofy().save().entity(schemaMapEntity).now();

            //store coordinates
            List<MappedPointEntity> mappedPointEntityList = getMappedPointEntityList(schemaMap);
            ofy().save().entities(mappedPointEntityList).now();

            // return DTO with unchanged ID
            logger.info("Updating SchemaMapEntity the new ID: " + schemaMap.id);
            return schemaMap;
        }
    }

    @ApiMethod(
            name = "remove",
            path = "schemaMapEntity/{id}",
            httpMethod = ApiMethod.HttpMethod.DELETE)
    public void remove(@Named("id") Long id) {
        ofy().delete().type(SchemaMapEntity.class).id(id).now();
        logger.info("Deleted SchemaMapEntity with ID: " + id);
    }





    private SchemaMapDto assembleSchemaMap(SchemaMapEntity schemaMapEntity, List<MappedPointEntity> mappedPointEntities){
        if (schemaMapEntity == null) return null;
        SchemaMapDto result = new SchemaMapDto();
        result.id = schemaMapEntity.getId();
        result.name = schemaMapEntity.getName();
        result.dateCreated = schemaMapEntity.getDateCreated();
        result.thumbnailImage = schemaMapEntity.getThumbnailImage();
        result.mappedPoints = new ArrayList<>();

        if (mappedPointEntities != null) {
            for (MappedPointEntity mappedPointEntity : mappedPointEntities) {
                MappedPointDto mappedPointDto = new MappedPointDto();

                mappedPointDto.id = mappedPointEntity.getId();

                mappedPointDto.x = mappedPointEntity.getX();
                mappedPointDto.y = mappedPointEntity.getY();
                mappedPointDto.radius = mappedPointEntity.getRadius();

                mappedPointDto.Latitude = mappedPointEntity.getLatitude();
                mappedPointDto.Longitude = mappedPointEntity.getLongitude();
                mappedPointDto.Altitude = mappedPointEntity.getAltitude();

                result.mappedPoints.add(mappedPointDto);
            }
        }

        return result;
    }

    private List<SchemaMapDto> assembleSchemaMaps(List<SchemaMapEntity> schemaMapEntities){
        if (schemaMapEntities == null) return null;

        List<SchemaMapDto> result = new ArrayList<>();
        for(SchemaMapEntity schemaMapEntity : schemaMapEntities){
            result.add(assembleSchemaMap(schemaMapEntity, null));
        }
        return result;
    }

    private SchemaMapEntity getSchemaMapEntity(SchemaMapDto schemaMapDto){
        return new SchemaMapEntity(schemaMapDto.id, schemaMapDto.name, schemaMapDto.dateCreated, schemaMapDto.thumbnailImage);
    }

    private List<MappedPointEntity> getMappedPointEntityList(SchemaMapDto schemaMapDto){
        List<MappedPointEntity> result = new ArrayList<>();
        if (schemaMapDto.mappedPoints != null) {
            for (MappedPointDto mappedPoint : schemaMapDto.mappedPoints) {
                result.add(new MappedPointEntity(schemaMapDto.id, mappedPoint.x, mappedPoint.y, mappedPoint.radius, mappedPoint.Latitude, mappedPoint.Longitude, mappedPoint.Altitude));
            }
        }
        return result;
    }
}