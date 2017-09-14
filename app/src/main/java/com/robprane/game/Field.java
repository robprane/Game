package com.robprane.game;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

public class Field {
    private Bitmap bitmap;

    private int x;
    private int y;

    public Field(Context context, int screenX, int screenY) {
        Bitmap src = BitmapFactory.decodeResource(context.getResources(), R.drawable.splash);
        bitmap = Bitmap.createScaledBitmap(src, screenY, screenY, false);
//        bitmap = Bitmap.


    }

    public void update(int playerSpeed) {

    }

}
