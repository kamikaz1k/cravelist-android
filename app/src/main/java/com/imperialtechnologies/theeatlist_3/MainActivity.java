package com.imperialtechnologies.theeatlist_3;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.ListFragment;
import android.support.v4.view.ViewPager;
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

import de.greenrobot.event.EventBus;


public class MainActivity extends ActionBarActivity {

    public final static String ACTIVE_TAB = "com.imperialtechnologies.theeatlist_3.MainActivity.ACTIVE_TAB";
    public final static String TAG = "MainActivity";

    private static ListFragment listTabFragment;
    private static ListFragment eatenTabFragment;
    private static ListFragment friendsTabFragment;

    //PagerAdapter declaration
    MyFragmentPagerAdapter pagerAdapter;

    // The Intent is used to issue an operation should be performed
    public final static int NEW_FOOD_ITEM = 1;
    public final static int EDIT_FOOD_ITEM = 2;

    // The object that allows me to manipulate the database
    DBTools dbTools;

    //filter references
    FilterQueryProvider searchFilter;
    FilterQueryProvider eatenFilter;

    //Declare event bus
    UpdateFoodListEvent event = new UpdateFoodListEvent();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initializeFragments();

        initializeToolbarAndTabs();

        //Only used for delete all items - deprecate it soon
        dbTools = new DBTools(this);

    }

    private void initializeFragments(){

        listTabFragment = new FragmentMainList();
        eatenTabFragment = new FragmentEatenList();
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

    public void onActivityResult(int requestCode, int resultCode, Intent data){
        // request code is the code sent out from startActivityForResult
        // result code is the first argument in setResult() from the returning intent

        Log.d(TAG, "requestCode: " + Integer.toString(requestCode));
        Log.d(TAG, "EDIT_FOOD_ITEM: " + Integer.toString(EDIT_FOOD_ITEM));
        Log.d(TAG, "NEW_FOOD_ITEM: " + Integer.toString(NEW_FOOD_ITEM));

        //Use eventbus to notify fragments
        //send a foodId value via event.setFoodId
        Log.d(TAG, "Posted on EventBus");
        EventBus.getDefault().post(event);

    }

    @Override
    protected void onDestroy() {

        Log.i(TAG,"calling onDestroy()");
        dbTools.close();

        super.onDestroy();
    }

    public void refreshFoodList(String eaten, String foodId){

        HashMap<String,String> foodDetails = dbTools.getFoodItemDetails(foodId);

        String[] foodItem = {foodDetails.get("_id"),foodDetails.get("foodItemName"),foodDetails.get("foodItemLocation")};
        //"_id","foodItemName", "foodItemLocation"

        //cant foodListAdapter.add(); - not a valid method...

    }

    public void showAddFoodItem(View view) {
        Intent theIntent = new Intent(getApplicationContext(), NewFoodItem.class);

        //TODO - remove ACTIVE_TAB extra
        theIntent.putExtra(ACTIVE_TAB, 0);

        Toast.makeText(getApplicationContext(), "Sending Intent to NewFoodItem", Toast.LENGTH_SHORT).show();
        Log.d("Sending to NewFoodItem", "Active_Tab: " + Integer.toString(0));

        startActivityForResult(theIntent, NEW_FOOD_ITEM);
        overridePendingTransition(R.animator.push_left_in, R.animator.fade_under);

    }

    public void searchMainList(){

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Search");

        // Set up the input
        final EditText input = new EditText(this);
        input.setGravity(EditText.TEXT_ALIGNMENT_CENTER);
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

        //foodListAdapter.getFilter().filter(search.toString());

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

        //TODO - use a different event for deletion update
        EventBus.getDefault().post(event);

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
                Toast.makeText(getApplicationContext(), "Not Searching", Toast.LENGTH_SHORT).show();
                //searchMainList();
                return true;

            case R.id.action_new_item:
                showAddFoodItem(findViewById(R.id.action_new_item));
                return true;

            case R.id.action_delete_all_items:
                confirmDeleteAllFoodItems();
                return true;

            case R.id.action_main_help:
                Toast.makeText(this,"No.",Toast.LENGTH_SHORT).show();
                return true;

            case R.id.action_refresh:
                Toast.makeText(getApplicationContext(), "Not Refreshed!", Toast.LENGTH_SHORT).show();
                return true;

            default:
                return super.onOptionsItemSelected(item);

        }
    }
}
