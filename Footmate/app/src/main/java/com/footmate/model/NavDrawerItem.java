package com.footmate.model;

import android.graphics.drawable.Drawable;

public class NavDrawerItem {
    private String title;
    private Drawable icons;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Drawable getIcons() {
        return icons;
    }

    public void setIcons(Drawable icons) {
        this.icons = icons;
    }
}
