package com.example.koivucorp.payback_20;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import java.net.URL;
import java.util.HashMap;

public class GetPicsActivity extends AsyncTask<String, Void, Bitmap> {

    private Context context;
    private RelativeLayout r1;
    private HashMap<String, Friend> friends;
    private String id;

    public GetPicsActivity(Context context, String id, HashMap<String, Friend> friends, RelativeLayout r1) {
        this.context = context;
        this.friends = friends;
        this.r1 = r1;
        this.id = id;
    }

    protected Bitmap doInBackground(String... arg0) {
        String urlname = arg0[0];
        Bitmap bmp = null;
        try {
            URL url = new URL(urlname);
            bmp = BitmapFactory.decodeStream(url.openConnection().getInputStream());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return bmp;
    }

    protected void onPostExecute(Bitmap result){
        Friend friend = friends.get(id);
        if (friend != null) friend.setPic(result);
    }

}
