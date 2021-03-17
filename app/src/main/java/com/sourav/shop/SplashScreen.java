package com.sourav.shop;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

public class SplashScreen extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        getSupportActionBar().hide();
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        Animation slideInRt = AnimationUtils.loadAnimation(this, R.anim.slide_from_rt_to_lt);
        Animation fadeIn = AnimationUtils.loadAnimation(this, R.anim.fade_in_widget);
        Animation textAnim = AnimationUtils.loadAnimation(this, R.anim.text_animation);
        Animation imageAnim = AnimationUtils.loadAnimation(this, R.anim.image_animation);
        Animation slideInUp = AnimationUtils.loadAnimation(this, R.anim.slide_from_down_to_up);


        ImageView logoNameSplashText = findViewById(R.id.logoSplashScreenText), logoNameSplashImage = findViewById(R.id.logoSplashScreen), madeInIndiaLogo = findViewById(R.id.made_in_india_logo);
        logoNameSplashText.setAnimation(textAnim);
        logoNameSplashImage.setAnimation(imageAnim);
        madeInIndiaLogo.setAnimation(slideInUp);

        TextView tagline = findViewById(R.id.tagline);
        tagline.setAnimation(fadeIn);

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(SplashScreen.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        }, 5000);
    }
}