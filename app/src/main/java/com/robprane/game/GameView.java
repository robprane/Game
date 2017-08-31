package com.robprane.game;

import android.content.Context;
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
    private boolean showsplash = false;
    private int timeshowsplash;
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

    //Adding an stars list
    private ArrayList<Star> stars = new
            ArrayList<Star>();

    public Player getPlayer(){
        return player;
    }

    public GameView(Context context, int screenX, int screenY) {
        super(context);

        ScreenX = screenX;
        ScreenY = screenY;

        splash = new Splash(context, screenX, screenY);

        player = new Player(context, screenX, screenY);

        surfaceHolder = getHolder();
        paint = new Paint();

        //adding 100 stars you may increase the number
        int starNums = 150;
        for (int i = 0; i < starNums; i++) {
            Star s  = new Star(screenX, screenY);
            stars.add(s);
        }
    }

    @Override
    public void run() {
        while (playing) {
            if (showsplash) {
                timeshowsplash++;
            }
            update();
            draw();
            control();
        }
    }

    private void update() {
        if (showsplash) {
            splash.update(timeshowsplash);
        } else {
            player.update();

            //Updating the stars with player speed
            for (Star s : stars) {
                s.update(player.getSpeed());
            }
        }
    }

    private void draw() {
        if (surfaceHolder.getSurface().isValid()) {
            canvas = surfaceHolder.lockCanvas();
            if (showsplash) {
                canvas.drawColor(Color.WHITE);

                paint.setColor(Color.BLACK);

                paint.setTextSize(50);

                canvas.drawBitmap(splash.getBitmap(), splash.getX(), splash.getY(), paint);
            } else {
                canvas.drawColor(Color.BLACK);

                //setting the paint color to white to draw the stars
                paint.setColor(Color.WHITE);

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
            gameThread.sleep(10);
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