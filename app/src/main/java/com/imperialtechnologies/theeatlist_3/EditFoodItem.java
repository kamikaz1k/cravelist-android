package com.imperialtechnologies.theeatlist_3;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.widget.Toast;

public class EditFoodItem extends ActionBarActivity {

    public static final String TAG = "EditFoodItem";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Toast.makeText(this,TAG,Toast.LENGTH_SHORT).show();
        finish();
    }
}
