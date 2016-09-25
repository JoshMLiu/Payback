package com.example.koivucorp.payback_20;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

public class RegisterIdActivity  extends AsyncTask<String,Void,String> {

    private Context context;

    public RegisterIdActivity(Context context) {
        this.context = context;
    }

    protected void onPreExecute(){

    }
    @Override
    protected String doInBackground(String... arg0) {
         try{
                String id = (String)arg0[0];
                String name = (String)arg0[1];
                name = name.replace(' ', '*');
                String link="http://holdenkoivu.com/server.php?mode=newuser&name="+name+"&uid="+id;
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
            }catch(Exception e){
                return new String("Exception: " + e.getMessage());
            }
    }
    @Override
    protected void onPostExecute(String result){
        if (result.equals("NEW USER CREATED")) {
            Toast.makeText(context, "Account Successfully Created", Toast.LENGTH_LONG).show();
        }
    }
}

