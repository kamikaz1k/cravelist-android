package com.imperialtechnologies.theeatlist_3;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.util.Log;
import android.util.SparseArray;
import android.view.ViewGroup;

import java.util.ArrayList;

/**
 * Created by kdandang on 6/13/2015.
 */
public class MyFragmentPagerAdapter extends FragmentPagerAdapter {

    private Context context;
    SparseArray<Fragment> registeredFragments = new SparseArray<Fragment>();
    ArrayList<Fragment> fragmentList = new ArrayList<>();

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
        registeredFragments.put(position, fragment);

        Log.d("PagerAdapter", "Position value: " + Integer.toString(position));

        return fragment;

    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {

        registeredFragments.remove(position);
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
                    Log.wtf("FragmentPagerAdapter", "getItem(): Position requested was not within bounds");
                    return null;
            }
        } else {

            Log.d("MFPA","Returning Fragment in position: " + Integer.toString(position));
            return fragmentList.get(position);

        }

    }

    public Fragment getRegisteredFragment(int position){

        Fragment frag = registeredFragments.get(position);
        if (frag == null) {
            Log.d("getRegFrag", "Position: " + Integer.toString(position));
        }

        return frag;

    }

    /**
     * Return the number of views available.
     */
    @Override
    public int getCount() {
        return TAB_COUNT;
    }
}
