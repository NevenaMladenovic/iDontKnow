package com.nevena.idontknow.Adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.nevena.idontknow.MainActivity;
import com.nevena.idontknow.Models.Place;
import com.nevena.idontknow.PlaceActivity;
import com.nevena.idontknow.ProfileActivity;
import com.nevena.idontknow.R;

import java.util.List;

public class PlacesAdapter extends RecyclerView.Adapter<PlacesAdapter.MyViewHolder>
{
    private Context context;
    private List<Place> placesList;

    public class MyViewHolder extends RecyclerView.ViewHolder
    {
        public TextView name, rate, workingHours, address;
        public ImageView thumbnail;
        public RelativeLayout rootView;

        public MyViewHolder(View view)
        {
            super(view);
            name = (TextView) view.findViewById(R.id.name);
            rate = (TextView) view.findViewById(R.id.rate);
            workingHours = (TextView) view.findViewById(R.id.workingHours);
            address = (TextView) view.findViewById(R.id.address);
            thumbnail = (ImageView) view.findViewById(R.id.thumbnail);
            rootView =  view.findViewById(R.id.rootview);

        }
    }


    public PlacesAdapter(Context context, List<Place> placesList)
    {
        this.context = context;
        this.placesList = placesList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.list_row, parent, false);

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
            public void onClick(View view) {
                context.startActivity(new Intent(context, PlaceActivity.class));

            }
        });

        //TODO slike se ne ucitavaju
        Glide.with(context)
                .load(place.getThumbnailUrl())
                .into(holder.thumbnail);
    }

    @Override
    public int getItemCount() {
        return placesList.size();
    }


}