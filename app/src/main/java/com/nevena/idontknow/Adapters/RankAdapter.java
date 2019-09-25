package com.nevena.idontknow.Adapters;

import android.content.Context;
import android.content.SharedPreferences;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.toolbox.NetworkImageView;
import com.bumptech.glide.load.Key;
import com.bumptech.glide.signature.ObjectKey;
import com.nevena.idontknow.GlideApp;
import com.nevena.idontknow.Models.User;
import com.nevena.idontknow.R;

import java.util.List;

import static android.content.Context.MODE_PRIVATE;

public class RankAdapter extends RecyclerView.Adapter<RankAdapter.MyViewHolder>
{
    private Context context;
    private List<User> usersList;
    private int counter;
    private String userID;
    public class MyViewHolder extends RecyclerView.ViewHolder
    {
        public TextView nickname, poens, num;
        public ImageView thumbnail;
        public RelativeLayout rootView;

        public MyViewHolder(View view)
        {
            super(view);
            num = view.findViewById(R.id.num);
            nickname = (TextView) view.findViewById(R.id.nickname);
            poens = (TextView) view.findViewById(R.id.poens);
            thumbnail = view.findViewById(R.id.thumbnail);

            rootView =  view.findViewById(R.id.rootview);

        }
    }


    public RankAdapter(Context context, List<User> usersList, String userID)
    {
        this.context = context;
        this.usersList = usersList;
        this.userID = userID;
        this.counter = 0;
    }

    public void setUsersList(List<User> usersList) {
        this.usersList = usersList;
        notifyDataSetChanged();
    }

    @Override
    public RankAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.user_list_row, parent, false);

        return new RankAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(RankAdapter.MyViewHolder holder, final int position)
    {
        final User user = usersList.get(position);
        int rankPos = holder.getAdapterPosition() + 1;
        holder.num.setText(String.valueOf(rankPos));
        holder.nickname.setText(user.getNickname());
        holder.poens.setText(String.valueOf(user.getPoens()));


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
        return usersList.size();
    }
}
