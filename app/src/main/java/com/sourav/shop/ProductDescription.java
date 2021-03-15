package com.sourav.shop;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import static com.sourav.shop.MainActivity.imageUrl;

public class ProductDescription extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_description);
        getSupportActionBar().hide();

        LoadCard ld = new LoadCard();
        ld.execute();
    }

    private class LoadCard extends AsyncTask<String, Void, Integer> {
        @SuppressLint("SetTextI18n")
        protected Integer doInBackground(String... urls) {

            try {
                JSONObject productData = new JSONObject(getIntent().getStringExtra("productDetails"));
                TextView title = findViewById(R.id.productDescriptionTitle),
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