package com.sourav.shop;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
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
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class SplashScreen extends AppCompatActivity {

    static String sessionIdFilePath =  "";

    static String getUserData(String sessionIdFilePath)
    {
        String output = null;
        File sessionFile = new File(sessionIdFilePath);
        if(sessionFile.exists())
        {
            try {
                ObjectInputStream objIn = new ObjectInputStream(new FileInputStream(sessionFile));
                output = (String)objIn.readObject();
                objIn.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return output;
    }
    public void animationStart()
    {
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
    }

    public void noAnimationStart()
    {
        ImageView logoNameSplashText = findViewById(R.id.logoSplashScreenText);
        logoNameSplashText.setVisibility(View.INVISIBLE);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        setContentView(R.layout.activity_splash_screen);
        getSupportActionBar().hide();
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        sessionIdFilePath = getApplicationContext().getFilesDir().getAbsolutePath() + File.separator + "session_id.json";

        JSONObject userData = null;

        String sessionId = null;
        String temp = getUserData(sessionIdFilePath);
        if (temp != null) {
            try {
                userData = new JSONObject(temp);
                sessionId = userData.get("session_id").toString();
                String finalSessionId = sessionId;
                JSONObject finalUserData = userData;
                if (finalSessionId != null)
                {
                    noAnimationStart();
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {

                            Intent intent = new Intent(SplashScreen.this, MainActivity.class);
                            intent.putExtra("user_data", finalUserData.toString());
                            startActivity(intent);
                            finish();

                        }
                    }, 1000);

                }
                else
                {
                    animationStart();
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            Intent intent = new Intent(SplashScreen.this, LoginPage.class);
                            intent.putExtra("session_id_file_path", sessionIdFilePath);
                            startActivity(intent);
                            finish();
                        }
                    }, 5000);
                }
            } catch (JSONException e) {
                e.getSuppressed();
            }
        }
        else
        {
            animationStart();
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    Intent intent = new Intent(SplashScreen.this, LoginPage.class);
                    intent.putExtra("session_id_file_path", sessionIdFilePath);
                    startActivity(intent);
                    finish();
                }
            }, 5000);
        }

    }
}