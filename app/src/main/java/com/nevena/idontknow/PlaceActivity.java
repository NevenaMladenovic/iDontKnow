package com.nevena.idontknow;

import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

public class PlaceActivity extends AppCompatActivity
{

    private Button writeReview, save;



    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place);

        initLayout();
        initListeners();
    }

    private void initLayout()
    {
        writeReview = findViewById(R.id.btn_writeReview);
        save = findViewById(R.id.btn_save);
    }

    private void initListeners()
    {
        writeReview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder mBuilder = new AlertDialog.Builder(PlaceActivity.this);
                View mView = getLayoutInflater().inflate(R.layout.dialog_write_review, null);
                final EditText review =  mView.findViewById(R.id.etxt_review);

                final Spinner rate = mView.findViewById(R.id.spinner);
                String[] items = new String[]{"-select-", "5", "4", "3", "2", "1"};
                ArrayAdapter<String>adapter = new ArrayAdapter<String>(PlaceActivity.this,
                        android.R.layout.simple_spinner_item,items);

                mBuilder.setView(mView);
                final AlertDialog dialog = mBuilder.create();

                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                rate.setAdapter(adapter);
                rate.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView<?> parent, View view,
                                               int position, long id) {
                        Log.v("item", (String) parent.getItemAtPosition(position));
                        if(position > 0){
                            // get spinner value
                        }
                        else{
                            // show toast select gender
                        }
                    }

                    @Override
                    public void onNothingSelected(AdapterView<?> parent) {
                        // TODO Auto-generated method stub
                    }
                });

                save = mView.findViewById(R.id.btn_save);

                save.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view)
                    {
                        //TODO save txt
                        if(!review.getText().toString().isEmpty() || rate.getSelectedItemPosition() != 0)
                        {
                            Toast.makeText(PlaceActivity.this, getString(R.string.error_review), Toast.LENGTH_SHORT).show();
                            dialog.cancel();
                        }
                        else
                        {
                            Toast.makeText(PlaceActivity.this, getString(R.string.success_review), Toast.LENGTH_SHORT).show();
                        }
                    }
                });


                dialog.show();

            }
        });
    }

    @Override
    public void onBackPressed()
    {
        finish();
    }
}
