package com.imperialtechnologies.theeatlist_3;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.util.Log;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by kdandang on 6/13/2015.
 */
public class MyFragmentPagerAdapter extends FragmentPagerAdapter {

    public static final String TAG = "MyFragmentPagerAdapter";

    private Context context;
    ArrayList<Fragment> fragmentList = new ArrayList<>();
    private static HashMap<Integer,String> positionToTag = new HashMap<>();

    public int TAB_COUNT = 3;
    private String tabTitles[] = new String[] {"List", "Eaten", "Friends"};

    public MyFragmentPagerAdapter(FragmentManager fm, Context context, ArrayList<Fragment> fragments) {

        super(fm);
        this.context = context;
        this.fragmentList = fragments;
        TAB_COUNT = fragments.size();

    }

    @Override
    public Object instantiateItem(ViewGroup container, int position) {

        Fragment fragment = (Fragment) super.instantiateItem(container, position);
        Log.d(TAG, "instantiateItem Position value: " + Integer.toString(position));

        positionToTag.put(position, fragment.getTag());
        Log.d(TAG, "positionToTag Position: " + Integer.toString(position)
                + " hashTag: " + positionToTag.get(position));

        return fragment;

    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {

        Log.w(TAG,"starting destroyItem on position: " + Integer.toString(position));
        fragmentList.remove(position);
        super.destroyItem(container, position, object);

    }

    @Override
    public CharSequence getPageTitle(int position) {
        return tabTitles[position];
    }

    /**
     * Return the Fragment associated with a specified position.
     *
     * @param position
     */
    @Override
    public Fragment getItem(int position) {

        Log.d(TAG,"starting getItem");

        if (fragmentList.isEmpty()) {
            //so it works with an empty fragment list? its stupid lol..
            switch (position) {
                case 0:
                    return new FragmentMainList();
                case 1:
                    return new FragmentMainList();
                case 2:
                    return new FragmentFriendsList();
                default:
                    Log.wtf(TAG, "getItem(): Position requested was not within bounds");
                    return null;
            }
        } else {

            Log.d(TAG,"Returning Fragment in position: " + Integer.toString(position));
            return fragmentList.get(position);

        }

    }

    public Fragment getFragmentFromPosition(int position){

        Log.d(TAG,"starting getFragmentFromPosition");

        Fragment fragment = fragmentList.get(position);
        if (fragment == null) {
            Log.d(TAG, "Failed to get Fragment @ Position: " + Integer.toString(position));
        }

        return fragment;

    }

    /**
     * Returns the tag for the fragment in the requested position.
     * The tag and position values are based on when the fragments
     * were instantiated in the instantiateItem method.
     *
     * @param position
     * @return the tag for the position requested, or in case of
     * failure it returns the String "Empty String"
     */
    public String getTagOfPosition(int position){

        if (positionToTag.containsKey(position)){
            Log.d(TAG,"positionToTag Success");
            return positionToTag.get(position);
        }


        Log.d(TAG,"positionToTag Failure");
        return "Empty String";
    }

    /**
     * Return the number of views available.
     */
    @Override
    public int getCount() {
        return TAB_COUNT;
    }
}
