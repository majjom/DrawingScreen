package com.example.majo.drawingscreen.backendClient;

import android.test.InstrumentationTestCase;

import com.example.majo.backendClient.SchemaMapStorageClient;
import com.example.majo.myapplication.backend.schemaMapStorageApi.model.SchemaMapDto;
import com.google.android.gms.maps.model.LatLng;

import java.util.List;

/**
 * Created by majo on 03-Feb-15.
 */
public class SchemaMapStorageClientTest extends InstrumentationTestCase {
    public void testCrud(){
        // arrange
        SchemaMapStorageClient client = new SchemaMapStorageClient(true);

        SchemaMapDto schemaMap = new SchemaMapDto();
        schemaMap.setName("name");

        // act
        SchemaMapDto savedSchemaMap = client.save(schemaMap);

        // assert
        assertNotNull(savedSchemaMap.getId());

        List<SchemaMapDto> res =  client.getByName("ee");

        assertNotNull(res);
        assertEquals(1, res.size());
    }
}
