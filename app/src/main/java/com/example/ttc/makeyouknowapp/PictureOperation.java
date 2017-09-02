package com.example.ttc.makeyouknowapp;

import android.graphics.Bitmap;
import android.util.Log;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Created by ttc on 2017/3/14.
 */

public class PictureOperation {
    public void saveMyBitmap(Bitmap mBitmap,String bitName){
        File f = new File( "/sdcard/Note/"+bitName );
        FileOutputStream fOut = null;
        try {
            fOut = new FileOutputStream(f);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        mBitmap.compress(Bitmap.CompressFormat.JPEG, 100, fOut);
        try {
            fOut.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            fOut.close();
            Log.d("insert picture success!","hahahah");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
