package com.example.cygoapp;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ActivityOptions;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Pair;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

public class MainActivity extends AppCompatActivity {

    private static int SPLASH_SCREEN = 3000;

    Animation topAnim;
    ImageView logo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_main);

        Boolean isFirstRun = getSharedPreferences("PREFERENCE",MODE_PRIVATE).getBoolean("isFirstRun", true);

        topAnim= AnimationUtils.loadAnimation(this,R.anim.top_animation);

        logo = findViewById(R.id.logoImg);

        logo.setAnimation(topAnim);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                if(isFirstRun){
                    getSharedPreferences("PREFERENCE",MODE_PRIVATE).edit().putBoolean("isFirstRun", false).commit();

                    Intent intent = new Intent(MainActivity.this,GetStart.class);
                    startActivity(intent);
                }

                Intent intent = new Intent(MainActivity.this,Login.class);
                Pair[] pairs = new Pair[1];
                pairs[0] = new Pair<View,String>(logo,"logoTransition");

                ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(MainActivity.this,pairs);
                startActivity(intent,options.toBundle());
                finish();
            }
        },SPLASH_SCREEN);
    }
}