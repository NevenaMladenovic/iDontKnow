package com.nevena.idontknow.Adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.toolbox.NetworkImageView;
import com.nevena.idontknow.Models.User;
import com.nevena.idontknow.R;

import java.util.List;

public class UsersAdapter extends RecyclerView.Adapter<UsersAdapter.MyViewHolder>
{
    private Context context;
    private List<User> usersList;

    public class MyViewHolder extends RecyclerView.ViewHolder
    {
        public TextView nickname, poens;
        public NetworkImageView thumbnail;
        public RelativeLayout rootView;

        public MyViewHolder(View view)
        {
            super(view);
            nickname = (TextView) view.findViewById(R.id.nickname);
            poens = (TextView) view.findViewById(R.id.poens);
            thumbnail = view.findViewById(R.id.thumbnail);

            rootView =  view.findViewById(R.id.rootview);

        }
    }


    public UsersAdapter(Context context, List<User> usersList)
    {
        this.context = context;
        this.usersList = usersList;
    }

    @Override
    public UsersAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType)
    {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.user_list_row, parent, false);

        return new UsersAdapter.MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(UsersAdapter.MyViewHolder holder, final int position)
    {
        final User user = usersList.get(position);
        holder.nickname.setText(user.getNickname());
        holder.poens.setText(String.valueOf(user.getPoens()));

        //TODO slika
//        GlideApp.with(context)
//                .load(place.getThumbnailUrl())
//                .placeholder(R.drawable.logo)
//                //        .dontAnimate()
//                .thumbnail(0.25f)
//                .into(holder.thumbnail);


//        holder.rootView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view)
//            {
//                Intent i = new Intent(context, ProfileActivity.class);
//                context.startActivity(i);
//            }
//        });

        //TODO slike se ne ucitavaju



        // Reference to an image file in Cloud Storage
        //  StorageReference storageReference = FirebaseStorage.getInstance().getReference("logos");

        // Download directly from StorageReference using Glide
        // (See MyAppGlideModule for Loader registration)
//        Glide.with(context)
//                .load(storageReference)
//                .into(holder.thumbnail);
    }

    @Override
    public int getItemCount() {
        return usersList.size();
    }
}
