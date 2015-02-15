package com.example.majo.backendClient;

import com.example.majo.myapplication.backend.mappedPointEntityApi.MappedPointEntityApi;
import com.example.majo.myapplication.backend.mappedPointEntityApi.model.MappedPointDto;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;
import com.google.api.client.googleapis.services.AbstractGoogleClientRequest;
import com.google.api.client.googleapis.services.GoogleClientRequestInitializer;

import java.io.IOException;
import java.util.List;

/**
 * Created by majo on 14-Feb-15.
 */
public class MappedPointEntityClient {

    private static MappedPointEntityApi myApiService = null;

    public MappedPointEntityClient(boolean isLocal){
        if(myApiService == null) {  // Only do this once
            MappedPointEntityApi.Builder builder = new MappedPointEntityApi.Builder(AndroidHttp.newCompatibleTransport(),
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

    public List<MappedPointDto> getAllByMapId(Long mapId) {
        try {
            return this.myApiService.getAllByMapId(mapId).execute().getItems();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public MappedPointDto save(MappedPointDto mappedPointDto) {
        try {
            return this.myApiService.save(mappedPointDto).execute();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void deleteAllForMapId(Long mapId){
        try {
            this.myApiService.deleteAllForMapId(mapId).execute();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
