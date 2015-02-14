package com.example.majo.persistenceLocalDatabase;

import com.example.majo.BusinessObjects.SchemaMap;

import java.util.List;

/**
 * Created by moravekm on 19-Dec-14.
 */
public interface ISchemaMapPersistence {

    public List<SchemaMap> getAllMaps();

    public void addMaps(List<SchemaMap> maps);

    public void addMap(SchemaMap map);

    public void deleteAllMaps();

    public void deleteMap(SchemaMap map);
}
