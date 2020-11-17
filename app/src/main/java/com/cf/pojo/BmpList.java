package com.cf.pojo;

import android.graphics.Bitmap;

public class BmpList {
    private Bitmap bitmap;
    private String bmpText;

    public BmpList(Bitmap bitmap, String bmpText) {
        this.bitmap = bitmap;
        this.bmpText = bmpText;
    }

    public BmpList() {
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    public String getBmpText() {
        return bmpText;
    }

    public void setBmpText(String bmpText) {
        this.bmpText = bmpText;
    }
}
