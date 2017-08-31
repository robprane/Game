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

    public Splash(Context context, int screenX, int screenY) {
        x = (screenX / 2) - (screenY / 2);
        y = 0;
        Bitmap src = BitmapFactory.decodeResource(context.getResources(), R.drawable.splash);
        bitmap = Bitmap.createScaledBitmap(src, screenY, screenY, false);
    }

    public void update(int time) {
        //
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }
}