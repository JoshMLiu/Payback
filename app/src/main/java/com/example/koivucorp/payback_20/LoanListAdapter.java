package com.example.koivucorp.payback_20;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import java.util.ArrayList;

public class LoanListAdapter extends ArrayAdapter<Loan> {

    private ArrayList<Loan> loans;
    private int layoutResourceId;
    private Context context;
    private String id;

    public LoanListAdapter(Context context, int layoutResourceId, ArrayList<Loan> loans, String id) {
        super(context, layoutResourceId, loans);
        this.layoutResourceId = layoutResourceId;
        this.context = context;
        this.loans = loans;
        this.id = id;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        LoanHolder holder = null;

        LayoutInflater inflater = ((Activity) context).getLayoutInflater();
        row = inflater.inflate(layoutResourceId, parent, false);

        holder = new LoanHolder();
        holder.loan = loans.get(position);
        holder.info = (TextView) row.findViewById(R.id.loan_info);
        holder.acceptedinfo = (TextView) row.findViewById(R.id.accepted_info);
        holder.paybackButton = (Button) row.findViewById(R.id.payback_button);
        holder.paybackButton.setTag(holder.loan);
        row.setTag(holder);
        setupItem(holder);

        return row;
    }

    private void setupItem(LoanHolder holder) {
        if (holder.loan.getFromid().equals(id)) {
            holder.info.setText("To " + holder.loan.getToName() + ": " + holder.loan.getAmmount() + "\n" + holder.loan.getComment());
        }
        else {
            holder.info.setText("From " + holder.loan.getFromName() + ": " + holder.loan.getAmmount() + "\n" + holder.loan.getComment());
        }
        String s = "";
        // Not accepted
        if (!holder.loan.getAllAccepted()) {
            if (holder.loan.getFromAccepted()) {
                if (holder.loan.getFromid().equals(id)) {
                    s = "cancel\nrequest?";
                }
                else {
                    s = "accept\nnow";
                }
            }
            else if (holder.loan.getToAccepted()) {
                if (holder.loan.getToid().equals(id)) {
                    s = "cancel\nrequest?";
                }
                else {
                    s = "accept\nnow";
                }
            }
            else {
                s = "not\naccepted";
            }
        }
        else {
            if (holder.loan.getFromAccepted()) {
                if (holder.loan.getFromid().equals(id)) {
                    s = "pending\nresponse...";
                    holder.paybackButton.setVisibility(View.INVISIBLE);
                }
                else {
                    s = "cancel\nnow";
                }
            }
            else if (holder.loan.getToAccepted()) {
                if (holder.loan.getToid().equals(id)) {
                    s = "pending\nresponse...";
                    holder.paybackButton.setVisibility(View.INVISIBLE);
                }
                else {
                    s = "cancel\nnow";
                }
            }
            else {
                s = "cancel\nnow";
            }
        }

        holder.acceptedinfo.setText(s);
    }

    public static class LoanHolder {
        Loan loan;
        TextView info;
        TextView acceptedinfo;
        Button paybackButton;
    }
}