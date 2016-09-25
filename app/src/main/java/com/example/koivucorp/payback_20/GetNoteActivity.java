package com.example.koivucorp.payback_20;

import android.content.Context;
import android.os.AsyncTask;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;

public class GetNoteActivity extends AsyncTask<String,Void,String> {

    private ArrayList<Loan> myLoans;
    private ArrayList<Loan> myDebts;
    private HashMap<String, Friend> friends;
    private String id;

    public GetNoteActivity(ArrayList<Loan> loans, ArrayList<Loan> debts, String id, HashMap<String, Friend> friends) {
        this.myLoans = loans;
        this.myDebts = debts;
        this.friends = friends;
        this.id = id;
    }

    protected void onPreExecute(){

    }
    @Override
    protected String doInBackground(String... arg0) {
        try{
            String link = "http://holdenkoivu.com/server.php?mode=getnotes&id="+id;
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

        String allnotes = result;
        myLoans.clear();
        myDebts.clear();
        if (allnotes.equals("null")) return;

        String[] notearray = allnotes.split("#\\|#");
        for (String s: notearray) {
            try {
                String[] properties = s.split("~\\|~"); // 0:tname 1:toid 2:fname 3:fromid 4:comment 5:value 6:time 7:noteid 8:tstatus 9:fstatus 10:allstatus
                String tname = properties[0].replace('*', ' ');
                String fname = properties[2].replace('*', ' ');
                String comment = properties[4].replace('*', ' ');
                Loan loan = new Loan(tname, properties[1], fname, properties[3], comment, properties[5], properties[6], properties[7], properties[8]);
                if (properties[9].equals("true")) loan.setToAccepted(true);
                if (properties[10].equals("true")) loan.setFromAccepted(true);
                if (properties[11].equals("1")) loan.setAllAccepted(true);

                if (loan.getFromid().equals(id)) myLoans.add(loan);
                else myDebts.add(loan);
            }
            catch (Exception e) {}
        }
    }
}


