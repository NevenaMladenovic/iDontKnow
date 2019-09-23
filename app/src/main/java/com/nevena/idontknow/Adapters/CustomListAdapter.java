package com.nevena.idontknow.Adapters;

import com.nevena.idontknow.R;
import com.nevena.idontknow.Controllers.AppController;
import com.nevena.idontknow.Models.Place;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;

public class CustomListAdapter extends BaseAdapter 
{
    private Activity activity;
    private LayoutInflater inflater;
    private List<Place> placeItems;
    ImageLoader imageLoader = AppController.getInstance().getImageLoader();

    public CustomListAdapter(Activity activity, List<Place> placeItems) {
        this.activity = activity;
        this.placeItems = placeItems;
    }

    @Override
    public int getCount() {
        return placeItems.size();
    }

    @Override
    public Object getItem(int location) {
        return placeItems.get(location);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public void updateList(List<Place> placeItems)
    {
        this.placeItems = placeItems;
        notifyDataSetChanged();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (inflater == null)
            inflater = (LayoutInflater) activity
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (convertView == null)
            convertView = inflater.inflate(R.layout.place_list_row, null);

        if (imageLoader == null)
            imageLoader = AppController.getInstance().getImageLoader();
        NetworkImageView thumbNail = (NetworkImageView) convertView
                .findViewById(R.id.thumbnail);
        TextView name = (TextView) convertView.findViewById(R.id.name);
        TextView rating = (TextView) convertView.findViewById(R.id.rate);
        TextView workingHours = (TextView) convertView.findViewById(R.id.workingHours);
        TextView address = (TextView) convertView.findViewById(R.id.address);

        // getting Place data for the row
        Place m = placeItems.get(position);

        // thumbnail image
        thumbNail.setImageUrl(m.getThumbnailUrl(), imageLoader);
//        GlideApp.with(activity).applyDefaultRequestOptions(new RequestOptions()).load(m.getThumbnailUrl()).thumbnail(0.4f).into(thumbNail);

        // name
        name.setText(m.getName());

        // stars
        rating.setText( String.valueOf(m.getRate()));

        // working hours
        workingHours.setText(String.valueOf(m.getWorkingHours()));

        // address
        address.setText(String.valueOf(m.getAddress()));


        return convertView;
    }

}
