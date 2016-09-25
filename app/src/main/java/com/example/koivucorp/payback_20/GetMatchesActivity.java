package com.example.koivucorp.payback_20;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import java.net.URL;

public class GetMatchesActivity extends AsyncTask<String, Void, String> {

    private Context context;

    public  GetMatchesActivity(Context context) {
        this.context = context;
    }

    protected String doInBackground(String... arg0) {
        String urlname = arg0[0];
        try {
            URL url = new URL(urlname);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }

    protected void onPostExecute(String result){

    }

}
