package com.imperialtechnologies.theeatlist_3;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v7.internal.widget.AdapterViewCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

public class FragmentEatenList extends ListFragment implements LoaderManager.LoaderCallbacks<Cursor>{
//AdapterViewCompat.OnItemClickListener,
    private static final String TAG = "FragmentEatenList";

    //Loader References
    private LoaderManager.LoaderCallbacks<Cursor> mCallbacks;
    private FoodListLoader eatenListLoader;
    private SimpleCursorAdapter eatenListAdapter;


    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //set list adapter
        eatenListAdapter = new SimpleCursorAdapter(getActivity().getApplicationContext(),R.layout.food_item_listview, null,
                new String[] { "_id","foodItemName", "foodItemLocation"},
                new int[] {R.id.foodId, R.id.foodItemNameTextView, R.id.foodItemLocationTextView},0);

        getListView().setAdapter(eatenListAdapter);

    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){

        View rootView = inflater.inflate(R.layout.activity_main_fragment_foodlist, null);

        return rootView;

    }

    public void onActivityResult(int requestCode, int resultCode, Intent data){

        //TODO - refresh list adapter?
        Log.d(TAG,"OnActivityResult method triggered");
        eatenListLoader.onContentChanged();
//        MainActivity mainActivity = (MainActivity) getActivity();
//        mainActivity.refreshFoodList();

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Start loader
        mCallbacks = this;
        getActivity().getSupportLoaderManager().initLoader(1, null, mCallbacks);

        getActivity().getSupportLoaderManager().enableDebugLogging(true);

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
        startActivityForResult(theIntent, MainActivity.EDIT_FOOD_ITEM);

        getActivity().overridePendingTransition(R.animator.push_left_in,R.animator.fade_under);

    }
/*

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
*/

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        //id of loader to create
        //case EATEN_LIST:
        eatenListLoader = new FoodListLoader(getActivity().getApplicationContext());
        Log.d(TAG, "LoaderID: " + Integer.toString(id) + " eatenListLoader FoodLoader Created");
        return eatenListLoader;

    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

        FoodListCursorWrapper eatenCursorWrapper;

        Log.d(TAG, "eatenListAdapter about to swap");
        if (data != null) {
            eatenCursorWrapper = new FoodListCursorWrapper(data, DBTools.EATEN, DBTools.EATEN_COL);
        } else {
            eatenCursorWrapper = null;
        }

        eatenListAdapter.swapCursor(eatenCursorWrapper);

        Log.i(TAG+"-Loadr", "eatenListAdapter cursor swapped");
        Log.d(TAG, "loaderID: " + Integer.toString(loader.getId()));
        if (data != null) {
            Log.d(TAG+"-Loadr", "Rows update: " + Integer.toString(data.getCount()));
        } else {
            Log.d(TAG+"-Loadr", "Rows update: " + "Cursor was null");
        }

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

        eatenListAdapter.swapCursor(null);

    }
}