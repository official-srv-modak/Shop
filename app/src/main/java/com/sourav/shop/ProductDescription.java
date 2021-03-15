package com.sourav.shop;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

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

                if(productData.has("name"))
                    title.setText(productData.getString("name"));
                if(productData.has("available_flag"))
                    MiscOperations.addStockFlagColorTextView(productData.getString("available_flag"), stockDetails);
                if(productData.has("description"))
                    description.setText(description.getText()+productData.getString("description"));
                if(productData.has("origin_place") && productData.has("country_of_origin"))
                    shippingFrom.setText(shippingFrom.getText()+productData.getString("origin_place")+", "+productData.getString("country_of_origin"));
                if(productData.has("price"))
                    price.setText(price.getText()+productData.getString("price"));
                if(productData.has("delivery_fee"))
                    deliveryFee.setText(deliveryFee.getText()+productData.getString("delivery_fee"));
                if(productData.has("seller"))
                    seller.setText(seller.getText()+productData.getString("seller"));

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
                                Glide.with(ProductDescription.this).load(imagePath).into(imageView);
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