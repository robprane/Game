package com.robprane.game;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

/**
 * Created by wakeapp on 29.08.17.
 */

public class Splash {
    private Bitmap bitmap;
    private int x;
    private int y;
    private boolean enable;
    private int time;

    public Splash(boolean portrait, Context context, int screenX, int screenY) {
        if (portrait) {
            y = (screenX / 2) - (screenY / 2);
            x = 0;
        } else {
            x = (screenX / 2) - (screenY / 2);
            y = 0;
        }
        Bitmap src = BitmapFactory.decodeResource(context.getResources(), R.drawable.splash);
        bitmap = Bitmap.createScaledBitmap(src, screenY, screenY, false);
        time = context.getResources().getInteger(R.integer.splash_time);
        enable = context.getResources().getBoolean(R.bool.splash_enabled);
    }

    public void update() {
        //
    }

    public void setTime(int time) {
        this.time = time;
    }

    public int getTime() { return time; }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public boolean enabled() {
        return enable;
    }

    public void setEnable(boolean enable) { this.enable = enable; }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }
}