package com.example.koivucorp.payback_20;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.provider.SyncStateContract;
import android.text.InputFilter;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.FilterQueryProvider;
import android.widget.ImageButton;
import android.widget.RadioButton;
import android.support.v4.widget.SimpleCursorAdapter;
import android.widget.Toast;


import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class NewLoanActivity extends Activity {

    private ImageButton lendbutton;
    private ImageButton loansbutton;
    private EditText loanammount;
    private EditText note;
    private boolean loanVerified;
    private String id, username;
    private HashMap<String, String> friends;
    private RadioButton askcheck, sendcheck;
    private boolean lending;

    private AutoCompleteTextView name;
    private HashMap<String, String> listItems;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_new_loan);
        setNewLoanPage();
        loanVerified = false;
        Bundle bundle = getIntent().getExtras();
        id = bundle.getString("id");
        username = bundle.getString("name");
        friends = (HashMap<String, String>) bundle.getSerializable("friends");
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private void setNewLoanPage() {

        loansbutton = (ImageButton) findViewById(R.id.topbar_add);
        loansbutton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //Loans page
                        finish();
                    }
                }

        );

        listItems = new HashMap<String, String>();
        name = (AutoCompleteTextView) findViewById(R.id.name);
        String[] from = { "name" };
        int[] to = {android.R.id.text1};
        SimpleCursorAdapter a = new SimpleCursorAdapter(this,
                android.R.layout.simple_dropdown_item_1line, null, from, to, 0);
        a.setStringConversionColumn(1);
        FilterQueryProvider provider = new FilterQueryProvider() {
            @Override
            public Cursor runQuery(CharSequence constraint) {
                if (constraint == null) {
                    return null;
                }
                String[] columnNames = {SyncStateContract.Columns._ID, "name" };
                MatrixCursor c = new MatrixCursor((columnNames));
                try {
                    String result;
                    String letters = messageParse(constraint.toString());
                    listItems.clear();
                    int index = 0;

                    for (Map.Entry<String, String> entry: friends.entrySet()) {
                        if (entry.getValue().toUpperCase().startsWith(letters.toUpperCase())) {
                            c.newRow().add(index).add(entry.getValue());
                            index++;
                        }
                    }

                    String link="http://holdenkoivu.com/server.php?mode=getmatches&letters="+letters;
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
                    result = sb.toString();

                    if (result != null) {
                        String[] names = result.split("#\\|#");
                        for (String s: names) {
                            String[] parts = s.split("~\\|~");
                            String id = parts[0];
                            String name = parts[1].replaceAll("\\*", " ");

                            if (friends.containsValue(name)) continue;
                            listItems.put(id, name);
                            c.newRow().add(index).add(name);
                            index++;
                            if (index > 12) break;
                        }
                    }
                }
                catch (Exception e) {
                    e.printStackTrace();
                }
                return c;
            }
        };
        a.setFilterQueryProvider(provider);
        name.setAdapter(a);

        lendbutton = (ImageButton) findViewById(R.id.lend);
        loanammount = (EditText) findViewById(R.id.loan_amount);
        note = (EditText) findViewById(R.id.note);
        loanammount.setFilters(new InputFilter[] {new DecimalDigitsInputFilter(10,2)});

        lendbutton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        makeNote();
                    }
                }

        );

        askcheck = (RadioButton) findViewById(R.id.ask_loan);
        sendcheck = (RadioButton) findViewById(R.id.send_loan);
        lending = sendcheck.isChecked();

        askcheck.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (lending) {
                            lending = false;
                            sendcheck.setChecked(false);
                            askcheck.setChecked(true);
                        }
                    }
                }
        );

        sendcheck.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (!lending) {
                            askcheck.setChecked(false);
                            sendcheck.setChecked(true);
                            lending = true;
                        }
                    }
                }
        );

    }

    public void onResume() {
        super.onResume();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void makeNote() {
        String recipient, ammount, message;
        loanVerified = false;
        boolean match = false;
        boolean local = false;
        recipient = name.getText().toString();
        ammount = loanammount.getText().toString();
        message = note.getText().toString();

        for(Map.Entry entry: friends.entrySet()) {
            //TODO same name handling
            if (entry.getValue().equals(recipient)) {
                local = true;
                match = true;
            }
        }

        if (!local) {
            for (Map.Entry entry: listItems.entrySet()) {
                if (entry.getValue().equals(recipient)) {
                    match = true;
                }
            }
        }

        if (!match) {
            makeToast("FRIEND NOT FOUND");
            return;
        }

        if (!ammount.isEmpty()) {
            final Handler handler = new Handler() {
                @Override
                public void handleMessage(Message mesg) {
                    throw new RuntimeException();
                }
            };
            new AlertDialog.Builder(this)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setTitle("New Loan")
                    .setMessage("Are you sure you want to make this loan?")
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener()
                    {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            loanVerified = true;
                            handler.sendMessage(handler.obtainMessage());
                        }

                     }).setNegativeButton("No", null).show();

            try { Looper.loop(); }
            catch(RuntimeException e2) {}

            if (loanVerified) {
                Date date = new Date();
                Loan loan;
                if (lending) {
                    String recipid = "";
                    if (local) {
                        for (Map.Entry entry: friends.entrySet()) {
                            if (entry.getValue().equals(recipient)) {
                                recipid = entry.getKey().toString();
                            }
                        }
                    }
                    else {
                        for (Map.Entry entry: listItems.entrySet()) {
                            if (entry.getValue().equals(recipient)) {
                                recipid = entry.getKey().toString();
                            }
                        }
                    }
                    if (recipid.isEmpty()) return;
                    loan = new Loan(recipient, recipid, username, id, message, ammount, Long.toString(date.getTime()), null, id);
                    loan.setFromAccepted(true);
                }
                else {
                    String recipid = "";
                    if (local) {
                        for (Map.Entry entry: friends.entrySet()) {
                            if (entry.getValue().equals(recipient)) {
                                recipid = entry.getKey().toString();
                            }
                        }
                    }
                    else {
                        for (Map.Entry entry: listItems.entrySet()) {
                            if (entry.getValue().equals(recipient)) {
                                recipid = entry.getKey().toString();
                            }
                        }
                    }
                    if (recipid.isEmpty()) return;
                    loan = new Loan(username, id, recipient, recipid, message, ammount, Long.toString(date.getTime()), null, id);
                    loan.setToAccepted(true);
                }
                String toast = "";
                toast += "New Loan Created between " + loan.getToName() + " and " + loan.getFromName() + " on " + loan.getTimestamp() + "\n";
                toast += "Ammount: " + loan.getAmmount() + "\n";
                toast += "Message: " + loan.getComment();
                makeToast(toast);
                loanVerified = false;
                new SendNoteActivity(loan, getApplicationContext()).execute();
                clearTexts();
                return;
            }
        }
        else {
            makeToast("FILL IN AN AMMOUNT");
        }
    }

    private void clearTexts() {
        name.setText("");
        loanammount.setText("");
        note.setText("");
    }

    private void makeToast(String s) {
        Toast.makeText(getApplicationContext(), s, Toast.LENGTH_LONG).show();
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
