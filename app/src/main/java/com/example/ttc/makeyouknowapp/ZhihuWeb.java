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
 * Created by ttc on 2017/3/12.
 */

public class ZhihuWeb {

    private static final String TAG = "ZhihuWeb";
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
    public ZhihuDailyNews fetchItems(String zhihuUri) {

        ZhihuDailyNews items1 = new ZhihuDailyNews() ;

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
    private ZhihuDailyNews parseItems(JSONObject jsonBody)
            throws IOException, JSONException {
        ZhihuDailyNews items = new ZhihuDailyNews();
        List<ZhihuDailyNews.Question> tempItems = new ArrayList<>();
        String tempUri;
        String date = jsonBody.getString("date");
        items.setDate(date);
        JSONArray photoJsonArray = jsonBody.getJSONArray("stories");
        for (int i = 0; i < photoJsonArray.length(); i++) {
            JSONObject photoJsonObject = photoJsonArray.getJSONObject(i);
            ZhihuDailyNews.Question item = (new ZhihuDailyNews()).new Question();
            if (!photoJsonObject.has("images")) {
                continue;
            }
            tempUri = photoJsonObject.getString("images");
            item.setImages(new ZhihuImageUriFormat(tempUri).FormatChange());
            item.setType(photoJsonObject.getInt("type"));
            item.setId(photoJsonObject.getInt("id"));
            item.setGa_prefix(photoJsonObject.getString("ga_prefix"));
            item.setTitle(photoJsonObject.getString("title"));
            Log.d("ZhihuWeb",item.getTitle());
            tempItems.add(item);

        }

        items.setStories((ArrayList<ZhihuDailyNews.Question>) tempItems);
        return items;
    }


}
