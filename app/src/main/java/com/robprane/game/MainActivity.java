package com.robprane.game;

import android.content.pm.ActivityInfo;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.Display;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.Toast;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdSize;
import com.google.android.gms.ads.AdView;

public class MainActivity extends AppCompatActivity {

    // ~~~~~~~~~~ Fullscreen ~~~~~~~~~~

    private static final int UI_ANIMATION_DELAY = 300;
    private final Handler mHideHandler = new Handler();
    private final Runnable mHidePart2Runnable = new Runnable() {
        @Override
        public void run() {
            getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                    | View.SYSTEM_UI_FLAG_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        }
    };
    private final Runnable mShowPart2Runnable = new Runnable() {
        @Override
        public void run() {
            ActionBar actionBar = getSupportActionBar();
            if (actionBar != null) {
                actionBar.show();
            }
        }
    };
    private void hide() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
        mHideHandler.removeCallbacks(mShowPart2Runnable);
        mHideHandler.postDelayed(mHidePart2Runnable, UI_ANIMATION_DELAY);
    }
    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        hide();
    }
    @Override
    protected void onPostResume() {
        super.onPostResume();
        hide();
    }

    // ~~~~~~~~~~ Create application ~~~~~~~~~~

    private GameView gameView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        RelativeLayout layout = new RelativeLayout(this);
        layout.setLayoutParams(new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT));

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        if (getResources().getBoolean(R.bool.portrait)) { setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT); }
        else { setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE); }

        Display display = getWindowManager().getDefaultDisplay();

        Point size = new Point();
        display.getRealSize(size);

        gameView = new GameView(this, size.x, size.y);

        layout.addView(gameView);

        if (getResources().getBoolean(R.bool.banner_enabled)) {

            AdView adView = new AdView(this);
            adView.setAdSize(AdSize.SMART_BANNER);
            adView.setAdUnitId(getResources().getString(R.string.banner_ad_unit_id));

            AdRequest adRequest = new AdRequest.Builder().addTestDevice("Test").build();

            adView.loadAd(adRequest);

            RelativeLayout.LayoutParams adParams =
                    new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT,
                            RelativeLayout.LayoutParams.WRAP_CONTENT);
            if (getResources().getBoolean(R.bool.banner_position_top)) {
                adParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
            } else {
                adParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
            }
            layout.addView(adView, adParams);

        }

        setContentView(layout);

    }

    // ~~~~~~~~~~ Pause and resume application ~~~~~~~~~~

    @Override
    protected void onPause() {
        super.onPause();
        gameView.pause();
    }

    @Override
    protected void onResume() {
        super.onResume();
        gameView.resume();
    }

    // ~~~~~~~~~~ Pressing back to exit ~~~~~~~~~~

    private boolean exitFlag;

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if (exitFlag) {
                finish();
            } else {
                Toast.makeText(this,R.string.notice_exit,Toast.LENGTH_SHORT).show();
                exitFlag = true;
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        exitFlag = false;
                    }
                }, 2000);
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    // ~~~~~~~~~~ Destroying application ~~~~~~~~~~

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

}
