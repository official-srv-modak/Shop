package com.sourav.shop;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.drawerlayout.widget.DrawerLayout;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.material.navigation.NavigationView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;

import static com.sourav.shop.MainActivity.imageUrl;

public class ProductDescription extends AppCompatActivity {

    static JSONObject userInfo;

    public static ArrayList<String> formatKeys(ArrayList<String> keys)
    {
        for(int i = 0; i<keys.size(); i++)
        {
            String val = keys.get(i);
             val = val.replace("_", " ");
            keys.set(i, val);
        }

        return keys;
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_description);
        getSupportActionBar().hide();
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);


        LoadCard ld = new LoadCard();
        ld.execute();

        //App bar codes

        NavigationView navigationView = findViewById(R.id.productDescriptionNav_bar);
        ImageView menu = findViewById(R.id.productDescriptionMenu_btn);
        DrawerLayout drawerLayout = findViewById(R.id.productDescriptionDrawerlayout);
        MiscOperations.initialiseHeaderMenu(navigationView, menu, drawerLayout, ProductDescription.this);

        try {
            userInfo = new JSONObject(getIntent().getStringExtra("user_data"));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        ImageView searchBtn = findViewById(R.id.productDescriptionSearch_btn);
        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent searchIntent = new Intent(ProductDescription.this, SearchActivity.class);
                searchIntent.putExtra("user_data", userInfo.toString());
                startActivity(searchIntent);
            }
        });

    }

    private class LoadCard extends AsyncTask<String, Void, Integer> {
        @SuppressLint("SetTextI18n")
        protected Integer doInBackground(String... urls) {

            try {
                JSONObject productData = new JSONObject(getIntent().getStringExtra("productDetails"));
                TextView title = findViewById(R.id.orderDetailsTitle),
                        stockDetails = findViewById(R.id.productDescriptionCardStockDetails),
                        description = findViewById(R.id.productDescriptionCardDescription),
                        shippingFrom = findViewById(R.id.productDescriptionCardOrigin),
                        price = findViewById(R.id.productDescriptionPrice),
                        deliveryFee = findViewById(R.id.productDescriptionDeliveryFee),
                        seller = findViewById(R.id.productDescriptionSellerName);
                ImageView image = findViewById(R.id.productDescriptionImage);
                String availableFlag = productData.getString("available_flag");


                if(productData.has("name"))
                    title.setText(productData.getString("name"));
                if(productData.has("available_flag"))
                    MiscOperations.addStockFlagColorTextView(availableFlag, stockDetails);
                if(productData.has("description"))
                    description.setText(Html.fromHtml("<b>"+description.getText()+"</b><br><br>"+productData.getString("description")));
                if(productData.has("origin_place") && productData.has("country_of_origin"))
                    shippingFrom.setText(Html.fromHtml("<b>"+shippingFrom.getText()+"</b>"+productData.getString("origin_place")+", "+productData.getString("country_of_origin")));
                if(productData.has("price"))
                    price.setText(price.getText()+productData.getString("price"));
                if(productData.has("delivery_fee"))
                    deliveryFee.setText(deliveryFee.getText()+productData.getString("delivery_fee"));
                if(productData.has("seller_name"))
                    seller.setText(Html.fromHtml("<b>Sold by - </b>"+productData.getString("seller_name")));


                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            ImageView imageView = (ImageView) findViewById(R.id.productDescriptionImage);
                            JSONArray images = null;
                            images = new JSONArray(productData.getString("images"));
                            String id = (String)images.get(0);  // only the first image
                            String imagePath = imageUrl + id;
                            if(!imagePath.isEmpty())
                            {
                                Glide.with(ProductDescription.this)
                                        .load(imagePath)
                                        .into(imageView);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        Button buyBtn = findViewById(R.id.productDescriptionBuyBtn), saveBtn = findViewById(R.id.productDescriptionSaveBtn);
                        LinearLayout lBtn= findViewById(R.id.productDescriptionLinearLayoutBtn);
                        if(availableFlag.equalsIgnoreCase("out of stock"))
                        {
                            buyBtn.setText("Notify me");
                            lBtn.removeView(saveBtn);
                            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                            lp.setMargins(40,0,40,0);
                            buyBtn.setLayoutParams(lp);
                            buyBtn.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Toast.makeText(ProductDescription.this, "Will be notified", Toast.LENGTH_SHORT).show();
                                }
                            });
                        }

                        try {
                            JSONObject productData1 = new JSONObject(productData.toString());
                            Iterator<String> ar = productData1.keys();

                            ArrayList<String> keys = new ArrayList<String>();
                            while(ar.hasNext())
                                keys.add(ar.next());

                            ArrayList<String> keys1 = new ArrayList<String>(keys);
                            keys = new ArrayList<String>(formatKeys(keys));

                            TableLayout prodDescLayout = findViewById(R.id.productDescriptionTable);
                            for(int j = 0; j<keys1.size(); j++)
                            {
                                String key = keys1.get(j);

                                // filter
                                if(!key.startsWith("image") && !key.equalsIgnoreCase("quantity") && !key.equalsIgnoreCase("description") && !key.equalsIgnoreCase("available_flag") && !key.equalsIgnoreCase("pid") && !key.equalsIgnoreCase("sid") && !key.equalsIgnoreCase("price"))
                                {
                                    try
                                    {
                                        TableRow row = (TableRow) LayoutInflater.from(ProductDescription.this).inflate(R.layout.row_layout, null);
                                        TextView keyView = row.findViewById(R.id.key), valueView = row.findViewById(R.id.value);
                                        keyView.setText(keys.get(j).substring(0, 1).toUpperCase() + keys.get(j).substring(1));
                                        String val = productData1.getString(key);
                                        if (!val.isEmpty())
                                        {
                                            val = val.substring(0, 1).toUpperCase() + val.substring(1);
                                            valueView.setText(val);
                                        }

                                        prodDescLayout.addView(row);
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }

                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                });


            } catch (JSONException e) {
                e.printStackTrace();
            }

            return null;
        }

        ProgressDialog progressDialog = new ProgressDialog(ProductDescription.this);;

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