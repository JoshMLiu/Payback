package com.example.koivucorp.payback_20;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.TextView;
import java.util.ArrayList;
import android.widget.CheckedTextView;


@SuppressWarnings("unchecked")
public class ExpandableListAdapter extends BaseExpandableListAdapter {

    public ArrayList<String> groupItem;
    public Loan childloan;
    public ArrayList<Loan> childtem = new ArrayList<Loan>();
    public LayoutInflater minflater;
    public Activity activity;
    private String id;
    private Context context;

    public ExpandableListAdapter(ArrayList<String> grList, ArrayList<Loan> childItem, String id, Context context) {
        groupItem = grList;
        childtem = childItem;
        this.id = id;
        this.context = context;
    }

    public interface OnButtonClickListener {
        public void onButtonClicked();
    }

    public void setInflater(LayoutInflater mInflater, Activity act) {
        this.minflater = mInflater;
        activity = act;
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return null;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return 0;
    }

    public void setClickListener(Loan l, Button b) {
        final Loan loan = l;
        final Button button = b;
        if (!loan.getAllAccepted()) {
            if (loan.getFromAccepted() && !loan.getToAccepted()) {
                // accepting
                if (!loan.getFromid().equals(id)) {
                    new AlertDialog.Builder(context)
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .setTitle(loan.getFromName() + " wants to lend you money.")
                            .setPositiveButton("Accept", new DialogInterface.OnClickListener()
                            {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    new ConfirmLoanActivity(context.getApplicationContext()).execute(loan.getNoteId());
                                    button.setVisibility(View.INVISIBLE);
                                }

                            }).setNegativeButton("Ignore", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    new RejectLoanActivity().execute(loan.getNoteId(), id);
                                    button.setVisibility(View.INVISIBLE);
                                }

                            }).show();
                }
                else {
                    new AlertDialog.Builder(context)
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .setTitle("Cancel Request?")
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener()
                            {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    new CancelRequestActivity(context.getApplicationContext()).execute(loan.getNoteId(), id);
                                    button.setVisibility(View.INVISIBLE);
                                }

                            }).setNegativeButton("No", null).show();
                }
            }
            else if (loan.getToAccepted() && !loan.getFromAccepted()) {
                if (!loan.getToid().equals(id)) {
                    new AlertDialog.Builder(context)
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .setTitle(loan.getToName() + " is asking for money.")
                            .setPositiveButton("Accept", new DialogInterface.OnClickListener()
                            {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    new ConfirmLoanActivity(context.getApplicationContext()).execute(loan.getNoteId());
                                    button.setVisibility(View.INVISIBLE);
                                }

                            }).setNegativeButton("Ignore", new DialogInterface.OnClickListener()
                            {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    new RejectLoanActivity().execute(loan.getNoteId(), id);
                                    button.setVisibility(View.INVISIBLE);
                                }

                            }).show();
                }
                else {
                    new AlertDialog.Builder(context)
                            .setIcon(android.R.drawable.ic_dialog_alert)
                            .setTitle("Cancel Request?")
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener()
                            {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    new CancelRequestActivity(context.getApplicationContext()).execute(loan.getNoteId(), id);
                                    button.setVisibility(View.INVISIBLE);
                                }

                            }).setNegativeButton("No", null).show();
                }
            }
        }
        else {
            new AlertDialog.Builder(context)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setTitle("Cancel?")
                    .setPositiveButton("Yes", new DialogInterface.OnClickListener()
                    {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            new ConfirmCancelActivity(context.getApplicationContext()).execute(loan.getNoteId(), id);
                            button.setVisibility(View.INVISIBLE);
                        }

                    }).setNegativeButton("No", null).show();
        }
        onButtonClicked();
    }

    @Override
    public View getChildView(int groupPosition, final int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {

        childloan = (Loan) childtem.get(groupPosition);
        TextView text = null;
        if (convertView == null) {
            convertView = minflater.inflate(R.layout.childrow, null);
        }
        text = (TextView) convertView.findViewById(R.id.textView1);
        text.setText("For: " + childloan.getComment());
        final Button button = (Button) convertView.findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    setClickListener(childloan, button);
                }
            }
        );
        String s = "";
        // Not accepted
        if (!childloan.getAllAccepted()) {
            if (childloan.getFromAccepted()) {
                // Loan from me
                if (childloan.getFromid().equals(id)) {
                    s = "cancel\nrequest?";
                    button.setText("Cancel?");
                    button.setVisibility(View.VISIBLE);
                }
                else {
                    s = "accept\nnow";
                    button.setText("Accept?");
                    button.setVisibility(View.VISIBLE);
                }
            }
            // Debt from me
            else if (childloan.getToAccepted()) {
                if (childloan.getToid().equals(id)) {
                    s = "cancel\nrequest?";
                    button.setText("Cancel?");
                    button.setVisibility(View.VISIBLE);
                }
                else {
                    s = "accept\nnow";
                    button.setText("Accept?");
                    button.setVisibility(View.VISIBLE);
                }
            }
            else {
                s = "not\naccepted";
                button.setVisibility(View.INVISIBLE);
            }
        }
        else {
            if (childloan.getFromAccepted()) {
                if (childloan.getFromid().equals(id)) {
                    s = "pending\nresponse...";
                    button.setVisibility(View.INVISIBLE);
                }
                else {
                    s = "cancel\nnow";
                    button.setText("Cancel?");
                    button.setVisibility(View.VISIBLE);
                }
            }
            else if (childloan.getToAccepted()) {
                if (childloan.getToid().equals(id)) {
                    s = "pending\nresponse...";
                    button.setVisibility(View.INVISIBLE);
                }
                else {
                    s = "cancel\nnow";
                    button.setText("Cancel?");
                    button.setVisibility(View.VISIBLE);
                }
            }
            else {
                s = "cancel\nnow";
                button.setText("Cancel?");
                button.setVisibility(View.VISIBLE);
            }
        }
        final String popup = s;
        /*convertView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(activity, popup, Toast.LENGTH_SHORT).show();
            }
        }); */
        return convertView;
    }

    @Override
    public Object getGroup(int groupPosition) {
        return null;
    }

    public int getChildrenCount(int i) { return 1; }

    @Override
    public int getGroupCount() {
        return groupItem.size();
    }

    @Override
    public void onGroupCollapsed(int groupPosition) {
        super.onGroupCollapsed(groupPosition);
    }

    @Override
    public void onGroupExpanded(int groupPosition) {
        super.onGroupExpanded(groupPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return 0;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {

        if (convertView == null) {
            convertView = minflater.inflate(R.layout.grouprow, null);
        }
        ((CheckedTextView) convertView).setText(groupItem.get(groupPosition));
        ((CheckedTextView) convertView).setChecked(isExpanded);
        return convertView;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return false;
    }

}
