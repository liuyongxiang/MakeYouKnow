package com.example.ttc.makeyouknowapp;

/**
 * Created by ttc on 2017/3/13.
 */

public class ZhihuImageUriFormat {
    private String mUri;
    public ZhihuImageUriFormat(String uri){
           this.mUri = uri;
    }
    public String FormatChange(){
        String tempUri;
        tempUri = mUri.replace("[","");
        tempUri = tempUri.replace("]","");
        tempUri = tempUri.replace("\"","");
        return tempUri;
    }

    public String CoverChange(){
        String temp;
        temp = mUri.replace("\\","");
        return temp;
    }
}
