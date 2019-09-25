package com.nevena.idontknow.Adapters;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.nevena.idontknow.Models.Place;
import com.nevena.idontknow.PlaceActivity;
import com.nevena.idontknow.R;

import java.util.List;

public class ValidPlacesAdapter extends RecyclerView.Adapter<ValidPlacesAdapter.MyViewHolder>
{
    private Context context;
    private List<Place> placesList;
    private AlertDialog dialog;
    public class MyViewHolder extends RecyclerView.ViewHolder
    {
        public TextView name;

        public MyViewHolder(View view)
        {
            super(view);
            name = (TextView) view.findViewById(R.id.name);

        }
    }


    public ValidPlacesAdapter(Context context, List<Place> placesList, AlertDialog dialog)
    {
        this.context = context;
        this.placesList = placesList;
        this.dialog = dialog;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.valid_place_item, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, final int position)
    {
        final Place place = placesList.get(position);
        holder.name.setText(place.getName());

        holder.name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                Intent i = new Intent(context, PlaceActivity.class);
                i.putExtra("name", place.getName());
                i.putExtra("reviewValid", true);
                context.startActivity(i);
                if(dialog != null && dialog.isShowing())
                    dialog.dismiss();
            }
        });

    }


    @Override
    public int getItemCount() {
        return placesList.size();
    }

}
