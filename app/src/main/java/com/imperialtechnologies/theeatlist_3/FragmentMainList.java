package com.imperialtechnologies.theeatlist_3;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.support.v7.internal.widget.AdapterViewCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

public class FragmentMainList extends ListFragment implements AdapterViewCompat.OnItemClickListener{

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){

        View rootView = inflater.inflate(R.layout.activity_main_fragment_foodlist, null);

        return rootView;

    }

    public void onActivityResult(int requestCode, int resultCode, Intent data){

        MainActivity mainActivity = (MainActivity) getActivity();
        mainActivity.refreshFoodList();

    }

    @Override
    public void onItemClick(AdapterViewCompat<?> adapterViewCompat, View view, int i, long l) {

        // When an item is clicked get the TextView
        TextView foodId = (TextView) view.findViewById(R.id.foodId);

        // Convert that contactId into a String
        String foodIdValue = foodId.getText().toString();

        // Signals an intention to do something
        // getApplication() returns the application that owns
        // this activity
        Intent theIntent = new Intent(getActivity(), EditFoodItem.class);

        // Put additional data in for EditFoodItem to use
        theIntent.putExtra("foodId", foodIdValue);

        //Set list selector background
        //Drawable listSelectorBackground = getResources().getDrawable(R.drawable.list_selector_background);
        //view.setBackground(listSelectorBackground);

        startActivityForResult(theIntent, MainActivity.EDIT_FOOD_ITEM);

        getActivity().overridePendingTransition(R.animator.push_left_in,R.animator.fade_under);

    }
}