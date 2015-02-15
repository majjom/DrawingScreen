package com.example.majo.myapplication.servlet;

import com.google.appengine.api.blobstore.BlobKey;
import com.google.appengine.api.blobstore.BlobstoreService;
import com.google.appengine.api.blobstore.BlobstoreServiceFactory;
import com.google.appengine.api.images.ImagesService;
import com.google.appengine.api.images.ImagesServiceFactory;
import com.google.appengine.api.images.ServingUrlOptions;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Created by majo on 15-Feb-15.
 */
public class BlobStoreUploadServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        try {
            BlobstoreService blobstoreService = BlobstoreServiceFactory.getBlobstoreService();
            List<BlobKey> blobs = blobstoreService.getUploads(req).get("file");
            BlobKey blobKey = blobs.get(0);

            ImagesService imagesService = ImagesServiceFactory.getImagesService();
            ServingUrlOptions servingOptions = ServingUrlOptions.Builder.withBlobKey(blobKey);

            String servingUrl = imagesService.getServingUrl(servingOptions);

            resp.setStatus(HttpServletResponse.SC_OK);
            resp.setContentType("application/json");

            JSONObject json = new JSONObject();

            json.put("servingUrl", servingUrl);
            json.put("blobKey", blobKey.getKeyString());

            PrintWriter out = resp.getWriter();
            out.print(json.toString());
            out.flush();
            out.close();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
