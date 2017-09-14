package com.robprane.game;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.util.ArrayList;
import java.util.Locale;

import static java.lang.Thread.sleep;

public class GameView extends SurfaceView implements Runnable {

    // ~~~~~~~~~~ Create game ~~~~~~~~~~

    int ScreenX;
    int ScreenY;

    volatile boolean playing = true;
    private Thread gameThread = new Thread(this);
    private Player player;
    private Splash splash;

    private ArrayList<Star> stars = new
            ArrayList<Star>();

//    private Bitmap bitmap;
//    private Bitmap src;

    public GameView(Context context, int screenX, int screenY) {

        super(context);

        fpsRect = new Rect(screenX - 300, 0, screenX, 150);

        fpsPaint = new Paint();
        fpsPaint.setColor(Color.RED);
        fpsPaint.setTextSize(40);

        STEP = 1000 / getResources().getInteger(R.integer.frame_rate);

        FADE_TIME = getResources().getInteger(R.integer.fade_time);

        ALPHA_STEP = 256 / (FADE_TIME / STEP);

        SPLASH_TIME = getResources().getInteger(R.integer.splash_time);

        ScreenX = screenX;
        ScreenY = screenY;

        splash = new Splash(getResources().getBoolean(R.bool.portrait), context, Math.max(screenX, screenY), Math.min(screenX, screenY));

        player = new Player(context, screenX, screenY);

        surfaceHolder = getHolder();
        paint = new Paint();

        int starNums = 500;
        for (int i = 0; i < starNums; i++) {
            Star s = new Star(screenX, screenY);
            stars.add(s);
        }

//        src = BitmapFactory.decodeResource(this.getResources(), R.drawable.brick_tile);
//        bitmap = Bitmap.createScaledBitmap(src, 300, 300, false);

        fadein();
    }

    // ~~~~~~~~~~ Game activity ~~~~~~~~~~

    @Override
    public void run() {
        while (playing) {
            long startTime = System.currentTimeMillis();
            update();
            draw();
            long endTime = System.currentTimeMillis();
            int remainTime = (int) STEP - (int) (endTime - startTime);
            if (remainTime > 0) {
                try {
                    sleep(remainTime);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    // ~~~~~~~~~~ Update game data ~~~~~~~~~~

    float ALPHA_STEP;
    long STEP;
    long FADE_TIME;
    long SPLASH_TIME;

    private void update() {
        if (splash.enabled()) { // All about splash of the application
            if (splash.getTime() >= FADE_TIME) {
                if (splash.getTime() >= SPLASH_TIME - FADE_TIME) {
                    fadein();
                }
                splash.setTime(splash.getTime() - (int) STEP);
            } else {
                fadeout();
                if (currentAlpha <= ALPHA_STEP) {
                    splash.setEnable(false);
                }
            }
        } else { // All about game
            fadein();
            player.update();

            for (Star s : stars) {
                s.update(player.getBoosting(), player.getSpeed());
            }
        }
    }

    // ~~~~~~~~~~ Fade in and fade out animations ~~~~~~~~~~

    private int currentAlpha = 0;

    private void fadeout() {
        if (currentAlpha >= ALPHA_STEP) {
            currentAlpha -= ALPHA_STEP;
        }
    }

    private void fadein() {
        if (currentAlpha <= 255 - ALPHA_STEP) {
            currentAlpha += ALPHA_STEP;
        }
    }

    // ~~~~~~~~~~ Draw game ~~~~~~~~~~

    private Paint paint;
    private SurfaceHolder surfaceHolder;

    private void draw() {
        if (surfaceHolder.getSurface().isValid()) {
            Canvas canvas = surfaceHolder.lockCanvas();
            if (splash.enabled()) {
                canvas.drawColor(Color.BLACK);
                paint.setARGB(currentAlpha, 255, 255, 255);

                canvas.drawBitmap(splash.getBitmap(), splash.getX(), splash.getY(), paint);
            } else {

                canvas.drawColor(Color.BLACK);

                paint.setARGB(currentAlpha, 255, 255, 255);

                for (Star s : stars) {
                    paint.setStrokeWidth(s.getStarWidth());
                    canvas.drawLine(s.getX(), s.getY(), s.getX() + s.getSpeed(), s.getY(), paint);
                }

                canvas.drawBitmap(
                        player.getBitmap(),
                        player.getX(),
                        player.getY(),
                        paint);

//                paint.setColor(Color.argb(255, 119, 61, 66));
//                canvas.drawRect(0, 0, 300, 300, paint);
//                canvas.drawRect(300, 300, 600, 600, paint);
//                paint.setColor(Color.argb(255, 60, 92, 119));
//                canvas.drawRect(0, 300, 300, 600, paint);
//                canvas.drawRect(300, 0, 600, 300, paint);
//                canvas.drawBitmap(bitmap, 0, 0, paint);
//                canvas.drawBitmap(bitmap, 0, 300, paint);
//                canvas.drawBitmap(bitmap, 300, 0, paint);
//                canvas.drawBitmap(bitmap, 300, 300, paint);

            }
            drawFps(canvas);
            surfaceHolder.unlockCanvasAndPost(canvas);
        }
    }

    // ~~~~~~~~~~ Pause and resume game ~~~~~~~~~~

    public void pause() {
        playing = false;
        try {
            gameThread.join();
        } catch (InterruptedException ignored) {
        }
    }

    public void resume() {
        playing = true;
        gameThread = new Thread(this);
        gameThread.start();
    }

    // ~~~~~~~~~~ Motion events ~~~~~~~~~~

    private int lastX = 0;
    private int lastY = 0;
    private long lastTime;

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                lastX = (int) event.getX();
                lastY = (int) event.getY();
                lastTime = System.currentTimeMillis();
                return true;
            case MotionEvent.ACTION_UP:
                int x = (int) event.getX();
                int y = (int) event.getY();
                int time = (int) (System.currentTimeMillis() - lastTime);
                float vx = (x - lastX) / (float) time;
                float vy = (y - lastY) / (float) time;
                handleTouchEvent(vx, vy, time);
                return true;
        }
        return super.onTouchEvent(event);
    }

    private void handleTouchEvent(float vx, float vy, int time) {
        float absVx = Math.abs(vx);
        float absVy = Math.abs(vy);
        if (absVx < 0.2 && absVy < 0.2) {
            if (time < 300) {
                // Tap
            } else {
                // Long press
            }
        } else if (absVx > absVy) {
            if (vx > 0) {
                // Right Swipe
                player.setBoosting();
            } else {
                // Left swipe
                player.stopBoosting();
            }
        } else if (vy > 0) {
            //Down swipe
        } else {
            // Up swipe
        }
    }

    // ~~~~~~~~~~ FPS counter ~~~~~~~~~~

    private Rect fpsRect;
    private Paint fpsPaint;
    private long fpsLastTime;

    private void drawFps(Canvas canvas) {
        long currTime = System.currentTimeMillis();
        if (fpsLastTime != 0) {
            int fps = (int) (1000 / (currTime - fpsLastTime));
            canvas.drawText(Integer.toString(fps), fpsRect.centerX(), fpsRect.centerY(), fpsPaint);
        }
        fpsLastTime = currTime;
    }

}