package com.robprane.game;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.support.v4.view.GestureDetectorCompat;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.util.ArrayList;
import java.util.Random;

/**
 * Created by wakeapp on 29.08.17.
 */

public class GameView extends SurfaceView implements Runnable {

    private static final String TAG = "Logs";

    volatile boolean playing;
    private Thread gameThread = null;
    private Player player;
    private Splash splash;

    private Paint paint;
    private Canvas canvas;
    private SurfaceHolder surfaceHolder;

    float motionX;
    float motionY;

    int ScreenX;
    int ScreenY;

    float ALPHA_STEP;

    int STEP;

    // Need to keep track of the current alpha value
    private int currentAlpha = 0;

    //Adding an stars list
    private ArrayList<Star> stars = new
            ArrayList<Star>();

    public Player getPlayer(){
        return player;
    }

    public GameView(Context context, int screenX, int screenY) {
        super(context);

        STEP = getResources().getInteger(R.integer.step);

        ALPHA_STEP = 256 / (getResources().getInteger(R.integer.fade_time) / getResources().getInteger(R.integer.step));

        ScreenX = screenX;
        ScreenY = screenY;

        splash = new Splash(getResources().getBoolean(R.bool.portrait), context, Math.max(screenX, screenY), Math.min(screenX, screenY));

        player = new Player(context, screenX, screenY);

        surfaceHolder = getHolder();
        paint = new Paint();

        //adding 100 stars you may increase the number
        int starNums = 150;
        for (int i = 0; i < starNums; i++) {
            Star s  = new Star(screenX, screenY);
            stars.add(s);
        }

        fadein();
    }

    @Override
    public void run() {
        while (playing) {
            update();
            draw();
            control();
        }
    }

    private void update() {
        if (splash.enabled()) {
            if (splash.getTime() >= getResources().getInteger(R.integer.fade_time)) {
                if (splash.getTime() >= getResources().getInteger(R.integer.splash_time) - getResources().getInteger(R.integer.fade_time)) {
                    fadein();
                }
                splash.setTime(splash.getTime() - STEP);
            } else {
                fadeout();
                if (currentAlpha <= ALPHA_STEP) {
                    splash.setEnable(false);
                }
            }
        } else {
            fadein();
            player.update();

            //Updating the stars with player speed
            for (Star s : stars) {
                s.update(player.getSpeed());
            }
        }
    }

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

    private void draw() {
        if (surfaceHolder.getSurface().isValid()) {
            canvas = surfaceHolder.lockCanvas();
            if (splash.enabled()) {
                canvas.drawColor(Color.WHITE);

                paint.setARGB(currentAlpha, 255, 255, 255);

                canvas.drawBitmap(splash.getBitmap(), splash.getX(), splash.getY(), paint);
            } else {

                canvas.drawColor(Color.WHITE);

                //setting the paint color to white to draw the stars

                paint.setARGB(currentAlpha, 255, 255, 255);

                //drawing all stars
                for (Star s : stars) {
                    paint.setStrokeWidth(s.getStarWidth());
                    canvas.drawPoint(s.getX(), s.getY(), paint);
                }

                canvas.drawBitmap(
                        player.getBitmap(),
                        player.getX(),
                        player.getY(),
                        paint);

            }
            surfaceHolder.unlockCanvasAndPost(canvas);
        }
    }

    private void control() {
        try {
            gameThread.sleep(STEP);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

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

    @Override
    public boolean onTouchEvent(MotionEvent motionEvent) {
        motionX = motionEvent.getX();
        motionY = motionEvent.getY();
        Log.d(TAG, motionEvent.toString());

        switch (motionEvent.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_UP:
                player.stopBoosting();
                break;
            case MotionEvent.ACTION_DOWN:
                if (motionEvent.getX() >= player.getX() &&
                    motionEvent.getX() <= player.getX() + player.getBitmap().getWidth() &&
                    motionEvent.getY() >= player.getY() &&
                    motionEvent.getY() <= player.getY() + player.getBitmap().getHeight()) {
                    player.setBoosting();
                }
                break;
            case MotionEvent.ACTION_MOVE:
                if (motionEvent.getX() >= player.getX() &&
                    motionEvent.getX() <= player.getX() + player.getBitmap().getWidth() &&
                    motionEvent.getY() >= player.getY() &&
                    motionEvent.getY() <= player.getY() + player.getBitmap().getHeight()) {
                    player.setBoosting();
                }
                break;
            default:
                break;
        }
        return true;
    }


}