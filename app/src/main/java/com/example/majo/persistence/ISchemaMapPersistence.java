package com.example.majo.persistence;

import com.example.majo.BusinessObjects.MappedPoint;
import com.example.majo.BusinessObjects.SchemaMap;

import java.util.ArrayList;

/**
 * Created by moravekm on 19-Dec-14.
 */
public interface ISchemaMapPersistence {

    public ArrayList<SchemaMap> getAllMaps();

    public void addMaps(ArrayList<SchemaMap> maps);

    public void deleteAllMaps();

    public void deleteMap(SchemaMap map);
}
