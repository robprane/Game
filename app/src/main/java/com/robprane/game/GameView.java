package com.robprane.game;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.v4.view.GestureDetectorCompat;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.util.ArrayList;
import java.util.Random;
import android.os.Handler;
import android.widget.Toast;

import java.util.logging.LogRecord;

import static java.lang.Thread.sleep;

/**
 * Created by wakeapp on 29.08.17.
 */

public class GameView extends SurfaceView implements Runnable {

    volatile boolean playing = true;
    private Thread gameThread = new Thread(this);
    private Player player;
    private Splash splash;

    private Paint paint;
    private Canvas canvas;
    private SurfaceHolder surfaceHolder;

    int ScreenX;
    int ScreenY;

    float ALPHA_STEP;

    long STEP;

    long FADE_TIME;

    private int currentAlpha = 0;

    private ArrayList<Star> stars = new
            ArrayList<Star>();

    public GameView(Context context, int screenX, int screenY) {

        super(context);

        fpsRect = new Rect(screenX - 300, 0, screenX, 150);

        fpsPaint = new Paint();
        fpsPaint.setColor(Color.RED);
        fpsPaint.setTextSize(40);

        STEP = 1000 / getResources().getInteger(R.integer.frame_rate);

        FADE_TIME = getResources().getInteger(R.integer.fade_time);

        ALPHA_STEP = 256 / (FADE_TIME / STEP);

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

    private void update() {
        if (splash.enabled()) { // All about splash of the application
            if (splash.getTime() >= getResources().getInteger(R.integer.fade_time)) {
                if (splash.getTime() >= getResources().getInteger(R.integer.splash_time) - getResources().getInteger(R.integer.fade_time)) {
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

    private void draw() {
        if (surfaceHolder.getSurface().isValid()) {
            canvas = surfaceHolder.lockCanvas();
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
        } catch (InterruptedException e) {
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
            canvas.drawText(String.format("fps:%d", fps), fpsRect.centerX(), fpsRect.centerY(), fpsPaint);
        }
        fpsLastTime = currTime;
    }

}