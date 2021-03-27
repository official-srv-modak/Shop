package com.sourav.shop;

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

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

import com.bumptech.glide.Glide;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class SearchActivity extends AppCompatActivity {

    private static String username = "";
    JSONObject userInfo;
    int numberOfCardLoaded = 0;
    boolean cardLoaded = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        getSupportActionBar().hide();
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        ImageView backBtn = findViewById(R.id.backBtn);

        username = getIntent().getStringExtra("username");
        try {
            userInfo = new JSONObject(getIntent().getStringExtra("user_data"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent returnIntent = new Intent();
                setResult(Activity.RESULT_OK, returnIntent);
                finish();
            }
        });

        EditText searchTextBox = findViewById(R.id.searchTextBox);
        searchTextBox.requestFocus();
        searchTextBox.addTextChangedListener(new TextWatcher() {
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                String query = searchTextBox.getText().toString();
                TextView queryTitle = findViewById(R.id.queryTitle);
                queryTitle.setText("Loading...");
                LinearLayout linearLayout1 = SearchActivity.this.findViewById(R.id.linearLayout1);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

                    if(!query.isEmpty())
                    {
                        LoadCard ld = new LoadCard();
                        ld.execute(MainActivity.searchUrl, query);
                    }
                    else
                    {
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
                TextView queryTitle = findViewById(R.id.queryTitle);
                if(searchTextBox.getText().toString().isEmpty())
                    queryTitle.setText("Enter something");


            }
        });
    }

    private class LoadCard extends AsyncTask<String, Void, Integer> {
        protected Integer doInBackground(String... urls) {

            JSONObject result = null, resumeData = null, postData = new JSONObject();
            String query = urls[1];
            try {
                postData.put("session_id", userInfo.get("session_id"));
                postData.put("parameter", "product");
                postData.put("query", query);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                result = MiscOperations.getDataFromServerPOST(urls[0], postData);
            }
            JSONObject finalResult = result;
            runOnUiThread(new Runnable() {

                @Override
                public void run() {
                    // Stuff that updates the UI

                    if(finalResult != null) {   //json object of search is not null

                        TextView queryTitle = findViewById(R.id.queryTitle);
                        queryTitle.setText("Results of \""+query+"\"");

                        LinearLayout linearLayout1 = SearchActivity.this.findViewById(R.id.linearLayout1);
                        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                        linearLayout1.removeAllViews();

                        try {
                            JSONArray show = finalResult.getJSONArray("cards");
                            numberOfCardLoaded = show.length();
                            List<Integer> idList = new ArrayList<Integer>();
                            for(int i = 0; i < show.length(); i++)
                            {
                                JSONObject card = show.getJSONObject(i);
                                View view = LayoutInflater.from(SearchActivity.this).inflate(R.layout.search_result_elements, null);
                                @SuppressLint({"NewApi", "LocalSuppress"}) int uniqueId = View.generateViewId();
                                view.setId(uniqueId);
                                idList.add(uniqueId);

                                ImageView imageView = (ImageView) view.findViewById(R.id.image);
                                String album_art_path = (String) card.getJSONArray("images").get(0);
                                album_art_path = MainActivity.imageUrl+album_art_path;
                                if(!album_art_path.isEmpty())
                                    Glide.with(SearchActivity.this).load(album_art_path).into(imageView);

                                TextView tv = (TextView) view.findViewById(R.id.showNameSearch), priceTv = (TextView) view.findViewById(R.id.price), stockInfoTv = (TextView) view.findViewById(R.id.searchStockInfo);
                                CharSequence name = card.getString("name"), price = card.getString("price"), availableFlag = card.getString("available_flag");
                                if(!name.toString().isEmpty())
                                    tv.setText(name);
                                if(!price.toString().isEmpty())
                                    priceTv.setText(priceTv.getText()+price.toString());

                                if(!availableFlag.toString().isEmpty())
                                    MiscOperations.addStockFlagColorTextView(availableFlag.toString(), stockInfoTv);

                                view.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        Intent intent = new Intent(SearchActivity.this, ProductDescription.class);
                                        intent.putExtra("productDetails", card.toString());
                                        intent.putExtra("user_data", userInfo.toString());
                                        SearchActivity.this.startActivityForResult(intent, 1);
                                    }
                                });

                                if(i != show.length()-1)
                                {
                                    lp.setMargins(40, 40, 40, 40);
                                    view.setLayoutParams(lp);
                                }
                                else
                                {
                                    lp.setMargins(40, 40, 40, 0);
                                    view.setLayoutParams(lp);
                                }
                                linearLayout1.addView(view);
                            }
                            if(show.length() == 0 && !query.isEmpty())
                            {
                                queryTitle.setText("Sorry, no results");
                            }
                            else if(show.length() == 0)
                            {
                                queryTitle.setText("Enter something");
                            }

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                    else
                    {
                        TextView queryTitle = findViewById(R.id.queryTitle);
                        queryTitle.setText("Sorry, no results");
                    }
                    cardLoaded = true;
                }
            });


            return 0;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

    }
}