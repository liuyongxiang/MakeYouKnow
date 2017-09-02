package com.example.ttc.makeyouknowapp;

import android.net.Uri;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by ttc on 2017/3/13.
 */

public class GetZhihuNewsContent {

    private static final String TAG = "GetNewsContent";
    public byte[] getUrlBytes(String urlSpec) throws IOException {
        URL url = new URL(urlSpec);
        HttpURLConnection connection = (HttpURLConnection)url.openConnection();
        try {
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            InputStream in = connection.getInputStream();
            if (connection.getResponseCode() != HttpURLConnection.HTTP_OK) {
                throw new IOException(connection.getResponseMessage() +
                        ": with " +
                        urlSpec);
            }
            int bytesRead = 0;
            byte[] buffer = new byte[1024];
            while ((bytesRead = in.read(buffer)) > 0) {
                out.write(buffer, 0, bytesRead);
            }
            out.close();
            return out.toByteArray();
        } finally {
            connection.disconnect();
        }
    }
    public String getUrlString(String urlSpec) throws IOException {
        return new String(getUrlBytes(urlSpec));
    }
    public ZhihuDailyNewsContent fetchContent(String contentUri) {

        ZhihuDailyNewsContent items1 = new ZhihuDailyNewsContent() ;

        try {
            String url = Uri.parse(contentUri)
                    .buildUpon()
                    .appendQueryParameter("format", "json")
                    .appendQueryParameter("nojsoncallback", "1")
                    .appendQueryParameter("extras", "url_s")
                    .build().toString();
            String jsonString = getUrlString(url);
            Log.i(TAG, "Received JSON: " + jsonString);
            JSONObject jsonBody = new JSONObject(jsonString);
            items1=parseItems(jsonBody);
        }catch (JSONException je){
            Log.e(TAG,"Failed to parse JSON",je);
        } catch (IOException ioe) {
            Log.e(TAG, "Failed to fetch items", ioe);
        }
        return items1;
    }
    private ZhihuDailyNewsContent parseItems(JSONObject jsonBody)
            throws IOException, JSONException {
        ZhihuDailyNewsContent item = new ZhihuDailyNewsContent();
        String tempUri;

        item.setBody(jsonBody.getString("body"));
        if(!jsonBody.has("image_source")){

        }else{
            item.setImageSource(jsonBody.getString("image_source"));
        }
        item.setTitle(jsonBody.getString("title"));
        if(!jsonBody.has("image")) {
        }else{
            item.setImage(jsonBody.getString("image"));
        }
        item.setShareUri(jsonBody.getString("share_url"));
        item.setGa_prefix(jsonBody.getString("ga_prefix"));

        if (!jsonBody.has("images")) {

        }else {
            tempUri = jsonBody.getString("images");
            item.setImages(new ZhihuImageUriFormat(tempUri).FormatChange());
        }
        item.setType(jsonBody.getInt("type"));
        item.setId(jsonBody.getInt("id"));

        return item;
    }
}
