package com.example.majo.backendClient;


import com.example.majo.myapplication.backend.schemaMapEntityApi.SchemaMapEntityApi;
import com.example.majo.myapplication.backend.schemaMapEntityApi.model.SchemaMapDto;
import com.example.majo.myapplication.backend.schemaMapEntityApi.model.SchemaMapEntity;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;
import com.google.api.client.googleapis.services.AbstractGoogleClientRequest;
import com.google.api.client.googleapis.services.GoogleClientRequestInitializer;

import java.io.IOException;
import java.util.List;

/**
 * Created by majo on 03-Feb-15.
 */
public class SchemaMapEntityClient {

    private static SchemaMapEntityApi myApiService = null;

    public SchemaMapEntityClient(boolean isLocal){
        if(myApiService == null) {  // Only do this once
            SchemaMapEntityApi.Builder builder = new SchemaMapEntityApi.Builder(AndroidHttp.newCompatibleTransport(),
                    new AndroidJsonFactory(), null);

            if (isLocal){
                // options for running against local devappserver
                // - 10.0.2.2 is localhost's IP address in Android emulator
                // - turn off compression when running against local devappserver
                builder.setRootUrl("http://10.0.2.2:8080/_ah/api/")
                        .setGoogleClientRequestInitializer(new GoogleClientRequestInitializer() {
                            @Override
                            public void initialize(AbstractGoogleClientRequest<?> abstractGoogleClientRequest) throws IOException {
                                abstractGoogleClientRequest.setDisableGZipContent(true);
                            }
                        });
            }

            myApiService = builder.build();
        }
    }


    public List<SchemaMapDto> getByName(String name) {
        try {
            return this.myApiService.getByName(name).execute().getItems();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public SchemaMapDto getById(Long id) {
        try {
            return this.myApiService.getById(id).execute();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }


    public SchemaMapDto save(SchemaMapDto schemaMap) {
        try {
            return this.myApiService.save(schemaMap).execute();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }


    public void delete(Long id) {
        try {
            this.myApiService.delete(id).execute();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
