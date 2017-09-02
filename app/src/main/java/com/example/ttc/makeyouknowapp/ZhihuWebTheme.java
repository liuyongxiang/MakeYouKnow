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
 * Created by ttc on 2017/3/15.
 */

public class ZhihuWebTheme {

    private static final String TAG = "ZhihuWebTheme";
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
    public List<ZhihuDailyNews.Question> fetchItems(String zhihuUri) {

        List<ZhihuDailyNews.Question> items1 = new ArrayList<>() ;

        try {
            String url = Uri.parse(zhihuUri)
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
    private List<ZhihuDailyNews.Question> parseItems(JSONObject jsonBody)
            throws IOException, JSONException {

        List<ZhihuDailyNews.Question> items = new ArrayList<>();
        String tempUri;
        JSONArray photoJsonArray = jsonBody.getJSONArray("stories");
        for (int i = 0; i < photoJsonArray.length(); i++) {
            ZhihuDailyNews.Question item = new ZhihuDailyNews().getQuestion();
            JSONObject photoJsonObject = photoJsonArray.getJSONObject(i);
            if (!photoJsonObject.has("images")) {
                continue;
            }
            tempUri = photoJsonObject.getString("images");
            item.setImages(new ZhihuImageUriFormat(tempUri).FormatChange());
            item.setType(photoJsonObject.getInt("type"));
            item.setId(photoJsonObject.getInt("id"));
            item.setTitle(photoJsonObject.getString("title"));
            Log.d("ZhihuWeb",item.getTitle());

            items.add(item);
        }

        return items;
    }


}

