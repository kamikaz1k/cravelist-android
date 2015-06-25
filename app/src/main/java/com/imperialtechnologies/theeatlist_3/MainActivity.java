package com.imperialtechnologies.theeatlist_3;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.ListFragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.SimpleCursorAdapter;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.FilterQueryProvider;
import android.widget.Toast;

import com.imperialtechnologies.theeatlist_3.stab.SlidingTabLayout;

import java.util.ArrayList;
import java.util.HashMap;


public class MainActivity extends ActionBarActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    public final static String ACTIVE_TAB = "com.imperialtechnologies.theeatlist_3.MainActivity.ACTIVE_TAB";
    public final static String TAG = "MainActivity";

    private static ListFragment listTabFragment = new FragmentMainList();
    private static ListFragment eatenTabFragment = new FragmentMainList();
    private static ListFragment friendsTabFragment = new FragmentFriendsList();

    //
    MyFragmentPagerAdapter pagerAdapter;

    // The Intent is used to issue an operation should be performed
    public final static int NEW_FOOD_ITEM = 1;
    public final static int EDIT_FOOD_ITEM = 2;

    // The object that allows me to manipulate the database
    DBTools dbTools;

    //Loader references
    private final static int FULL_FOOD_LIST = 0;
    private final static int EATEN_LIST = 1;
    private final static Integer FOOD_DETAILS = 2;
    private LoaderManager.LoaderCallbacks<Cursor> mCallbacks;
    private FoodListLoader foodListLoader;
    private FoodListLoader eatenListLoader;
    SimpleCursorAdapter foodListAdapter;
    SimpleCursorAdapter eatenListAdapter;
    FoodListCursorWrapper eatenCursorWrapper;
    FilterQueryProvider searchFilter;
    FilterQueryProvider eatenFilter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initializeFragments();

        initializeToolbarAndTabs();

        populateListFragment();
        populateEatenListFragment();

        //Start the Async Loaders
        mCallbacks = this;
        dbTools = new DBTools(this);

        getSupportLoaderManager().initLoader(FULL_FOOD_LIST, null, mCallbacks);
        getSupportLoaderManager().initLoader(EATEN_LIST, null, mCallbacks);

        getSupportLoaderManager().enableDebugLogging(true);

    }

    private void initializeFragments(){

        listTabFragment = new FragmentMainList();
        eatenTabFragment = new FragmentMainList();
        friendsTabFragment = new FragmentFriendsList();

    }

    private void initializeToolbarAndTabs(){

        Toolbar toolbar = (Toolbar) findViewById(R.id.app_bar);
        toolbar.setTitleTextColor(Color.WHITE);

        setSupportActionBar(toolbar);

        ArrayList<Fragment> fragments = new ArrayList<>();
        fragments.add(listTabFragment);
        fragments.add(eatenTabFragment);
        fragments.add(friendsTabFragment);

        final ViewPager viewPager = (ViewPager) findViewById(R.id.viewPager);
        viewPager.setOffscreenPageLimit(2);
        // default is 1, and was causing failure on the overridden destroyItem method in MyFragmentPagerAdapter
        // curious however, because this was not an issue till I started checking for tags in the adapter setting methods

        viewPager.setAdapter(new MyFragmentPagerAdapter(getSupportFragmentManager(), this, fragments));

        final SlidingTabLayout mTabs = (SlidingTabLayout) findViewById(R.id.slidingTabs);

        mTabs.SELECTED_TITLE_COLOR = Color.WHITE;
        mTabs.UNSELECTED_TITLE_COLOR = Color.parseColor("#1B5E20");

        mTabs.setBackgroundColor(getResources().getColor(R.color.main_view_banner));
        mTabs.setSelectedIndicatorColors(getResources().getColor(R.color.main_view_accent));

        mTabs.setDistributeEvenly(true);
        mTabs.setViewPager(viewPager);

        pagerAdapter = (MyFragmentPagerAdapter) viewPager.getAdapter();

    }

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

    public void populateEatenListFragment(){

        eatenListAdapter = new SimpleCursorAdapter(this,R.layout.food_item_listview, null,
                new String[] { "_id","foodItemName", "foodItemLocation"},
                new int[] {R.id.foodId, R.id.foodItemNameTextView, R.id.foodItemLocationTextView},0);

        searchFilter = new FilterQueryProvider() {

            @Override
            public Cursor runQuery(CharSequence constraint) {

                // TODO - Need a dbTools method for eaten filtered foods
                return dbTools.getCursorAllFilteredFoodItems(constraint.toString());

            }
        };

        eatenListAdapter.setFilterQueryProvider(searchFilter);

        Log.d(TAG, "Getting fragment for list adapter");
        eatenTabFragment.setListAdapter(eatenListAdapter);

        // Check that if there is a fragment to recover, (i.e. from orientation changes),
        // that it receives the adapter
        Log.d(TAG, "tagOfPosition 1: " + pagerAdapter.getTagOfPosition(1));
        if (!pagerAdapter.getTagOfPosition(1).equals("Empty String")){ // Empty String is default fail response
            ListFragment fragment = (ListFragment) getSupportFragmentManager().findFragmentByTag(pagerAdapter.getTagOfPosition(1));
            fragment.setListAdapter(eatenListAdapter);
            Log.d(TAG,"Fragment recovered and adapter set");
        }

        Log.d(TAG, "Adapter setup for eaten list");

    }

    public void onActivityResult(int requestCode, int resultCode, Intent data){
        // request code is the code sent out from startActivityForResult
        // result code is the first argument in setResult() from the returning intent

        Log.d(TAG, "requestCode: " + Integer.toString(requestCode));
        Log.d(TAG, "EDIT_FOOD_ITEM: " + Integer.toString(EDIT_FOOD_ITEM));
        Log.d(TAG, "NEW_FOOD_ITEM: " + Integer.toString(NEW_FOOD_ITEM));

        switch (requestCode) {

            case NEW_FOOD_ITEM:
                Log.d(TAG,"Returned from NEW FOOD");
                refreshFoodList();
                break;

            case EDIT_FOOD_ITEM:
                Log.d(TAG,"Returned from EDIT FOOD");
                refreshFoodList();
                break;
        }

    }

    @Override
    protected void onDestroy() {

        Log.i(TAG,"calling onDestroy()");
        getSupportLoaderManager().destroyLoader(FULL_FOOD_LIST);
        getSupportLoaderManager().destroyLoader(EATEN_LIST);
        dbTools.close();

        super.onDestroy();
    }

    public FoodListLoader onCreateLoader(int id, Bundle args) {

        switch (id) {

            case FULL_FOOD_LIST:
                foodListLoader = new FoodListLoader(getApplicationContext());
                Log.d(TAG, "LoaderID: " + Integer.toString(id) + " foodListLoader FoodLoader Created");
                return foodListLoader;

            case EATEN_LIST:
                eatenListLoader = new FoodListLoader(getApplicationContext());
                Log.d(TAG, "LoaderID: " + Integer.toString(id) + " eatenListLoader FoodLoader Created");
                return eatenListLoader;
            default:
                Log.wtf(TAG, "Got unhandled LoaderID: " + Integer.toString(id));
                return null;
        }

    }

    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        //Once data is ready, swap in the new data

        switch (loader.getId()){

            case FULL_FOOD_LIST:
                Log.d(TAG, "foodListAdapter about to swap");
                foodListAdapter.swapCursor(data);
                Log.i("Main-LoadManag", "foodListAdapter cursor swapped");
                Log.d("MainActivity", "loaderID: " + Integer.toString(loader.getId()));
                if (data != null) {
                    Log.d("Main-LoadManag", "Rows update: " + Integer.toString(data.getCount()));
                } else {
                    Log.d("Main-LoadManag", "Rows update: " + "Cursor was null");
                }
                break;

            case EATEN_LIST:
                Log.d(TAG, "eatenListAdapter about to swap");
                if (data != null) {
                    eatenCursorWrapper = new FoodListCursorWrapper(data, dbTools.EATEN, dbTools.EATEN_COL);
                } else {
                    eatenCursorWrapper = null;
                }

                eatenListAdapter.swapCursor(eatenCursorWrapper);

                Log.i("Main-LoadManag", "eatenListAdapter cursor swapped");
                Log.d("MainActivity", "loaderID: " + Integer.toString(loader.getId()));
                if (data != null) {
                    Log.d("Main-LoadManag", "Rows update: " + Integer.toString(data.getCount()));
                } else {
                    Log.d("Main-LoadManag", "Rows update: " + "Cursor was null");
                }
                break;

            default:
                Log.wtf(TAG, "onLoadFinished: Default case triggered unexpectedly");
                break;
        }

    }

    public void onLoaderReset(Loader<Cursor> loader) {
        //Un-map all the information
        foodListAdapter.swapCursor(null);
        eatenListAdapter.swapCursor(null);
    }

    public void refreshFoodList(){

        Log.i(TAG, "Refreshing Food List");

        Log.d(TAG, "Triggering foodListLoader.onContentChanged");
        foodListLoader.onContentChanged();

        Log.d(TAG, "Triggering eatenListLoader.onContentChanged");
        eatenListLoader.onContentChanged();

    }

    public void refreshFoodList(String eaten, String foodId){

        HashMap<String,String> foodDetails = dbTools.getFoodItemDetails(foodId);

        String[] foodItem = {foodDetails.get("_id"),foodDetails.get("foodItemName"),foodDetails.get("foodItemLocation")};
        //"_id","foodItemName", "foodItemLocation"

        //cant foodListAdapter.add(); - not a valid method...

    }

    public void showAddFoodItem(View view) {
        Intent theIntent = new Intent(getApplicationContext(), NewFoodItem.class);

        theIntent.putExtra(ACTIVE_TAB, 0);

        Toast.makeText(getApplicationContext(), "Sending Intent to NewFoodItem", Toast.LENGTH_SHORT).show();
        Log.d("Sending to NewFoodItem", "Active_Tab: " + Integer.toString(0));

        startActivityForResult(theIntent, NEW_FOOD_ITEM);
        overridePendingTransition(R.animator.push_left_in, R.animator.fade_under);

    }

    public void searchMainList(){

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Title");

        // Set up the input
        final EditText input = new EditText(this);
        // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        builder.setView(input);

        // Set up the buttons
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String mText = input.getText().toString();
                Log.d("Search Dialog", "Starting filterMainList");
                filterMainList(mText);
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        builder.show();

    }

    public void filterMainList(String search){
        Log.d(TAG, "setting filter to: " + search);

        foodListAdapter.getFilter().filter(search.toString());

    }

    public void confirmDeleteAllFoodItems(){

        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);

        builder.setTitle("Clear you plate?");

        builder.setPositiveButton("Yes",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface arg0, int arg1) {
                        deleteAllFoodItems();
                    }
                });

        builder.setNegativeButton("Not Yet!",
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface arg0, int arg1) {
                        return;
                    }
                });

        builder.setMessage("This will clear all the food items in your list. Are you sure you want to do that?");

        AlertDialog theAlertDialog = builder.create();
        theAlertDialog.show();

    }

    public void deleteAllFoodItems(){

        Toast.makeText(getApplicationContext(), "Deleted all", Toast.LENGTH_SHORT).show();
        dbTools.clearAllRows();

        refreshFoodList();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        // Handle presses on the action bar items
        switch (item.getItemId()) {

            case R.id.action_search:
                Toast.makeText(getApplicationContext(), "Searching", Toast.LENGTH_SHORT).show();
                searchMainList();
                return true;

            case R.id.action_new_item:
                showAddFoodItem(findViewById(R.id.action_new_item));
                return true;

            case R.id.action_delete_all_items:
                confirmDeleteAllFoodItems();
                return true;

            case R.id.action_main_help:
                Toast.makeText(this,"No.",Toast.LENGTH_SHORT).show();
                //showMainListHelpDialog();
                return true;

            case R.id.action_refresh:
                Toast.makeText(getApplicationContext(), "Refreshed!", Toast.LENGTH_SHORT).show();
                refreshFoodList();
                return true;

            default:
                return super.onOptionsItemSelected(item);

        }
    }
}
