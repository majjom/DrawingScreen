package contract;

import java.io.IOException;

public interface ISchemaMapStorage {
    SchemaMapDto getById(Long id) throws IOException;
    SchemaMapDto save(SchemaMapDto schemaMap);
    void remove(Long id);
}
