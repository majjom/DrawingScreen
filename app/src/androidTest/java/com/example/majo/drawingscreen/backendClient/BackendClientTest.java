package com.example.majo.drawingscreen.backendClient;

import android.test.InstrumentationTestCase;

import com.example.majo.backendClient.BlobStoreImageClient;
import com.example.majo.backendClient.MappedPointEntityClient;
import com.example.majo.backendClient.SchemaMapEntityClient;
import com.example.majo.maps.MapManager;
import com.example.majo.myapplication.backend.mappedPointEntityApi.model.MappedPointDto;
import com.example.majo.myapplication.backend.schemaMapEntityApi.model.SchemaMapDto;
import com.google.api.client.util.DateTime;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by majo on 03-Feb-15.
 */
public class BackendClientTest extends InstrumentationTestCase {

    public void testSchemaMapClient(){
        /////////////////////////////////////////
        /////////////// CREATE //////////////////
        /////////////////////////////////////////
        // arrange
        SchemaMapEntityClient client = new SchemaMapEntityClient(true);

        SchemaMapDto schemaMapschemaMapDto = new SchemaMapDto();
        schemaMapschemaMapDto.setName("myName");
        schemaMapschemaMapDto.setDateCreated(new DateTime(new Date()));
        schemaMapschemaMapDto.setThumbnailImage("aaaa");

        // act
        SchemaMapDto savedSchemaMapDto = client.save(schemaMapschemaMapDto);
        SchemaMapDto savedSchemaMapDto2 = client.save(schemaMapschemaMapDto);

        // assert
        assertNotNull(savedSchemaMapDto.getId());
        assertNotNull(savedSchemaMapDto2.getId());
        assertNotSame(savedSchemaMapDto.getId(), savedSchemaMapDto2.getId());

        /////////////////////////////////////////
        /////////////// UPDATE //////////////////
        /////////////////////////////////////////
        // arrange
        SchemaMapDto retrievedDto = client.getById(savedSchemaMapDto.getId());
        retrievedDto.setName("myName2");

        // act
        SchemaMapDto savedSchemaMapEntityAgain = client.save(retrievedDto);

        // assert
        assertNotNull(savedSchemaMapEntityAgain.getId());
        assertEquals("myName2", savedSchemaMapEntityAgain.getName());
        assertEquals(savedSchemaMapDto.getId(), savedSchemaMapEntityAgain.getId());

        /////////////////////////////////////////
        /////////////// DELETE //////////////////
        /////////////////////////////////////////
        // act
        retrievedDto = client.getById(savedSchemaMapDto.getId());
        client.delete(savedSchemaMapDto.getId());

        // assert
        assertNull(client.getById(savedSchemaMapDto.getId()));
    }

    public void testSchemaMapClient_getByNamePrefix(){
        SchemaMapEntityClient client = new SchemaMapEntityClient(true);

        // pre -arrange
        String name = "myName";
        List<SchemaMapDto> leftovers = client.getByNamePrefix(name);
        for (SchemaMapDto leftover : leftovers){
            client.delete(leftover.getId());
        }

        // arrange
        String name01 = name + "01";
        String name02 = name + "02";

        SchemaMapDto schemaMapDto = new SchemaMapDto();
        schemaMapDto.setName(name01);
        schemaMapDto.setDateCreated(new DateTime(new Date()));
        schemaMapDto.setThumbnailImage("aaaa");
        SchemaMapDto savedSchemaMapDto = client.save(schemaMapDto);

        schemaMapDto.setName(name02);
        SchemaMapDto savedSchemaMapDto2 = client.save(schemaMapDto);

        // act
        List<SchemaMapDto> result = client.getByNamePrefix(name);
        List<SchemaMapDto> result01 = client.getByNamePrefix(name01);
        List<SchemaMapDto> result02 = client.getByNamePrefix(name02);

        // assert
        assertEquals(1, result01.size());
        assertEquals(1, result02.size());
        assertEquals(2, result.size());
    }

    public void testMappedPointClient(){
        /////////////////////////////////////////
        /////////////// CREATE //////////////////
        /////////////////////////////////////////
        // arrange
        SchemaMapEntityClient schemaMapClient = new SchemaMapEntityClient(true);
        MappedPointEntityClient mappedPointClient = new MappedPointEntityClient(true);

        SchemaMapDto schemaMapDto = new SchemaMapDto();
        schemaMapDto.setName("myName");
        schemaMapDto.setDateCreated(new DateTime(new Date()));
        schemaMapDto.setThumbnailImage("aaaa");
        SchemaMapDto savedSchemaMapDto = schemaMapClient.save(schemaMapDto);

        MappedPointDto mappedPointDto = new MappedPointDto();
        mappedPointDto.setX(10f);
        mappedPointDto.setY(20f);
        mappedPointDto.setLatitude(30f);
        mappedPointDto.setLongitude(40f);
        mappedPointDto.setAltitude(50f);
        mappedPointDto.setRadius(60f);
        mappedPointDto.setSchemaMapId(savedSchemaMapDto.getId());

        // act
        MappedPointDto savedMappedPointDto = mappedPointClient.save(mappedPointDto);
        MappedPointDto savedMappedPointDto2 = mappedPointClient.save(mappedPointDto);

        // assert
        assertNotNull(savedMappedPointDto.getId());
        assertNotNull(savedMappedPointDto2.getId());
        assertNotSame(savedMappedPointDto.getId(), savedMappedPointDto2.getId());

        /////////////////////////////////////////
        /////////////// UPDATE //////////////////
        /////////////////////////////////////////
        // arrange
        List<MappedPointDto> retrievedMappedPoints = mappedPointClient.getAllByMapId(savedSchemaMapDto.getId());
        MappedPointDto retrievedMappedPoint =  retrievedMappedPoints.get(0);
        retrievedMappedPoint.setX(1500f);

        // act
        MappedPointDto savedMappedPointEntityAgain = mappedPointClient.save(retrievedMappedPoint);

        // assert
        assertEquals(2, retrievedMappedPoints.size());
        assertNotNull(savedMappedPointEntityAgain.getId());
        assertEquals(1500f, savedMappedPointEntityAgain.getX());
        assertEquals(retrievedMappedPoint.getId(), savedMappedPointEntityAgain.getId());

        /////////////////////////////////////////
        /////////////// DELETE //////////////////
        /////////////////////////////////////////
        // act
        mappedPointClient.deleteAllForMapId(savedSchemaMapDto.getId());

        // assert
        assertEquals(0, mappedPointClient.getAllByMapId(savedSchemaMapDto.getId()).size());
    }
}
