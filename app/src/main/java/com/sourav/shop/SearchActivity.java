package com.sourav.shop;

import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.material.navigation.NavigationView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class SearchActivity extends AppCompatActivity {

    private static String username = "";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        overridePendingTransition(0, 0);

        //App bar codes
        ImageView menu = findViewById(R.id.searchMenu_btn);
        DrawerLayout drawerLayout = findViewById(R.id.searchDrawerlayout);
        NavigationView navigationView = findViewById(R.id.searchNav_bar);
        MiscOperations.initialiseHeaderMenu(navigationView, menu, drawerLayout, SearchActivity.this);

        ImageView searchBtn = findViewById(R.id.searchSearch_btn);
        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // Search operation
        EditText searchTextBox = findViewById(R.id.queryBox);
        searchTextBox.requestFocus();
        searchTextBox.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

                    if(!searchTextBox.getText().toString().isEmpty())
                    {
                        String query = searchTextBox.getText().toString();
                        LinearLayout linearLayout1 =  findViewById(R.id.searchLinearLayout);
                        linearLayout1.removeAllViews();
                        LoadCard ld = new LoadCard();
                        ld.execute(MainActivity.searchUrl, query);
                        TextView searchTitle = findViewById(R.id.searchTitle);
                        searchTitle.setText("Results of - \""+query+"\"");
                    }
                    else
                    {
                        TextView searchTitle= findViewById(R.id.searchTitle);
                        searchTitle.setText("Enter something");
                        LinearLayout linearLayout1 =  findViewById(R.id.searchLinearLayout);
                        linearLayout1.removeAllViews();
                    }

                }

            }
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // TODO Auto-generated method stub
            }
            @Override
            public void afterTextChanged(Editable s) {
                // TODO Auto-generated method stub

            }
        });

    }

    public static void initialiseActivity(JSONObject outputData, String query, Activity activity) throws JSONException {

    }
    private class LoadCard extends AsyncTask<String, Void, Integer> {
        @SuppressLint("SetTextI18n")
        protected Integer doInBackground(String... data) {

            JSONObject output = null;
            String query = data[1];
            try {
                JSONObject searchDataJson = new JSONObject();
                searchDataJson.put("query", query);
                output = MiscOperations.getDataFromServerPOST(data[0],searchDataJson);

            } catch (JSONException e) {
                e.printStackTrace();
            }
            JSONObject finalResult = output;
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    try {

                        if (finalResult != null) {

                            LinearLayout linearLayout1 =  findViewById(R.id.searchLinearLayout);
                            linearLayout1.removeAllViews();
                            TextView searchTitle = findViewById(R.id.searchTitle);
                            try {
                                JSONArray prod = finalResult.getJSONArray("cards");
                                List<Integer> idList = new ArrayList<Integer>();
                                linearLayout1.removeAllViews();

                                for (int i = 0; i < prod.length(); i++) {
                                    JSONObject card = prod.getJSONObject(i);
                                    View view = LayoutInflater.from(SearchActivity.this).inflate(R.layout.search_card, null);
                                    view.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            Intent productDescritpionActivity = new Intent(SearchActivity.this, ProductDescription.class);
                                            productDescritpionActivity.putExtra("productDetails", card.toString());
                                            startActivity(productDescritpionActivity);
                                        }
                                    });
                                    @SuppressLint({"NewApi", "LocalSuppress"}) int uniqueId = View.generateViewId();
                                    view.setId(uniqueId);
                                    idList.add(uniqueId);

                                    //initialise
                                    ImageView searchImage = view.findViewById(R.id.searchImage);
                                    TextView searchHeadingFromResultView= view.findViewById(R.id.searchHeading), priceFromSearchView = view.findViewById(R.id.searchPrice);

                                    String headingFromResult = card.get("name").toString(), priceFromSearch = card.get("price").toString();

                                    if(!headingFromResult.isEmpty())
                                        searchHeadingFromResultView.setText(headingFromResult);
                                    if(!priceFromSearch.isEmpty())
                                        priceFromSearchView.setText(priceFromSearchView.getText()+priceFromSearch);

                                    JSONArray images = new JSONArray(card.getString("images"));
                                    String id = (String)images.get(0);  // only the first image
                                    String album_art_path = MainActivity.imageUrl + id;
                                    if(!album_art_path.isEmpty())
                                    {
                                        Glide.with(SearchActivity.this)
                                                .load(album_art_path)
                                                .into(searchImage);
                                    }

                                    linearLayout1.addView(view);
                                }
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }



                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            });

            return null;
        }
    }
}