package com.nevena.idontknow.Adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
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
import com.nevena.idontknow.Models.Place;
import com.nevena.idontknow.PlaceActivity;
import com.nevena.idontknow.R;

import java.util.List;

public class PlacesAdapter extends RecyclerView.Adapter<PlacesAdapter.MyViewHolder>
{
    private Context context;
    private List<Place> placesList;
    private ImageLoader mImageLoader;

    public class MyViewHolder extends RecyclerView.ViewHolder
    {
        public TextView name, rate, workingHours, address;
        public NetworkImageView thumbnail;
        public RelativeLayout rootView;

        public MyViewHolder(View view)
        {
            super(view);
            name = (TextView) view.findViewById(R.id.name);
            rate = (TextView) view.findViewById(R.id.rate);
            workingHours = (TextView) view.findViewById(R.id.workingHours);
            address = (TextView) view.findViewById(R.id.address);
            thumbnail = view.findViewById(R.id.thumbnail);
            rootView =  view.findViewById(R.id.rootview);

        }
    }


    public PlacesAdapter(Context context, List<Place> placesList)
    {
        this.context = context;
        this.placesList = placesList;

        RequestQueue mRequestQueue = Volley.newRequestQueue(context);
        mImageLoader = new ImageLoader(mRequestQueue, new ImageLoader.ImageCache() {
            private final LruCache<String, Bitmap> mCache = new LruCache<String, Bitmap>(10);
            public void putBitmap(String url, Bitmap bitmap) {
                mCache.put(url, bitmap);
            }
            public Bitmap getBitmap(String url) {
                return mCache.get(url);
            }
        });
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.place_list_row, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position)
    {
        final Place place = placesList.get(position);
        holder.name.setText(place.getName());
        holder.rate.setText(String.valueOf(place.getRate()));
        holder.workingHours.setText(place.getWorkingHours());
        holder.address.setText(place.getAddress());

        holder.rootView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                Intent i = new Intent(context, PlaceActivity.class);
                i.putExtra("name", place.getName());
                i.putExtra("reviewValid", false);
                context.startActivity(i);
            }
        });

        holder.thumbnail.setImageUrl(place.getThumbnailUrl(),mImageLoader);

    }

    @Override
    public int getItemCount() {
        return placesList.size();
    }


}