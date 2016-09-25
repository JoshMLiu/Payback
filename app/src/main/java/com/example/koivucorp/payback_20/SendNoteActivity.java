package com.example.koivucorp.payback_20;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

public class SendNoteActivity extends AsyncTask<String,Void,String> {

    private Loan loan;
    private Context context;

    public SendNoteActivity(Loan loan, Context context) {
        this.loan = loan;
        this.context = context;
    }

    protected void onPreExecute(){

    }
    @Override
    protected String doInBackground(String... arg0) {
        try{
            String toid = loan.getToid();
            String fromid = loan.getFromid();
            String comment = messageParse(loan.getComment());
            comment = comment.replace(' ', '*');
            String value = decimalConvert(loan.getAmmount());
            String time = loan.getTimestamp();
            String tname = loan.getToName();
            tname = tname.replace(' ', '*');
            String fname = loan.getFromName();
            fname = fname.replace(' ', '*');
            String sender = loan.getSender();
            String link="http://holdenkoivu.com/server.php?mode=makenote&toid="+toid+"&fromid="+fromid+"&comment="+comment+"&value="+value+"&time="+time+"&tstatus="+loan.getToAccepted()+"&fstatus="+loan.getFromAccepted()+"&tname="+tname+"&fname="+fname+"&sender="+sender;
            System.out.println(link);
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

    private String decimalConvert(String s) {
        if (!s.contains(".")) {
            return s + ".00";
        }
        else {
            if (s.charAt(s.length() - 1) == '.') {
                return s + "00";
            }
            else if (s.charAt(s.length() - 2) == '.') {
                return s + "0";
            }
        }
        return s;
    }

    private String messageParse(String s) {
        if (s.contains("#|#")) {
            return s.replaceAll("#\\|#", "");
        }
        else if (s.contains("~|~")) {
            return s.replaceAll("~\\|~", "");
        }
        return s;
    }
}

