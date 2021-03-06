package com.robprane.game;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.graphics.Matrix;
import android.view.Display;

class Player {
    private Bitmap bitmap;
    private int x;
    private int y;
    private int speed = 0;

    private boolean boosting;

    Player(Context context, int screenX, int screenY) {
        speed = 1;
        bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.player);
//        bitmap = RotateBitmap(bitmap, 90);
        x = screenX / 2 - bitmap.getWidth() / 2;
        y = screenY / 2 - bitmap.getHeight() / 2;

        boosting = false;
    }

    public static Bitmap RotateBitmap(Bitmap source, float angle)
    {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, true);
    }

    void setBoosting() {
        boosting = true;
    }

    void stopBoosting() {
        boosting = false;
    }

    boolean getBoosting() { return boosting; }

    void update() {
        if (boosting) {
            speed += 2;
        } else {
            speed -= 4;
        }
        int MAX_SPEED = 150;
        if (speed > MAX_SPEED) {
            speed = MAX_SPEED;
        }
        int MIN_SPEED = 1;
        if (speed < MIN_SPEED) {
            speed = MIN_SPEED;
        }

    }

    Bitmap getBitmap() {
        return bitmap;
    }

    int getX() {
        return x;
    }

    int getY() {
        return y;
    }

    int getSpeed() {
        return speed;
    }
}