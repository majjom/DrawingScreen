package com.example.majo.drawingscreen.backendClient;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.test.ActivityInstrumentationTestCase2;
import android.test.InstrumentationTestCase;
import android.test.suitebuilder.annotation.SmallTest;

import com.example.majo.backendClient.BackendImageDto;
import com.example.majo.backendClient.BlobStoreImageClient;
import com.example.majo.drawingscreen.DrawingPointsActivity;
import com.example.majo.maps.MapManager;

import java.io.IOException;

/**
 * Created by majo on 15-Feb-15.
 */
public class BackendImageClientTest extends InstrumentationTestCase {


    public void testBlobStoreImageClient(){
        // arrange
        BlobStoreImageClient blobStoreImageClient = new BlobStoreImageClient(true);

        // act - UPLOAD
        String url = blobStoreImageClient.getUploadUrl();
        BackendImageDto result = blobStoreImageClient.sendImage(url, "/data/data/com.example.majo.drawingscreen/files/map_34_small.png");

        // assert
        assertEquals(true, result.isSuccess);

        // act 2 - DOWNLOAD
        Bitmap badBitmap = blobStoreImageClient.downloadImage("eee");
        Bitmap bitmap = blobStoreImageClient.downloadImage(result.servingUrl);

        // assert 2
        assertNull(badBitmap);
        assertNotNull(bitmap);

        // act 3 - DELETE
        blobStoreImageClient.deleteImageByKey(result.blobKey);
        Bitmap bitmap2 = blobStoreImageClient.downloadImage(result.servingUrl);

        // assert 3
        assertNull(bitmap2);
    }
}
