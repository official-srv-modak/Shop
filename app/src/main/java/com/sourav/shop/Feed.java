package com.sourav.shop;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.target.Target;
import com.bumptech.glide.signature.ObjectKey;
import com.google.android.material.transition.Hold;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.nio.file.FileAlreadyExistsException;
import java.util.ArrayList;
import java.util.List;

public class Feed extends RecyclerView.Adapter<Feed.FeedViewHolder> {

   // JSONObject card;
    ArrayList<String> title = new ArrayList<String>(), origin = new ArrayList<String>(), price = new ArrayList<String>(), imageUrl = new ArrayList<String>(), availableFlag = new ArrayList<String>(), productDet = new ArrayList<String>(), seller = new ArrayList<String>();
    Context context;
    String userInfo, productDt;

    public Feed(Context context, ArrayList<String> title, ArrayList<String> seller, ArrayList<String> origin, ArrayList<String> price, ArrayList<String> imageUrl, ArrayList<String> availableFlag, String userInfo, ArrayList<String> productDet)
    {
        this.context = context;
        this.title = title;
        this.origin = origin;
        this.price = price;
        this.availableFlag = availableFlag;
        this.imageUrl = imageUrl;
        this.userInfo = userInfo;
        this.productDet = productDet;
        this.seller = seller;
    }

    /*public Feed(Context context, JSONObject card, String userInfo)
    {
        this.card = card;
        this.context = context;
        this.userInfo = userInfo;
    }*/


    @NonNull
    @Override
    public Feed.FeedViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        LayoutInflater layoutInflater = LayoutInflater.from(context);
        // We have inflated it
        View view = layoutInflater.inflate(R.layout.new_product_card, parent, false);
        return new FeedViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull Feed.FeedViewHolder holder, int position) {

        Glide.with(context)
                .load(imageUrl.get(position))
                .override(holder.imageView.getWidth(), holder.imageView.getHeight())
                //.override(Target.SIZE_ORIGINAL)
                .timeout(60000)
                .into(holder.imageView);
        holder.price.setText("");
        holder.title.setText("");
        holder.origin.setText("");
        holder.stockInfo.setText("");

        holder.price.setText(holder.price.getText() + price.get(position));
        holder.title.setText(title.get(position));
        holder.origin.setText(holder.origin.getText()+ origin.get(position));
        holder.seller.setText(seller.get(position));
        MiscOperations.addStockFlagColorTextView(availableFlag.get(position), holder.stockInfo);
        holder.card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent productDescritpionActivity = new Intent(context, ProductDescription.class);
                productDescritpionActivity.putExtra("productDetails", productDet.get(position));
                productDescritpionActivity.putExtra("user_data", userInfo);
                context.startActivity(productDescritpionActivity);
            }
        });

    }

    @Override
    public int getItemCount() {
        return title.size();
    }

    public class FeedViewHolder extends RecyclerView.ViewHolder{

        ImageView imageView;
        CardView card;
        TextView price, title, origin, stockInfo, seller;

        public FeedViewHolder(@NonNull View view)
        {
            super(view);

                card = view.findViewById(R.id.card);
                // Images
                imageView = (ImageView) view.findViewById(R.id.image);

                // Text price
                price = view.findViewById(R.id.price);

                // Text title
                title = view.findViewById(R.id.productName);

                origin = view.findViewById(R.id.origin);

                stockInfo = view.findViewById(R.id.stock_info);

                seller = view.findViewById(R.id.sellerName);

        }

    }

}
