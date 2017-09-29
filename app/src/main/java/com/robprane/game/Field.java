package com.robprane.game;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

import java.nio.Buffer;

class Field {
    private Bitmap bitmap;

    Bitmap src2;

    private int x;
    private int y;
    private int[][] field;

    Field(Context context, int screenX, int screenY, int side, int colors) {
        field = {{1, 2}, {2, 1}, };

//        bitmap = null;
//        Bitmap src = BitmapFactory.decodeResource(context.getResources(), R.drawable.brick_tile);
//        src2 = Bitmap.createScaledBitmap(src, Math.min(screenX / width, screenY / height), Math.min(screenX / width, screenY / height), false);
//        bitmap = createField(src2, width, height);
    }

    Bitmap getBitmap() {
        return bitmap;
    }

    private Bitmap createField(Bitmap c, int width, int height) {
        Bitmap cs = Bitmap.createBitmap(width * src2.getWidth(), height * src2.getHeight(), Bitmap.Config.ARGB_8888);

        Canvas comboImage = new Canvas(cs);

        Paint paint = new Paint();

        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                if (((i + j) & 1) == 0) {
                    paint.setColor(Color.BLUE);
                } else {
                    paint.setColor(Color.RED);
                }
                comboImage.drawRect(i * c.getWidth(), j * c.getHeight(), i * c.getWidth() + c.getWidth(), j * c.getHeight() + c.getHeight(), paint);
                comboImage.drawBitmap(c, i * c.getWidth(), j * c.getHeight(), null);
            }
        }

//        paint.setColor(Color.BLUE);
//        comboImage.drawRect(0f, 0f, c.getWidth(), c.getHeight(), paint);
//        comboImage.drawBitmap(c, 0f, 0f, null);
//        paint.setColor(Color.RED);
//        comboImage.drawRect(c.getWidth(), 0f, c.getWidth() + s.getWidth(), s.getHeight(), paint);
//        comboImage.drawBitmap(s, c.getWidth(), 0f, null);

        return cs;
    }

}
