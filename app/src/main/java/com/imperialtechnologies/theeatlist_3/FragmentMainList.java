package com.imperialtechnologies.theeatlist_3;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.support.v7.internal.widget.AdapterViewCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

public class FragmentMainList extends ListFragment implements AdapterViewCompat.OnItemClickListener{

    private static final String TAG = "FragmentMainList";

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){

        View rootView = inflater.inflate(R.layout.activity_main_fragment_foodlist, null);

        return rootView;

    }

    public void onActivityResult(int requestCode, int resultCode, Intent data){

        Log.d(TAG,"OnActivityResult method triggered");
        MainActivity mainActivity = (MainActivity) getActivity();
        mainActivity.refreshFoodList();

    }

    /**
     * This method will be called when an item in the list is selected.
     * Subclasses should override. Subclasses can call
     * getListView().getItemAtPosition(position) if they need to access the
     * data associated with the selected item.
     *
     * @param l        The ListView where the click happened
     * @param view        The view that was clicked within the ListView
     * @param position The position of the view in the list
     * @param id       The row id of the item that was clicked
     */
    @Override
    public void onListItemClick(ListView l, View view, int position, long id) {

        Log.d(TAG,"OnItemClick triggered");

        // When an item is clicked get the TextView
        TextView foodId = (TextView) view.findViewById(R.id.foodId);

        // Convert that contactId into a String
        String foodIdValue = foodId.getText().toString();
        Log.d(TAG,"foodIdValue: " + foodIdValue);
        Log.d(TAG,"Position int from adapter: " + Integer.toString(position));
        Log.d(TAG,"Row id from adapter: " + Long.toString(id));

        Intent theIntent = new Intent(getActivity(), EditFoodItem.class);

        // Put additional data in for EditFoodItem to use
        theIntent.putExtra("foodId", foodIdValue);

        //Set list selector background
        //Drawable listSelectorBackground = getResources().getDrawable(R.drawable.custom_bg, null);
        //view.setBackground(listSelectorBackground);

        Log.d(TAG,"Sending intent to EditFoodItem Activity");
        Log.d(TAG,"RequestCode: " + Integer.toString(MainActivity.EDIT_FOOD_ITEM));
        getActivity().startActivityForResult(theIntent, MainActivity.EDIT_FOOD_ITEM);

        getActivity().overridePendingTransition(R.animator.push_left_in,R.animator.fade_under);

    }

    @Override
    public void onItemClick(AdapterViewCompat<?> adapterViewCompat, View view, int position, long id) {

        Log.d(TAG,"OnItemClick triggered");
        // When an item is clicked get the TextView

        TextView foodId = (TextView) view.findViewById(R.id.foodId);

        // Convert that contactId into a String
        String foodIdValue = foodId.getText().toString();
        Log.d(TAG,"foodIdValue: " + foodIdValue);
        Log.d(TAG,"Position int from adapter: " + Integer.toString(position));
        Log.d(TAG,"Row id from adapter: " + Long.toString(id));

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