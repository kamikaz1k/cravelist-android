package com.imperialtechnologies.theeatlist_3;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v4.widget.SimpleCursorAdapter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;

import de.greenrobot.event.EventBus;

public class FragmentMainList extends ListFragment implements LoaderManager.LoaderCallbacks<Cursor>{

    private static final String TAG = "FragmentMainList";

    //Loader References
    private LoaderManager.LoaderCallbacks<Cursor> mCallbacks;
    private FoodListLoader mainListLoader;
    private SimpleCursorAdapter mainListAdapter;

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        //set list adapter
        mainListAdapter = new SimpleCursorAdapter(getActivity().getApplicationContext(),R.layout.food_item_listview, null,
                new String[] { "_id","foodItemName", "foodItemLocation"},
                new int[] {R.id.foodId, R.id.foodItemNameTextView, R.id.foodItemLocationTextView},0);

        getListView().setAdapter(mainListAdapter);

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //Start loader
        mCallbacks = this;
        getActivity().getSupportLoaderManager().initLoader(0, null, mCallbacks);
        getActivity().getSupportLoaderManager().enableDebugLogging(true);

        //register for events
        EventBus.getDefault().register(this);

    }

    @Override
    public void onDestroy() {

        //Unregister for events
        EventBus.getDefault().unregister(this);

        //TODO - do I need to destroy/Close loaders

        super.onDestroy();
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){

        View rootView = inflater.inflate(R.layout.activity_main_fragment_foodlist, null);

        return rootView;

    }

    public void onEvent(UpdateFoodListEvent event){

        //If the foodlistupdate event is triggered reload the loader
        Log.d(TAG, "Event received");
        mainListLoader.onContentChanged();

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
        //getActivity().startActivityForResult(theIntent, MainActivity.EDIT_FOOD_ITEM);
        startActivityForResult(theIntent, MainActivity.EDIT_FOOD_ITEM);

        getActivity().overridePendingTransition(R.animator.push_left_in,R.animator.fade_under);

    }

    /*@Override
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
        mainListLoader = new FoodListLoader(getActivity().getApplicationContext());
        Log.d(TAG, "LoaderID: " + Integer.toString(id) + " eatenListLoader FoodLoader Created");
        return mainListLoader;

    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {

        Log.d(TAG, "mainListAdapter about to swap");
        mainListAdapter.swapCursor(data);

        Log.i(TAG+"-Loadr", "mainListAdapter cursor swapped");
        Log.d(TAG, "loaderID: " + Integer.toString(loader.getId()));
        if (data != null) {
            Log.d(TAG+"-Loadr", "Rows update: " + Integer.toString(data.getCount()));
        } else {
            Log.d(TAG+"-Loadr", "Rows update: " + "Cursor was null");
        }

    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

        mainListAdapter.swapCursor(null);

    }

    /*
    public void populateListFragment(){

        foodListAdapter = new SimpleCursorAdapter(this,R.layout.food_item_listview, null,
                new String[] { "_id","foodItemName", "foodItemLocation"},
                new int[] {R.id.foodId, R.id.foodItemNameTextView, R.id.foodItemLocationTextView},0);

        searchFilter = new FilterQueryProvider() {

            @Override
            public Cursor runQuery(CharSequence constraint) {

                return dbTools.getCursorAllFilteredFoodItems(constraint.toString());

            }
        };

        foodListAdapter.setFilterQueryProvider(searchFilter);

        Log.d(TAG, "Getting fragment for list adapter");
        listTabFragment.setListAdapter(foodListAdapter);

        Log.d(TAG, "tagOfPosition 0: " + pagerAdapter.getTagOfPosition(0));
        if (!pagerAdapter.getTagOfPosition(0).equals("Empty String")){
            ListFragment fragment = (ListFragment) getSupportFragmentManager().findFragmentByTag(pagerAdapter.getTagOfPosition(0));
            fragment.setListAdapter(foodListAdapter);
            Log.d(TAG,"Fragment recovered and adapter set");
        }

        Log.d(TAG, "Adapter setup for main list");

    }
    */

}