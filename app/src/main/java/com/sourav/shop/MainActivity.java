package com.sourav.shop;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.material.navigation.NavigationView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import static android.text.Layout.JUSTIFICATION_MODE_INTER_WORD;

public class MainActivity extends AppCompatActivity {

    public static String awsdomain = "www.kisara.in:8081/";
    //public static String domain = "192.168.0.4:8081/";
    public static String domain = "192.168.0.4:8081/";
    public static String sslProtocol = "http://";
    public static String productUrl = sslProtocol+domain+"product/fetchproduct";
    public static String imageUrl = sslProtocol+domain+"ShopManager/image?id=";
    public static String loginUrl = sslProtocol+domain+"account/login";
    public static String searchUrl = sslProtocol+domain+"search/query";
    public static String verifyinfoUrl = sslProtocol+domain+"account/verifyinfo";
    public static String createCustAccountUrl = sslProtocol+domain+"account/createaccount";
    static String username = "GUEST";
    static JSONObject userInfo = null, productDt = null;

    @SuppressLint("CheckResult")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        getSupportActionBar().hide();
        overridePendingTransition(0, R.anim.fade_out);

        try {
            userInfo = new JSONObject(getIntent().getStringExtra("user_data"));
            String sessionId = userInfo.get("session_id").toString();
            JSONObject temp = userInfo.getJSONObject("user_info");
            username = temp.getString("first_name")+" "+temp.getString("last_name");
            CheckSession checkSession = new CheckSession();
            checkSession.execute(sessionId);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        JSONArray pidArray = new JSONArray();
        pidArray.put("all");
        TextView heading = findViewById(R.id.heading);
        heading.setText("Our collections");
        JSONObject pidObj = new JSONObject();
        try {
            pidObj.put("session_id", userInfo.get("session_id").toString());
            pidObj.put("pid", pidArray);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        LoadCard ld = new LoadCard();
        ld.execute(productUrl, pidObj.toString());


        //App bar codes
        ImageView menu = findViewById(R.id.menu_btn);
        DrawerLayout drawerLayout = findViewById(R.id.drawerlayout);
        NavigationView navigationView = findViewById(R.id.nav_bar);
        MiscOperations.initialiseHeaderMenu(navigationView, menu, drawerLayout, MainActivity.this);

        ImageView searchBtn = findViewById(R.id.search_btn);
        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent searchIntent = new Intent(MainActivity.this, SearchActivity.class);
                searchIntent.putExtra("user_data", userInfo.toString());
                startActivity(searchIntent);
            }
        });
    }



    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawerlayout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }



    @SuppressLint({"WrongConstant"})
    public void getCard(JSONObject finalresumeData)
    {
        RecyclerView recyclerView = findViewById(R.id.pageScrollView);
        Animation fadeIn = AnimationUtils.loadAnimation(this, R.anim.fade_in_card);
        recyclerView.setAnimation(fadeIn);
        ArrayList<String> title = new ArrayList<String>(), origin = new ArrayList<String>(), price = new ArrayList<String>(), imageUrl = new ArrayList<String>(), availableFlag = new ArrayList<String>(), productDet = new ArrayList<String>();
        try {
            if (finalresumeData!=null) // Resume
            {
                JSONArray prod = finalresumeData.getJSONArray("cards"); // get the cards into an array

                for(int i = 0; i < prod.length(); i++)  // We then loop over the whole product array
                {

                    JSONObject card = prod.getJSONObject(i); // Take into of each card

                    productDet.add(i, card.toString());

                    // Now we set the data
                    // Images
                    JSONArray images = new JSONArray(card.getString("images"));
                    String id = (String)images.get(0);  // only the first image
                    String album_art_path = MainActivity.imageUrl + id;
                    if(!album_art_path.isEmpty())
                    {
                        imageUrl.add(i, album_art_path);
                    }

                    // Text price
                    price.add(i, card.getString("price"));

                    // Text title
                    title.add(i, card.getString("name"));

                    origin.add(i, card.getString("origin_place"));

                    availableFlag.add(i, card.getString("available_flag"));


                }
                Feed feedAdapter = new Feed(MainActivity.this, title, origin, price, imageUrl, availableFlag, userInfo.toString(), productDet);
                recyclerView.setAdapter(feedAdapter);
                recyclerView.setLayoutManager(new LinearLayoutManager(MainActivity.this));
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private class LoadCard extends AsyncTask<String, Void, Integer> {
        protected Integer doInBackground(String... urls) {

            JSONObject postReq = null;
            try {
                Log.e("URL", urls[1]);
                postReq = new JSONObject(urls[1]);
                JSONObject respObject = MiscOperations.getDataFromServerPOST(productUrl, postReq);
                String temp = "1"; ///// remove
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        getCard(respObject);
                    }
                });
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return null;
        }

        ProgressDialog progressDialog = new ProgressDialog(MainActivity.this);;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog.setMessage("Welcome, "+username+" loading products");
            progressDialog.setIndeterminate(false);
            progressDialog.setCancelable(true);
            progressDialog.show();

        }

        @Override
        protected void onPostExecute(Integer integer) {
            super.onPostExecute(integer);

            progressDialog.dismiss();

        }

    }

    class CheckSession extends AsyncTask<String, Void, Integer> {
        JSONObject userInfo = new JSONObject(), output = new JSONObject();
        protected Integer doInBackground(String... data) {

            //login

            try {
                userInfo.put("session_id", data[0]);
                output = MiscOperations.getDataFromServerPOST(MainActivity.loginUrl, userInfo);
            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        }


        @Override
        protected void onPreExecute() {
            super.onPreExecute();


        }

        @Override
        protected void onPostExecute(Integer integer) {
            super.onPostExecute(integer);
            try {

                if(output.length() == 0)
                {
                    sessionExpired("Your session has expired. You might have logged in from another device");
                }
                else
                {
                    output = output.getJSONObject("user_info");
                    username = output.getString("first_name")+" "+output.getString("last_name");
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

        }

    }

    public void sessionExpired(String Message)
    {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage(Message);
        alertDialogBuilder.setCancelable(false);
        alertDialogBuilder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                File file = new File(SplashScreen.sessionIdFilePath);
                if(file.exists())
                    file.delete();
                Intent intent = new Intent(MainActivity.this, LoginPage.class);
                intent.putExtra("session_id_file_path", SplashScreen.sessionIdFilePath);
                startActivity(intent);
                finish();

            }
        });
        alertDialogBuilder.show();
    }
}