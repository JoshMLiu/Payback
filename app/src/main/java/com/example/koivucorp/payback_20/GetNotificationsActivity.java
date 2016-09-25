package com.example.koivucorp.payback_20;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

public class GetNotificationsActivity extends AsyncTask<String, Void, String> {

    private Context context;

    public  GetNotificationsActivity(Context context) {
        this.context = context;
    }

    protected String doInBackground(String... arg0) {
        String id = arg0[0];
        try {
            String link="http://holdenkoivu.com/server.php?mode=getnotifications&id="+id;
            URL url = new URL(link);
            URLConnection conn = url.openConnection();
            BufferedReader reader = new BufferedReader
                    (new InputStreamReader(conn.getInputStream()));
            StringBuilder sb = new StringBuilder();
            String line = null;
            while((line = reader.readLine()) != null)
            {
                sb.append(line);
                break;
            }
            return sb.toString();
        } catch (Exception e) {
            return "error:" + e.toString();
        }

    }

    protected void onPostExecute(String result){
        if(result.equals("NONE")) {
            Toast.makeText(context, "No new notifications", Toast.LENGTH_LONG).show();
        }
        else {
            String[] messages = result.split("~\\|~");
            for (String message: messages) {
                Toast.makeText(context, message.replace('*', ' '), Toast.LENGTH_LONG).show();
            }
        }
    }

}