package com.nevena.idontknow.Adapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.LruCache;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.load.Key;
import com.bumptech.glide.signature.ObjectKey;
import com.nevena.idontknow.GlideApp;
import com.nevena.idontknow.Models.Review;
import com.nevena.idontknow.Models.User;
import com.nevena.idontknow.R;

import org.osmdroid.api.IMapView;

import java.io.File;
import java.util.List;

import static android.content.Context.MODE_PRIVATE;

public class ReviewsAdapter extends RecyclerView.Adapter<ReviewsAdapter.MyViewHolder> {
    private Context context;
    private List<Review> reviewsList;
    private List<User> usersList;
    private String userID;
    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView nickname, rate, comment;
        public ImageView thumbnail;
        public RelativeLayout rootView;

        public MyViewHolder(View view) {
            super(view);
            nickname = (TextView) view.findViewById(R.id.nickname);
            rate = (TextView) view.findViewById(R.id.rate);
            comment = view.findViewById(R.id.comment);
            thumbnail = view.findViewById(R.id.thumbnail);
            rootView = view.findViewById(R.id.rootview);

        }
    }

    public void setLists(List<Review> reviewsList, List<User> usersList)
    {
        this.reviewsList = reviewsList;
        this.usersList = usersList;

        notifyDataSetChanged();
    }

    public ReviewsAdapter(Context context, List<Review> reviewsList, List<User> usersList, String userID)
    {
        this.context = context;
        this.reviewsList = reviewsList;
        this.usersList = usersList;
        this.userID = userID;
    }

    @Override
    public ReviewsAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.review_list_row, parent, false);

        return new ReviewsAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ReviewsAdapter.MyViewHolder holder, final int position) {
        final Review review = reviewsList.get(position);

        final User user = usersList.get(position);

        holder.nickname.setText(user.getNickname());
        holder.rate.setText(String.valueOf(review.getRate()));
        holder.comment.setText(review.getComment());

        if (!readSP().equalsIgnoreCase("") && userID.equalsIgnoreCase(user.getUserID()))
        {
            loadProfile(readSP(), holder.thumbnail);
        }
        else
        {
            loadProfileDefault(holder.thumbnail);
        }

    }

    private String readSP()
    {
        SharedPreferences sharedPreferencesA = context.getSharedPreferences(context.getPackageName() + "Images", MODE_PRIVATE);
        return sharedPreferencesA.getString("ImagePath2", "");
    }
    private void loadProfile(String url, ImageView imageView) {
        Key glideKey = new ObjectKey(new Object());
        GlideApp.with(context).load(url)
                .signature(glideKey)
                .into(imageView);

    }

    private void loadProfileDefault(ImageView imageView) {
        Key glideKey = new ObjectKey(new Object());
        GlideApp.with(context).load(R.drawable.ic_avatar)
                .signature(glideKey)
                .into(imageView);

    }

    @Override
    public int getItemCount() {
        return reviewsList.size();
    }

}
