package com.example.koivucorp.payback_20;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

public class ConfirmLoanActivity extends AsyncTask<String, Void, String> {

    private Context context;

    public ConfirmLoanActivity(Context context) {
        this.context = context;
    }

    protected void onPreExecute(){
    }
    @Override
    protected String doInBackground(String... arg0) {
        try{
            String noteid = (String) arg0[0];
            String link = "http://holdenkoivu.com/server.php?mode=confirmloan&noteid="+noteid;
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
        Toast.makeText(context, result, Toast.LENGTH_LONG).show();
    }
}
