package com.sourav.shop;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.HorizontalScrollView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.material.navigation.NavigationView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static android.text.Layout.JUSTIFICATION_MODE_INTER_WORD;

public class MainActivity extends AppCompatActivity {

    public static String domain = "192.168.0.4:8081/";
    public static String sslProtocol = "http://";
    public static String productUrl = sslProtocol+domain+"home/product";
    public static String imageUrl = sslProtocol+domain+"ShopManager/image?id=";


    @SuppressLint("CheckResult")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getSupportActionBar().hide();
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out);

        JSONArray pidArray = new JSONArray();
        pidArray.put("all");
        TextView heading = findViewById(R.id.heading);
        heading.setText("Our collections");
        JSONObject pidObj = new JSONObject();
        try {
            pidObj.put("pid", pidArray);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        LoadCard ld = new LoadCard();
        ld.execute(productUrl, pidObj.toString());

        ImageView menu = findViewById(R.id.menu_btn);
        DrawerLayout drawerLayout = findViewById(R.id.drawerlayout);
        menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                drawerLayout.openDrawer(GravityCompat.START);
            }
        });

        NavigationView navigationView = findViewById(R.id.nav_bar);
        navigationView.bringToFront();
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {

                    /*case R.id.profiles: {
                        Intent intent = new Intent(MainActivity.this, Profiles.class);
                        intent.putExtra("startFlag", "1");
                        startActivity(intent);
                        finish();
                        break;
                    }
                    case R.id.resetProfile: {
                        resetProfile("Do you really want to reset all you watching history?");
                        break;
                    }
                    case R.id.contactUs: {
                        showContactUs("Developer - Sourav Modak\nContact Number - +91 9500166574\nE-Mail - official.srv.modak@gmail.com");
                        break;
                    }
                    case R.id.resetIp: {
                        showServerDialogNoExit("Do you really want to reset IP? It can crash app if false IP is set");
                        break;
                    }*/
                }
                DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawerlayout);
                drawer.closeDrawer(GravityCompat.START);
                return true;
            }
        });

        View headerView = navigationView.getHeaderView(0);
        /*ImageView loginProfile = headerView.findViewById(R.id.dp);
        Glide.with(MainActivity.this).load(R.drawable.kisaraa);*/


        ImageView menuInHeader = headerView.findViewById(R.id.menu_header);
        menuInHeader.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("WrongConstant")
            @Override
            public void onClick(View v) {
                if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
                    drawerLayout.closeDrawer(GravityCompat.START);
                }
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
        LinearLayout linearLayout1 = findViewById(R.id.linearLayout);
        linearLayout1.removeAllViews();
        List<Integer> idList = new ArrayList<Integer>();
        try {
            if (finalresumeData!=null) // Resume
            {
                JSONArray prod = finalresumeData.getJSONArray("cards"); // get the cards into an array

                for(int i = 0; i < prod.length(); i++)  // We then loop over the whole product array
                {

                    JSONObject card = prod.getJSONObject(i); // Take into of each card
                    // We have inflated it
                    View view = LayoutInflater.from(MainActivity.this).inflate(R.layout.product_card, null);
                    @SuppressLint({"NewApi", "LocalSuppress"}) int uniqueId = View.generateViewId();
                    view.setId(uniqueId);
                    idList.add(uniqueId);

                    // Now we set the data
                    // Images
                    ImageView imageView = (ImageView) view.findViewById(R.id.image);
                    JSONArray images = new JSONArray(card.getString("images"));
                    String id = (String)images.get(0);  // only the first image
                    String album_art_path = imageUrl + id;
                    if(!album_art_path.isEmpty())
                        Glide.with(MainActivity.this).load(album_art_path).into(imageView);

                    // Text price
                    TextView price = view.findViewById(R.id.price);
                    price.setText(price.getText()+card.getString("price"));

                    // Text title
                    TextView title = view.findViewById(R.id.productName);
                    title.setText(card.getString("name"));

                    // Text description
                    TextView desc = view.findViewById(R.id.description);
                    desc.setText(card.getString("description"));

                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        desc.setJustificationMode(JUSTIFICATION_MODE_INTER_WORD);
                    }

                    TextView origin = view.findViewById(R.id.origin);
                    origin.setText(origin.getText()+card.getString("origin_place"));

                    String availableFlag = card.getString("available_flag");
                    TextView stockInfo = view.findViewById(R.id.stock_info);
                    MiscOperations.addStockFlagColorTextView(availableFlag, stockInfo);

                    view.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent productDescritpionActivity = new Intent(MainActivity.this, ProductDescription.class);
                            productDescritpionActivity.putExtra("productDetails", card.toString());
                            startActivity(productDescritpionActivity);
                        }
                    });
                    Button buyBtn = view.findViewById(R.id.buyBtn), saveBtn = view.findViewById(R.id.saveBtn);
                    LinearLayout lBtn= view.findViewById(R.id.linearLayoutBtn);
                    if(availableFlag.equalsIgnoreCase("out of stock"))
                    {
                        saveBtn.setVisibility(View.INVISIBLE);
                        saveBtn.setEnabled(false);
                        buyBtn.setText("Notify me");
                        lBtn.removeView(saveBtn);
                        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                        lp.setMargins(40,0,40,0);
                        buyBtn.setLayoutParams(lp);
                        buyBtn.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Toast.makeText(MainActivity.this, "Will be notified", Toast.LENGTH_SHORT).show();
                            }
                        });
                    }

                    linearLayout1.addView(view);   // Add the horizontal layout to the vertical linear layout
                }

            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private class LoadCard extends AsyncTask<String, Void, Integer> {
        protected Integer doInBackground(String... urls) {

            JSONObject postReq = null;
            try {
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

                progressDialog.setMessage("Welcome, loading products");
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
}