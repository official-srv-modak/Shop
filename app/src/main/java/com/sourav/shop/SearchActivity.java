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
import android.widget.ImageButton;
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
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        getSupportActionBar().hide();
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        ImageView backBtn = findViewById(R.id.backBtn);

        username = getIntent().getStringExtra("username");
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

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

                    if(!searchTextBox.getText().toString().isEmpty())
                    {
                        LoadCard ld = new LoadCard();
                        ld.execute(MainActivity.searchUrl, searchTextBox.getText().toString());
                    }
                    else
                    {
                        LinearLayout linearLayout1 = SearchActivity.this.findViewById(R.id.linearLayout1);
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
                if(searchTextBox.getText().toString().isEmpty())
                {
                    TextView queryTitle = findViewById(R.id.queryTitle);
                    queryTitle.setText("Enter something");
                }
            }
        });
    }

    private class LoadCard extends AsyncTask<String, Void, Integer> {
        protected Integer doInBackground(String... urls) {

            JSONObject result = null, resumeData = null, postData = new JSONObject();
            String query = urls[1];
            try {
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
                    TextView queryTitle = findViewById(R.id.queryTitle);

                    if(finalResult != null) {   //json object of search is not null
                        /*resultV.setText("");
                        try {
                            resultV.setText(finalResult.getString("cards"));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }*/

                        LinearLayout linearLayout1 = SearchActivity.this.findViewById(R.id.linearLayout1);
                        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                        linearLayout1.removeAllViews();

                        try {
                            JSONArray show = finalResult.getJSONArray("cards");
                            List<Integer> idList = new ArrayList<Integer>();
                            for(int i = 0; i < show.length(); i++)
                            {
                                queryTitle.setText("Results of \""+query+"\"");
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

                                TextView tv = (TextView) view.findViewById(R.id.showNameSearch), priceTv = (TextView) view.findViewById(R.id.price);
                                CharSequence name = card.getString("name"), price = card.getString("price");
                                if(!name.toString().isEmpty())
                                    tv.setText(name);
                                if(!price.toString().isEmpty())
                                    priceTv.setText(priceTv.getText()+price.toString());

                                view.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        Intent intent = new Intent(SearchActivity.this, ProductDescription.class);
                                        intent.putExtra("productDetails", card.toString());
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

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                    else
                    {
                        // show nothing found
                    }
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