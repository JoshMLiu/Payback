package com.example.koivucorp.payback_20;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ExpandableListView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.facebook.HttpMethod;
import com.facebook.Request;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.facebook.model.GraphObject;
import com.facebook.model.GraphUser;
import com.facebook.widget.LoginButton;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends Activity implements ExpandableListAdapter.OnButtonClickListener {

    private ViewFlipper viewFlipper;
    private HashMap<String, Friend> friends;
    private ArrayList<Loan> myLoans;
    private ArrayList<Loan> myDebts;
    private UiLifecycleHelper uiHelper;
    private String username, id;

    //Login page
    private LoginButton authButton;

    //Loans page
    private ImageButton newLoanButton;
    private Button displayLoans;
    private LoanListAdapter debtAdapter;
    private Button notificationButton;
    private ExpandableListView elist;
    private ImageButton clicked_else;
    private ImageButton clicked_add;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        // General stuff
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        viewFlipper = (ViewFlipper) findViewById(R.id.view_flipper);
        uiHelper = new UiLifecycleHelper(this, callback);
        uiHelper.onCreate(savedInstanceState);

        // Current user data
        username = "";
        id = "";
        friends = new HashMap<String, Friend>();
        myLoans = new ArrayList<>();
        myDebts = new ArrayList<>();

        // Views
        setLoginPage();
        setLoanPage();

        Session session = Session.getActiveSession();
        if (session.isOpened()) delayedRefresh(1500);

    }

    private void setLoginPage() {
        authButton = (LoginButton) this.findViewById(R.id.authButton);
        authButton.setReadPermissions(Arrays.asList("public_profile", "user_friends", "email", "user_birthday"));
    }


    private void setLoanPage() {

       /* displayLoans = (Button) this.findViewById(R.id.display_loan_button);
        displayLoans.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                refresh();
            }

        });
        */
        notificationButton = (Button) this.findViewById(R.id.notification_button);
        notificationButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                new GetNotificationsActivity(getApplicationContext()).execute(id);
            }

        });

        clicked_else = (ImageButton) this.findViewById(R.id.click_else);
        clicked_else.setBackgroundColor(Color.TRANSPARENT);
        clicked_else.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                    }
                }
        );
        clicked_add = (ImageButton) this.findViewById(R.id.click_add);
        clicked_add.setBackgroundColor(Color.TRANSPARENT);
        clicked_add.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        //New Loan page
                        Intent myIntent = new Intent(MainActivity.this, NewLoanActivity.class);
                        myIntent.putExtra("id", id);
                        myIntent.putExtra("name", username);
                        HashMap<String, String> tempfriends = getShortFriends();
                        myIntent.putExtra("friends", tempfriends);
                        MainActivity.this.startActivity(myIntent);
                    }
                }
        );
        updateLoanList();
        updateDebtList();
    }

    public void onResume() {
        super.onResume();
        uiHelper.onResume();
        Session session = Session.getActiveSession();
        if (session.getState().isClosed()) {
            viewFlipper.setDisplayedChild(1);
        }
        else {
            setInfo(session);
            viewFlipper.setDisplayedChild(0);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        uiHelper.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onPause() {
        super.onPause();
        uiHelper.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        uiHelper.onDestroy();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        uiHelper.onSaveInstanceState(outState);
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
            logout();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void onSessionStateChange(Session session, SessionState state, Exception exception) {
        if (state.isOpened()) {
            // loans menu
            viewFlipper.setDisplayedChild(0);
            setInfo(session);
        } else if (state.isClosed()) {
            //Logged out menu
            viewFlipper.setDisplayedChild(1);
            username = "";
            id = "";
            friends.clear();
            myLoans.clear();
            myDebts.clear();
        }
    }

    private void setInfo(Session session) {
        Request request = Request.newMeRequest(session, new Request.GraphUserCallback() {

            @Override
            public void onCompleted(GraphUser user, Response response) {
                if (user != null) {
                    String uname = user.getName();
                    id = user.getId();
                    username = uname;
                    new RegisterIdActivity(getApplicationContext()).execute(id, username);
                    setMyPicture();
                }
            }
        });
        Request friendrequest = new Request(session, "/me/friends", null, HttpMethod.GET, new Request.Callback() {
            @Override
            public void onCompleted(Response response) {
                try {
                    if (response.getGraphObject() != null) {
                        GraphObject go = response.getGraphObject();
                        JSONObject jso = go.getInnerJSONObject();
                        JSONArray arr = jso.getJSONArray("data");
                        for (int i = 0; i < arr.length(); i++) {
                            JSONObject jobj = arr.getJSONObject(i);
                            String name = jobj.getString("name");
                            String id = jobj.getString("id");
                            friends.put(id, new Friend(id, name));
                        }
                        initilizeFriends();
                        getNotes();
                        displayLoans();
                    }
                } catch (Exception e) {
                }
            }
        });
        request.executeAsync();
        friendrequest.executeAsync();
        delayedRefresh(500);
    }

    private void logout() {
        final Session sesh = Session.getActiveSession();
        if (sesh != null) {
            new AlertDialog.Builder(this)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setTitle("Logout of Payback")
                    .setMessage("Are you sure you want to logout?")
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener()
                    {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            sesh.closeAndClearTokenInformation();
                        }

                    }).setNegativeButton("No", null).show();
        }
    }


    private void getPictures() {
        RelativeLayout rl = (RelativeLayout) findViewById(R.id.mainmenu);
        GetPicsActivity getpics;
        for (Map.Entry entry : friends.entrySet()) {
            getpics = new GetPicsActivity(getApplicationContext(), entry.getKey().toString(), friends, rl);
            Friend f = (Friend) entry.getValue();
            if (f.getURL() != null) getpics.execute(f.getURL());
        }
    }

    private HashMap<String, String> getShortFriends() {
        HashMap<String, String> temp = new HashMap<String, String>();
        for (Map.Entry entry: friends.entrySet()) {
            Friend f = (Friend) entry.getValue();
            temp.put(entry.getKey().toString(), f.getName());
        }
        return temp;
    }

    private void getPicturesUrls() {
        String image_value;
        for (Map.Entry entry : friends.entrySet()) {
            try {
                image_value = "https://graph.facebook.com/"+entry.getKey().toString()+"/picture?type=large";
                Friend f = (Friend) entry.getValue();
                f.setURL(image_value);
            }
            catch(Exception e) {}
        }
    }

    private void setMyPicture() {
        String image_value = "https://graph.facebook.com/"+id+"/picture?type=large";
        RelativeLayout rl = (RelativeLayout) findViewById(R.id.mainmenu);
        new GetMyPicActivity(getApplicationContext(), rl).execute(image_value);
    }

    private void displayLoans() {
        updateLoanList();
        updateDebtList();
    }

    public ArrayList<String> getLoanTitles() {
        ArrayList<String> temp = new ArrayList<String>();
        for (Loan l: myLoans) {
            String s = "          " + l.getToName();
            s += "     " + l.getAmmount();
            temp.add(s);
        }
        return temp;
    }

    private void updateLoanList() {
        ExpandableListAdapter loanAdapter = new ExpandableListAdapter(getLoanTitles(), myLoans, id, this);
        loanAdapter.setInflater((LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE), this);
        //loanListView = (ListView) findViewById(R.id.loan_list);
        //loanListView.setAdapter(loanAdapter);
        elist = (ExpandableListView) findViewById(R.id.eloan_list);
        elist.setAdapter(loanAdapter);
    }
    private void updateDebtList() {
    //    debtAdapter = new LoanListAdapter(MainActivity.this, R.layout.loan_list_item, myDebts, id);
    //    debtListView = (ListView) findViewById(R.id.debt_list);
    //    debtListView.setAdapter(debtAdapter);
    }

    private void getNotes() {
        new GetNoteActivity(myLoans, myDebts, id, friends).execute();
    }

    private void makeToast(String s) {
        Toast.makeText(getApplicationContext(), s, Toast.LENGTH_LONG).show();
    }

    private void initilizeFriends() {
        getPicturesUrls();
        getPictures();
    }

    private void refresh() {
        getNotes();
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                displayLoans();
            }
        }, 500);
    }

    private void delayedRefresh(int millis) {
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                refresh();
            }
        }, millis);
    }

    public void paybackOnClickHandler(Loan l) {
        final Loan loan = l;
        if (!loan.getAllAccepted()) {
            if (loan.getFromAccepted() && !loan.getToAccepted()) {
                // accepting
                if (!loan.getFromid().equals(id)) {
                    new AlertDialog.Builder(this)
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .setTitle(loan.getFromName() + " wants to lend you money.")
                            .setPositiveButton("Accept", new DialogInterface.OnClickListener()
                            {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    new ConfirmLoanActivity(getApplicationContext()).execute(loan.getNoteId());
                                    delayedRefresh(1000);
                                }

                            }).setNegativeButton("Ignore", new DialogInterface.OnClickListener()
                            {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    new RejectLoanActivity().execute(loan.getNoteId(), id);
                                    delayedRefresh(1000);
                                }

                            }).show();
                }
                else {
                    new AlertDialog.Builder(this)
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .setTitle("Cancel Request?")
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener()
                            {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    new CancelRequestActivity(getApplicationContext()).execute(loan.getNoteId(), id);
                                    delayedRefresh(1000);
                                }

                            }).setNegativeButton("No", null).show();
                }
            }
            else if (loan.getToAccepted() && !loan.getFromAccepted()) {
                if (!loan.getToid().equals(id)) {
                    new AlertDialog.Builder(this)
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .setTitle(loan.getToName() + " is asking for money.")
                            .setPositiveButton("Accept", new DialogInterface.OnClickListener()
                            {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    new ConfirmLoanActivity(getApplicationContext()).execute(loan.getNoteId());
                                    delayedRefresh(1000);
                                }

                            }).setNegativeButton("Ignore", new DialogInterface.OnClickListener()
                            {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                     new RejectLoanActivity().execute(loan.getNoteId(), id);
                                delayedRefresh(1000);
                            }

                            }).show();
                }
                else {
                    new AlertDialog.Builder(this)
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .setTitle("Cancel Request?")
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener()
                            {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    new CancelRequestActivity(getApplicationContext()).execute(loan.getNoteId(), id);
                                    delayedRefresh(1000);
                                }

                            }).setNegativeButton("No", null).show();
                }
            }
        }
        else {
            new AlertDialog.Builder(this)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setTitle("Cancel?")
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener()
                    {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            new ConfirmCancelActivity(getApplicationContext()).execute(loan.getNoteId(), id);
                            delayedRefresh(1000);
                        }

                    }).setNegativeButton("No", null).show();
        }
    }

    private Session.StatusCallback callback = new Session.StatusCallback() {
        @Override
        public void call(Session session, SessionState state, Exception exception) {
            onSessionStateChange(session, state, exception);
        }
    };

    @Override
    public void onButtonClicked() {
        delayedRefresh(1000);
    }

}
