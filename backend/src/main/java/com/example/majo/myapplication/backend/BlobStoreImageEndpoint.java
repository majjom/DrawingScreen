package com.example.majo.myapplication.backend;

import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiMethod;
import com.google.api.server.spi.config.ApiNamespace;
import com.google.appengine.api.blobstore.BlobInfo;
import com.google.appengine.api.blobstore.BlobInfoFactory;
import com.google.appengine.api.blobstore.BlobKey;
import com.google.appengine.api.blobstore.BlobstoreService;
import com.google.appengine.api.blobstore.BlobstoreServiceFactory;

import java.util.logging.Logger;

import javax.inject.Named;

/**
 * An endpoint class we are exposing
 */
@Api(
        name = "blobStoreImageApi",
        version = "v1",
        resource = "blobStoreImage",
        namespace = @ApiNamespace(
                ownerDomain = "backend.myapplication.majo.example.com",
                ownerName = "backend.myapplication.majo.example.com",
                packagePath = ""
        )
)
public class BlobStoreImageEndpoint {

    private static final Logger logger = Logger.getLogger(BlobStoreImageEndpoint.class.getName());

    @ApiMethod(name = "getUploadUrl")
    public BlobStoreUrlDto getUploadUrl(){
        BlobstoreService blobstoreService = BlobstoreServiceFactory.getBlobstoreService();
        String uploadUrl = blobstoreService.createUploadUrl("/blobstore/upload");
        BlobStoreUrlDto result = new BlobStoreUrlDto();
        result.url = uploadUrl;
        return result;
    }

    @ApiMethod(name = "deleteImageByKey")
    public void deleteImageByKey(@Named("imageBlobKey") String imageBlobKey){
        // TODO delete does not work. Finish it later
        BlobstoreService blobstoreService = BlobstoreServiceFactory.getBlobstoreService();
        BlobKey blobKey = new BlobKey(imageBlobKey);
        BlobInfoFactory blobInfoFactory = new BlobInfoFactory();
        BlobInfo blobInfo = blobInfoFactory.loadBlobInfo(blobKey);
        blobstoreService.delete(blobKey);
    }
}