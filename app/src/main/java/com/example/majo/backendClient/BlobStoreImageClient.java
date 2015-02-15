package com.example.majo.backendClient;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.example.majo.myapplication.backend.blobStoreImageApi.BlobStoreImageApi;
import com.example.majo.myapplication.backend.blobStoreImageApi.model.BlobStoreUrl;
import com.example.majo.myapplication.backend.blobStoreImageApi.model.BlobStoreUrlDto;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;
import com.google.api.client.googleapis.services.AbstractGoogleClientRequest;
import com.google.api.client.googleapis.services.GoogleClientRequestInitializer;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;

import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by majo on 15-Feb-15.
 */
public class BlobStoreImageClient {
    private static BlobStoreImageApi myApiService = null;

    private boolean isLocal;

    public BlobStoreImageClient(boolean isLocal){
        this.isLocal = isLocal;

        if(myApiService == null) {  // Only do this once
            BlobStoreImageApi.Builder builder = new BlobStoreImageApi.Builder(AndroidHttp.newCompatibleTransport(),
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

    public BackendImageDto sendImage(String blobStoreUrl, String imageFilePath){
        return sendImage(blobStoreUrl, imageFilePath, this.isLocal);
    }

    public Bitmap downloadImage(String imageServingUrl){
        return downloadImage(imageServingUrl, this.isLocal);
    }

    public String getUploadUrl() {
        try {
            BlobStoreUrlDto urlDto = this.myApiService.getUploadUrl().execute();
            return urlDto.getUrl();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public void deleteImageByKey(String imageBlobKey) {
        try {
            this.myApiService.deleteImageByKey(imageBlobKey).execute();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



    private BackendImageDto sendImage(String blobStoreUrl, String imageFilePath, boolean isLocal){
        BackendImageDto result = new BackendImageDto();
        result.isSuccess = false;
        if ((blobStoreUrl == null) || (blobStoreUrl.isEmpty())){
            return result;
        }
        if ((imageFilePath == null) || (imageFilePath.isEmpty())){
            return result;
        }

        // retype address if local
        if (isLocal) {
            blobStoreUrl = replaceLocal(blobStoreUrl);
        }

        // prepare HTTP request with file content
        HttpClient httpClient = new DefaultHttpClient();
        HttpPost httpPost = new HttpPost(blobStoreUrl);

        MultipartEntity entity = new MultipartEntity();
        entity.addPart("file", new FileBody(new File(imageFilePath)));
        httpPost.setEntity(entity);

        // send it
        HttpResponse httpResponse = null;
        try {
            httpResponse = httpClient.execute(httpPost);
        } catch (IOException e) {
            e.printStackTrace();
            result.isSuccess = false;
            return result;
        }
        int code = httpResponse.getStatusLine().getStatusCode();
        if (code != 200){
            result.isSuccess = false;
            return result;
        }

        // parse the response
        HttpEntity responseEntity = httpResponse.getEntity();
        InputStream in = null;
        try {
            in = responseEntity.getContent();
        } catch (IOException e) {
            e.printStackTrace();
            result.isSuccess = false;
            return result;
        }

        // read the response as string
        String responseString = "";
        while (true){
            int character = 0;
            try {
                character = in.read();
            } catch (IOException e) {
                e.printStackTrace();
                result.isSuccess = false;
                return result;
            }
            if (character == -1) break;
            responseString += (char)character;
        }

        // parse response - Key, URL
        try {
            JSONObject resultJson = new JSONObject(responseString);
            result.blobKey = resultJson.getString("blobKey");
            result.servingUrl = resultJson.getString("servingUrl");
            result.isSuccess = true;
        } catch (JSONException e) {
            e.printStackTrace();
            result.isSuccess = false;
            return result;
        }

        return result;
    }

    private Bitmap downloadImage(String imageServingUrl, boolean isLocal){
        // retype address if local
        if (isLocal) {
            imageServingUrl = replaceLocal(imageServingUrl);
        }

        try {
            URL url = new URL(imageServingUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap myBitmap = BitmapFactory.decodeStream(input);
            return myBitmap;
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    private String replaceLocal(String url){
        return url.replace("localhost:8080", "10.0.2.2:8080");
    }
}
