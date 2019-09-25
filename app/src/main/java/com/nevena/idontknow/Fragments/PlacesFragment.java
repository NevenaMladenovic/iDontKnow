package com.nevena.idontknow.Fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.Resource;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.nevena.idontknow.Adapters.CustomListAdapter;
import com.nevena.idontknow.Adapters.PlacesAdapter;
//import com.nevena.idontknow.App.MyApplication;
import com.nevena.idontknow.Models.Place;
import com.nevena.idontknow.R;
import com.wang.avi.AVLoadingIndicatorView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;




public class PlacesFragment extends Fragment
{
    private static final String TAG = PlacesFragment.class.getSimpleName();
//    private static final String URL = "https://api.androidhive.info/json/movies_2017.json";

    private RecyclerView recyclerView;
    private List<Place> placesList;
    private PlacesAdapter mAdapter;
    private LinearLayoutManager linearLayoutManager;

    View loadingHolder;
    AVLoadingIndicatorView avi;

   // private OnFragmentInteractionListener mListener;

    public PlacesFragment()
    {
        // Required empty public constructor
    }


    public static PlacesFragment newInstance(String param1, String param2) {
        PlacesFragment fragment = new PlacesFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_places, container, false);

        recyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);

        loadingHolder =  view.findViewById(R.id.loadingHolder);
        avi =  view.findViewById(R.id.indicator);

        linearLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(linearLayoutManager);
        placesList = new ArrayList<>();
        mAdapter = new PlacesAdapter(getActivity(), placesList);

        //RecyclerView.LayoutManager mLayoutManager = new GridLayoutManager(getActivity(), 3);
        //recyclerView.setLayoutManager(mLayoutManager);
        //recyclerView.addItemDecoration(new GridSpacingItemDecoration(2, dpToPx(8), true));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(mAdapter);
//        recyclerView.setNestedScrollingEnabled(false);



        //fetchStoreItems();


        new ReadJSONFILE().execute();


        return view;
    }

    private void readJSon()
    {
        try
        {
            JSONArray response = new JSONArray(loadJSONFromAsset());
            for (int i = 0; i < response.length(); i++) {


                JSONObject obj = response.getJSONObject(i);
                Place place = new Place();
                place.setName(obj.getString("name"));
                place.setThumbnailUrl(obj.getString("image"));
                place.setRate(((Number) obj.get("rate"))
                        .doubleValue());
                place.setWorkingHours(obj.getString("workingHours"));
                place.setAddress(obj.getString("address"));

                // adding place to places array
                placesList.add(place);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    public String loadJSONFromAsset() {
        String json = null;
        try {
            InputStream is = getContext().getAssets().open("places.json");
            int size = is.available();
            byte[] buffer = new byte[size];
            is.read(buffer);
            is.close();
            json = new String(buffer, "UTF-8");
        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return json;
    }

    private class ReadJSONFILE extends AsyncTask<Void, Void, Void>
    {

        @Override
        protected Void doInBackground(Void... voids)
        {
            readJSon();
            return null;
        }

        @Override
        protected void onPreExecute()
        {
            avi.show();
        }

        @Override
        protected void onPostExecute(Void result)
        {

            new CountDownTimer(1000,1000)
            {
                @Override
                public void onTick(long millisUntilFinished)
                {

                }

                @Override
                public void onFinish()
                {

                    recyclerView.setVerticalScrollbarPosition(placesList.size() - 2);
                }
            }.start();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }


//    private void fetchStoreItems() {
//        JsonArrayRequest request = new JsonArrayRequest(URL,
//                new Response.Listener<JSONArray>() {
//                    @Override
//                    public void onResponse(JSONArray response) {
//                        if (response == null) {
//                            Toast.makeText(getActivity(), "Couldn't fetch the store items! Pleas try again.", Toast.LENGTH_LONG).show();
//                            return;
//                        }
//
//                        List<Place> items = new Gson().fromJson(response.toString(), new TypeToken<List<Place>>() {
//                        }.getType());
//
//                        placesList.clear();
//                        placesList.addAll(items);
//
//                        // refreshing recycler view
//                        mAdapter.notifyDataSetChanged();
//                    }
//                }, new Response.ErrorListener() {
//            @Override
//            public void onErrorResponse(VolleyError error) {
//                // error in getting json
//                Log.e(TAG, "Error: " + error.getMessage());
//                Toast.makeText(getActivity(), "Error: " + error.getMessage(), Toast.LENGTH_SHORT).show();
//            }
//        });
//
//        MyApplication.getInstance().addToRequestQueue(request);
//    }


}
