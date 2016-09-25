package com.example.koivucorp.payback_20;

import android.graphics.Bitmap;

public class Friend {

    private String id, name, photourl;
    private Bitmap picture = null;

    public Friend(String id, String name) {
        this.id = id;
        this.name = name;
        photourl = null;
    }

    public void setName(String n) {
        name = n;
    }

    public void setPic(Bitmap pic) {
        picture = pic;
    }

    public void setURL(String u) {
        photourl = u;
    }

    public Bitmap getPic() {
        return picture;
    }

    public String getName() {
        return name;
    }

    public String getId() {
        return id;
    }

    public String getURL() {
        return photourl;
    }

}
