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

/**
 * Created by JLiu on 2015-03-10.
 */
public class GetMyPicActivity extends AsyncTask<String, Void, Bitmap> {

    private Context context;
    private RelativeLayout r1;

    public GetMyPicActivity(Context context, RelativeLayout r1) {
        this.context = context;
        this.r1 = r1;
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
        ImageView imageView = new ImageView(context);
        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        lp.height = 60;
        lp.width = 60;
        lp.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        lp.leftMargin = 20;
        lp.bottomMargin = 10;
        imageView.setImageBitmap(result);
        r1.addView(imageView, lp);
    }

}
