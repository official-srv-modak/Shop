package com.sourav.shop;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.drawerlayout.widget.DrawerLayout;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.material.navigation.NavigationView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import static com.sourav.shop.MainActivity.imageUrl;

public class OrderDetails extends AppCompatActivity {

    static JSONObject userInfo = null, productData = null;
    static String username = "GUEST";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_details);

        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        getSupportActionBar().hide();


        try {
            userInfo = new JSONObject(getIntent().getStringExtra("user_data"));
            productData = new JSONObject(getIntent().getStringExtra("product_details"));
            JSONObject temp = userInfo.getJSONObject("user_info");
            username = temp.getString("first_name")+" "+temp.getString("last_name");

        } catch (JSONException e) {
            e.printStackTrace();
        }

        //App bar codes
        ImageView menu = findViewById(R.id.orderDetailsMenu_btn);
        DrawerLayout drawerLayout = findViewById(R.id.orderDetailsDrawerlayout);
        NavigationView navigationView = findViewById(R.id.orderDetailsNav_bar);
        MiscOperations.initialiseHeaderMenu(navigationView, menu, drawerLayout, OrderDetails.this);

        ImageView searchBtn = findViewById(R.id.orderDetailsSearch_btn);
        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent searchIntent = new Intent(OrderDetails.this, SearchActivity.class);
                searchIntent.putExtra("user_data", userInfo.toString());
                startActivity(searchIntent);
            }
        });

        LoadData ld = new LoadData();
        ld.execute();
    }

    private class LoadData extends AsyncTask<String, Void, Integer> {
        @SuppressLint("SetTextI18n")
        protected Integer doInBackground(String... urls) {

            try
            {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            ImageView imageView = (ImageView) findViewById(R.id.orderDetailsImageView);
                            JSONArray images = null;
                            images = new JSONArray(productData.getString("images"));
                            String id = (String)images.get(0);  // only the first image
                            String imagePath = imageUrl + id;
                            if(!imagePath.isEmpty())
                            {
                                Glide.with(OrderDetails.this)
                                        .load(imagePath)
                                        .into(imageView);
                            }

                            TextView orderNameView = findViewById(R.id.orderDetailsOrderName);
                            TextView orderDateView = findViewById(R.id.orderDetailsOrderDate);
                            TextView orderPriceView = findViewById(R.id.orderDetailsPrice);

                            // set order name
                            if(productData.has("name"))
                                orderNameView.setText(productData.getString("name"));
                            // set order date
                            SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
                            String currentDate = formatter.format(new Date());
                            orderDateView.setText(currentDate);
                            //set order price
                            if(productData.has("price"))
                                orderPriceView.setText(orderPriceView.getText()+productData.getString("price"));

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
            catch(Exception e)
            {
                e.printStackTrace();
            }
            return 0;
        }

        ProgressDialog progressDialog = new ProgressDialog(OrderDetails.this);;

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            progressDialog.setMessage("Loading details, please wait");
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