package com.example.koivucorp.payback_20;

public class Loan {

    private String toname, fromname, toid, fromid, comment, ammount, timestamp, noteid, sender;
    private boolean toaccepted, fromaccepted, allaccepted;

    public Loan (String toname, String toid, String fromname, String fromid, String comment, String ammount, String timestamp, String noteid, String sender){
        this.toname = toname;
        this.toid = toid;
        this.fromname = fromname;
        this.fromid = fromid;
        this.comment = comment;
        this.ammount = ammount;
        this.timestamp = timestamp;
        this.noteid = noteid;
        this.sender = sender;
        toaccepted = false;
        fromaccepted = false;
        allaccepted = false;
    }

    public String getToid() { return toid; }

    public String getFromid() { return fromid; }

    public String getToName() {
        return toname;
    }

    public String getFromName() {
        return fromname;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public String getAmmount() {
        return ammount;
    }

    public String getComment() {
        return comment;
    }

    public boolean getToAccepted() { return toaccepted; }

    public void setToAccepted(boolean b) { toaccepted = b; }

    public boolean getFromAccepted() { return fromaccepted; }

    public void setFromAccepted(boolean b) { fromaccepted = b; }

    public String getNoteId() {
        return noteid;
    }

    public void setAllAccepted(boolean b) {
        allaccepted = b;
    }

    public boolean getAllAccepted() {
        return allaccepted;
    }

    public String getSender() {
        return sender;
    }

}